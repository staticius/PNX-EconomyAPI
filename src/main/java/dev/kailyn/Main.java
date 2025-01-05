package dev.kailyn;

import cn.nukkit.plugin.PluginBase;
import cn.nukkit.utils.TextFormat;
import dev.kailyn.commands.CommandMenu;
import dev.kailyn.forms.FormMenu;

public class Main extends PluginBase {

    @Override
    public void onEnable() {
        getLogger().info(TextFormat.GREEN + "+");
        registerCommands();
        registerEvents();
    }

    @Override
    public void onDisable() {
        getLogger().info(TextFormat.RED + "-");
    }

    private void registerCommands() {
        this.getServer().getCommandMap().register("menu", new CommandMenu("menu", "Ekonomi Menüsü"));
    }

    private void registerEvents() {
        this.getServer().getPluginManager().registerEvents(new FormMenu(), this);
    }

}