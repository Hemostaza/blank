package com.hemostaza.blank.listeners;

import com.hemostaza.blank.BlankPlugin;
import com.hemostaza.blank.SignUtils;
import com.hemostaza.blank.Warp;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.scheduler.BukkitTask;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Objects;
import java.util.UUID;

public class TeleportListener implements Listener {
    private final BlankPlugin plugin;
    private static FileConfiguration config;
    private final HashMap<UUID, BukkitTask> teleportTasks = new HashMap<>();
    private final HashSet<UUID> invinciblePlayers = new HashSet<>();
    private final HashMap<UUID, Double> pendingTeleportCosts = new HashMap<>();
    private final HashMap<UUID, Integer> pendingItemCosts = new HashMap<>();

    public TeleportListener(BlankPlugin plugin) {
        this.plugin = plugin;
        config = plugin.getConfig();
    }

    @EventHandler
    public void onPlayerUse(PlayerInteractEvent event) {
        if (!(Objects.equals(event.getHand(), EquipmentSlot.HAND))) {
            return;
        }
        Player player = event.getPlayer();
        if (!player.isSneaking()) {
            return;
        }
        if (!(event.getAction() == Action.RIGHT_CLICK_BLOCK || event.getAction() == Action.RIGHT_CLICK_AIR)) {
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
        String homeName = player.getInventory().getItemInMainHand().getItemMeta().getLore().get(1);
        if(homeName==null){
            return;
        }

        teleportPlayer(player,homeName,false,0);

    }

    private void teleportPlayer(Player player, String warpName, boolean useEconomy, double cost) {
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
            player.getInventory().getItemInMainHand().setAmount(player.getInventory().getItemInMainHand().getAmount() - 1);
            // Remove the task from the map after completion
            teleportTasks.remove(playerUUID);
            // Remove the player from the invincible list
            invinciblePlayers.remove(playerUUID);

        }, cooldown * 20L); // 20 ticks = 1 second

        // Store the task in the map
        teleportTasks.put(playerUUID, teleportTask);
    }

}
