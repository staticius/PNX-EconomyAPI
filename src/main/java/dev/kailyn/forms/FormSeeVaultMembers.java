package dev.kailyn.forms;

import cn.nukkit.Player;
import cn.nukkit.form.window.FormWindowSimple;
import dev.kailyn.api.EconomyAPI;

import java.sql.SQLException;
import java.util.List;

public class FormSeeVaultMembers {

    public static final int SEE_VAULT_MEMBERS_ID = 6;

    public void sendVaultFormDetails(Player player) throws SQLException {

        List<String> members = EconomyAPI.getInstance().getVaultManager().getVaultMembers(player.getName());

        StringBuilder details = new StringBuilder("Kasanızdaki Oyuncular:\n");
        for (String member : members) {
            details.append("- ").append(member).append("\n");
        }

        FormWindowSimple vaultDetails = new FormWindowSimple("Kasadaki Oyuncular", details.toString());
        player.showFormWindow(vaultDetails);
    }
}