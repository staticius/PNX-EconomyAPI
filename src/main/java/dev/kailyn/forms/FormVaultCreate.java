package dev.kailyn.forms;

import cn.nukkit.Player;
import cn.nukkit.form.window.FormWindow;
import cn.nukkit.form.window.FormWindowModal;

public class FormVaultCreate {

    public static final int CREATE_VAULT_FORM_ID = 8;

    public static void open(Player player) {

        FormWindowModal createVaultForm = new FormWindowModal("Kasa Oluştur", "Henüz bir kasanız yok, kasa oluşturmak istermisiniz ?", "Evet", "Hayır");
        player.showFormWindow(createVaultForm, CREATE_VAULT_FORM_ID);
    }
}
