package dev.kailyn.forms;

import cn.nukkit.Player;
import cn.nukkit.form.element.ElementInput;
import cn.nukkit.form.element.ElementLabel;
import cn.nukkit.form.window.FormWindowCustom;
import dev.kailyn.database.DatabaseManage;

import java.sql.SQLException;

public class FormDepositVault {

    public static final int DEPOSIT_VAULT_FORM_ID = 3;

    public static void formDepositVault(Player player) {

        FormWindowCustom formWindowCustom = new FormWindowCustom("Para Yatır");

        formWindowCustom.addElement(new ElementInput("Yatırılacak Miktar", "Miktar"));

        formWindowCustom.addElement(new ElementLabel("Mevcut Bakiyen:"));
        try {
            formWindowCustom.addElement(new ElementLabel(String.valueOf(DatabaseManage.getBalance(player.getName()))));
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        player.showFormWindow(formWindowCustom, DEPOSIT_VAULT_FORM_ID);

    }

}
