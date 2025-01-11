package dev.kailyn.forms;

import cn.nukkit.Player;
import cn.nukkit.form.element.ElementInput;
import cn.nukkit.form.element.ElementLabel;
import cn.nukkit.form.window.FormWindowCustom;
import dev.kailyn.database.DatabaseManage;

import java.sql.SQLException;

public class FormWithdrawVault {

    public static final int WITHDRAW_VAULT_ID = 9;

    public static void withdrawVault (Player player) {

        FormWindowCustom formWindowCustom = new FormWindowCustom("Para Çek");


        formWindowCustom.addElement(new ElementInput("Çekilecek Miktar", "Miktar"));


        try {
            formWindowCustom.addElement(new ElementLabel("Kasadaki Bakiye:"));
            formWindowCustom.addElement(new ElementLabel(String.valueOf(DatabaseManage.formatNumber(DatabaseManage.getVaultTotalBalance(player.getName())))));
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        player.showFormWindow(formWindowCustom, WITHDRAW_VAULT_ID);

    }

}
