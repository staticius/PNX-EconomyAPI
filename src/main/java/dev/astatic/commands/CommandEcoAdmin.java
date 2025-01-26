package dev.astatic.commands;

import cn.nukkit.Player;
import cn.nukkit.command.Command;
import cn.nukkit.command.CommandSender;
import cn.nukkit.utils.TextFormat;
import dev.astatic.Prefix;
import dev.astatic.forms.FormEcoAdmin;

public class CommandEcoAdmin extends Command {
    public CommandEcoAdmin(String name, String description) {
        super(name, description);
        this.setPermission("ecoadmin.admin");
    }

    @Override
    public boolean execute(CommandSender sender, String commandLabel, String[] args) {

        if (sender instanceof Player) {


            if (sender.hasPermission("ecoadmin.admin")) {

                Player player = (Player) sender;

                FormEcoAdmin formEcoAdmin = new FormEcoAdmin();
                formEcoAdmin.sendFormEcoAdmin(player);

                return true;
            }

        } else {
            sender.sendMessage(Prefix.getPrefix() + TextFormat.RED + "Bu komut yanlızca oyun içinde kullanılabilir.");
        }


        return false;
    }
}
