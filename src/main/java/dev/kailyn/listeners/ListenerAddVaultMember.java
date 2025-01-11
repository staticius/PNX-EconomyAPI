package dev.kailyn.listeners;

import cn.nukkit.Player;
import cn.nukkit.event.EventHandler;
import cn.nukkit.event.Listener;
import cn.nukkit.event.player.PlayerFormRespondedEvent;
import cn.nukkit.form.window.FormWindowCustom;
import dev.kailyn.Prefix;
import dev.kailyn.database.DatabaseManage;

import java.sql.SQLException;

public class ListenerAddVaultMember implements Listener {

    @EventHandler
    public void onFormRespond(PlayerFormRespondedEvent respondedEvent) {
        Player player = respondedEvent.getPlayer();

        if (respondedEvent.wasClosed()) {
            player.sendMessage(Prefix.getPrefix() + "İşlem iptal edildi. İyi oyunlar!");
            return;
        }

        // Form ID kontrolü (33 olarak belirttik)
        if (respondedEvent.getFormID() == 33) {
            if (respondedEvent.getWindow() instanceof FormWindowCustom formWindowCustom) {
                // Dropdown'dan seçilen oyuncu adını al
                String selectedPlayer = formWindowCustom.getResponse()
                        .getDropdownResponse(0) // İlk dropdown'ı al
                        .getElementContent();   // Seçilen oyuncunun adını al

                try {
                    // Seçilen oyuncuyu Vault'a ekle
                    boolean success = DatabaseManage.addMemberToVault(player.getName(), selectedPlayer);

                    if (success) {
                        player.sendMessage(Prefix.getPrefix() + selectedPlayer + " başarıyla kasaya eklendi!");
                    } else {
                        player.sendMessage(Prefix.getPrefix() + "Oyuncu eklenemedi. Zaten üye olabilir.");
                    }
                } catch (SQLException e) {
                    player.sendMessage(Prefix.getPrefix() + "Bir hata oluştu.");
                    e.printStackTrace();
                }
            }
        }
    }
}
