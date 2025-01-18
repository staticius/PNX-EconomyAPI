package dev.kailyn.forms;

import cn.nukkit.Player;
import cn.nukkit.form.element.ElementDropdown;
import cn.nukkit.form.element.ElementInput;
import cn.nukkit.form.window.FormWindowCustom;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class FormEcoAdmin {

    private static List<String> getOnlinePlayerNames(Player currentPlayer) {
        return getStrings(currentPlayer);
    }

    @NotNull
    static List<String> getStrings(Player currentPlayer) {
        List<String> playerNames = new ArrayList<>();
        for (Player player : currentPlayer.getServer().getOnlinePlayers().values()) {
            if (!player.equals(currentPlayer)) {
                playerNames.add(player.getName());
            }
        }
        if (playerNames.isEmpty()) {
            playerNames.add("Hiçbir oyuncu mevcut değil.");
        }
        return playerNames;
    }

    public void sendFormEcoAdmin(Player player) {
        FormWindowCustom formWindowCustom = new FormWindowCustom("Economy Admin Menüsü");

        formWindowCustom.addElement(new ElementDropdown("Oyuncu", getOnlinePlayerNames(player)));

        formWindowCustom.addElement(new ElementDropdown("İşlem", getOption()));

        formWindowCustom.addElement(new ElementInput("Miktar", "Eklenecek/Çıkarılacak Miktar"));

        player.showFormWindow(formWindowCustom);

    }

    private List<String> getOption() {
        List<String> options = new ArrayList<>();
        options.add("Bakiye Ekle");
        options.add("Bakiye Çıkar");
        options.add("Bakiyeyi Görüntüle");
        options.add("Kasa Bakiyesi Ekle");
        options.add("Kasa Bakiyesi Çıkar");
        options.add("Kasa Bakiyesini Görüntüle");
        return options;
    }


}
