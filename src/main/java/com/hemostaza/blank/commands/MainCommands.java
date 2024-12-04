package com.hemostaza.blank.commands;

import com.hemostaza.blank.Warp;
import com.hemostaza.blank.items.ItemManager;
import org.bukkit.*;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitTask;

import java.util.UUID;

public class MainCommands implements CommandExecutor {

    private final JavaPlugin plugin;
    public MainCommands(JavaPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String s, String[] args) {
        if(!(sender instanceof Player)){
            sender.sendMessage("Chuj ci w dupe xD");
            return true;
        }
        Player p = (Player) sender;
        if(args[0].equalsIgnoreCase("patyk")){
            p.getInventory().addItem(ItemManager.houseCreator);
        }
        if(args[0].equalsIgnoreCase("papier")){
            p.getInventory().addItem(ItemManager.paper);
        }

        if(args[0].equalsIgnoreCase("tp")){
            teleportPlayer(p,args[1],false,0);
        }

        return true;
    }

    private void teleportPlayer(Player player, String warpName, boolean useEconomy, double cost) {
        Warp warp = Warp.getByName(warpName);

        if (warp == null) {
            String warpNotFoundMessage = "Niema takiego wartpa";
            if (warpNotFoundMessage != null) {
                player.sendMessage(ChatColor.translateAlternateColorCodes('&', warpNotFoundMessage));
            }
            return;
        }

        int cooldown = 5;

        String teleportMessage = "chuja";
        if (teleportMessage != null) {
            player.sendMessage(ChatColor.translateAlternateColorCodes('&', teleportMessage.replace("{warp-name}", warp.getName()).replace("{time}", String.valueOf(cooldown))));
        }
        Location targetLocation = warp.getLocation();
        player.teleport(targetLocation);
    }
}
