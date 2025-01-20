package dev.kailyn.commands;

import cn.nukkit.Player;
import cn.nukkit.command.Command;
import cn.nukkit.command.CommandSender;
import cn.nukkit.utils.TextFormat;
import dev.kailyn.Prefix;

public class CommandTopBalance extends Command {
    public CommandTopBalance(String name, String description, String usageMessage) {
        super(name, description, usageMessage);
    }

    @Override
    public boolean execute(CommandSender sender, String commandLabel, String[] args) {

        if(sender instanceof Player) {

            Player player = (Player) sender;




        } else {
            sender.sendMessage(Prefix.getPrefix() + TextFormat.RED + "Bu komut yanlızca oyun içinde kullanılabilir.");
        }

        return false;
    }
}
