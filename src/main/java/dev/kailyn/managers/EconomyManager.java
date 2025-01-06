package dev.kailyn.managers;

import dev.kailyn.Prefix;
import dev.kailyn.database.DatabaseConnect;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class EconomyManager {

    private void ensureConnection() {
        try{
            Connection connection = DatabaseConnect.getConnection();
            if (connection != null || connection.isClosed()) {
                DatabaseConnect.databaseConnect("/home/staticius/Masaüstü/Software/PNXSERVER/plugins/WL-Economy/economy.db");
            }
        } catch (SQLException e) {
            logError(Prefix.getPrefix() + "Veritabanı bağlantısı kontrol edilemedi", e);
        }
    }


    public boolean createAccount(String playerName) {
        ensureConnection();
        String sql = "INSERT OR IGNORE INTO Player (playerName, balance) VALUES (?, 0.0)";

        try (Connection connection = DatabaseConnect.getConnection(); PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.setString(1, playerName);
            return preparedStatement.executeUpdate() > 0;
        } catch (SQLException e) {
            logError("Hesap oluşturulamadı: ", e);
            return false;
        }
    }

    public double getBalance(String playerName) {
        ensureConnection();
        String sql = "SELECT balance FROM player WHERE playerName = ?";
        try (Connection connection = DatabaseConnect.getConnection(); PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.setString(1, playerName);
            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                return resultSet.getDouble("balance");
            }

        } catch (SQLException exception) {
            logError("Bakiye alınırken bir sorun oluştu: ", exception);
        }
        return 0.0;
    }

    public boolean updateBalance(String playerName, double newBalance) {
        ensureConnection();
        String sql = "INSERT OR REPLACE INTO Player (playerName, balance) VALUES (?, ?)";

        try (Connection connection = DatabaseConnect.getConnection(); PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.setString(1, playerName);
            preparedStatement.setDouble(2, newBalance);
            return preparedStatement.executeUpdate() > 0;

        } catch (SQLException sqlException) {
            logError("Bakiye güncellenirken hata oluştu: ", sqlException);
            return false;
        }
    }

    public boolean deposit(String playerName, double amount) {
        ensureConnection();
        double balance = getBalance(playerName);
        return updateBalance(playerName, balance + amount);
    }

    public boolean withdraw(String playerName, double amount) {
        ensureConnection();
        double currentBalance = getBalance(playerName);
        if(currentBalance < amount) {
            return false;
        }
        return updateBalance(playerName, currentBalance - amount);
    }

    public boolean transfer(String fromPlayerName, String toPlayerName, double amount) {
        ensureConnection();
        if (withdraw(fromPlayerName, amount)) {
            return deposit(toPlayerName, amount);
        }
        return false;
    }


    private void logError(String message, SQLException e) {
        System.err.println(message + e.getMessage());
        e.printStackTrace();
    }

}
