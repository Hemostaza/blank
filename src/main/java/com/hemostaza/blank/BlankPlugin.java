package com.hemostaza.blank;

import com.hemostaza.blank.commands.MainCommands;
import com.hemostaza.blank.commands.TransferExp;
import com.hemostaza.blank.items.ItemManager;
import com.hemostaza.blank.listeners.*;
import org.bukkit.command.PluginCommand;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;

public class BlankPlugin extends JavaPlugin implements Listener {
    @Override
    public void onEnable() {

        saveDefaultConfig();
        //create db
        Warp.createTable();

        PluginCommand command = getCommand("blank");
        if(command!=null){
            MainCommands mc = new MainCommands(this);
            command.setExecutor(mc);
        }
        //getServer().getPluginCommand("transferexp").setExecutor(new TransferExp(this));

        ItemManager.init(this);
        getServer().getPluginManager().registerEvents(new HomeWandListener(this), this);
        getServer().getPluginManager().registerEvents(new CreatingCouponListener(this), this);
        getServer().getPluginManager().registerEvents(new TeleportListener(this), this);
        getServer().getPluginManager().registerEvents(new DestroyListener(this), this);
        getServer().getPluginManager().registerEvents(new ExperiencePotionListener(this), this);
        getServer().getPluginManager().registerEvents(new BrewingListener(this), this);
        getLogger().info("onEnable is called!");
    }
    @Override
    public void onDisable() {
        getLogger().info("onDisable is called!");
    }
}
