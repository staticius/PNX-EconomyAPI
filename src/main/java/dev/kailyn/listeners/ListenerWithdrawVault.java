package dev.kailyn.listeners;

import cn.nukkit.Player;
import cn.nukkit.Server;
import cn.nukkit.event.EventHandler;
import cn.nukkit.event.Listener;
import cn.nukkit.event.player.PlayerFormRespondedEvent;
import cn.nukkit.form.response.FormResponseCustom;
import cn.nukkit.utils.TextFormat;
import dev.kailyn.Prefix;
import dev.kailyn.database.DatabaseManage;
import dev.kailyn.forms.FormWithdrawVault;

import java.sql.SQLException;
import java.util.Optional;

public class ListenerWithdrawVault implements Listener {

    @EventHandler
    public void onFormResponded(PlayerFormRespondedEvent event) {
        if (event.getFormID() == FormWithdrawVault.WITHDRAW_VAULT_ID || event.getFormID() == FormWithdrawVault.WITHDRAW_VAULT_FOR_MEMBER_ID) {

            Player player = event.getPlayer();
            Object response = event.getResponse();

            if (response instanceof FormResponseCustom formResponse) {
                String amountInput = formResponse.getInputResponse(0);

                if (amountInput != null && !amountInput.trim().isEmpty()) {
                    try {
                        double cekilecekMiktar = Double.parseDouble(amountInput.trim());

                        if (cekilecekMiktar <= 0) {
                            player.sendMessage(Prefix.getPrefix() + TextFormat.DARK_GREEN + "Çekilecek miktar sıfırdan büyük olmalı.");
                            return;
                        }

                        Optional<String> optOwnr = DatabaseManage.getVaultOwner(player.getName());

                        // Kasa bakiyesi kontrolü
                        double kasaBakiyesi = DatabaseManage.getVaultTotalBalance(player.getName());

                        boolean kasaSahibiMi = DatabaseManage.vaultExists(player.getName());
                        boolean kasaUyesiMi = DatabaseManage.isPlayerInAnyVault(player.getName());

                        if (kasaSahibiMi) {

                            if (cekilecekMiktar > kasaBakiyesi) {
                                player.sendMessage(Prefix.getPrefix() + TextFormat.DARK_RED + "Kasada yeterli bakiye yok, mevcut kasa bakiyesi: " + TextFormat.RED + DatabaseManage.formatNumber(kasaBakiyesi));
                                return;
                            }

                            // Oyuncu kasa sahibi ise
                            double yeniKasaBakiyesi = kasaBakiyesi - cekilecekMiktar;
                            DatabaseManage.updateVault(player.getName(), yeniKasaBakiyesi);

                            // Oyuncunun bakiyesine ekle
                            double mevcutOyuncuBakiyesi = DatabaseManage.getBalance(player.getName());
                            double yeniOyuncuBakiyesi = mevcutOyuncuBakiyesi + cekilecekMiktar;
                            DatabaseManage.updateBalance(player.getName(), yeniOyuncuBakiyesi);

                            player.sendMessage(Prefix.getPrefix() + TextFormat.DARK_GREEN + "Başarıyla " + TextFormat.GREEN + DatabaseManage.formatNumber(cekilecekMiktar) + TextFormat.DARK_GREEN + " Wolf Coin çekildi, güncel kasa bakiyesi: " + TextFormat.GREEN + DatabaseManage.formatNumber(yeniKasaBakiyesi));

                        }

                        if (kasaUyesiMi) {
                            // Oyuncu bir kasanın üyesi ise
                            Optional<String> optionalKasaSahibiKim = DatabaseManage.getVaultOwner(player.getName());

                            if (optionalKasaSahibiKim.isPresent()) {
                                String kasaSahibiKim = optionalKasaSahibiKim.get();
                                double kasaSahibiBakiyesi = DatabaseManage.getVaultTotalBalance(kasaSahibiKim);

                                if (cekilecekMiktar > kasaSahibiBakiyesi) {
                                    player.sendMessage(Prefix.getPrefix() + TextFormat.DARK_RED + "Kasada yeterli bakiye yok, mevcut kasa bakiyesi: " + TextFormat.RED + DatabaseManage.formatNumber(kasaSahibiBakiyesi));
                                    return;
                                }

                                double yeniKasaBakiyesi = kasaSahibiBakiyesi - cekilecekMiktar;
                                DatabaseManage.updateVault(kasaSahibiKim, yeniKasaBakiyesi);

                                // Oyuncunun bakiyesine ekle
                                double mevcutOyuncuBakiyesi = DatabaseManage.getBalance(player.getName());
                                double yeniOyuncuBakiyesi = mevcutOyuncuBakiyesi + cekilecekMiktar;
                                DatabaseManage.updateBalance(player.getName(), yeniOyuncuBakiyesi);

                                player.sendMessage(Prefix.getPrefix() + TextFormat.DARK_GREEN + "Başarıyla " + TextFormat.GREEN + DatabaseManage.formatNumber(cekilecekMiktar) + TextFormat.DARK_GREEN + " Wolf Coin çekildi, güncel kasa bakiyesi: " + TextFormat.GREEN + DatabaseManage.formatNumber(yeniKasaBakiyesi));

                            } else {
                                player.sendMessage(Prefix.getPrefix() + "Kasa sahibi bulunamadı.");
                            }

                        }

                    } catch (NumberFormatException e) {
                        player.sendMessage(Prefix.getPrefix() + TextFormat.DARK_RED + "Geçersiz bir değer girdiniz, lütfen sadece sayısal bir değer giriniz.");
                    } catch (SQLException e) {
                        player.sendMessage(Prefix.getPrefix() + "Bir veritabanı hatası oluştu. Lütfen daha sonra tekrar deneyin.");
                        Server.getInstance().getLogger().error(e.getMessage());
                    }
                } else {
                    player.sendMessage(Prefix.getPrefix() + TextFormat.DARK_RED + "Bir miktar girmediniz.");
                }
            }
        }
    }
}
