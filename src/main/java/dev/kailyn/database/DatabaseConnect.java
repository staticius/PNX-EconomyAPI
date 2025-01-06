package dev.kailyn.database;

import java.sql.*;
import java.util.Optional;

public class DatabaseConnect {

    private static Connection connection;

    public static void databaseConnect(String dbPath) throws SQLException{

        try {
            connection = DriverManager.getConnection("jdbc:sqlite:" + dbPath);

            createTables();

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

    }

    private static void createTables() throws SQLException {
        String playerTable = """
                    CREATE TABLE IF NOT EXISTS Player (
                        id INTEGER PRIMARY KEY AUTOINCREMENT,
                        playerName TEXT UNIQUE NOT NULL,
                        balance REAL NOT NULL DEFAULT 0.0
                    );
                """;

        String vaultTable = """
                    CREATE TABLE IF NOT EXISTS Vaults (
                        vaultId INTEGER PRIMARY KEY AUTOINCREMENT,
                        ownerName TEXT NOT NULL,
                        members TEXT NOT NULL,
                        totalBalance REAL NOT NULL DEFAULT 0.0
                    );
                """;

        connection.createStatement().execute(playerTable);
        connection.createStatement().execute(vaultTable);
    }

    public static double getBalance(String playerName) throws SQLException {

        String sql = "SELECT balance FROM Player WHERE playerName = ?";
        PreparedStatement preparedStatement = connection.prepareStatement(sql);
        preparedStatement.setString(1, playerName);
        ResultSet resultSet = preparedStatement.executeQuery();
        if (resultSet.next()) {
            return resultSet.getDouble("balance");
        }
        return 0;
    }

    public static void updateBalance(String playerName, double newBalance) throws SQLException {
        String sql = "UPDATE Player SET balance = ? WHERE playerName = ?";
        PreparedStatement preparedStatement = connection.prepareStatement(sql);
        preparedStatement.setDouble(1, newBalance);
        preparedStatement.setString(2, playerName);
        preparedStatement.executeUpdate();
    }

    public static void closeConnection() throws SQLException {
        try {
            if (connection != null && !connection.isClosed()) {
                connection.close();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static Connection getConnection() {
        return connection;
    }

    public static boolean vaultExists(String playerName) throws SQLException {
        String sql = "SELECT COUNT(*) FROM Vaults WHERE ownerName = ?";
        try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.setString(1, playerName);
            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                return resultSet.getInt(1) > 0;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }


    public static Optional<String> getVaultMembers(String ownerName) throws SQLException {
        String sql = "SELECT members FROM Vaults WHERE ownerName = ?";
        try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.setString(1, ownerName);
            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                return Optional.of(resultSet.getString("members"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return Optional.empty();
    }

    /**
     * Ortak kasayı oluşturur veya günceller.
     */
    public static boolean updateVault(String ownerName, String memberJson, double totalBalance) throws SQLException {
        String sql = """
                INSERT OR REPLACE INTO Vaults (ownerName, members, totalBalance)
                VALUES (?, ?, ?)
                """;
        try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.setString(1, ownerName);
            preparedStatement.setString(2, memberJson);
            preparedStatement.setDouble(3, totalBalance);
            return preparedStatement.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }




}
