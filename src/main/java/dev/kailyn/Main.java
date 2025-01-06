package dev.kailyn;

import cn.nukkit.plugin.PluginBase;
import cn.nukkit.utils.TextFormat;
import dev.kailyn.api.EconomyAPI;
import dev.kailyn.commands.CommandMenu;
import dev.kailyn.database.DatabaseConnect;
import dev.kailyn.forms.FormMenu;
import dev.kailyn.tasks.DatabaseTaskManager;

import java.sql.SQLException;

public class Main extends PluginBase {

    @Override
    public void onEnable() {
        getLogger().info(TextFormat.GREEN + "+");


        try {
            DatabaseConnect.databaseConnect(getDataFolder().getAbsolutePath() + "/economy.db");
        } catch (SQLException e) {
            getLogger().error("Veritabanı bağlantısında bir sorun oluştu!", e.getCause());
            this.getServer().getPluginManager().disablePlugin(this);
            return;
        }

        EconomyAPI.init();

        registerCommands();
        registerEvents();
    }

    @Override
    public void onDisable() {
        getLogger().info(TextFormat.RED + "-");
        try {
            DatabaseConnect.closeConnection();
        } catch (SQLException e) {
            getLogger().error("Veritabanı bağlantısı kapatılırken bir sorun oluştu!", e.getCause());
        }

        DatabaseTaskManager.shutdown();
    }

    private void registerCommands() {
        this.getServer().getCommandMap().register("menu", new CommandMenu("menu", "Ekonomi Menüsü"));
    }

    private void registerEvents() {
        this.getServer().getPluginManager().registerEvents(new FormMenu(), this);
    }

}