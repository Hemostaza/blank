package com.hemostaza.homeregister.utils;

import org.bukkit.entity.Player;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.Objects;

public class Utils {
    public static boolean isValidUse(Player p, PlayerInteractEvent event, boolean canBeAir, boolean shiftNeeded) {
        if (!(Objects.equals(event.getHand(), EquipmentSlot.HAND))) {
            return false;
        }
        if (shiftNeeded) {
            if (!p.isSneaking()) {
                return false;
            }
        }
        if (canBeAir) {
            return event.getAction() == Action.RIGHT_CLICK_BLOCK || event.getAction() == Action.RIGHT_CLICK_AIR;
        } else return event.getAction() == Action.RIGHT_CLICK_BLOCK;
    }

    public static boolean isValidMeta(Player p, ItemMeta meta) {
        ItemMeta metaInHand;
        metaInHand = p.getInventory().getItemInMainHand().getItemMeta();
        if (metaInHand == null) {
            return false;
        }
        return metaInHand.equals(meta);
    }
}
