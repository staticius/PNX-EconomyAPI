package dev.kailyn.listeners;

import cn.nukkit.Player;
import cn.nukkit.Server;
import cn.nukkit.event.EventHandler;
import cn.nukkit.event.Listener;
import cn.nukkit.event.player.PlayerFormRespondedEvent;
import cn.nukkit.form.response.FormResponseCustom;
import dev.kailyn.Prefix;
import dev.kailyn.database.DatabaseManage;
import dev.kailyn.forms.FormWithdrawVault;

import java.sql.SQLException;
import java.util.Optional;

public class ListenerWithdrawVault implements Listener {

    @EventHandler
    public void onFormResponded(PlayerFormRespondedEvent event) {
        if (event.getFormID() == FormWithdrawVault.WITHDRAW_VAULT_ID) {

            Player player = event.getPlayer();
            Object response = event.getResponse();

            if (response instanceof FormResponseCustom formResponse) {
                String amountInput = formResponse.getInputResponse(0);

                if (amountInput != null && !amountInput.trim().isEmpty()) {
                    try {
                        double cekilecekMiktar = Double.parseDouble(amountInput.trim());

                        if (cekilecekMiktar <= 0) {
                            player.sendMessage(Prefix.getPrefix() + "Çekilecek miktar sıfırdan büyük olmalı.");
                            return;
                        }

                        // Kasa bakiyesi kontrolü
                        double kasaBakiyesi = DatabaseManage.getVaultTotalBalance(player.getName());

                        if (cekilecekMiktar > kasaBakiyesi) {
                            player.sendMessage(Prefix.getPrefix() + "Kasada yeterli bakiye yok. Mevcut bakiye: " + DatabaseManage.formatNumber(kasaBakiyesi));
                            return;
                        }

                        boolean kasaSahibiMi = DatabaseManage.vaultExists(player.getName());
                        boolean kasaUyesiMi = DatabaseManage.isPlayerInAnyVault(player.getName());

                        if (kasaSahibiMi) {
                            // Oyuncu kasa sahibi ise
                            double yeniKasaBakiyesi = kasaBakiyesi - cekilecekMiktar;
                            DatabaseManage.updateVault(player.getName(), yeniKasaBakiyesi);

                            // Oyuncunun bakiyesine ekle
                            double mevcutOyuncuBakiyesi = DatabaseManage.getBalance(player.getName());
                            double yeniOyuncuBakiyesi = mevcutOyuncuBakiyesi + cekilecekMiktar;
                            DatabaseManage.updateBalance(player.getName(), yeniOyuncuBakiyesi);

                            player.sendMessage(Prefix.getPrefix() + "Başarıyla " + DatabaseManage.formatNumber(cekilecekMiktar) + " Wolf Coin çekildi. Güncel kasa bakiyesi: " + DatabaseManage.formatNumber(yeniKasaBakiyesi) + ", Senin güncel bakiyen: " + DatabaseManage.formatNumber(yeniOyuncuBakiyesi));

                        } else if (kasaUyesiMi) {
                            // Oyuncu bir kasanın üyesi ise
                            Optional<String> optionalKasaSahibiKim = DatabaseManage.getVaultOwner(player.getName());

                            if (optionalKasaSahibiKim.isPresent()) {
                                String kasaSahibiKim = optionalKasaSahibiKim.get();
                                double kasaSahibiBakiyesi = DatabaseManage.getVaultTotalBalance(kasaSahibiKim);

                                if (cekilecekMiktar > kasaSahibiBakiyesi) {
                                    player.sendMessage(Prefix.getPrefix() + "Kasada yeterli bakiye yok. Mevcut bakiye: " + DatabaseManage.formatNumber(kasaSahibiBakiyesi));
                                    return;
                                }

                                double yeniKasaBakiyesi = kasaSahibiBakiyesi - cekilecekMiktar;
                                DatabaseManage.updateVault(kasaSahibiKim, yeniKasaBakiyesi);

                                // Oyuncunun bakiyesine ekle
                                double mevcutOyuncuBakiyesi = DatabaseManage.getBalance(player.getName());
                                double yeniOyuncuBakiyesi = mevcutOyuncuBakiyesi + cekilecekMiktar;
                                DatabaseManage.updateBalance(player.getName(), yeniOyuncuBakiyesi);

                                player.sendMessage(Prefix.getPrefix() + "Başarıyla " + DatabaseManage.formatNumber(cekilecekMiktar) + " Wolf Coin çekildi. Güncel kasa bakiyesi: " + DatabaseManage.formatNumber(yeniKasaBakiyesi) + ", Senin güncel bakiyen: " + DatabaseManage.formatNumber(yeniOyuncuBakiyesi));

                            } else {
                                player.sendMessage(Prefix.getPrefix() + "Kasa sahibi bulunamadı.");
                            }

                        } else {
                            // Oyuncu ne sahibi ne de üyesi ise
                            player.sendMessage(Prefix.getPrefix() + "Kasa sahibi veya üyesi olmadığınız için işlem gerçekleştirilemiyor.");
                        }

                    } catch (NumberFormatException e) {
                        player.sendMessage(Prefix.getPrefix() + "Geçersiz bir değer girdiniz. Lütfen sadece sayısal bir değer giriniz.");
                    } catch (SQLException e) {
                        player.sendMessage(Prefix.getPrefix() + "Bir veritabanı hatası oluştu. Lütfen daha sonra tekrar deneyin.");
                        Server.getInstance().getLogger().error(e.getMessage());
                    }
                } else {
                    player.sendMessage(Prefix.getPrefix() + "Bir miktar girmediniz.");
                }
            }
        }
    }
}
