package dev.kailyn.forms;

import cn.nukkit.Player;
import cn.nukkit.form.element.ElementButton;
import cn.nukkit.form.element.ElementDropdown;
import cn.nukkit.form.window.FormWindowCustom;
import cn.nukkit.form.window.FormWindowSimple;
import cn.nukkit.utils.TextFormat;

import java.util.List;

public class FormVaultCreate {

    public static final int FORM_ID = 11;

    public void open(Player player) {

        FormWindowSimple createVaultForm = new FormWindowSimple("Kasa Oluştur", "Kasa oluşturmak istiyormusunuz ?");
        createVaultForm.addButton(new ElementButton(TextFormat.GREEN + "Evet"));
        createVaultForm.addButton(new ElementButton(TextFormat.RED + "Hayır"));

        player.showFormWindow(createVaultForm, FORM_ID);
    }
}
