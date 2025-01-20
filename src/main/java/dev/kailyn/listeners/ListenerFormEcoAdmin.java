package dev.kailyn.listeners;

import cn.nukkit.Player;
import cn.nukkit.event.Listener;
import cn.nukkit.event.player.PlayerFormRespondedEvent;
import cn.nukkit.form.response.FormResponseCustom;
import cn.nukkit.form.window.FormWindowCustom;
import cn.nukkit.utils.TextFormat;
import dev.kailyn.Prefix;
import dev.kailyn.database.DatabaseManage;
import dev.kailyn.forms.FormEcoAdmin;

import java.sql.SQLException;

public class ListenerFormEcoAdmin implements Listener {

    public void onFormResponded(PlayerFormRespondedEvent event) {

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
                String amountInput = response.getDropdownResponse(2).getElementContent();

                if (selectedPlayer.equals("Hiçbir oyuncu mevcut değil.")) {
                    player.sendMessage(Prefix.getPrefix() + TextFormat.DARK_GREEN + "Hiçbir oyuncu mevcut değil.");
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
                            DatabaseManage.updateBalance(selectedPlayer, DatabaseManage.getBalance(selectedPlayer) + amount);
                            player.sendMessage(Prefix.getPrefix() + TextFormat.BLUE + selectedPlayer + TextFormat.DARK_BLUE + " adlı oyuncunun bakiyesine " + TextFormat.BLUE + DatabaseManage.formatNumber(amount) + TextFormat.DARK_BLUE + " Wolf Coin eklediniz.");
                            break;
                        case "Bakiye Çıkar":
                            if (amount < DatabaseManage.getBalance(selectedPlayer)) {
                                DatabaseManage.updateBalance(selectedPlayer, DatabaseManage.getBalance(selectedPlayer) - amount);
                                player.sendMessage(Prefix.getPrefix() + TextFormat.BLUE + selectedPlayer + TextFormat.DARK_BLUE + " adlı oyuncunun bakiyesinden " + TextFormat.BLUE + DatabaseManage.formatNumber(amount) + TextFormat.DARK_BLUE + " Wolf Coin çıkardınız.");
                            } else {
                                player.sendMessage(Prefix.getPrefix() + TextFormat.BLUE + selectedPlayer + TextFormat.DARK_BLUE + " adlı oyuncunun bakiyesi çıkarılmak istenen bakiyeden daha az, lütfen ilk önce oyuncunun bakiyesini görüntüleyin.");
                            }
                            break;
                        case "Bakiyeyi Görüntüle":
                            break;
                        case "Kasa Bakiyesi Ekle":
                            break;
                        case "Kasa Bakiyesi Çıkar":
                            break;
                        case "Kasa Bakiyesini Görüntüle":
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
