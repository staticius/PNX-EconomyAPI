package dev.astatic.forms;

import cn.nukkit.Player;
import cn.nukkit.form.element.ElementInput;
import cn.nukkit.form.element.ElementLabel;
import cn.nukkit.form.window.FormWindowCustom;
import dev.astatic.database.DatabaseManage;

import java.sql.SQLException;

public class FormWithdrawVault {

    public static final int WITHDRAW_VAULT_ID = 9;
    public static final int WITHDRAW_VAULT_FOR_MEMBER_ID = 12;

    public static void withdrawVaultGeneric(Player player, boolean isMember) {
        FormWindowCustom formWindowCustom = new FormWindowCustom("Para Çek");
        formWindowCustom.addElement(new ElementInput("Çekilecek Miktar", "Miktar"));

        try {
            formWindowCustom.addElement(new ElementLabel("Kasadaki Bakiye:"));
            String owner = isMember
                    ? DatabaseManage.getVaultOwner(player.getName()).orElse(null)
                    : player.getName();

            if (owner != null) {
                formWindowCustom.addElement(new ElementLabel(DatabaseManage.formatNumber(DatabaseManage.getVaultTotalBalance(owner))));
            } else {
                formWindowCustom.addElement(new ElementLabel("Sahip bulunamadı."));
            }

        } catch (SQLException e) {
            player.sendMessage("Bakiye bilgisi alınırken hata oluştu.");
            e.printStackTrace();
        }

        player.showFormWindow(formWindowCustom, isMember ? WITHDRAW_VAULT_FOR_MEMBER_ID : WITHDRAW_VAULT_ID);
    }

    public static void withdrawVault(Player player) {
        withdrawVaultGeneric(player, false); // Kasa sahibi için
    }

    public static void withdrawVaultForMember(Player player) {
        withdrawVaultGeneric(player, true); // Kasa üyesi için
    }
}
