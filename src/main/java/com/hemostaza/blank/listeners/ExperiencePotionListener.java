package com.hemostaza.blank.listeners;

import com.hemostaza.blank.BlankPlugin;
import com.hemostaza.blank.items.ItemManager;
import com.hemostaza.blank.utils.ExperienceUtils;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerItemConsumeEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.potion.PotionType;

public class ExperiencePotionListener implements Listener {
    private final BlankPlugin plugin;
    private static FileConfiguration config;
    public ExperiencePotionListener(BlankPlugin plugin) {
        this.plugin = plugin;
        config = plugin.getConfig();
    }
    @EventHandler
    public void drinkPotion(PlayerItemConsumeEvent event) {

        Player player = event.getPlayer();
        ItemStack item = event.getItem();
        if(!item.getType().equals(Material.POTION)){
            return;
        }
        PotionMeta potionMeta = (PotionMeta) item.getItemMeta();

        assert potionMeta != null;

        if(potionMeta.equals(ItemManager.weakExpBottle.getItemMeta())) {
            drinkExpPotion(player, config.getInt("weak_potion.exp"));
            return;
        }
        if(potionMeta.equals(ItemManager.mediumExpBottle.getItemMeta())) {
            drinkExpPotion(player, config.getInt("medium_potion.exp"));
            return;
        }
        if(potionMeta.equals(ItemManager.strongExpBottle.getItemMeta())) {
            drinkExpPotion(player, config.getInt("strong_potion.exp"));
            return;
        }

        if(potionMeta.getBasePotionType()==null){
            return;
        }

        if(potionMeta.getBasePotionType().equals(PotionType.MUNDANE)){
            //-100
            drinkDeexpPotion(player, event, config.getInt("weak_potion.exp"),ItemManager.weakExpBottle);
            return;
        }
        if(potionMeta.getBasePotionType().equals(PotionType.AWKWARD)){
            //awkward -500exp
            drinkDeexpPotion(player, event, config.getInt("medium_potion.exp"),ItemManager.mediumExpBottle);
            return;
        }
        if(potionMeta.getBasePotionType().equals(PotionType.THICK)){
            //-1500;
            drinkDeexpPotion(player, event, config.getInt("strong_potion.exp"),ItemManager.strongExpBottle);
            return;
        }


    }

    private void drinkDeexpPotion(Player player, PlayerItemConsumeEvent event, int potionDeExp, ItemStack potionExp){
        if(ExperienceUtils.getExp(player)<potionDeExp){
            player.sendMessage(config.getString("messages.not_enough_exp"));
            event.setCancelled(true);
            return;
        }
        ExperienceUtils.changeExp(player,-potionDeExp);
        ItemStack itemInHand = player.getInventory().getItemInMainHand();
        int amount = itemInHand.getAmount()-1;
        itemInHand.setAmount(amount);
        player.getInventory().addItem(potionExp);
    }
    private void drinkExpPotion(Player player,int potionExp){
        ExperienceOrb expOrb = (ExperienceOrb)player.getWorld().spawnEntity(player.getLocation(),EntityType.EXPERIENCE_ORB);
        expOrb.setExperience(potionExp);
    }
}
