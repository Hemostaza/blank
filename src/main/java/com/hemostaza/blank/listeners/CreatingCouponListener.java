package com.hemostaza.blank.listeners;

import com.hemostaza.blank.BlankPlugin;
import com.hemostaza.blank.SignData;
import com.hemostaza.blank.SignUtils;
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
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.Objects;

public class CreatingCouponListener implements Listener {
    private final BlankPlugin plugin;
    private static FileConfiguration config;

    public CreatingCouponListener(BlankPlugin plugin) {
        this.plugin = plugin;
        config = plugin.getConfig();
    }

    @EventHandler
    public void onPlayerUse(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        if (!Utils.isValidUse(player, event, false, false)) {
            return;
        }
        Block block = event.getClickedBlock();

        Sign sign = SignUtils.getSignFromBlock(block);
        if (sign == null) {
            return;
        }

        SignData signData = new SignData(sign.getSide(Side.FRONT).getLines());

        String signHomeName = signData.warpName;

        if (Utils.isValidMeta(player, "Kupon powrotu", signHomeName) || Utils.isValidMeta(player, ItemManager.paper.getItemMeta())) {

            if (!signData.isHomeSign()) {
                return;
            }
            if (!signData.isValidHomeName()) {

                String noWarpNameMessage = config.getString("messages.no_warp_name");
                if (noWarpNameMessage != null) {
                    player.sendMessage(ChatColor.translateAlternateColorCodes('&', noWarpNameMessage));
                }
                event.setCancelled(true);
                return;
            }

            Warp existingWarp = Warp.getByName(signData.warpName + signData.warpNameSuf);
            if (existingWarp == null) {
                String warpNameTakenMessage = "WArrp nie istnieje kurwiju cos zjebales";
                if (warpNameTakenMessage != null) {
                    player.sendMessage(ChatColor.translateAlternateColorCodes('&', warpNameTakenMessage));
                }
                event.setCancelled(true);
                return;
            }
            if (player.isSneaking()) {
                int quantity = player.getInventory().getItemInMainHand().getAmount();
                player.getInventory().addItem(ItemManager.createTeleportPaper(signData.warpName, signData.warpNameSuf, quantity));
                player.getInventory().getItemInMainHand().setAmount(player.getInventory().getItemInMainHand().getAmount() - quantity);
            } else {
                player.getInventory().addItem(ItemManager.createTeleportPaper(signData.warpName, signData.warpNameSuf));
                player.getInventory().getItemInMainHand().setAmount(player.getInventory().getItemInMainHand().getAmount() - 1);
            }
        }
    }
}
