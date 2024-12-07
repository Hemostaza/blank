package com.hemostaza.blank.listeners;

import com.hemostaza.blank.BlankPlugin;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.BrewEvent;
import org.bukkit.event.player.PlayerItemConsumeEvent;
import org.bukkit.inventory.BrewerInventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.potion.PotionType;

import java.util.ArrayList;
import java.util.List;

public class BrewingListener implements Listener {

    private final BlankPlugin plugin;
    private static FileConfiguration config;
    public BrewingListener(BlankPlugin plugin) {
        this.plugin = plugin;
        config = plugin.getConfig();
    }
    @EventHandler
    public void brewEvent(BrewEvent event) {
        List<ItemStack> results = event.getResults();
        for (ItemStack r : results){
            if(r.getType().equals(Material.POTION)){
                PotionMeta pm = (PotionMeta) r.getItemMeta();
                if(pm==null){
                    continue;
                }
                if(pm.getBasePotionType()==null){
                    continue;
                }
                if(pm.getBasePotionType().equals(PotionType.AWKWARD) || pm.getBasePotionType().equals(PotionType.THICK)
                || pm.getBasePotionType().equals(PotionType.MUNDANE)){
                    Bukkit.getLogger().info("Potion craft?");
                    List<String> lore = new ArrayList<>();
                    lore.add(config.getString("potion.lore.l1"));
                    lore.add(config.getString("potion.lore.l2"));
                    lore.add(config.getString("potion.lore.l3"));
                    pm.setLore(lore);
                    r.setItemMeta(pm);
                }
            }
        }
    }
}
