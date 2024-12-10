package com.hemostaza.blank.listeners;

import com.hemostaza.blank.BlankPlugin;
import com.hemostaza.blank.SignData;
import com.hemostaza.blank.utils.SignUtils;
import com.hemostaza.blank.Warp;
import com.hemostaza.blank.items.ItemManager;
import com.hemostaza.blank.utils.Utils;
import org.bukkit.ChatColor;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.bukkit.block.sign.Side;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;

public class HomeWandListener implements Listener {
    private final BlankPlugin plugin;
    private static FileConfiguration config;

    public HomeWandListener(BlankPlugin plugin) {
        this.plugin = plugin;
        config = plugin.getConfig();
    }

    @EventHandler
    public void onPlayerUse(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        if(!Utils.isValidUse(player,event,false,true)){
            return;
        }
        Block block = event.getClickedBlock();

        Sign sign = SignUtils.getSignFromBlock(block);
        if(sign==null){
            return;
        }
        SignData signData = new SignData(sign.getSide(Side.FRONT).getLines());

        if(Utils.isValidMeta(player, ItemManager.houseCreator.getItemMeta())){

            if(!signData.isHomeSign()){
                return;
            }
            if(!player.hasPermission("homedepot.create")){
                String message = config.getString("messages.create_permission");
                if(message!=null){
                    player.sendMessage(ChatColor.translateAlternateColorCodes('&', message));
                }
                return;
            }
            if(!signData.isValidHomeName()){

                String noWarpNameMessage = config.getString("messages.no_warp_name");
                if (noWarpNameMessage != null) {
                    player.sendMessage(ChatColor.translateAlternateColorCodes('&', noWarpNameMessage));
                }
                event.setCancelled(true);
                return;
            }

            String suff = "#"+player.getName();
            Warp existingWarp = Warp.getByName(signData.warpName+suff);
            if(existingWarp!=null){
                String warpNameTakenMessage = config.getString("messages.warp_name_taken");
                if (warpNameTakenMessage != null) {
                    player.sendMessage(ChatColor.translateAlternateColorCodes('&', warpNameTakenMessage));
                }
                event.setCancelled(true);
                return;
            }

            String currentDateTime = java.time.LocalDateTime.now().toString();
            Warp warp = new Warp(signData.warpName+suff, player.getLocation(), currentDateTime);
            warp.save();

            sign.getSide(Side.FRONT).setLine(0, ChatColor.BLUE + "[HOME]");

            String targetSignCreatedMessage = config.getString("messages.target_sign_created");
            if (targetSignCreatedMessage != null) {
                player.sendMessage(ChatColor.translateAlternateColorCodes('&', targetSignCreatedMessage));
            }
            player.getInventory().getItemInMainHand().setAmount(player.getInventory().getItemInMainHand().getAmount() - 1);
            sign.getSide(Side.FRONT).setLine(2, suff);
            sign.setWaxed(true);
            sign.update();
        }
    }
}
