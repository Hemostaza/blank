package com.hemostaza.blank.items;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;

public class ItemManager {

    public static ItemStack paper;
    public static ItemStack houseCreator;

    public static void init(){
        createPaper();
        createHouseCreator();
    }

    private static void createPaper() {
        ItemStack item = new ItemStack(Material.PAPER, 1);
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName("Czysty kupon powrotu");
        List<String> lore = new ArrayList<>();
        lore.add("Czysty kupon powrotu,");
        lore.add("który musisz połączyć z domem.");
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
        meta.setDisplayName("Kupon powrotu");
        List<String> lore = new ArrayList<>();
        lore.add("Kupon powrotu do domu:");
        lore.add(homename);
        lore.add(homeSuff);
        meta.setLore(lore);
        item.setItemMeta(meta);
        return item;
    }

    private static void createHouseCreator(){
        ItemStack item = new ItemStack(Material.BLAZE_ROD,1);
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName("Pałka domostwa");
        List<String> lore = new ArrayList<>();
        lore.add("Pałka do rejestracji domu");
        lore.add("daunie");
        meta.setLore(lore);
        item.setItemMeta(meta);
        houseCreator = item;
    }
}
