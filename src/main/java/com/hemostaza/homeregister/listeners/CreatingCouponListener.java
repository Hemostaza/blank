package com.hemostaza.homeregister.listeners;

import com.hemostaza.homeregister.MainPlugin;
import com.hemostaza.homeregister.SignData;
import com.hemostaza.homeregister.utils.SignUtils;
import com.hemostaza.homeregister.Warp;
import com.hemostaza.homeregister.items.ItemManager;
import com.hemostaza.homeregister.utils.Utils;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.bukkit.block.sign.Side;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.List;

public class CreatingCouponListener implements Listener {
    private final MainPlugin plugin;
    private static FileConfiguration config;

    public CreatingCouponListener(MainPlugin plugin) {
        this.plugin = plugin;
        config = plugin.getConfig();
    }

    @EventHandler
    public void onPlayerUse(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        //first check if it is even a paper
        ItemStack itemInHand = player.getInventory().getItemInMainHand();
        if (!itemInHand.getType().equals(Material.PAPER)) {
            return;
        }
        //second check if this paper have proper meta
        ItemMeta metaInHand = itemInHand.getItemMeta();
        if(metaInHand==null){
            return;
        }

        //check if it's old coupon
        if(metaInHand.getDisplayName().equals(config.getString("couponitem.name"))){
            List<String> lore = metaInHand.getLore();
            if(lore==null){
                return;
            }
            if(lore.isEmpty()) {
                return;
            }
        }else if(!metaInHand.equals(ItemManager.paper.getItemMeta())){
            return;
        }


        //is use valid?
        if (!Utils.isValidUse(player, event, false, false)) {
            return;
        }
        Block block = event.getClickedBlock();

        Sign sign = SignUtils.getSignFromBlock(block);
        if (sign == null) {
            return;
        }

        SignData signData = new SignData(sign.getSide(Side.FRONT).getLines());

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
            String warpNameTakenMessage = config.getString("messages.warp_name_taken");
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
