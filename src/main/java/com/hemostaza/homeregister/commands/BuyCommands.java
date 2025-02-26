package com.hemostaza.homeregister.commands;

import com.hemostaza.homeregister.VaultEconomy;
import com.hemostaza.homeregister.items.ItemManager;
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
import java.util.Objects;

public class BuyCommands implements CommandExecutor, TabCompleter {
    private final JavaPlugin plugin;
    private static FileConfiguration config;

    public BuyCommands(JavaPlugin plugin) {
        this.plugin = plugin;
        config = plugin.getConfig();
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String s, String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage("Only players can use this command");
            return true;
        }
        if (args == null) {
            return true;
        }

        Economy economy = VaultEconomy.getEconomy();
        String registererName = config.getString("registerer.name",
                "Registerer");
        String ticketName = config.getString("blankticket.name",
                "Blank ticket");

        if (args[0].equalsIgnoreCase("howmuch")) {
            if (economy != null) {
                String itemCost = config.getString("messages.item_cost",
                        "{item} cost {cost}");
                String registererCost = config.getString("registerer.cost",
                        "null value");
                String ticketCost = config.getString("blankticket.cost",
                        "null value");

                player.sendMessage(ChatColor.GOLD + itemCost.replace("{item}", registererName).replace("{cost}", registererCost));
                player.sendMessage(ChatColor.GOLD + itemCost.replace("{item}", ticketName).replace("{cost}", ticketCost));

            } else {
                String economyFalse = config.getString("messages.economy_false", "Economy not enabled on server. There is no cost.");
                player.sendMessage(ChatColor.GOLD + economyFalse);
            }

        }
        if (!config.getBoolean("canbuy")) {
            String noBuying = config.getString("messages.buying_disabled",
                    "Buying is disabled on server.");
            player.sendMessage(ChatColor.RED+ noBuying);
            return true;
        }
        //kupno patyka
        if (args[0].equalsIgnoreCase("registerer")) {
            int wandCost = config.getInt("registerer.cost");
            int amount = 1;
            if ((args.length > 1)) {
                try {
                    amount = Integer.parseInt(args[1]);
                    if (amount <= 0) amount = 1;
                } catch (NumberFormatException e) {
                    player.sendMessage(ChatColor.RED + "Wrong number format");
                    return true;
                }
            }
            if (wandCost > 0) {
                if (economy != null) {
                    int cost = amount * wandCost;
                    if (economy.getBalance(player) < cost) {
                        String message = ChatColor.RED + config.getString("messages.not_enough_for_item",
                                "You don't have enough money to buy {item}.");
                        player.sendMessage(message.replace("{item}", registererName));
                        return true;
                    } else {
                        String message = ChatColor.GREEN + config.getString("messages.you_paid",
                                "You paid {cost} for {amount} {item}");
                        player.sendMessage(message.replace("{cost}", String.valueOf(cost))
                                .replace("{amount}", String.valueOf(amount))
                                .replace("{item}", registererName));
                        economy.withdrawPlayer(player, cost);
                    }
                }
            }
            ItemStack item = ItemManager.houseCreator;
            item.setAmount(amount);
            player.getInventory().addItem(item);
        }
        if (args[0].equalsIgnoreCase("ticket")) {
            int couponCost = config.getInt("blankticket.cost");
            int amount = 1;
            if ((args.length > 1)) {
                try {
                    amount = Integer.parseInt(args[1]);
                    if (amount <= 0) amount = 1;
                } catch (NumberFormatException e) {
                    player.sendMessage(ChatColor.RED + "Wrong number format");
                    return true;
                }
            }

            if (couponCost > 0) {
                if (economy != null) {
                    int cost = amount * couponCost;
                    if (economy.getBalance(player) < cost) {
                        String message = ChatColor.RED + config.getString("messages.not_enough_for_item",
                                "You don't have enough money to buy {item}.");
                        player.sendMessage(message.replace("{item}", ticketName));
                        return true;
                    } else {
                        String message = ChatColor.GREEN + config.getString("messages.you_paid",
                                "You paid {cost} for {amount} {item}");
                        player.sendMessage(message.replace("{cost}", String.valueOf(cost))
                                .replace("{amount}", String.valueOf(amount))
                                .replace("{item}", ticketName));
                        economy.withdrawPlayer(player, cost);
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
            if ("ticket".startsWith(args[0].toLowerCase())) {
                completions.add("ticket");
            }
            if ("howmuch".startsWith(args[0].toLowerCase())) {
                completions.add("howmuch");
            }

        }
        return completions;
    }
}
