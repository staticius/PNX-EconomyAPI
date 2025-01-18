package dev.kailyn.listeners;

import cn.nukkit.Player;
import cn.nukkit.Server;
import cn.nukkit.event.EventHandler;
import cn.nukkit.event.Listener;
import cn.nukkit.event.player.PlayerFormRespondedEvent;
import cn.nukkit.form.response.FormResponseModal;
import cn.nukkit.form.window.FormWindowModal;
import cn.nukkit.form.window.FormWindowSimple;
import cn.nukkit.utils.TextFormat;
import dev.kailyn.Prefix;
import dev.kailyn.database.DatabaseManage;
import dev.kailyn.forms.FormRemoveVaultMember;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

public class ListenerRemoveVaultMember implements Listener {

    private final Map<Player, String> selectedMembers = new HashMap<>();

    @EventHandler
    public void onFormRespond(PlayerFormRespondedEvent event) {
        Player player = event.getPlayer();
        Object response = event.getResponse();

        // Oyuncu seçme formu
        if (event.getFormID() == FormRemoveVaultMember.REMOVE_MEMBER_FORM_ID) {
            if (event.getWindow() instanceof FormWindowSimple formWindowSimple) {
                String selectedMember = formWindowSimple.getResponse().getClickedButton().getText();

                // Seçilen oyuncuyu kaydet
                selectedMembers.put(player, selectedMember);

                // Onay formunu aç
                FormRemoveVaultMember.openConfirmRemoveForm(player, selectedMember);
            }
        }

        // Onay formu
        if (event.getFormID() == FormRemoveVaultMember.CONFIRM_REMOVE_FORM_ID) {
            if (response instanceof FormResponseModal formResponse) {
                int responseId = formResponse.getClickedButtonId();

                if (responseId == 0) { // Evet
                    // Seçilen oyuncuyu al ve kaldır
                    String selectedMember = selectedMembers.get(player);
                    Player oyuncu = Server.getInstance().getPlayer(selectedMember);

                    try {
                        if (DatabaseManage.removeMemberFromVault(player.getName(), selectedMember)) {
                            player.sendMessage(Prefix.getPrefix() + TextFormat.GREEN + selectedMember + TextFormat.DARK_GREEN + " adlı oyuncu başarıyla kasadan çıkarıldı.");
                            oyuncu.sendMessage(Prefix.getPrefix() + TextFormat.GREEN + player.getName() + TextFormat.DARK_GREEN + " adlı oyuncu sizi kasasından çıkardı.");
                        } else {
                            player.sendMessage("Kasadan oyuncuyu çıkarırken bir hata oluştu.");
                        }
                    } catch (SQLException e) {
                        player.sendMessage("Veritabanı hatası oluştu.");
                        e.printStackTrace();
                    }

                    // İşlem tamamlandığından seçimi temizle
                    selectedMembers.remove(player);

                } else if (responseId == 1) { // Geri
                    // Oyuncu seçme formunu tekrar aç
                    FormRemoveVaultMember.openRemoveVaultMemberForm(player);
                }
            }
        }
    }
}
