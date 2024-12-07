package com.hemostaza.blank.items;

import com.hemostaza.blank.BlankPlugin;
import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemRarity;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.PotionMeta;

import java.util.ArrayList;
import java.util.List;

public class ItemManager {
    private static FileConfiguration config;

    public static ItemStack paper;
    public static ItemStack houseCreator;
    public static ItemStack weakExpBottle;
    public static ItemStack mediumExpBottle;
    public static ItemStack strongExpBottle;

    public static void init(BlankPlugin plugin){

        config = plugin.getConfig();
        createPaper();
        createHouseCreator();
        createWeakExpPotion();
        createMediumExpPotion();
        createStrongExpPotion();
    }

    private static void createPaper() {
        ItemStack item = new ItemStack(Material.PAPER, 1);
        ItemMeta meta = item.getItemMeta();
        assert meta != null;
        meta.setDisplayName(config.getString("blankcouponitem.name"));
        List<String> lore = new ArrayList<>();
        lore.add(config.getString("blankcouponitem.lore.l1"));
        lore.add(config.getString("blankcouponitem.lore.l2"));
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
        List<String> lore = new ArrayList<>();
        lore.add(config.getString("couponitem.lore.l1"));
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
        List<String> lore = new ArrayList<>();
        lore.add(config.getString("wanditem.lore.l1"));
        meta.setLore(lore);
        item.setItemMeta(meta);
        houseCreator = item;
    }

    private static void createWeakExpPotion(){
        ItemStack potion = new ItemStack(Material.POTION,1);
        PotionMeta meta = (PotionMeta) potion.getItemMeta();
        assert meta != null;
        meta.setDisplayName(config.getString("weak_potion.name"));
        List<String> lore = new ArrayList<>();
        lore.add(config.getString("weak_potion.lore.l1"));
        lore.add(config.getString("weak_potion.lore.l2"));
        meta.setLore(lore);
        meta.setColor(Color.fromRGB(94,124,22));
        meta.addItemFlags(ItemFlag.HIDE_ADDITIONAL_TOOLTIP);
        potion.setItemMeta(meta);
        meta.setEnchantmentGlintOverride(true);
        weakExpBottle = potion;
    }
    private static void createMediumExpPotion(){
        ItemStack potion = new ItemStack(Material.POTION,1);
        PotionMeta meta = (PotionMeta) potion.getItemMeta();
        assert meta != null;
        meta.setDisplayName(config.getString("medium_potion.name"));
        List<String> lore = new ArrayList<>();
        lore.add(config.getString("medium_potion.lore.l1"));
        lore.add(config.getString("medium_potion.lore.l2"));
        meta.setLore(lore);
        meta.setRarity(ItemRarity.UNCOMMON);
        meta.setColor(Color.fromRGB(128,199,31));
        meta.addItemFlags(ItemFlag.HIDE_ADDITIONAL_TOOLTIP);
        potion.setItemMeta(meta);
        meta.setEnchantmentGlintOverride(true);
        mediumExpBottle = potion;
    }
    private static void createStrongExpPotion(){
        ItemStack potion = new ItemStack(Material.POTION,1);
        PotionMeta meta = (PotionMeta) potion.getItemMeta();
        assert meta != null;
        meta.setDisplayName(config.getString("strong_potion.name"));
        List<String> lore = new ArrayList<>();
        lore.add(config.getString("strong_potion.lore.l1"));
        lore.add(config.getString("strong_potion.lore.l2"));
        meta.setLore(lore);
        meta.setRarity(ItemRarity.RARE);
        meta.setColor(Color.fromRGB(150,239,72));
        meta.addItemFlags(ItemFlag.HIDE_ADDITIONAL_TOOLTIP);
        meta.setEnchantmentGlintOverride(true);
        potion.setItemMeta(meta);
        strongExpBottle = potion;
    }
}
