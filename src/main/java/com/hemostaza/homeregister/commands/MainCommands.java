package com.hemostaza.homeregister.commands;

import com.hemostaza.homeregister.MainPlugin;
import com.hemostaza.homeregister.Warp;
import com.hemostaza.homeregister.items.ItemManager;
import org.bukkit.*;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class MainCommands implements CommandExecutor, TabCompleter {

    private final MainPlugin plugin;
    private static FileConfiguration config;

    public MainCommands(MainPlugin plugin) {
        this.plugin = plugin;
        config = plugin.getConfig();
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String s, String[] args) {
        if (args[0].equalsIgnoreCase("give")) {
            return CommandGive(sender, args);
        }
        if (args[0].equalsIgnoreCase("sethome")) {
            return WarpCommand(sender, args, true);
        }
        if (args[0].equalsIgnoreCase("delhome")) {
            return WarpCommand(sender, args, false);
        }
        return true;
    }

    private boolean WarpCommand(CommandSender sender, String[] args, boolean createWarp) {

        String usage = "/homeregistry sethome/delhome <warpname>";

        if (!(sender instanceof Player player)) {
            sender.sendMessage("Only players can use this command");
            return true;
        }
        String warpName;
        if (args.length > 1) {
            if (args[1] == null || args[1].isEmpty()) {
                String message = config.getString("messages.no_home_name",
                        "No warp name set!\nPlease specify the warp name.");
                player.sendMessage(ChatColor.RED + message);
                return true;
            }
            warpName = args[1];
            Warp existingWarp = Warp.getByName(warpName);
            if (createWarp) {
                if (existingWarp != null) {
                    String message = config.getString("messages.home_name_taken",
                            "A home with the same name already exists!");
                    player.sendMessage(ChatColor.RED + message);
                    return true;
                }
                String currentDateTime = java.time.LocalDateTime.now().toString();
                Warp warp = new Warp(warpName, player.getLocation(), currentDateTime);
                String message = config.getString("messages.home_created",
                        "Home successfully registered.");
                player.sendMessage(ChatColor.GREEN + message);
                warp.save();
            } else {
                if (existingWarp == null) {
                    String message = config.getString("messages.home_not_found",
                            "Specified home does not exist!");
                    player.sendMessage(ChatColor.RED + message);
                    return true;
                }
                String message = config.getString("messages.home_destroyed",
                        "Home destroyed.");
                player.sendMessage(ChatColor.GOLD + message);
                existingWarp.remove();
            }
        } else {
            player.sendMessage(usage); //ubrac w config
        }
        return true;
    }

    private boolean CommandGive(CommandSender sender, String[] args) {
        //Subcommand GIVE
        String usage = "/homeregistry give <player> <item> <?amount?> <?home?>";
        Player target;
        int amount;
        String warp;

        if (args.length < 3) {
            sender.sendMessage(usage);
            return true;
        }
        try {
            amount = Integer.parseInt(args[3]);
            if (amount <= 0 || amount > 64) {
                sender.sendMessage(usage);
                String wrongAmount = config.getString("messages:wrong_stack_amount",
                        "Wrong stack amount value");
                sender.sendMessage(ChatColor.RED + wrongAmount);
                return true;
            }
        } catch (IndexOutOfBoundsException e) {
            amount = 1;
        } catch (NumberFormatException e) {
            sender.sendMessage(usage);
            return true;
        }

        try {
            warp = args[4];
            Warp warpName = Warp.getByName(warp);
            if (warpName == null) {
                String message =config.getString("messages.home_not_found",
                        "Specified home does not exist!");
                sender.sendMessage(ChatColor.RED + message);
                return true;
            }
        } catch (IndexOutOfBoundsException e) {
            warp = "";
        }
        String playerName = args[1];
        target = sender.getServer().getPlayerExact(playerName);
        if (target == null) {
            sender.sendMessage("Player " + playerName + " is not online.");
            return true;
        }
        sender.sendMessage(GiveItem(target, args[2], amount, warp));
        return true;
    }


    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        List<String> completions = new ArrayList<>();
        List<String> players = new ArrayList<>();
        for (Player p : Bukkit.getOnlinePlayers()) {
            players.add(p.getName());
        }
        if (args.length == 1) {
            if ("give".startsWith(args[0].toLowerCase())) {
                completions.add("give");
            }
            if ("sethome".startsWith(args[0].toLowerCase())) {
                completions.add("sethome");
            }
            if ("delhome".startsWith(args[0].toLowerCase())) {
                completions.add("delhome");
            }
        }
        if (args.length == 2 && args[0].equalsIgnoreCase("give")) {
            return players;
        }
        if (args.length == 3 && args[0].equalsIgnoreCase("give")) {
            if ("registerer".startsWith(args[2].toLowerCase())) {
                completions.add("registerer");
            }
            if ("ticket".startsWith(args[2].toLowerCase())) {
                completions.add("ticket");
            }
        }
        return completions;
    }

    private String GiveItem(Player player, String item, int amount, String warpTo) {
        ItemStack itemStack = null;
        switch (item) {
            case "registerer":
                itemStack = ItemManager.houseCreator;
                break;
            case "ticket":
                if (warpTo.isEmpty()) {
                    itemStack = ItemManager.paper;
                } else {
                    itemStack = plugin.getItemManager().createTeleportPaper(warpTo, 1);
                }
                break;
            default:
                return ChatColor.RED + "Item should be registerer or ticket";
        }
        itemStack.setAmount(amount);
        player.getInventory().addItem(itemStack);
        String message = ChatColor.GREEN + config.getString("messages.item_received",
                "{player} received {amount} {item}.");
        return message.replace("{player}", player.getName()).
                replace("{amount}", String.valueOf(amount)).
                replace("{item}", itemStack.getItemMeta().getDisplayName());
    }
}
