package com.hemostaza.blank;

import com.hemostaza.blank.commands.BuyCommands;
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

        PluginCommand command = getCommand("homedepot");
        if(command!=null){
            MainCommands mc = new MainCommands(this);
            command.setExecutor(mc);
        }
        PluginCommand buyCommand = getCommand("homedepotbuy");
        if(buyCommand!=null){
            BuyCommands bc = new BuyCommands(this);
            buyCommand.setExecutor(bc);
        }

        // Setup Vault economy if available
        if (getServer().getPluginManager().getPlugin("Vault") != null) {
            if (!VaultEconomy.setupEconomy()) {
                getLogger().warning("Vault is installed but economy setup failed.");
            } else {
                getLogger().info("Vault economy setup successfully.");
            }
        } else {
            getLogger().warning("Vault not found. Economy features are disabled.");
        }

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
