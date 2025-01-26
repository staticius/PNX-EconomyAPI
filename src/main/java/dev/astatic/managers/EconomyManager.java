package dev.astatic.managers;

import dev.astatic.database.DatabaseManage;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;

public class EconomyManager {


    public boolean createAccount(String playerName) {
        String sql = "INSERT OR IGNORE INTO Player (playerName, balance) VALUES (?, 0.0)";

        try (Connection connection = DatabaseManage.getConnection(); PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.setString(1, playerName);
            return preparedStatement.executeUpdate() > 0;
        } catch (SQLException e) {
            logError("Hesap oluşturulamadı: ", e);
            return false;
        }
    }

    public double getBalance(String playerName) {
        try {
            return DatabaseManage.getBalance(playerName);
        } catch (SQLException e) {
            logError("Bakiye alınırken hata oluştu.", e);
        }
        return 0.0;
    }

    public boolean updateBalance(String playerName, double newBalance) {
        try {
            DatabaseManage.updateBalance(playerName, newBalance);
            return true;
        } catch (SQLException exception) {
            logError("Bakiye güncellenirken hata oluştu.", exception);
            return false;
        }
    }

    public boolean addBalance(String playerName, double amount) {
        double balance = getBalance(playerName);
        return updateBalance(playerName, balance + amount);
    }

    public boolean removeBalance(String playerName, double amount) {
        double currentBalance = getBalance(playerName);
        if (currentBalance < amount) {
            return false;
        }
        return updateBalance(playerName, currentBalance - amount);
    }

    public boolean transfer(String fromPlayerName, String toPlayerName, double amount) {
        if (removeBalance(fromPlayerName, amount)) {
            return addBalance(toPlayerName, amount);
        }
        return false;
    }

    public List<String> getTopBalanceList(int limit) throws SQLException {
        return DatabaseManage.getTopBalanceList(limit);
    }


    private void logError(String message, SQLException e) {
        System.err.println(message + e.getMessage());
        e.printStackTrace();
    }

}
