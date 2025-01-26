package dev.astatic.forms;

import cn.nukkit.Player;
import cn.nukkit.form.window.FormWindowSimple;
import dev.astatic.api.EconomyAPI;
import dev.astatic.database.DatabaseManage;

import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

public class FormSeeVaultMembers {

    public static final int SEE_VAULT_MEMBERS_ID = 6;
    public static final int SEE_VAULT_MEMBERS_FOR_MEMBER_ID = 10;

    public void sendVaultFormDetailsForMember(Player player) throws SQLException {
        // 1. Oyuncunun üye olduğu kasanın sahibini bul
        Optional<String> ownerOptional = DatabaseManage.getVaultOwner(player.getName());

        if (ownerOptional.isEmpty()) {
            return;
        }

        String ownerName = ownerOptional.get();

        // 2. Sahibin kasasındaki üyeleri al
        List<String> members = EconomyAPI.getInstance().getVaultManager().getVaultMembers(ownerName);

        // 3. Detayları birleştir
        StringBuilder details = new StringBuilder();
        details.append("Kasa Sahibi: ").append(ownerName).append("\n\n");
        details.append("Kasa Üyeleri:\n");

        for (String member : members) {
            details.append("- ").append(member).append("\n");
        }

        // 4. Formu oluştur ve oyuncuya göster
        FormWindowSimple vaultDetails = new FormWindowSimple("Kasadaki Oyuncular", details.toString());
        player.showFormWindow(vaultDetails, SEE_VAULT_MEMBERS_ID);
    }

    public void sendVaultFormDetails(Player player) throws SQLException {

        List<String> members = EconomyAPI.getInstance().getVaultManager().getVaultMembers(player.getName());

        StringBuilder details = new StringBuilder("Kasanızdaki Oyuncular:\n");
        for (String member : members) {
            details.append("- ").append(member).append("\n");
        }

        FormWindowSimple vaultDetails = new FormWindowSimple("Kasadaki Oyuncular", details.toString());
        player.showFormWindow(vaultDetails, SEE_VAULT_MEMBERS_FOR_MEMBER_ID);
    }

    }
