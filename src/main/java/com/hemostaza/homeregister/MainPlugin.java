package com.hemostaza.homeregister;

import com.hemostaza.homeregister.commands.BuyCommands;
import com.hemostaza.homeregister.commands.MainCommands;
import com.hemostaza.homeregister.items.ItemManager;
import com.hemostaza.homeregister.listeners.*;
import org.bukkit.NamespacedKey;
import org.bukkit.command.PluginCommand;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;

public class MainPlugin extends JavaPlugin implements Listener {

    private final NamespacedKey key = new NamespacedKey(this,"warpName");
    private ItemManager im;

    @Override
    public void onEnable() {

        saveDefaultConfig();
        //create db
        Warp.createTable();

        PluginCommand command = getCommand("homeregistry");
        if(command!=null){
            MainCommands mc = new MainCommands(this);
            command.setExecutor(mc);
        }
        PluginCommand buyCommand = getCommand("homeregistrybuy");
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

        im = new ItemManager(this);

        getServer().getPluginManager().registerEvents(new ItemListeners(this),this);
        getServer().getPluginManager().registerEvents(new DestroyListener(this), this);
        getLogger().info("onEnable is called!");
    }
    @Override
    public void onDisable() {
        getLogger().info("onDisable is called!");
    }

    public NamespacedKey getKey(){
        return key;
    }

    public ItemManager getItemManager(){
        return im;
    }

}
