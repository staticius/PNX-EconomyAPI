package dev.kailyn.listeners;

import cn.nukkit.Player;
import cn.nukkit.event.EventHandler;
import cn.nukkit.event.Listener;
import cn.nukkit.event.player.PlayerFormRespondedEvent;
import cn.nukkit.form.response.FormResponseSimple;
import dev.kailyn.Prefix;
import dev.kailyn.api.EconomyAPI;
import dev.kailyn.forms.FormVaultCreate;
import org.json.JSONArray;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class ListenerCreateVault implements Listener {

    @EventHandler
    public void handleResponse(PlayerFormRespondedEvent event) throws SQLException {

        List<String> deneme = new ArrayList<>();
        deneme.add("Staticius");
        deneme.add("HardSiamang655");

        String members = new JSONArray(deneme).toString();

        if (event.getFormID() == FormVaultCreate.FORM_ID) {
            Player player = event.getPlayer();
            Object response = event.getResponse();

            if (response instanceof FormResponseSimple) {
                FormResponseSimple formResponse = (FormResponseSimple) response;
                int responseId = formResponse.getClickedButtonId();

                try{
                    if (responseId == 0) { //Evet Seçeneği
                        EconomyAPI.getInstance().getVaultManager().createVault(player.getName(), deneme);
                    } else if (responseId == 1) { // Hayır seçeneği
                        player.sendMessage("İptal edildi.");
                    }
                } catch (SQLException e) {
                    player.sendMessage(Prefix.getPrefix() + "Kasa oluşturulurken bir hata oluştu.");
                    e.printStackTrace();
                }
            }
        }

    }

}
