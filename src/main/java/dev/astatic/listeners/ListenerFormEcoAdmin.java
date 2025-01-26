package dev.astatic.listeners;

import cn.nukkit.Player;
import cn.nukkit.event.EventHandler;
import cn.nukkit.event.Listener;
import cn.nukkit.event.player.PlayerFormRespondedEvent;
import cn.nukkit.form.response.FormResponseCustom;
import cn.nukkit.form.window.FormWindowCustom;
import cn.nukkit.utils.TextFormat;
import dev.astatic.Prefix;
import dev.astatic.database.DatabaseManage;
import dev.astatic.forms.FormEcoAdmin;

import java.sql.SQLException;
import java.util.Optional;

public class ListenerFormEcoAdmin implements Listener {

    @EventHandler
    public void onFormResponded(PlayerFormRespondedEvent event) throws SQLException {

        // 0 da oyuncu
        // 1 de işlem
        // 2 de miktar

        Player player = event.getPlayer();

        if (event.getWindow() instanceof FormWindowCustom formWindowCustom) {


            if (event.getFormID() == FormEcoAdmin.ECO_ADMIN_ID) {


                FormResponseCustom response = formWindowCustom.getResponse();

                if (response == null) {
                    return;
                }

                String selectedPlayer = response.getDropdownResponse(0).getElementContent();
                String selectedAction = response.getDropdownResponse(1).getElementContent();
                String amountInput = response.getInputResponse(2);

                double vaultBalance = DatabaseManage.getVaultTotalBalance(selectedPlayer);
                String playerVaultRole = DatabaseManage.getPlayerVaultRole(selectedPlayer);
                Optional<String> vaultOwnerOpt = DatabaseManage.getVaultOwner(selectedPlayer);
                String vaultOwner = vaultOwnerOpt.get();


                if (selectedPlayer.equals("Hiçbir oyuncu mevcut değil.")) {
                    player.sendMessage(Prefix.getPrefix() + TextFormat.DARK_AQUA + "Hiçbir oyuncu mevcut değil.");
                    return;
                }

                double amount = 0;

                try {
                    amount = Double.parseDouble(amountInput);
                    if (amount <= 0) {
                        player.sendMessage(Prefix.getPrefix() + TextFormat.RED + "Miktar sıfırdan büyük olmalı.");
                    }

                } catch (NumberFormatException e) {
                    player.sendMessage(Prefix.getPrefix() + TextFormat.RED + "Lütfen sayı girin.");
                }


                try {
                    switch (selectedAction) {
                        case "Bakiye Ekle":

                            if (amount > 0) {

                                DatabaseManage.updateBalance(selectedPlayer, DatabaseManage.getBalance(selectedPlayer) + amount);
                                player.sendMessage(

                                        Prefix.getPrefix() + TextFormat.BLUE + selectedPlayer + TextFormat.DARK_AQUA + " adlı oyuncunun bakiyesine " + TextFormat.BLUE + DatabaseManage.formatNumber(amount) + TextFormat.DARK_AQUA + " Wolf Coin eklediniz.");
                            } else {
                                player.sendMessage(Prefix.getPrefix() + TextFormat.RED + "Miktar sıfırdan büyük olmalı.");
                            }

                            break;
                        case "Bakiye Çıkar":
                            if (amount < DatabaseManage.getBalance(selectedPlayer)) {
                                DatabaseManage.updateBalance(selectedPlayer, DatabaseManage.getBalance(selectedPlayer) - amount);
                                player.sendMessage(

                                        Prefix.getPrefix() + TextFormat.BLUE + selectedPlayer + TextFormat.DARK_AQUA + " adlı oyuncunun bakiyesinden " + TextFormat.BLUE + DatabaseManage.formatNumber(amount) + TextFormat.DARK_AQUA + " Wolf Coin çıkardınız.");
                            } else {
                                player.sendMessage(Prefix.getPrefix() + TextFormat.BLUE + selectedPlayer + TextFormat.DARK_AQUA + " adlı oyuncunun bakiyesi çıkarılmak istenen bakiyeden daha az, lütfen ilk önce oyuncunun bakiyesini görüntüleyin.");
                            }
                            break;
                        case "Bakiyeyi Görüntüle":
                            double balance = DatabaseManage.getBalance(selectedPlayer);
                            player.sendMessage(Prefix.getPrefix() + TextFormat.BLUE + selectedPlayer + TextFormat.DARK_AQUA + " adlı oyuncunun bakiyesi: " + TextFormat.BLUE + balance + TextFormat.DARK_AQUA + " Wolf Coin.");
                            break;
                        case "Kasa Bakiyesi Ekle":
                            break;
                        case "Kasa Bakiyesi Çıkar":

                            break;
                        case "Kasa Bakiyesini Görüntüle":


                            switch (playerVaultRole) {
                                case "none":
                                    player.sendMessage(Prefix.getPrefix() + TextFormat.BLUE + selectedPlayer + TextFormat.DARK_AQUA + " adlı oyuncunun bir kasası yok.");
                                    break;
                                case "owner":
                                    player.sendMessage(Prefix.getPrefix() + TextFormat.BLUE + selectedPlayer + TextFormat.DARK_AQUA + " adlı oyuncunun kendine ait bir kasası var, mevcut kasa bakiyesi: " + TextFormat.BLUE + vaultBalance + TextFormat.DARK_AQUA + " Wolf Coin.");
                                    break;
                                case "member":
                                    player.sendMessage(Prefix.getPrefix() + TextFormat.BLUE + selectedPlayer + TextFormat.DARK_AQUA + " adlı oyuncu, " + TextFormat.BOLD + vaultOwner + TextFormat.DARK_AQUA + " adlı oyuncunun kasasında üye, mevcut kasa bakiyesi: " + TextFormat.BLUE + vaultBalance + TextFormat.DARK_AQUA + " Wolf Coin.");
                                    break;
                            }

                            break;
                        default:
                            player.sendMessage(Prefix.getPrefix() + TextFormat.DARK_BLUE + "Bilinmeyen işlem.");
                    }
                } catch (SQLException e) {
                    player.sendMessage(Prefix.getPrefix() + TextFormat.RED + "Veritabanı hatası oluştu.");
                    e.printStackTrace();
                }

            }
        }


    }

}
