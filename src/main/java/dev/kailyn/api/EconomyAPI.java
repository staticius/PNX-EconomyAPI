package dev.kailyn.api;

import dev.kailyn.managers.EconomyManager;
import dev.kailyn.managers.VaultManager;

import java.util.List;

public class EconomyAPI {

    private static EconomyAPI instance;
    private final EconomyManager economyManager;
    private final VaultManager vaultManager;

    private EconomyAPI() {
        this.economyManager = new EconomyManager();
        this.vaultManager = new VaultManager();
    }

    public static void init() {
        if (instance == null) {
            instance = new EconomyAPI();
        }
    }

    public static EconomyAPI getInstance() {
        return instance;
    }

    public EconomyManager getEconomyManager() {
        return economyManager;
    }

    public VaultManager getVaultManager() {
        return vaultManager;
    }

}
