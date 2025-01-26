package com.hemostaza.homeregister.listeners;

import com.hemostaza.homeregister.MainPlugin;
import com.hemostaza.homeregister.SignData;
import com.hemostaza.homeregister.utils.SignUtils;
import com.hemostaza.homeregister.Warp;
import com.hemostaza.homeregister.items.ItemManager;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Tag;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.bukkit.block.sign.Side;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;

import java.io.IOException;

public class DestroyListener implements Listener {
    private final MainPlugin plugin;
    private static FileConfiguration config;


    public DestroyListener(MainPlugin plugin) {
        this.plugin = plugin;
        config = plugin.getConfig();
    }

    @EventHandler
    public void onBlockBreak(BlockBreakEvent event) throws IOException {

        Block block = event.getBlock();
        Material blockType = block.getType();

        if (!Tag.ALL_SIGNS.isTagged(blockType)) {
            if (hasBlockWarpSign(block)) {
                event.setCancelled(true);
            }
            return;
        }

        Sign signBlock = SignUtils.getSignFromBlock(block);

        if (signBlock == null) {
            return;
        }

        SignData signData = new SignData(signBlock.getSide(Side.FRONT).getLines());

        if (!signData.isHomeSign()) {
            return;
        }

        if (!signData.isValidHomeName()) {
            return;
        }

        Player player = event.getPlayer();

        //if player is owner
        if (!player.hasPermission("homedepot.destroy")) {
            String noPermissionMessage = config.getString("messages.destroy_permission");
            if (noPermissionMessage != null) {
                player.sendMessage(ChatColor.translateAlternateColorCodes('&', noPermissionMessage));
            }
            event.setCancelled(true);
            signBlock.getSide(Side.FRONT).setLine(0,"[HOME]");
            signBlock.getSide(Side.FRONT).setLine(1,signData.warpName);
            signBlock.getSide(Side.FRONT).setLine(2,signData.warpNameSuf);
            return;
        }
        //if player isn't owner
        if(!signData.warpNameSuf.equals("#"+player.getName())){
            if (!player.hasPermission("homedepot.remove")) {
                String noPermissionMessage = config.getString("messages.destroy_permission");
                if (noPermissionMessage != null) {
                    player.sendMessage(ChatColor.translateAlternateColorCodes('&', noPermissionMessage));
                }
                event.setCancelled(true);
                signBlock.getSide(Side.FRONT).setLine(0,"[HOME]");
                signBlock.getSide(Side.FRONT).setLine(1,signData.warpName);
                signBlock.getSide(Side.FRONT).setLine(2,signData.warpNameSuf);
                return;
            }
        }

        Warp warp = Warp.getByName(signData.warpName+signData.warpNameSuf);

        if (warp == null) {
            return;
        }

        block.getWorld().dropItem(block.getLocation(),ItemManager.houseCreator);

        warp.remove();
        String message = config.getString("messages.warp_destroyed");
        if(message!=null){
            player.sendMessage(ChatColor.translateAlternateColorCodes('&', message));
        }
    }
    private boolean hasBlockWarpSign(Block block) {
        return SignUtils.hasBlockSign(block, this::isWarpSign);
    }

    private boolean isWarpSign(Sign signBlock) {
        SignData signData = new SignData(signBlock.getSide(Side.FRONT).getLines());
        return signData.isHomeSign();
    }
}

