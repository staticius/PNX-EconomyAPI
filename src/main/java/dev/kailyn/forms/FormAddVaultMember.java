package dev.kailyn.forms;

import cn.nukkit.Player;
import cn.nukkit.form.element.ElementDropdown;
import cn.nukkit.form.window.FormWindowCustom;
import dev.kailyn.Prefix;
import dev.kailyn.database.DatabaseManage;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class FormAddVaultMember {

    private static final int ADD_MEMBER_MENU_ID = 1;

    public static void openAddMemberMenu(Player player) {

        //Aktif oyuncular
        List<String> activePlayers = getOnlinePlayerNames(player);

        //Kasa sahibi ve üye olan oyuncuları filtrele
        List<String> filteredPlayers = filterEligiblePlayers(activePlayers);

        if (filteredPlayers.isEmpty()) {
            player.sendMessage(Prefix.getPrefix() + "Eklenebilecek oyuncu yok.");
            return;
        }

        FormWindowCustom formWindowCustom = new FormWindowCustom("Eklenecek Oyuncular");
        formWindowCustom.addElement(new ElementDropdown("Eklenecek Oyuncu", filteredPlayers));

        player.showFormWindow(formWindowCustom, ADD_MEMBER_MENU_ID);

    }

    private static List<String> getOnlinePlayerNames(Player currentPlayer) {
        List<String> playerNames = new ArrayList<>();
        for (Player player : currentPlayer.getServer().getOnlinePlayers().values()) {
            // Mevcut oyuncuyu kendisiyle eşleşmesin
            if (!player.equals(currentPlayer)) {
                playerNames.add(player.getName());
            }
        }
        return playerNames;
    }

    private static List<String> filterEligiblePlayers(List<String> activePlayers) {
        List<String> eligiblePlayers = new ArrayList<>();
        for (String playerName : activePlayers) {
            try {
                // Oyuncu Vault sahibi veya üyesi değilse listeye ekle
                if (!DatabaseManage.isVaultOwner(playerName) && !DatabaseManage.isVaultMember(playerName)) {
                    eligiblePlayers.add(playerName);
                }
            } catch (SQLException e) {
                System.err.println("Vault kontrolü sırasında hata: " + e.getMessage());
            }
        }
        return eligiblePlayers;
    }

}
