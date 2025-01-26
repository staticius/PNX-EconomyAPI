package dev.astatic;

import cn.nukkit.plugin.PluginBase;
import cn.nukkit.registry.RegisterException;
import cn.nukkit.registry.Registries;
import cn.nukkit.utils.TextFormat;
import dev.astatic.api.EconomyAPI;
import dev.astatic.commands.CommandEcoAdmin;
import dev.astatic.commands.CommandMenu;
import dev.astatic.commands.CommandTopBalance;
import dev.astatic.commands.CommandVaultTopBalance;
import dev.astatic.database.DatabaseManage;
import dev.astatic.forms.FormEcoMenu;
import dev.astatic.forms.FormSendMoney;
import dev.astatic.forms.FormVaultManage;
import dev.astatic.forms.FormVaultMemberManage;
import dev.astatic.items.*;
import dev.astatic.listeners.*;
import dev.astatic.managers.EconomyManager;
import dev.astatic.managers.VaultManager;
import dev.astatic.tasks.DatabaseTaskManager;

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
            Registries.ITEM.registerCustomItem(this, ItemWithdrawMoney.class);
            Registries.ITEM.registerCustomItem(this, ItemDepositMoney.class);
            Registries.ITEM.registerCustomItem(this, ItemSeeMoney.class);
            Registries.ITEM.registerCustomItem(this, ItemSendMoney.class);
            Registries.ITEM.registerCustomItem(this, ItemTac.class);
            Registries.ITEM.registerCustomItem(this, ItemBaltopOne.class);
            Registries.ITEM.registerCustomItem(this, ItemBaltopTwo.class);
            Registries.ITEM.registerCustomItem(this, ItemBaltopThree.class);
            Registries.ITEM.registerCustomItem(this, ItemBaltopOther.class);
            Registries.ITEM.registerCustomItem(this, ItemVaultTopMoney.class);

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
        this.getServer().getCommandMap().register("topbalance", new CommandTopBalance("topbalance", "En zengin 10 oyuncuyu görüntüle.", "/topbalance"));
        this.getServer().getCommandMap().register("topvbalance", new CommandVaultTopBalance("topvbalance", "En çok bakiyeye sahip 10 kasayı görüntüle"));
    }

    private void registerEvents() {

        this.getServer().getPluginManager().registerEvents(new ListenerFormEcoAdmin(), this);
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

    }
}
