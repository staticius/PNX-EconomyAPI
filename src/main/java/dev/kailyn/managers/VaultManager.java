package dev.kailyn.managers;

import java.sql.*;
import dev.kailyn.database.DatabaseConnect;
import org.json.JSONArray;
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


}
