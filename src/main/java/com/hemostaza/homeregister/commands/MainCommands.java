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

public class MainCommands implements CommandExecutor, TabCompleter {

    private final MainPlugin plugin;
    private static FileConfiguration config;

    public MainCommands(MainPlugin plugin) {
        this.plugin = plugin;
        config = plugin.getConfig();
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String s, String[] args) {
        //Subcommand GIVE
        //TODO: Odjebać to w nowe miejsce i dojebać reszte komend.
        if (args[0].equalsIgnoreCase("give")) {
            return CommandGive(sender, args);
        }
        if (args[0].equalsIgnoreCase("setWarp")) {
            return WarpCommand(sender, args, true);
        }
        if (args[0].equalsIgnoreCase("delWarp")) {
            return WarpCommand(sender, args, false);
        }
        return true;
    }

    private boolean WarpCommand(CommandSender sender, String[] args, boolean createWarp) {

        String usage = "/homedepot setWarp/delWarp <warpname>";

        if (!(sender instanceof Player player)) {
            sender.sendMessage("Only players can use this command");
            return true;
        }
        String warpName;
        if (args.length > 1) {
            if (args[1] == null || args[1].isEmpty()) {
                String message = config.getString("messages.no_warp_name");
                if (message != null) {
                    player.sendMessage(ChatColor.translateAlternateColorCodes('&', message));
                }
                return true;
            }
            warpName = args[1];
            Warp existingWarp = Warp.getByName(warpName);
            if (createWarp) {
                if (existingWarp != null) {
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
            } else {
                if (existingWarp == null) {
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
                existingWarp.remove();
            }
        } else {
            player.sendMessage(usage); //ubrac w config
        }
        return true;
    }

    private boolean CommandGive(CommandSender sender, String[] args) {
        //Subcommand GIVE
        String usage = "/homedepot give <player> <item> <?amount?> <?warpto?>";
        Player target;
        String item;
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
                String wrongAmount = config.getString("messages:wrong_stack_amount");
                if (wrongAmount != null) {
                    sender.sendMessage(wrongAmount);
                }
                return true;
            }
        } catch (IndexOutOfBoundsException e) {
            amount = 1;
        } catch (NumberFormatException e) {
            sender.sendMessage(usage);
            sender.sendMessage("Wrong amount value.");
            return true;
        }

        try {
            warp = args[4];
            Warp warpName = Warp.getByName(warp);
            if (warpName == null) {
                String message = config.getString("messages.warp_not_found");
                if (message != null) {
                    sender.sendMessage(ChatColor.translateAlternateColorCodes('&', message));
                }
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
        item = args[2];
        sender.sendMessage(GiveItem(target, item, amount, warp));
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
            if ("setwarp".startsWith(args[0].toLowerCase())) {
                completions.add("setwarp");
            }
            if ("delwarp".startsWith(args[0].toLowerCase())) {
                completions.add("delwarp");
            }
        }
        if (args.length == 2 && args[0].equalsIgnoreCase("give")) {
            return players;
        }
        if (args.length == 3 && args[0].equalsIgnoreCase("give")) {
            if ("registerer".startsWith(args[2].toLowerCase())) {
                completions.add("registerer");
            }
            if ("coupon".startsWith(args[2].toLowerCase())) {
                completions.add("coupon");
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
            case "coupon":
                if (warpTo.isEmpty()) {
                    itemStack = ItemManager.paper;
                } else {
                    itemStack = plugin.getItemManager().createTeleportPaper(warpTo, 1);
                }
                break;
            default:
                return "Item shoudl be registerer or coupon";
        }
        itemStack.setAmount(amount);
        player.getInventory().addItem(itemStack);
        return "Player " + player.getName() + " received " + itemStack.getItemMeta().getDisplayName();
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
