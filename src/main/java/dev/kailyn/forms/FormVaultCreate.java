package dev.kailyn.forms;

import cn.nukkit.Player;
import cn.nukkit.form.window.FormWindowModal;
import cn.nukkit.utils.TextFormat;

public class FormVaultCreate {

    public static final int FORM_ID = 8;

    public void open(Player player) {

        FormWindowModal createVaultForm = new FormWindowModal("Kasa Oluştur", "Henüz bir kasanız yok, kasa oluşturmak istermisiniz ?", TextFormat.GREEN + "Evet", TextFormat.RED + "Hayır");
        player.showFormWindow(createVaultForm, FORM_ID);
    }
}
