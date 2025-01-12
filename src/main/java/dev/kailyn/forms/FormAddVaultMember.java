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

    /**
     * Oyuncu için "Eklenecek Oyuncular" menüsünü açar.
     *
     * @param player Oyuncu
     */
    public static void openAddMemberMenu(Player player) {
        try {
            // Aktif oyuncuları al
            List<String> activePlayers = getOnlinePlayerNames(player);

            // Uygun oyuncuları filtrele
            List<String> filteredPlayers = getEligiblePlayers(activePlayers);

            // Uygun oyuncu yoksa bilgilendir
            if (filteredPlayers.isEmpty()) {
                player.sendMessage(Prefix.getPrefix() + "Eklenebilecek oyuncu yok.");
                return;
            }

            // Formu oluştur ve oyuncuya göster
            FormWindowCustom formWindowCustom = new FormWindowCustom("Eklenecek Oyuncular");
            formWindowCustom.addElement(new ElementDropdown("Eklenecek Oyuncu", filteredPlayers));
            player.showFormWindow(formWindowCustom, ADD_MEMBER_MENU_ID);

        } catch (SQLException e) {
            player.sendMessage(Prefix.getPrefix() + "Oyuncu listesi alınırken bir hata oluştu.");
            e.printStackTrace();
        }
    }

    /**
     * Oyuncunun çevrimiçi oyuncular listesini alır (kendisi hariç).
     *
     * @param currentPlayer Oyuncu
     * @return Çevrimiçi oyuncular listesi
     */
    private static List<String> getOnlinePlayerNames(Player currentPlayer) {
        List<String> playerNames = new ArrayList<>();
        for (Player player : currentPlayer.getServer().getOnlinePlayers().values()) {
            if (!player.equals(currentPlayer)) { // Kendisi hariç
                playerNames.add(player.getName());
            }
        }
        return playerNames;
    }

    /**
     * Kasa sahibi veya üyesi olmayan oyuncuları filtreler.
     *
     * @param activePlayers Çevrimiçi oyuncular listesi
     * @return Filtrelenmiş oyuncular listesi
     * @throws SQLException Veritabanı hatası oluşursa
     */
    private static List<String> getEligiblePlayers(List<String> activePlayers) throws SQLException {
        // Aktif oyuncuları geçici listeye kopyala
        List<String> eligiblePlayers = new ArrayList<>(activePlayers);

        // Kasa sahiplerini ve üyelerini alın
        List<String> vaultOwnersAndMembers = DatabaseManage.getAllVaultOwnersAndMembers();

        // Kasa sahiplerini ve üyelerini uygun oyuncular listesinden çıkar
        eligiblePlayers.removeAll(vaultOwnersAndMembers);

        return eligiblePlayers;
    }
}
