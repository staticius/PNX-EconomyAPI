package dev.kailyn.listeners;

import cn.nukkit.Player;
import cn.nukkit.event.EventHandler;
import cn.nukkit.event.Listener;
import cn.nukkit.event.player.PlayerFormRespondedEvent;
import cn.nukkit.form.response.FormResponseModal;
import cn.nukkit.utils.TextFormat;
import dev.kailyn.Prefix;
import dev.kailyn.database.DatabaseManage;
import dev.kailyn.forms.FormVaultCreate;

import java.sql.SQLException;

public class ListenerVaultCreate implements Listener {

    @EventHandler
    public void onFormRespond(PlayerFormRespondedEvent event) {

        Player player = event.getPlayer();
        Object response = event.getResponse();

        if (event.getFormID() == FormVaultCreate.CREATE_VAULT_FORM_ID) {

            if (response instanceof FormResponseModal formResponseModal) {
                int responseId = formResponseModal.getClickedButtonId();

                if (responseId == 0) { //Evet seçeneği

                    try {
                        if (DatabaseManage.createVault(player.getName())) {
                            player.sendMessage(Prefix.getPrefix() + TextFormat.DARK_GREEN + "Kasanız başarıyla oluşturuldu, detaylar için tekrar " + TextFormat.GREEN + "/ecomenu" + TextFormat.GREEN + " komutunu kullanın.");
                        }
                    } catch (SQLException e) {
                        throw new RuntimeException(e);
                    }

                }

                else if (responseId == 1) {
                    player.sendMessage(Prefix.getPrefix() + TextFormat.DARK_GREEN + "İyi oyunlar!");
                }
            }

        }


    }
}
