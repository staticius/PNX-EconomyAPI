package dev.kailyn.api;

import dev.kailyn.managers.EconomyManager;
import dev.kailyn.managers.VaultManager;
import dev.kailyn.tasks.DatabaseTaskManager;

import java.util.function.Consumer;

public class EconomyAPI {

    private static EconomyAPI instance;
    private final EconomyManager economyManager;
    private final VaultManager vaultManager;

    private EconomyAPI(EconomyManager economyManager, VaultManager vaultManager) {
        this.economyManager = new EconomyManager();
        this.vaultManager = new VaultManager();
    }

    public static void init(EconomyManager economyManager, VaultManager vaultManager) {
        if (instance == null) {
            instance = new EconomyAPI(economyManager, vaultManager);
        }
    }

    public static EconomyAPI getInstance() {
        if (instance == null) {
            throw new IllegalStateException("EconomyAPI başlatılmadı!");
        }
        return instance;
    }

    public boolean transferMoney(String from, String to, double amount) {
        return economyManager.transfer(from, to, amount);
    }

    public void transferMoneyAsync(String from, String to, double amount, Runnable onSuccess, Runnable onFailure) {

        DatabaseTaskManager.submitTask(() -> {
            if (economyManager.transfer(from, to, amount)) {
                onSuccess.run();
            }
            else {
                onFailure.run();
            }
        });
    }

    public double getBalance(String playerName){
        return economyManager.getBalance(playerName);
    }

    public void getBalanceAsync(String playerName, Consumer<Double> callback){
        DatabaseTaskManager.submitTask(() -> {
            double balance = economyManager.getBalance(playerName);
            callback.accept(balance);
        });
    }


    public EconomyManager getEconomyManager() {
        return economyManager;
    }

    public VaultManager getVaultManager() {
        return vaultManager;
    }

}
