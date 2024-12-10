package com.hemostaza.blank.commands;

import com.hemostaza.blank.VaultEconomy;
import com.hemostaza.blank.items.ItemManager;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;
import net.milkbowl.vault.economy.Economy;

import java.util.ArrayList;
import java.util.List;

public class BuyCommands implements CommandExecutor, TabCompleter {
    private final JavaPlugin plugin;
    private static FileConfiguration config;
    public BuyCommands(JavaPlugin plugin) {
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

        Economy economy = VaultEconomy.getEconomy();
        if(economy == null){
            Bukkit.getLogger().info("Vault is required for teleportation cost, but it is not installed or enabled.");
        }
        if(args[0].equalsIgnoreCase("howmuch")){
            player.sendMessage("Registerer stick cost: "+ config.getInt("wanditem.cost"));
            player.sendMessage("Blank coupon cost: "+ config.getInt("blankcouponitem.cost"));
        }
        //kupno patyka
        if(args[0].equalsIgnoreCase("registerer")){
            int wandCost = config.getInt("wanditem.cost");
            int amount = 1;
            if((args.length>1)){
                try{
                    amount = Integer.parseInt(args[1]);
                }catch (NumberFormatException e){
                    player.sendMessage(ChatColor.RED + "Wrong number format");
                    return true;
                }
            }
            if(wandCost>0){
                if (economy!=null){
                    int cost = amount*wandCost;
                    if(economy.getBalance(player) < cost) {
                    player.sendMessage(ChatColor.RED + config.getString("messages.not_enough_for_wand"));
                        return true;
                    }else{
                        String message = config.getString("messages.you_payed");
                        if(message!=null){
                            player.sendMessage(message.replace("{cost}",String.valueOf(cost))
                                    .replace("{amount}",String.valueOf(amount))
                                    .replace("{item}","registerer"));
                        }
                        economy.withdrawPlayer(player,cost);
                    }
                }
            }
            ItemStack item = ItemManager.houseCreator;
            item.setAmount(amount);
            player.getInventory().addItem(item);
        }
        if(args[0].equalsIgnoreCase("coupon")){
            int couponCost = config.getInt("blankcouponitem.cost");
            int amount = 1;
            if((args.length>1)){
                try{
                    amount = Integer.parseInt(args[1]);
                }catch (NumberFormatException e){
                    player.sendMessage(ChatColor.RED + "Wrong number format");
                    return true;
                }
            }
            if(amount==0){
                player.sendMessage(ChatColor.RED + "What?");
            }

            if(couponCost>0){
                if (economy!=null){
                    int cost = amount*couponCost;
                    if(economy.getBalance(player) < cost) {
                        player.sendMessage(ChatColor.RED + config.getString("messages.not_enough_for_coupon"));
                        return true;
                    }else{
                        String message = config.getString("messages.you_payed");
                        if(message!=null){
                            player.sendMessage(message.replace("{cost}",String.valueOf(cost))
                                    .replace("{amount}",String.valueOf(amount))
                                    .replace("{item}","coupon"));
                        }
                        economy.withdrawPlayer(player,cost);
                    }
                }
            }
            ItemStack item = ItemManager.paper;
            item.setAmount(amount);
            player.getInventory().addItem(item);
        }
        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender commandSender, Command command, String s, String[] args) {
        List<String> completions = new ArrayList<>();
        if (args.length == 1) {
            if ("registerer".startsWith(args[0].toLowerCase())) {
                completions.add("registerer");
            }
            if ("coupon".startsWith(args[0].toLowerCase())) {
                completions.add("coupon");
            }
            if ("howmuch".startsWith(args[0].toLowerCase())) {
                completions.add("howmuch");
            }

        }
        return completions;
    }
}
