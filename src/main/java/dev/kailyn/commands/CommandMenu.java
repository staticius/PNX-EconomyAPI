package dev.kailyn.commands;

import cn.nukkit.Player;
import cn.nukkit.command.Command;
import cn.nukkit.command.CommandSender;
import cn.nukkit.inventory.fake.FakeInventory;
import dev.kailyn.Prefix;
import dev.kailyn.forms.FormEcoMenu;


public class CommandMenu extends Command {
    public CommandMenu(String name, String description) {
        super(name, description, "/ecomenu");
    }

    @Override
    public boolean execute(CommandSender sender, String commandLabel, String[] args) {
        Player player = (Player) sender;

        if (sender instanceof Player) {
            FakeInventory fakeInventory = FormEcoMenu.menuGUI(player);
            player.addWindow(fakeInventory);
            return true;
        } else {
            sender.sendMessage(Prefix.getPrefix() + "Bu komut yanlızca oyun içinde kullanılabilir.");
            return false;
        }
    }
}
