package dev.kailyn.commands;

import cn.nukkit.Player;
import cn.nukkit.command.Command;
import cn.nukkit.command.CommandSender;
import dev.kailyn.Prefix;
import dev.kailyn.api.EconomyAPI;

public class CommandPay extends Command {

    EconomyAPI economyAPI = EconomyAPI.getInstance();

    public CommandPay(String name, String description) {
        super(name, description);
    }

    @Override
    public boolean execute(CommandSender sender, String commandLabel, String[] args) {

        if (!(sender instanceof Player)) {
            sender.sendMessage(Prefix.getPrefix() + "Bu komut yanlızca oyuncular içindir.");
            return false;
        }

        Player player = (Player) sender;

        if (args.length < 2) {
            sender.sendMessage(Prefix.getPrefix() + "Doğru kullanım: /pay <oyuncu> <miktar>");
            return false;
        }

        Player target = sender.getServer().getPlayer(args[0]);
        if (target == null) {
            sender.sendMessage(Prefix.getPrefix() + "Oyuncu çevrimdışı veya bulunamadı.");
            return false;
        }

        double amount;
        try {
            amount = Double.parseDouble(args[1]);
            if (amount <= 0) {
                sender.sendMessage(Prefix.getPrefix() + "Gönderilecek miktar pozitif bir sayı olmalı.");
                return false;
            }
        } catch (NumberFormatException e) {
            sender.sendMessage(Prefix.getPrefix() + "Miktar geçerli bir sayı olmalı.");
            return false;
        }

            double playerBalance = EconomyAPI.getInstance().getBalance(sender.getName());

            if (playerBalance < amount) {
                player.sendMessage(Prefix.getPrefix() + "Yeterli paran yok, Bakiyen: " + playerBalance);
                return false;
            }

            economyAPI.transferMoney(sender.getName(), target.getName(), amount);
            sender.sendMessage(Prefix.getPrefix() + target.getName() + " adlı oyuncuya " + amount + " " + Prefix.getMoneyUnit() + "gönderdin.");
            target.sendMessage(Prefix.getPrefix() + sender.getName() + " adlı oyuncu sana " + amount + " " + Prefix.getMoneyUnit() + "gönderdi.");

        return true;
    }
}
