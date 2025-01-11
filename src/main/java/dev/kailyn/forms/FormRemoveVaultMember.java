package dev.kailyn.forms;

import cn.nukkit.Player;
import cn.nukkit.form.element.ElementButton;
import cn.nukkit.form.window.FormWindowModal;
import cn.nukkit.form.window.FormWindowSimple;
import dev.kailyn.Prefix;
import dev.kailyn.database.DatabaseManage;
import org.json.JSONArray;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class FormRemoveVaultMember {

    public static final int REMOVE_MEMBER_FORM_ID = 4; // Oyuncu seçme formu ID'si
    public static final int CONFIRM_REMOVE_FORM_ID = 5; // Onay formu ID'si

    /**
     * Oyuncu çıkarma formunu açar.
     */
    public static void openRemoveVaultMemberForm(Player player) {
        try {
            // Vault üyelerini al
            List<String> members = getVaultMembersAsList(player.getName());

            if (members.isEmpty()) {
                player.sendMessage(Prefix.getPrefix() + "Kasada çıkarılacak üye bulunmuyor!");
                return;
            }

            FormWindowSimple formWindowSimple = new FormWindowSimple("Oyuncu Çıkar", "Kasadan çıkarmak istediğiniz oyuncuyu seçiniz.");
            for (String member : members) {
                formWindowSimple.addButton(new ElementButton(member)); // Her bir üye için bir buton ekle
            }

            player.showFormWindow(formWindowSimple, REMOVE_MEMBER_FORM_ID);
        } catch (SQLException e) {
            player.sendMessage("Üyeleri alırken bir hata oluştu.");
            e.printStackTrace();
        }
    }

    /**
     * Vault üyelerini JSON'dan bir List<String> olarak döndürür.
     *
     * @param ownerName Vault sahibinin adı
     * @return Vault üyeleri
     * @throws SQLException Veritabanı hatası oluşursa
     */
    private static List<String> getVaultMembersAsList(String ownerName) throws SQLException {
        return DatabaseManage.getVaultMembers(ownerName)
                .map(json -> {
                    List<String> members = new ArrayList<>();
                    JSONArray jsonArray = new JSONArray(json);
                    for (int i = 0; i < jsonArray.length(); i++) {
                        members.add(jsonArray.getString(i));
                    }
                    return members;
                })
                .orElse(new ArrayList<>()); // Eğer Optional boşsa boş bir liste döndür
    }

    /**
     * Oyuncu çıkarma işlemi için onay formu açar.
     */
    public static void openConfirmRemoveForm(Player player, String selectedMember) {
        FormWindowModal confirmForm = new FormWindowModal(
                "Oyuncu Çıkar",
                selectedMember + " adlı oyuncuyu gerçekten kasadan çıkarmak istiyor musunuz?",
                "Evet",
                "Geri"
        );

        player.showFormWindow(confirmForm, CONFIRM_REMOVE_FORM_ID);
    }
}
