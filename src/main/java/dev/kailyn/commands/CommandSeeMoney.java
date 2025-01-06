package dev.kailyn.commands;

import cn.nukkit.Player;
import cn.nukkit.command.Command;
import cn.nukkit.command.CommandSender;
import dev.kailyn.Prefix;
import dev.kailyn.api.EconomyAPI;

public class CommandSeeMoney extends Command {
    public CommandSeeMoney(String name, String description, String usageMessage) {
        super(name, description, usageMessage);
        this.setPermission("kailyn.admin");
    }

    @Override
    public boolean execute(CommandSender sender, String commandLabel, String[] args) {
        Player player = (Player) sender;
        if (!player.hasPermission("kailyn.admin")) {
            sender.sendMessage(Prefix.getPrefix() + "Bu komutu kullanmak için gerekli yetkiye sahip değilsin.");
            return false;
        }
        Player target = sender.getServer().getPlayer(args[0]);
        if (target == null) {
            sender.sendMessage(Prefix.getPrefix() + "Bu oyuncu aktif değil veya bulunamadı");
            return false;
        }

        double balance = EconomyAPI.getInstance().getBalance(target.getName());
        sender.sendMessage(Prefix.getPrefix() + target.getName() + " adlı oyuncunun bakiyesi: " + balance);
        return true;
    }
}
