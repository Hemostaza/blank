package com.hemostaza.blank.utils;

import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.block.HangingSign;
import org.bukkit.block.Sign;
import org.bukkit.block.data.BlockData;
import org.bukkit.block.data.type.WallSign;
import org.bukkit.block.sign.Side;
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

    public static boolean isValidMeta(Player p, String name, String signHomeName) {
        ItemMeta metaInHand;
        metaInHand = p.getInventory().getItemInMainHand().getItemMeta();
        if (metaInHand == null) {
            return false;
        }
        String homeName = metaInHand.getLore().get(1);
        if (homeName == null) {
            return false;
        }
        return (metaInHand.getDisplayName().equals(name) && !homeName.equals(signHomeName));
    }
    public static boolean isValidBlock(Block b){

        if(b==null){
            return false;
        }
        BlockState blockState = b.getState();

        if (blockState instanceof Sign) {
            return true;
        }
        if(blockState instanceof HangingSign){
            return true;
        }
        return false;
    }
    public static String[] SignLinesFromBloc(Block block){
        if(block==null){
            return null;
        }
        BlockData blockData = block.getBlockData();

//        if (!(blockData instanceof WallSign) && !(blockData instanceof org.bukkit.block.data.type.Sign)) {
//            return null;
//        }

        BlockState blockState = block.getState();
        if ((blockState instanceof Sign)) {
            Sign s = (Sign) blockState;
            return s.getSide(Side.FRONT).getLines();
        }
        if((blockState instanceof Sign)){
            HangingSign s = (HangingSign) blockState;
            return s.getSide(Side.FRONT).getLines();
        }

        return null;
    }
}
