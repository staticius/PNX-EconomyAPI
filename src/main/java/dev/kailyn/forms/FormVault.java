package dev.kailyn.forms;

import cn.nukkit.Player;
import cn.nukkit.form.window.FormWindowSimple;
import dev.kailyn.api.EconomyAPI;
import dev.kailyn.managers.VaultManager;

import java.sql.SQLException;
import java.util.List;

public class FormVault {

    public void sendVaultForm(Player player) throws SQLException {

        List<String> members = EconomyAPI.getInstance().getVaultManager().getVaultMembers(player.getName());

        if (members.isEmpty()) {
            FormWindowSimple createVaultWindow = new FormWindowSimple("Kasa Oluştur", "Henüz bir kasanız yok. Yeni bir kasa oluşturabilirsiniz.");
            player.showFormWindow(createVaultWindow);
        } else {
            StringBuilder details = new StringBuilder("Kasanızdaki Oyuncular:\n");
            for (String member : members) {
                details.append("- ").append(member).append("\n");
            }

            FormWindowSimple vaultDetails = new FormWindowSimple("Ortak Kasa", details.toString());
            player.showFormWindow(vaultDetails);
        }
    }

}
