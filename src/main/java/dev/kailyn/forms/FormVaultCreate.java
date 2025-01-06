package dev.kailyn.forms;

import cn.nukkit.Player;
import cn.nukkit.form.element.ElementDropdown;
import cn.nukkit.form.window.FormWindowCustom;

import java.util.List;

public class FormVaultCreate {

    public void open(Player player, List<String> onlinePlayers) {
        FormWindowCustom createVaultForm = new FormWindowCustom("Kasa Oluştur");
        createVaultForm.addElement(new ElementDropdown("Oyuncuları Seçiniz", onlinePlayers));

        player.showFormWindow(createVaultForm);
    }

}
