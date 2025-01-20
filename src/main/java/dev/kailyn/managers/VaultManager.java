package dev.kailyn.managers;

import dev.kailyn.database.DatabaseManage;
import org.json.JSONArray;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import static dev.kailyn.database.DatabaseManage.getConnection;

public class VaultManager {


    /**
     * Bir ortak kasadaki üyeleri döndürür.
     *
     * @param owner Kasa sahibi
     * @return Üyelerin listesi
     * @throws SQLException Eğer veritabanı hatası olursa
     */

    public List<String> getVaultMembers(String owner) throws SQLException {
        List<String> members = new ArrayList<>();
        String sql = "SELECT members FROM Vaults WHERE ownerName = ?";

        try (PreparedStatement preparedStatement = getConnection().prepareStatement(sql)) {
            preparedStatement.setString(1, owner);
            ResultSet resultSet = preparedStatement.executeQuery();

            if (resultSet.next()) {
                String jsonMembers = resultSet.getString("members");
                JSONArray jsonArray = new JSONArray(jsonMembers);

                // JSON Array'den listeye dönüştür
                for (int i = 0; i < jsonArray.length(); i++) {
                    members.add(jsonArray.getString(i));
                }
            }
        }
        return members;
    }

    /*** Kasa oluştur
     *
     * @param ownerName Kasa sahibi
     * @return Kasa oluşturulursa true döner oluşturulmazsa false döner
     * @throws SQLException Veri tabanı hatalarına karşı
     */
    
    public boolean createVault(String ownerName) throws SQLException {
        return DatabaseManage.createVault(ownerName);
    }

    /**
     * Ortak kasanın toplam bakiyesini döndürür.
     *
     * @param owner Kasa sahibi
     * @return Toplam bakiye
     * @throws SQLException Eğer veritabanı hatası olursa
     */
    public double getVaultTotalBalance(String owner) throws SQLException {
        return DatabaseManage.getVaultTotalBalance(owner);
    }


    /***
     *
     * @param owner Kasa sahibi
     * @param newMember Kasaya eklenecek oyuncu
     * @return Kasaya oyuncuyu ekler
     * @throws SQLException e
     */

    public boolean addMemberToVault(String owner, String newMember) throws SQLException {
        List<String> members = getVaultMembers(owner);
        if (members.size() >= 5) {
            return false;
        }

        if (members.contains(newMember)) {
            return false;
        }

        members.add(newMember);
        return updateVaultMembers(owner, members);
    }

    /***
     *
     * @param owner Kasa sahibi
     * @param memberToRemove Kasadan silinecek oyuncu
     * @return Kasadan oyuncuyu siler ve günceller
     * @throws SQLException
     */

    public boolean removeMemberFromVault(String owner, String memberToRemove) throws SQLException {
        List<String> members = getVaultMembers(owner);
        if (!members.contains(memberToRemove)) {
            return false;
        }
        members.remove(memberToRemove);
        return updateVaultMembers(owner, members);
    }

    /***
     *
     * @param owner Kasa sahibi
     * @param members Kasadaki oyuncular
     * @return Kasa oyuncularını günceller
     * @throws SQLException
     */

    public boolean updateVaultMembers(String owner, List<String> members) throws SQLException {
        try {
            DatabaseManage.updateVault(owner, String.valueOf(members));
        } catch (Exception e) {
            return false;
        }
        return true;
    }

    /***
     *
     * @param owner Kasa sahibi
     * @return Parametre olarak gönderdiğimiz kasa sahibinin kasasında kaç oyuncu varsa return eder hiç oyuncu yoksa 0 döner
     * @throws SQLException Veritabanı hatasına karşı
     */

    public int getVaultMembersCount(String owner) throws SQLException {
        return DatabaseManage.getVaultMemberCount(owner);
    }


    /***
     *
     * @param message Hata mesajı
     * @param e .
     */

    private void logError(String message, SQLException e) {
        System.err.println(message + e.getMessage());
        e.printStackTrace();
    }


}
