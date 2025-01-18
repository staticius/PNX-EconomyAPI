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
import dev.kailyn.forms.FormDepositVault;

import java.sql.SQLException;
import java.util.Optional;

public class ListenerDepositVault implements Listener {

    @EventHandler
    public void onFormResponded(PlayerFormRespondedEvent event) {

        if (event.getFormID() == FormDepositVault.DEPOSIT_VAULT_FORM_ID) {
            Player player = event.getPlayer();
            Object response = event.getResponse();
            if (response instanceof FormResponseCustom formResponse) {
                String amountInput = formResponse.getInputResponse(0);


                if (amountInput != null && !amountInput.trim().isEmpty()) {

                    try {
                        double yatirilacak = Double.parseDouble(amountInput.trim());

                        if (yatirilacak <= 0) {
                            player.sendMessage(Prefix.getPrefix() + TextFormat.DARK_GREEN + "Yatırılacak para sıfırdan büyük olmalıdır.");
                            return;
                        }

                        // Oyuncunun mevcut bakiyesini kontrol et
                        double mevcutPara = DatabaseManage.getBalance(player.getName()); // Oyuncunun mevcut bakiyesi

                        if (yatirilacak > mevcutPara) {
                            player.sendMessage(Prefix.getPrefix() + TextFormat.DARK_GREEN + "Yeterli bakiyeniz bulunmamaktadır, mevcut bakiyeniz: " + TextFormat.GREEN + DatabaseManage.formatNumber(mevcutPara));
                            return;
                        }

                        boolean kasaSahibiMi = DatabaseManage.vaultExists(player.getName());
                        boolean kasaUyesiMi = DatabaseManage.isPlayerInAnyVault(player.getName());

                        if (kasaSahibiMi) {
                            // Oyuncu kasa sahibi ise
                            double mevcutBakiye = DatabaseManage.getVaultTotalBalance(player.getName());
                            double guncelBakiye = yatirilacak + mevcutBakiye;
                            DatabaseManage.updateVault(player.getName(), guncelBakiye);

                            // Oyuncunun bakiyesini güncelle
                            DatabaseManage.updateBalance(player.getName(), mevcutPara - yatirilacak);

                            player.sendMessage(Prefix.getPrefix() + TextFormat.DARK_GREEN + "Başarıyla " + TextFormat.GREEN + DatabaseManage.formatNumber(yatirilacak) + TextFormat.DARK_GREEN + " Wolf Coin yatırıldı, güncel kasa bakiyesi: " + TextFormat.GREEN + DatabaseManage.formatNumber(guncelBakiye) + TextFormat.DARK_GREEN + " Wolf Coin");
                        } else if (kasaUyesiMi) {
                            // Oyuncu bir kasanın üyesi ise
                            Optional<String> optionalKasaSahibiKim = DatabaseManage.getVaultOwner(player.getName());

                            if (optionalKasaSahibiKim.isPresent()) {
                                String kasaSahibiKim = optionalKasaSahibiKim.get();
                                double mevcutBakiye = DatabaseManage.getVaultTotalBalance(kasaSahibiKim);
                                double guncelBakiye = yatirilacak + mevcutBakiye;
                                DatabaseManage.updateVault(kasaSahibiKim, guncelBakiye);

                                // Oyuncunun bakiyesini güncelle
                                DatabaseManage.updateBalance(player.getName(), mevcutPara - yatirilacak);

                                player.sendMessage(Prefix.getPrefix() + TextFormat.DARK_GREEN + "Başarıyla " + TextFormat.GREEN + DatabaseManage.formatNumber(yatirilacak) + TextFormat.DARK_GREEN + " Wolf Coin yatırıldı, güncel kasa bakiyesi: " + TextFormat.GREEN + DatabaseManage.formatNumber(guncelBakiye) + TextFormat.DARK_GREEN + " Wolf Coin");
                            } else {
                                player.sendMessage(Prefix.getPrefix() + "Kasa sahibi bulunamadı.");
                            }
                        } else {
                            // Oyuncu ne sahibi ne de üyesi ise
                            player.sendMessage(Prefix.getPrefix() + "Kasa sahibi veya üyesi olmadığınız için işlem gerçekleştirilemiyor.");
                        }

                    } catch (NumberFormatException e) {
                        player.sendMessage(Prefix.getPrefix() + TextFormat.DARK_RED + "Geçersiz bir değer girdiniz, lütfen sadece sayısal bir değer giriniz.");
                    } catch (SQLException e) {
                        player.sendMessage(Prefix.getPrefix() + "Bir veritabanı hatası oluştu lütfen daha sonra tekrar deneyin.");
                        Server.getInstance().getLogger().error(e.getMessage());
                    }
                } else {
                    player.sendMessage(Prefix.getPrefix() + TextFormat.DARK_RED + "Bir miktar girmediniz.");
                }
            }

        }

    }

}
