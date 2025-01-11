package dev.kailyn.commands;

import cn.nukkit.command.Command;
import cn.nukkit.command.CommandSender;

public class CommandEcoAdmin extends Command {
    public CommandEcoAdmin(String name, String description) {
        super(name, description);
        this.setPermission("ecoadmin.admin");
    }

    @Override
    public boolean execute(CommandSender sender, String commandLabel, String[] args) {

        if (sender.hasPermission("ecoadmin.admin")) {


            return true;
        }

        return false;
    }
}
