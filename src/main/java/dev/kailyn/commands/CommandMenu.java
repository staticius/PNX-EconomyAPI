package dev.kailyn.commands;

import cn.nukkit.Player;
import cn.nukkit.command.Command;
import cn.nukkit.command.CommandSender;
import cn.nukkit.inventory.fake.FakeInventory;
import dev.kailyn.forms.FormMenu;


public class CommandMenu extends Command {
    public CommandMenu(String name, String description) {
        super(name, description, "/menu");
    }

    @Override
    public boolean execute(CommandSender sender, String commandLabel, String[] args) {
        Player player = (Player) sender;

        FakeInventory fakeInventory = FormMenu.menuGUI(player);
        player.addWindow(fakeInventory);

        return true;
    }
}
