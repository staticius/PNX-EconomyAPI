package dev.kailyn.api;

import dev.kailyn.Prefix;
import dev.kailyn.database.DatabaseManage;
import dev.kailyn.managers.EconomyManager;
import dev.kailyn.managers.VaultManager;
import dev.kailyn.tasks.DatabaseTaskManager;

import java.sql.SQLException;
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

    /***
     *
     * @param from Transfer EDECEK oyuncu
     * @param to Transfer ALACAK oyuncu
     * @param amount Miktar
     * @return Transfer işlemi
     */

    public boolean transferMoney(String from, String to, double amount) {
        return economyManager.transfer(from, to, amount);
    }

    /*** Bakiyeyi async olarak transfer et. (Sunucunun main threadine girmeden ayrı threadde işlem yapar daha temizdir)
     *
     * @param from Transfer EDECEK oyuncu
     * @param to Transfer ALACAK oyuncu
     * @param amount Miktar
     * @param onSuccess Başarı durumunda
     * @param onFailure Hata durumunda
     */

    public void transferMoneyAsync(String from, String to, double amount, Runnable onSuccess, Runnable onFailure) {

        DatabaseTaskManager.submitTask(() -> {
            if (economyManager.transfer(from, to, amount)) {
                onSuccess.run();
            } else {
                onFailure.run();
            }
        });
    }

    /***
     *
     * @param playerName Oyuncunun adı ( player.getName(); )
     * @return Bakiyeyi al
     */

    public double getBalance(String playerName) {
        return economyManager.getBalance(playerName);
    }

    /*** Bakiyeyi async olarak al.(Sunucunun main thread ine girmeden ayrı threadde işlem yapar daha temizdir)
     *
     * @param playerName Oyuncu adı
     * @param callback Geri dönüş
     */

    public void getBalanceAsync(String playerName, Consumer<Double> callback) {
        DatabaseTaskManager.submitTask(() -> {
            double balance = economyManager.getBalance(playerName);
            callback.accept(balance);
        });
    }

    public void setBalance(String playerName, double amount) throws SQLException {
        DatabaseManage.updateBalance(playerName, amount);
    }


    public EconomyManager getEconomyManager() {
        return economyManager;
    }

    public VaultManager getVaultManager() {
        return vaultManager;
    }

    public String getMoneyUnit(){
        return Prefix.getMoneyUnit();
    }

    public String getPrefix(){
        return Prefix.getPrefix();
    }

}
