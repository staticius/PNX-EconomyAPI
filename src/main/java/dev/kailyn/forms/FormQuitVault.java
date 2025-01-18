package dev.kailyn.forms;

import cn.nukkit.Player;
import cn.nukkit.form.window.FormWindowModal;


public class FormQuitVault {

    public static final int FORM_QUİT_VAULT_ID = 11;

    public static void openQuitVaultForm(Player player) {

        FormWindowModal formWindowModal = new FormWindowModal("Kasadan Ayrıl", "Kasadan ayrılmak istiyormusunuz ?", "Evet", "Hayır");

        player.showFormWindow(formWindowModal, FORM_QUİT_VAULT_ID);

    }
}
