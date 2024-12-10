package com.hemostaza.blank.commands;

import com.hemostaza.blank.Warp;
import com.hemostaza.blank.items.ItemManager;
import org.bukkit.*;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitTask;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class MainCommands implements CommandExecutor, TabCompleter {

    private final JavaPlugin plugin;
    private static FileConfiguration config;
    public MainCommands(JavaPlugin plugin) {
        this.plugin = plugin;
        config = plugin.getConfig();
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String s, String[] args) {
        if(!(sender instanceof Player player)){
            sender.sendMessage("Chuj ci w dupe xD");
            return true;
        }

        if(args==null){
            return true;
        }
        if(!sender.hasPermission("homedepot.commands")){
            return true;
        }
        if(args[0].equalsIgnoreCase("wand")){

            ItemStack item = ItemManager.houseCreator;
            if(args.length>1){
                int amount = Integer.parseInt(args[1]);
                item.setAmount(amount);
            }
            player.getInventory().addItem(item);
        }
        if(args[0].equalsIgnoreCase("paper")){
            ItemStack item = ItemManager.paper;
            if((args.length>1)){
                int amount = Integer.parseInt(args[1]);
                item.setAmount(amount);
            }
            player.getInventory().addItem(item);
        }
        if(args[0].equalsIgnoreCase("setWarp")){
            String warpName;
            if(args.length>1){
                if(args[1]==null || args[1].isEmpty()){
                    String message = config.getString("messages.no_warp_name");
                    if (message != null) {
                        player.sendMessage(ChatColor.translateAlternateColorCodes('&', message));
                    }
                    return true;
                }
                warpName = args[1];
                Warp existingWarp = Warp.getByName(warpName);
                if(existingWarp!=null){
                    String message = config.getString("messages.warp_name_taken");
                    if (message != null) {
                        player.sendMessage(ChatColor.translateAlternateColorCodes('&', message));
                    }
                    return true;
                }
                String currentDateTime = java.time.LocalDateTime.now().toString();
                Warp warp = new Warp(warpName, player.getLocation(), currentDateTime);
                String message = config.getString("messages.warp_created");
                if (message != null) {
                    player.sendMessage(ChatColor.translateAlternateColorCodes('&', message));
                }
                warp.save();
            }
        }
        if(args[0].equalsIgnoreCase("delWarp")){
            String warpName;
            if(args.length>1){
                if(args[1]==null || args[1].isEmpty()){
                    String message = config.getString("messages.no_warp_name");
                    if (message != null) {
                        player.sendMessage(ChatColor.translateAlternateColorCodes('&', message));
                    }
                    return true;
                }
                warpName = args[1];
                Warp warp = Warp.getByName(warpName);
                if(warp==null){
                    String message = config.getString("messages.warp_not_found");
                    if (message != null) {
                        player.sendMessage(ChatColor.translateAlternateColorCodes('&', message));
                    }
                    return true;
                }
                String message = config.getString("messages.warp_destroyed");
                if (message != null) {
                    player.sendMessage(ChatColor.translateAlternateColorCodes('&', message));
                }
                warp.remove();
            }else {
                player.sendMessage("Dej nazwe warpa"); //ubrac w config
            }
        }
        if(args[0].equalsIgnoreCase("paperto")){
            String warpName;
            if(args.length>1){
                if(args[1]==null || args[1].isEmpty()){
                    String message = config.getString("messages.no_warp_name");
                    if (message != null) {
                        player.sendMessage(ChatColor.translateAlternateColorCodes('&', message));
                    }
                    return true;
                }
                warpName = args[1];
                Warp warp = Warp.getByName(warpName);
                if(warp==null){
                    String message = config.getString("messages.warp_not_found");
                    if (message != null) {
                        player.sendMessage(ChatColor.translateAlternateColorCodes('&', message));
                    }
                    return true;
                }
                player.getInventory().addItem(ItemManager.createTeleportPaper(warpName, "", 1));
            }
        }

        //if args listwarp lista adminowych warpow czyli szuka warpow bez #

        //if args listallwarp lsita wszystkich warpow

        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        List<String> completions = new ArrayList<>();
        if (args.length == 1) {
            if ("wand".startsWith(args[0].toLowerCase())) {
                completions.add("wand");
            }
            if ("paper".startsWith(args[0].toLowerCase())) {
                completions.add("paper");
                completions.add("paperto");
            }
            if ("paperto".startsWith(args[0].toLowerCase())) {
                completions.add("paperto");
            }
            if ("setwarp".startsWith(args[0].toLowerCase())) {
                completions.add("setwarp");
            }
            if ("delwarp".startsWith(args[0].toLowerCase())) {
                completions.add("delwarp");
            }

        }
        return completions;
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
        Location targetLocation = warp.getLocation();
        player.teleport(targetLocation);
    }
}
