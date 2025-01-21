package dev.kailyn.commands;

import cn.nukkit.Player;
import cn.nukkit.command.Command;
import cn.nukkit.command.CommandSender;
import cn.nukkit.utils.TextFormat;
import dev.kailyn.Prefix;
import dev.kailyn.forms.FormVaultTopBalance;

import java.sql.SQLException;

public class CommandVaultTopBalance extends Command {
    public CommandVaultTopBalance(String name, String description) {
        super(name, description);
    }

    @Override
    public boolean execute(CommandSender sender, String commandLabel, String[] args) {
        if (sender instanceof Player) {
            Player player = (Player) sender;

            FormVaultTopBalance formVaultTopBalance = new FormVaultTopBalance();

            try {
                player.addWindow(formVaultTopBalance.openFormTopVaultBalance(player));
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }

            return true;
        }
        else {
            sender.sendMessage(Prefix.getPrefix() + TextFormat.RED + "Bu komut yanlızca oyun içinde kullanılabilir.");
            return false;
        }
    }

}
