package dev.astatic.forms;

import cn.nukkit.Player;
import cn.nukkit.form.window.FormWindowModal;
import cn.nukkit.utils.TextFormat;

public class FormDeleteVault {

    public static final int DELETE_VAULT_FORM_ID = 2;

    public static void deleteVault(Player player) {

        FormWindowModal formWindowModal = new FormWindowModal("Kasayı Sil", "Kasanızı silmek istiyormusunuz ?", TextFormat.GREEN + "Evet", TextFormat.RED + "Hayır");

        player.showFormWindow(formWindowModal, DELETE_VAULT_FORM_ID);
    }

}
