package com.hemostaza.homeregister.items;

import com.hemostaza.homeregister.MainPlugin;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.inventory.ItemRarity;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;

import java.util.ArrayList;
import java.util.List;

public class ItemManager {
    private static FileConfiguration config;

    public static ItemStack paper;
    public static ItemStack houseCreator;
    private NamespacedKey key;

    public ItemManager(MainPlugin plugin){
        config = plugin.getConfig();
        this.key = plugin.getKey();

        createPaper();
        createHouseCreator();
    }

    private void createPaper() {
        Material material = Material.getMaterial(config.getString("blankticket.material","PAPER"));
        ItemStack item = new ItemStack(material, 1);
        ItemMeta meta = item.getItemMeta();
        assert meta != null;

        meta.getPersistentDataContainer().set(key, PersistentDataType.STRING, "*bl4nk*");

        meta.setDisplayName(config.getString("blankticket.name","Blank ticket"));
        List<String> lore = config.getStringList("blankticket.lore");
        meta.setLore(lore);
        item.setItemMeta(meta);
        paper = item;
    }

    public ItemStack createTeleportPaper(String warpName){
        return createTeleportPaper(warpName,1);
    }

    public ItemStack createTeleportPaper(String warpName, int amount){
        Material material = Material.getMaterial(config.getString("ticket.material","PAPER"));
        ItemStack item = new ItemStack(material,amount);
        ItemMeta meta = item.getItemMeta();
        assert meta != null;

        meta.getPersistentDataContainer().set(key, PersistentDataType.STRING, warpName);

        meta.setDisplayName(config.getString("ticket.name","Home ticket"));
        meta.setRarity(ItemRarity.RARE);
        meta.setEnchantmentGlintOverride(true);
        //change {warp} to warp name
        List<String> lore = new ArrayList<>();
        List<String> configList = config.getStringList("ticket.lore");
        //l.info(configList.toString());
        for (String line : configList) {
            lore.add(line.replace("{home}", warpName));
        }
        if (lore.isEmpty()) lore.add("Warp to "+ warpName);
//        lore.add(homename);
//        lore.add(homeSuff);
        meta.setLore(lore);
        item.setItemMeta(meta);
        return item;
    }

    private void createHouseCreator(){
        Material material = Material.getMaterial(config.getString("registerer.material","STICK"));
        ItemStack item = new ItemStack(material,1);
        ItemMeta meta = item.getItemMeta();
        assert meta != null;

        meta.getPersistentDataContainer().set(key, PersistentDataType.STRING, "*w4nd*");

        meta.setDisplayName(config.getString("registerer.name","Home registerer"));
        List<String> lore = config.getStringList("registerer.lore");
        meta.setLore(lore);
        item.setItemMeta(meta);
        houseCreator = item;
    }
}
