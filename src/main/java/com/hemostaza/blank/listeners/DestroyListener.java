package com.hemostaza.blank.listeners;

import com.hemostaza.blank.BlankPlugin;
import com.hemostaza.blank.SignData;
import com.hemostaza.blank.SignUtils;
import com.hemostaza.blank.Warp;
import com.hemostaza.blank.items.ItemManager;
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
import java.util.List;

public class DestroyListener implements Listener {
    private final BlankPlugin plugin;
    private static FileConfiguration config;


    public DestroyListener(BlankPlugin plugin) {
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

        if (!player.hasPermission("signwarp.create")) {
            String noPermissionMessage = config.getString("messages.destroy_permission");
            if (noPermissionMessage != null) {
                player.sendMessage(ChatColor.translateAlternateColorCodes('&', noPermissionMessage));
            }
            event.setCancelled(true);
            return;
        }

        Warp warp = Warp.getByName(signData.warpName+signData.warpNameSuf);

        if (warp == null) {
            return;
        }

        block.getWorld().dropItem(block.getLocation(),ItemManager.houseCreator);

        warp.remove();

        player.sendMessage(ChatColor.translateAlternateColorCodes('&', config.getString("messages.warp_destroyed")));
    }
    private boolean hasBlockWarpSign(Block block) {
        return SignUtils.hasBlockSign(block, this::isWarpSign);
    }

    private boolean hasBlockWarpSign(List<Block> blocks) {
        return SignUtils.hasBlockSign(blocks, this::isWarpSign);
    }

    private boolean isWarpSign(Sign signBlock) {
        SignData signData = new SignData(signBlock.getSide(Side.FRONT).getLines());
        return signData.isHomeSign();
    }
}

