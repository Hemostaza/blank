package com.hemostaza.blank;

import com.hemostaza.blank.commands.MainCommands;
import com.hemostaza.blank.items.ItemManager;
import com.hemostaza.blank.listeners.CreatingCouponListener;
import com.hemostaza.blank.listeners.DestroyListener;
import com.hemostaza.blank.listeners.HomeWandListener;
import com.hemostaza.blank.listeners.TeleportListener;
import org.bukkit.command.PluginCommand;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;

public class BlankPlugin extends JavaPlugin implements Listener {
    @Override
    public void onEnable() {

        saveDefaultConfig();
        //create db
        Warp.createTable();

        PluginCommand command = getCommand("bd");
        if(command!=null){
            MainCommands mc = new MainCommands(this);
            command.setExecutor(mc);
        }
        ItemManager.init();
        getServer().getPluginManager().registerEvents(new HomeWandListener(this), this);
        getServer().getPluginManager().registerEvents(new CreatingCouponListener(this), this);
        getServer().getPluginManager().registerEvents(new TeleportListener(this), this);
        getServer().getPluginManager().registerEvents(new DestroyListener(this), this);
        getLogger().info("onEnable is called!");
    }
    @Override
    public void onDisable() {
        getLogger().info("onDisable is called!");
    }
}
