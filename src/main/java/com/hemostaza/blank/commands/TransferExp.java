package com.hemostaza.blank.commands;

import com.hemostaza.blank.BlankPlugin;
import com.hemostaza.blank.items.ItemManager;
import com.hemostaza.blank.utils.ExperienceUtils;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class TransferExp implements CommandExecutor {

//    private static FileConfiguration config;
//
//    public TransferExp(BlankPlugin plugin) {
//        config = plugin.getConfig();
//    }

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] args) {
        return false;
//        if (!(commandSender instanceof Player player)) {
//            commandSender.sendMessage("Chuj ci w dupe xD");
//            return true;
//        }
//
//
//        if (command.getName().equalsIgnoreCase("transferexp")) {
//
//            ItemStack stackInHand = player.getInventory().getItemInMainHand();
//            //int amount = itemInHand.getAmount();
//            if (stackInHand.getType().equals(Material.GLASS_BOTTLE)) {
//
//                if (args.length == 0 || args == null) {
//                    Exp(player,1);
//                    return true;
//                }
//                if(s!=null){
//                    if(args[0].equalsIgnoreCase("all")){
//                        Exp(player);
//                        return true;
//                    }
//                    int amount;
//                    try {
//                        amount = Integer.parseInt(args[0]);
//                    }
//                    catch(NumberFormatException e) {
//                        player.sendMessage("Invalid Number");
//                        return true;
//                    }
//                    if(amount>0&&amount<=64){
//                        Exp(player,amount);
//                    }else {
//                        player.sendMessage(config.getString("message.wrong_stack_amount"));
//                    }
//                }
//            }else {
//                player.sendMessage(config.getString("messages.not_enough_bottles"));
//            }
//        }
//        return true;
//    }
//
//    public void Exp(Player player){
//        Exp(player,player.getInventory().getItemInMainHand().getAmount());
//    }
//
//    public void Exp(Player player, int amount) {
//
//        int xp = config.getInt("potion.exp");
//        int playerxp = ExperienceUtils.getExp(player);
//
//        ItemStack itemInHand = player.getInventory().getItemInMainHand();
//        ItemStack stackToRemove = itemInHand.clone();
//
//        if(amount>=itemInHand.getAmount()){
//            amount=itemInHand.getAmount();
//        }
//        if (playerxp < amount * xp) {
//            float howmuch = (float) playerxp/xp;
//            amount = (int)howmuch;
//        }
//
//        if(amount==0){
//            player.sendMessage(config.getString("messages.not_enough_exp"));
//            return;
//        }
//
//        int changeXP = amount * xp;
//
//        stackToRemove.setAmount(amount);
//        ItemStack stakcToAdd = ItemManager.expBottle;
//        stakcToAdd.setAmount(amount);
//
//
//        ExperienceUtils.changeExp(player, -changeXP);
//
//        ItemStack addStack = ItemManager.expBottle;
//        addStack.setAmount(amount);
//
//        player.getInventory().addItem(addStack);
//        player.getInventory().removeItem(stackToRemove);
    }
}
