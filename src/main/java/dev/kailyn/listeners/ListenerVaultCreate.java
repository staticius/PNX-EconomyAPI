package dev.kailyn.listeners;

import cn.nukkit.Player;
import cn.nukkit.event.EventHandler;
import cn.nukkit.event.Listener;
import cn.nukkit.event.player.PlayerFormRespondedEvent;
import cn.nukkit.form.response.FormResponseModal;
import dev.kailyn.Prefix;
import dev.kailyn.database.DatabaseManage;
import dev.kailyn.forms.FormVaultCreate;

import java.sql.SQLException;

public class ListenerVaultCreate implements Listener {

    @EventHandler
    public void handleResponse(PlayerFormRespondedEvent event) throws SQLException {


        if (event.getFormID() == FormVaultCreate.FORM_ID) {
            Player player = event.getPlayer();
            Object response = event.getResponse();

            if (response instanceof FormResponseModal formResponse) {
                int responseId = formResponse.getClickedButtonId();

                if (responseId == 0) { //Evet Seçeneği
                    if (!DatabaseManage.vaultExists(player.getName())) {
                        DatabaseManage.createVault(player.getName());
                        player.sendMessage(Prefix.getPrefix() + "Başarıyla kasa oluşturdunuz, kasanıza ortak oyuncu eklemek için tekrar /ecomenu komutunu kullanabilirsiniz.");
                    }
                } else if (responseId == 1) { // Hayır seçeneği
                    player.sendMessage(Prefix.getPrefix() + "İyi oyunlar!");
                }
            }
        }

    }

}
