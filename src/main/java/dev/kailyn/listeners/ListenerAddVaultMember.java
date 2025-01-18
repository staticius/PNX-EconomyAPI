package dev.kailyn.listeners;

import cn.nukkit.Player;
import cn.nukkit.Server;
import cn.nukkit.event.EventHandler;
import cn.nukkit.event.Listener;
import cn.nukkit.event.player.PlayerFormRespondedEvent;
import cn.nukkit.form.response.FormResponseCustom;
import cn.nukkit.form.window.FormWindowCustom;
import cn.nukkit.utils.TextFormat;
import dev.kailyn.Prefix;
import dev.kailyn.database.DatabaseManage;
import dev.kailyn.forms.FormAddVaultMember;

import java.sql.SQLException;

public class ListenerAddVaultMember implements Listener {

    @EventHandler
    public void onFormRespond(PlayerFormRespondedEvent respondedEvent) {
        Player player = respondedEvent.getPlayer();

        if (respondedEvent.getFormID() == FormAddVaultMember.ADD_MEMBER_MENU_ID) {
            if (respondedEvent.getWindow() instanceof FormWindowCustom formWindowCustom) {

                FormResponseCustom formResponse = formWindowCustom.getResponse();
                if (formResponse == null) {
                    return; // Yanıt boş, işlemi sonlandır.
                }

                // Dropdown'dan seçilen oyuncu adını al
                String selectedPlayerName = formResponse.getDropdownResponse(0).getElementContent();
                if (selectedPlayerName == null || selectedPlayerName.isEmpty()) {
                    player.sendMessage(Prefix.getPrefix() + TextFormat.RED + "Seçilen oyuncu geçersiz.");
                    return;
                }

                Player selectedPlayer = Server.getInstance().getPlayer(selectedPlayerName);

                if (selectedPlayer == null) {
                    player.sendMessage(Prefix.getPrefix() + TextFormat.RED + "Seçilen oyuncu şu anda çevrimdışı.");
                    return;
                }

                try {
                    // Seçilen oyuncuyu Vault'a ekle
                    boolean success = DatabaseManage.addMemberToVault(player.getName(), selectedPlayerName);

                    if (success) {
                        // Oyuncu adlarını almak için `getName()` metodu kullanılıyor
                        player.sendMessage(Prefix.getPrefix() + TextFormat.GREEN + selectedPlayer.getName() + TextFormat.DARK_GREEN + " başarıyla kasaya eklendi.");
                        selectedPlayer.sendMessage(Prefix.getPrefix() + TextFormat.GREEN + player.getName() + TextFormat.DARK_GREEN + " sizi kasasına ekledi.");
                    } else {
                        player.sendMessage(Prefix.getPrefix() + TextFormat.RED + "Oyuncu eklenemedi. Oyuncu zaten bir kasaya üye olabilir.");
                    }
                } catch (SQLException e) {
                    player.sendMessage(Prefix.getPrefix() + TextFormat.RED + "Bir hata oluştu. Lütfen daha sonra tekrar deneyin.");
                    e.printStackTrace();
                }
            }
        }
    }
}
