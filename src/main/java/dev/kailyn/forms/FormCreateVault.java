package dev.kailyn.forms;

import cn.nukkit.Player;
import cn.nukkit.form.window.FormWindowSimple;
import dev.kailyn.managers.VaultCreateController;

public class FormCreateVault{

    public static void sendCreateVault(Player player) {

        FormWindowSimple formWindowSimple = new FormWindowSimple("Ortak Kasa Oluştur", "Arkadaşlarınla ortak olduğun bir kasa oluştur");
        VaultCreateController vaultCreateController = new VaultCreateController();



        player.showFormWindow(formWindowSimple);

    }

}
