package dev.astatic.listeners;

import cn.nukkit.Player;
import cn.nukkit.event.EventHandler;
import cn.nukkit.event.Listener;
import cn.nukkit.event.player.PlayerFormRespondedEvent;
import cn.nukkit.form.response.FormResponseModal;
import dev.astatic.Prefix;
import dev.astatic.forms.FormDeleteVault;
import dev.astatic.forms.FormEcoMenu;

import java.sql.SQLException;

import static dev.astatic.database.DatabaseManage.*;

public class ListenerDeleteVault implements Listener {

    @EventHandler
    public void onFormRespond(PlayerFormRespondedEvent formRespondedEvent) throws SQLException {
        Player player = formRespondedEvent.getPlayer();
        Object response = formRespondedEvent.getResponse();

        if (formRespondedEvent.getFormID() == FormDeleteVault.DELETE_VAULT_FORM_ID) {
            if (response instanceof FormResponseModal formResponse) {

                int responseId = formResponse.getClickedButtonId();

                if (responseId == 0) { // Evet
                    boolean basarilimi;
                    if (!deleteVault(player.getName())) {
                        basarilimi = false;
                    } else {
                        basarilimi = true;
                    }

                    if (basarilimi) {
                        player.sendMessage(Prefix.getPrefix() + "Kasanız silindi.");
                    } else {
                        player.sendMessage(Prefix.getPrefix() + "Kasanızı silerken bir hata oluştu!");
                    }

                } else if (responseId == 1) { // Geri
                    player.getServer().getScheduler().scheduleDelayedTask(() -> {
                        FormEcoMenu.menuGUI(player);
                    },7);
                }
            }
        }
    }

}
