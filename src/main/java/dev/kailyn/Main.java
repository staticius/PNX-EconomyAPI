package dev.kailyn;

import cn.nukkit.plugin.PluginBase;
import cn.nukkit.registry.RegisterException;
import cn.nukkit.registry.Registries;
import cn.nukkit.utils.TextFormat;
import dev.kailyn.api.EconomyAPI;
import dev.kailyn.commands.CommandEcoAdmin;
import dev.kailyn.commands.CommandMenu;
import dev.kailyn.database.DatabaseManage;
import dev.kailyn.forms.FormEcoMenu;
import dev.kailyn.forms.FormSendMoney;
import dev.kailyn.forms.FormVaultManage;
import dev.kailyn.forms.FormVaultMemberManage;
import dev.kailyn.items.*;
import dev.kailyn.listeners.*;
import dev.kailyn.managers.EconomyManager;
import dev.kailyn.managers.VaultManager;
import dev.kailyn.tasks.DatabaseTaskManager;

import java.io.File;
import java.io.IOException;
import java.sql.SQLException;

public class Main extends PluginBase {

    EconomyManager economyManager = new EconomyManager();
    VaultManager vaultManager = new VaultManager();
    @Override
    public void onEnable() {
        getLogger().info(TextFormat.GREEN + "+");

        try {
            // Plugin data klasörünü kontrol et ve oluştur
            File dataFolder = getDataFolder();
            if (!dataFolder.exists() && !dataFolder.mkdir()) {
                getLogger().error(TextFormat.RED + "Plugin data klasörü oluşturulamadı!");
                this.getServer().getPluginManager().disablePlugin(this);
                return;
            }

            // Veritabanı dosyasını kontrol et
            File databaseFile = new File(dataFolder, "economy.sqlite");
            if (!databaseFile.exists() && !databaseFile.createNewFile()) {
                getLogger().error(TextFormat.RED + "Veritabanı dosyası oluşturulamadı!");
                this.getServer().getPluginManager().disablePlugin(this);
                return;
            }

            // Veritabanı bağlantısı
            String dbPath = databaseFile.getCanonicalPath();
            getLogger().info("Veritabanı yolu: " + dbPath);
            DatabaseManage.databaseConnect(dbPath);

        } catch (IOException e) {
            getLogger().error("Veritabanı bağlantısında bir sorun oluştu!", e);
            this.getServer().getPluginManager().disablePlugin(this);
            return;
        }

        // API başlatma
        EconomyAPI.init(economyManager, vaultManager);

        // Komut ve olayları kaydetme
        registerCommands();
        registerEvents();
    }


    @Override
    public void onLoad() {
        try {
            Registries.ITEM.registerCustomItem(this, ItemVault.class);
            Registries.ITEM.registerCustomItem(this, ItemSpace.class);
            Registries.ITEM.registerCustomItem(this, ItemSeeMembers.class);
            Registries.ITEM.registerCustomItem(this, ItemAddMember.class);
            Registries.ITEM.registerCustomItem(this, ItemRemoveMember.class);
            Registries.ITEM.registerCustomItem(this, ItemPlayerMoney.class);

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

        this.getServer().getCommandMap().register("ecomenu", new CommandMenu("ecomenu", "Ekonomi Menüsü"));
        this.getServer().getCommandMap().register("ecoadmin", new CommandEcoAdmin("ecoadmin", "Ekonomi admin paneli"));

    }

    private void registerEvents() {

        this.getServer().getPluginManager().registerEvents(new FormEcoMenu(), this);
        this.getServer().getPluginManager().registerEvents(new ListenerVaultCreate(), this);
        this.getServer().getPluginManager().registerEvents(new FormVaultManage(), this);
        this.getServer().getPluginManager().registerEvents(new ListenerAddVaultMember(), this);
        this.getServer().getPluginManager().registerEvents(new ListenerRemoveVaultMember(), this);
        this.getServer().getPluginManager().registerEvents(new ListenerDeleteVault(), this);
        this.getServer().getPluginManager().registerEvents(new ListenerDepositVault(), this);
        this.getServer().getPluginManager().registerEvents(new ListenerWithdrawVault(), this);
        this.getServer().getPluginManager().registerEvents(new ListenerPlayerJoin(), this);
        this.getServer().getPluginManager().registerEvents(new FormSendMoney(), this);
        this.getServer().getPluginManager().registerEvents(new FormVaultMemberManage(), this);
        this.getServer().getPluginManager().registerEvents(new ListenerQuitVault(), this);
        this.getServer().getPluginManager().registerEvents(new ListenerFormEcoAdmin(), this);

    }
}
