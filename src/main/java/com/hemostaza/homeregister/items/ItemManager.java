package com.hemostaza.homeregister.items;

import com.hemostaza.homeregister.MainPlugin;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.inventory.ItemRarity;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.List;

public class ItemManager {
    private static FileConfiguration config;

    public static ItemStack paper;
    public static ItemStack houseCreator;
    public static ItemStack weakExpBottle;
    public static ItemStack mediumExpBottle;
    public static ItemStack strongExpBottle;

    public static void init(MainPlugin plugin){

        config = plugin.getConfig();
        createPaper();
        createHouseCreator();
    }

    private static void createPaper() {
        ItemStack item = new ItemStack(Material.PAPER, 1);
        ItemMeta meta = item.getItemMeta();
        assert meta != null;
        meta.setDisplayName(config.getString("blankcouponitem.name"));
        List<String> lore = config.getStringList("blankcouponitem.lore");
//        lore.add(config.getString("blankcouponitem.lore.l1"));
//        lore.add(config.getString("blankcouponitem.lore.l2"));
        meta.setLore(lore);
        item.setItemMeta(meta);
        paper = item;
    }

    public static ItemStack createTeleportPaper(String homename,String homeSuff){
        return createTeleportPaper(homename,homeSuff,1);
    }

    public static ItemStack createTeleportPaper(String homename, String homeSuff, int amount){
        ItemStack item = new ItemStack(Material.PAPER,amount);
        ItemMeta meta = item.getItemMeta();
        assert meta != null;
        meta.setDisplayName(config.getString("couponitem.name"));
        meta.setRarity(ItemRarity.RARE);
        meta.setEnchantmentGlintOverride(true);
        List<String> lore = config.getStringList("couponitem.lore");
        lore.add(homename);
        lore.add(homeSuff);
        meta.setLore(lore);
        item.setItemMeta(meta);
        return item;
    }

    private static void createHouseCreator(){
        ItemStack item = new ItemStack(Material.STICK,1);
        ItemMeta meta = item.getItemMeta();
        assert meta != null;
        meta.setDisplayName(config.getString("wanditem.name"));
        List<String> lore = config.getStringList("wanditem.lore");
        meta.setLore(lore);
        item.setItemMeta(meta);
        houseCreator = item;
    }
}
