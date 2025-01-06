package dev.kailyn.managers;

import dev.kailyn.database.DatabaseConnect;
import org.json.JSONArray;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class VaultManager {


    /**
     * Yeni bir ortak kasa oluşturur.
     *
     * @param owner   Kasa sahibi
     * @param members Kasa üyeleri
     * @return Kasa oluşturulduysa true, aksi halde false
     * @throws SQLException Eğer veritabanı hatası olursa
     */

    public boolean createVault(String owner, List<String> members) throws SQLException {

        // Maksimum üye kontrolü
        if (members.size() > 5) {
            return false;
        }

        // JSON Array'e çevir
        JSONArray membersArray = new JSONArray();
        membersArray.putAll(members);

        // SQL sorgusu
        String sql = "INSERT INTO Vaults (ownerName, members, totalBalance) VALUES (?, ?, ?)";

        // Try-with-resources ile kaynak yönetimi
        try (PreparedStatement preparedStatement = DatabaseConnect.getConnection().prepareStatement(sql)) {
            preparedStatement.setString(1, owner);
            preparedStatement.setString(2, membersArray.toString());
            preparedStatement.setDouble(3, 0.0);
            return preparedStatement.executeUpdate() > 0;
        }
    }

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

        try (PreparedStatement preparedStatement = DatabaseConnect.getConnection().prepareStatement(sql)) {
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

    /***
     *
     * @param owner Kasa sahibi
     * @param newMember Kasaya eklenecek oyuncu
     * @return Kasaya oyuncuyu ekler
     * @throws SQLException
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
     * @param owner
     * @param members
     * @return Kasa oyuncularını günceller
     * @throws SQLException
     */

    public boolean updateVaultMembers(String owner, List<String> members) throws SQLException {
        JSONArray jsonArray = new JSONArray(members);
        String sql = "UPDATE Vaults SET members = ? WHERE ownerName = ?";
        try (Connection connection = DatabaseConnect.getConnection(); PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.setString(1, jsonArray.toString());
            preparedStatement.setString(2, owner);
            return preparedStatement.executeUpdate() > 0;
        } catch (SQLException e) {
            logError("Kasa üyeleri güncellenirken bir sorun oluştu!", e);
            return false;
        }
    }

    /***
     *
     * @param owner
     * @param amount
     * @return Kasaya bakiye ekler
     * @throws SQLException
     */

    public boolean addBalanceToVault(String owner, double amount) throws SQLException {
        String sql = "UPDATE Vaults SET totalBalance = totalBalance + ? WHERE ownerName = ?";
        try (Connection connection = DatabaseConnect.getConnection(); PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.setDouble(1, amount);
            preparedStatement.setString(2, owner);
            return preparedStatement.executeUpdate() > 0;
        } catch (SQLException e) {
            logError("Kasa bakiyesi güncellenemedi: ", e);
            return false;
        }
    }


    /***
     *
     * @param message Hata mesajı
     * @param e
     */

    private void logError(String message, SQLException e) {
        System.err.println(message + e.getMessage());
        e.printStackTrace();
    }


}
