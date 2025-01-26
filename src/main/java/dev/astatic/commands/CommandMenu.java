package dev.astatic.commands;

import cn.nukkit.Player;
import cn.nukkit.command.Command;
import cn.nukkit.command.CommandSender;
import cn.nukkit.inventory.fake.FakeInventory;
import cn.nukkit.utils.TextFormat;
import dev.astatic.Prefix;
import dev.astatic.forms.FormEcoMenu;


public class CommandMenu extends Command {
    public CommandMenu(String name, String description) {
        super(name, description, "/ecomenu");
    }

    @Override
    public boolean execute(CommandSender sender, String commandLabel, String[] args) {
        if (sender instanceof Player) {
            Player player = (Player) sender;
            FakeInventory fakeInventory = FormEcoMenu.menuGUI(player);
            player.addWindow(fakeInventory);
            return true;
        } else {
            sender.sendMessage(Prefix.getPrefix() + TextFormat.RED + "Bu komut yanlızca oyun içinde kullanılabilir.");
        }
        return false;
    }
}
