package dev.kailyn.listeners;

import cn.nukkit.Player;
import cn.nukkit.Server;
import cn.nukkit.event.EventHandler;
import cn.nukkit.event.Listener;
import cn.nukkit.event.player.PlayerFormRespondedEvent;
import cn.nukkit.form.response.FormResponseModal;
import cn.nukkit.utils.TextFormat;
import dev.kailyn.Prefix;
import dev.kailyn.database.DatabaseManage;
import dev.kailyn.forms.FormQuitVault;

import java.sql.SQLException;

public class ListenerQuitVault implements Listener {

    @EventHandler
    public void onFormRespond(PlayerFormRespondedEvent event) {
        Player player = event.getPlayer();

        // Form ID kontrolü

        if (event.getFormID() == FormQuitVault.FORM_QUİT_VAULT_ID) {
            Object response = event.getResponse();

            if (response instanceof FormResponseModal formResponseModal) {
                int clickedButtonId = formResponseModal.getClickedButtonId();

                if (clickedButtonId == 0) { // "Evet" seçeneği
                    try {
                        // Oyuncunun bulunduğu kasanın sahibini al
                        String ownerName = DatabaseManage.getVaultOwner(player.getName()).orElseThrow(() -> new IllegalStateException("Kasa sahibi bulunamadı."));

                        // Kasadan oyuncuyu çıkar
                        boolean success = DatabaseManage.removeMemberFromVault(ownerName, player.getName());

                        if (success) {
                            player.sendMessage(Prefix.getPrefix() + TextFormat.DARK_GREEN + "Başarıyla kasadan ayrıldınız.");

                            // Kasa sahibine bildirim gönder
                            Player owner = Server.getInstance().getPlayer(ownerName); // Kasa sahibi çevrimiçi mi?
                            if (owner != null) {
                                owner.sendMessage(Prefix.getPrefix() + TextFormat.GREEN + player.getName() + TextFormat.DARK_GREEN + " adlı oyuncu kasanızdan ayrıldı.");
                            }
                        } else {
                            player.sendMessage(Prefix.getPrefix() + "Kasadan ayrılırken bir hata oluştu.");
                        }

                    } catch (SQLException e) {
                        player.sendMessage(Prefix.getPrefix() + "Kasadan ayrılırken bir veritabanı hatası oluştu.");
                        e.printStackTrace();
                    } catch (IllegalStateException e) {
                        player.sendMessage(Prefix.getPrefix() + e.getMessage());
                    }
                } else if (clickedButtonId == 1) { // "Hayır" seçeneği
                    player.sendMessage(Prefix.getPrefix() + TextFormat.RED + "Kasadan ayrılma işlemi iptal edildi.");
                }
            }
        }
    }
}
