package com.hemostaza.homeregister.listeners;

import com.hemostaza.homeregister.MainPlugin;
import com.hemostaza.homeregister.SignData;
import com.hemostaza.homeregister.Warp;
import com.hemostaza.homeregister.items.ItemManager;
import com.hemostaza.homeregister.utils.SignUtils;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.bukkit.block.sign.Side;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.scheduler.BukkitTask;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Objects;
import java.util.UUID;
import java.util.logging.Logger;

public class ItemListeners implements Listener {
    private final MainPlugin plugin;
    private static FileConfiguration config;
    private final ItemManager itemManager;
    Logger l;
    private final HashMap<UUID, BukkitTask> teleportTasks = new HashMap<>();
    private final HashSet<UUID> invinciblePlayers = new HashSet<>();

    public ItemListeners(MainPlugin plugin) {
        this.plugin = plugin;
        l = plugin.getLogger();
        this.itemManager = plugin.getItemManager();
        config = plugin.getConfig();
    }

    @EventHandler
    public void onPlayerUse(PlayerInteractEvent event) {
        //Pierodlic czesc eventu na druga reke.
        if (!(Objects.equals(event.getHand(), EquipmentSlot.HAND))) {
            return;
        }
        Player player = event.getPlayer();
        ItemStack itemInHand = player.getInventory().getItemInMainHand();
        String warpName;
        if (event.getAction() != Action.RIGHT_CLICK_AIR && event.getAction() != Action.RIGHT_CLICK_BLOCK) {
            //l.info("Klikniecie lewym bylo guzikiem wiec jebac");
            return;
        }

        boolean isSneaking = player.isSneaking();
        try {
            PersistentDataContainer container = itemInHand.getItemMeta().getPersistentDataContainer();
            if (container.has(plugin.getKey(), PersistentDataType.STRING)) {
                warpName = container.get(plugin.getKey(), PersistentDataType.STRING);
                //event.setCancelled(true);
                if (warpName == null) {
                    //żaden z istotnych itemów więc jebać
                    //l.info("warpname is null");
                    return;
                }
            } else {
                //l.info(container + " do not have required key");
                return;
            }
        } catch (NullPointerException e) {
            //l.info("Klikniecie zlym itemem w łapkach");
            return;
        }

        Block block = event.getClickedBlock();
        Sign sign = SignUtils.getSignFromBlock(block);
        SignData signData;

        if (sign != null) {
            //l.info("Kliknieto na znak");
            signData = new SignData(sign.getSide(Side.FRONT).getLines());
            if (!signData.isHomeSign()) {
                //l.info("signData isn't homesign");
                return;
            }
            if (!signData.isValidHomeName()) {
                return;
            }
        } else {
            //Nie kliknieto na znak lol i w rekach jest item inny niz blank i wand
            if (warpName.equals("*w4nd*") || warpName.equals("*bl4nk*")) return;
            //l.info("Teleportujemy chyba teraz");
            ItemStack usedItem = player.getInventory().getItemInMainHand();
            teleportPlayer(player, warpName, usedItem);
            return;
        }
        //WAND
        if (warpName.equals("*w4nd*")) {
            //l.info("Wg wszystkich obliczen klikniecie powinno byc PPM i odbyc sie na znaku z uzyciem pałki");
            if (!player.hasPermission("homeregistry.create")) {
                String message = config.getString("messages.create_permission", "You do not have the required permissions to create home signs!");
                player.sendMessage(ChatColor.RED + message);
                return;
            }
            String suff = "#" + player.getName();
            String fullWarpName = signData.warpName + suff;
            if (signData.warpName.isBlank()) {
                String message = config.getString("messages.no_home_name", "No home name set!\nPlease specify the home name.");
                player.sendMessage(ChatColor.RED + message);
            }
            Warp existingWarp = Warp.getByName(fullWarpName);
            if (existingWarp != null) {
                String warpNameTakenMessage = config.getString("messages.home_name_taken", "A warp target with the same name already exists!");
                player.sendMessage(ChatColor.RED + warpNameTakenMessage);
                //event.setCancelled(true);
                return;
            }
            String currentDateTime = java.time.LocalDateTime.now().toString();
            Warp warp = new Warp(fullWarpName, player.getLocation(), currentDateTime);
            warp.save();

            sign.getSide(Side.FRONT).setLine(0, ChatColor.BLUE + "[HOME]");

            String targetSignCreatedMessage = config.getString("messages.home_sign_created", "Home successfully registered.");
            player.sendMessage(ChatColor.GREEN + targetSignCreatedMessage);
            //Zabranie pałki z rąk
            player.getInventory().getItemInMainHand().setAmount(player.getInventory().getItemInMainHand().getAmount() - 1);

            sign.getSide(Side.FRONT).setLine(2, suff);
            sign.setWaxed(true);
            sign.update();
        } else {
            //l.info("Wg wszystkich obliczen klikniecie powinno byc PPM na znaku z uzyciem jakiegokolwiek innego itema posiadajacego warpName");
            String fullWarpName = signData.warpName + signData.warpNameSuf;
            Warp warpOnSign = Warp.getByName(fullWarpName);
            if (warpOnSign == null) {
                //Warp on Sign doesn't exist
                String warpUnregistered = config.getString("messages.home_on_sign_not_exist", "Home on this sign is not registered!");
                player.sendMessage(ChatColor.RED + warpUnregistered);
                return;
            }
            if (warpName.equals(fullWarpName)) {
                //l.info("Ten warp jest juz na papierku weic pierdol bąka");
                return;
            }

            if (isSneaking) {
                int quantity = player.getInventory().getItemInMainHand().getAmount();
                player.getInventory().addItem(itemManager.createTeleportPaper(fullWarpName, quantity));
                player.getInventory().getItemInMainHand().setAmount(player.getInventory().getItemInMainHand().getAmount() - quantity);
            } else {
                player.getInventory().addItem(itemManager.createTeleportPaper(fullWarpName));
                player.getInventory().getItemInMainHand().setAmount(player.getInventory().getItemInMainHand().getAmount() - 1);
            }
        }
    }

    private void teleportPlayer(Player player, String warpName, ItemStack usedItem) {
        Warp warp = Warp.getByName(warpName);

        if (warp == null) {
            String warpNotFoundMessage = config.getString("messages.home_not_found", "Specified home does not exist!");
            player.sendMessage(ChatColor.RED + warpNotFoundMessage);
            return;
        }

        int cooldown = config.getInt("teleportcooldown", 1);

        String teleportMessage = config.getString("messages.teleport", "Teleporting to {home} in {time} seconds...");
        player.sendMessage(ChatColor.GOLD + teleportMessage.replace("{home}", warpName).replace("{time}", String.valueOf(cooldown)));

        UUID playerUUID = player.getUniqueId();

        // Cancel any previous teleport tasks for the player
        BukkitTask previousTask = teleportTasks.get(playerUUID);
        if (previousTask != null) {
            previousTask.cancel();
        }

        // Add the player to the invincible list
        invinciblePlayers.add(playerUUID);

        // Schedule the new teleport task
        BukkitTask teleportTask = Bukkit.getScheduler().runTaskLater(plugin, () -> {
            Location targetLocation = warp.getLocation();

            if (player.getInventory().containsAtLeast(usedItem, 1)) {
                ItemStack useItem = usedItem.clone();
                useItem.setAmount(1);
                player.getInventory().removeItem(useItem);
                player.teleport(targetLocation);

                Sound sound = Sound.ENTITY_ENDERMAN_TELEPORT;
                Effect effect = Effect.ENDER_SIGNAL;

                World world = targetLocation.getWorld();
                world.playSound(targetLocation, sound, 1, 1);
                world.playEffect(targetLocation, effect, 10);

                String successMessage = config.getString("messages.teleport-success", "Successfully teleported to {home}.");
                player.sendMessage(ChatColor.GREEN + successMessage.replace("{home}", warpName));

            } else {
                String successMessage = config.getString("messages.teleport-error", "An error occured, when teleporting to: {home}.");
                player.sendMessage(ChatColor.RED + successMessage.replace("{home}", warpName));
            }
            // Remove the task from the map after completion
            teleportTasks.remove(playerUUID);
            // Remove the player from the invincible list
            invinciblePlayers.remove(playerUUID);


        }, cooldown * 20L); // 20 ticks = 1 second

        // Store the task in the map
        teleportTasks.put(playerUUID, teleportTask);
    }

    @EventHandler
    public void onPlayerMove(PlayerMoveEvent event) {
        Player player = event.getPlayer();
        UUID playerUUID = player.getUniqueId();

        if (teleportTasks.containsKey(playerUUID)) {
            Location from = event.getFrom();
            Location to = event.getTo();

            if (from.getX() != to.getX() || from.getY() != to.getY() || from.getZ() != to.getZ()) {
                BukkitTask teleportTask = teleportTasks.get(playerUUID);
                if (teleportTask != null && !teleportTask.isCancelled()) {
                    teleportTask.cancel();
                    teleportTasks.remove(playerUUID);
                    invinciblePlayers.remove(playerUUID); // Remove invincibility
                    String cancelMessage = config.getString("messages.teleport-cancelled", "&cTeleportation cancelled.");
                    player.sendMessage(ChatColor.translateAlternateColorCodes('&', cancelMessage));
                }
            }
        }
    }

}
