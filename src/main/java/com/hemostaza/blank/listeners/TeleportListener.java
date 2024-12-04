package com.hemostaza.blank.listeners;

import com.hemostaza.blank.BlankPlugin;
import com.hemostaza.blank.SignUtils;
import com.hemostaza.blank.Warp;
import com.hemostaza.blank.utils.Utils;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.scheduler.BukkitTask;

import java.util.HashMap;
import java.util.HashSet;
import java.util.UUID;

public class TeleportListener implements Listener {
    private final BlankPlugin plugin;
    private static FileConfiguration config;
    private final HashMap<UUID, BukkitTask> teleportTasks = new HashMap<>();
    private final HashSet<UUID> invinciblePlayers = new HashSet<>();

    public TeleportListener(BlankPlugin plugin) {
        this.plugin = plugin;
        config = plugin.getConfig();
    }

    @EventHandler
    public void onPlayerUse(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        if(!Utils.isValidUse(player,event,true,false)){
            return;
        }
        Block block = event.getClickedBlock();
        Sign sign = SignUtils.getSignFromBlock(block);
        if(sign!=null){
            return;
        }

        ItemMeta metaInHand;
        metaInHand = player.getInventory().getItemInMainHand().getItemMeta();
        if(metaInHand==null){
            return;
        }
        if(!metaInHand.getDisplayName().equals("Kupon powrotu")){
            return;
        }
        String homeName = metaInHand.getLore().get(1);
        String homeSuff = metaInHand.getLore().get(2);
        if(homeName==null){
            return;
        }
        if(homeSuff==null){
            return;
        }

        ItemStack usedItem = player.getInventory().getItemInMainHand();

        teleportPlayer(player,homeName+homeSuff,usedItem);

    }

    private void teleportPlayer(Player player, String warpName, ItemStack usedItem) {
        Warp warp = Warp.getByName(warpName);

        if (warp == null) {
            String warpNotFoundMessage = config.getString("messages.warp_not_found");
            if (warpNotFoundMessage != null) {
                player.sendMessage(ChatColor.translateAlternateColorCodes('&', warpNotFoundMessage));
            }
            return;
        }

        int cooldown = 1;//config.getInt("teleport-cooldown", 5);

        String teleportMessage = config.getString("messages.teleport");
        if (teleportMessage != null) {
            player.sendMessage(ChatColor.translateAlternateColorCodes('&', teleportMessage.replace("{warp-name}", warp.getName()).replace("{time}", String.valueOf(cooldown))));
        }

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

            if(player.getInventory().containsAtLeast(usedItem,1)){
                ItemStack useItem = usedItem.clone();
                useItem.setAmount(1);
                player.getInventory().removeItem(useItem);
                player.teleport(targetLocation);


            String soundName = config.getString("teleport-sound", "ENTITY_ENDERMAN_TELEPORT");
            String effectName = config.getString("teleport-effect", "ENDER_SIGNAL");

            Sound sound = Sound.valueOf(soundName);
            Effect effect = Effect.valueOf(effectName);

            World world = targetLocation.getWorld();
            world.playSound(targetLocation, sound, 1, 1);
            world.playEffect(targetLocation, effect, 10);

            String successMessage = config.getString("messages.teleport-success");
            if (successMessage != null) {
                player.sendMessage(ChatColor.translateAlternateColorCodes('&', successMessage.replace("{warp-name}", warp.getName())));
            }
            //player.getInventory().getItemInMainHand().setAmount(player.getInventory().getItemInMainHand().getAmount() - 1);
            }else
            {
                String successMessage = config.getString("messages.teleport-error");
                if (successMessage != null) {
                    player.sendMessage(ChatColor.translateAlternateColorCodes('&', successMessage.replace("{warp-name}", warp.getName())));
                }
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
