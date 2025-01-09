package dev.kailyn;

import cn.nukkit.plugin.PluginBase;
import cn.nukkit.registry.RegisterException;
import cn.nukkit.registry.Registries;
import cn.nukkit.utils.TextFormat;
import dev.kailyn.api.EconomyAPI;
import dev.kailyn.commands.CommandMenu;
import dev.kailyn.commands.CommandSeeMoney;
import dev.kailyn.database.DatabaseManage;
import dev.kailyn.forms.FormMenu;
import dev.kailyn.items.ItemSpace;
import dev.kailyn.items.ItemVault;
import dev.kailyn.listeners.ListenerCreateVault;
import dev.kailyn.managers.EconomyManager;
import dev.kailyn.managers.VaultManager;
import dev.kailyn.tasks.DatabaseTaskManager;

import java.sql.SQLException;

public class Main extends PluginBase {

    EconomyManager economyManager = new EconomyManager();
    VaultManager vaultManager = new VaultManager();

    @Override
    public void onEnable() {
        getLogger().info(TextFormat.GREEN + "+");


        try {
            DatabaseManage.databaseConnect(getDataFolder().getAbsolutePath() + "/economy.db");
        } catch (SQLException e) {
            getLogger().error("Veritabanı bağlantısında bir sorun oluştu!", e.getCause());
            this.getServer().getPluginManager().disablePlugin(this);
            return;
        }
        EconomyAPI.init(economyManager, vaultManager);

        registerCommands();
        registerEvents();
    }

    @Override
    public void onLoad() {
        try {
            Registries.ITEM.registerCustomItem(this, ItemVault.class);
            Registries.ITEM.registerCustomItem(this, ItemSpace.class);
        } catch (RegisterException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void onDisable() {
        getLogger().info(TextFormat.RED + "-");
        try {
            DatabaseManage.closeConnection();
        } catch (SQLException e) {
            getLogger().error("Veritabanı bağlantısı kapatılırken bir sorun oluştu!", e.getCause());
        }

        DatabaseTaskManager.shutdown();
    }

    private void registerCommands() {
        this.getServer().getCommandMap().register("menu", new CommandMenu("menu", "Ekonomi Menüsü"));
        this.getServer().getCommandMap().register("seemoney", new CommandSeeMoney("seemoney", "Oyuncuların parasını görüntüle", "/seemoney <oyuncu>"));
    }

    private void registerEvents() {
        this.getServer().getPluginManager().registerEvents(new FormMenu(), this);
        this.getServer().getPluginManager().registerEvents(new ListenerCreateVault(), this);
    }

}