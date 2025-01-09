package dev.kailyn.database;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Optional;

public class DatabaseManage {

    private static HikariDataSource dataSource;

    public static void databaseConnect(String dbPath) throws SQLException {

        try {

            HikariConfig config = new HikariConfig();
            config.setJdbcUrl("jdbc:sqlite:" + dbPath);
            config.setMaximumPoolSize(10);
            config.setMinimumIdle(2);
            config.setIdleTimeout(30000);
            config.setMaxLifetime(1800000);
            config.setConnectionTimeout(30000);


            dataSource = new HikariDataSource(config);


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

        try (Connection connection = getConnection(); PreparedStatement playerStatement = connection.prepareStatement(playerTable); PreparedStatement vaultStatement = connection.prepareStatement(vaultTable)) {
            playerStatement.execute();
            vaultStatement.execute();
        }
    }


    public static Connection getConnection() throws SQLException {

        if (dataSource == null) {
            throw new IllegalStateException("Veritabanı bağlantısı başarısız.");
        }

        return dataSource.getConnection();

    }

    public static void closeConnection() throws SQLException {
        if (dataSource != null) {
            dataSource.close();
        }
    }

    /**
     * Değerleri 2 ondalık basamağa yuvarlamak için yardımcı yöntem
     *
     * @param value Yuvarlanacak değer
     * @return Yuvarlanan değer
     */
    public static double roundToTwoDecimals(double value) {
        BigDecimal bigDecimal = new BigDecimal(Double.toString(value));
        return bigDecimal.setScale(2, RoundingMode.HALF_UP).doubleValue();
    }


    public static double getBalance(String playerName) throws SQLException {
        String sql = "SELECT balance FROM Player WHERE playerName = ?";
        try (Connection connection = getConnection(); PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.setString(1, playerName);
            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                return roundToTwoDecimals(resultSet.getDouble("balance"));
            }
        }
        return 0.0;
    }

    /***
     *
     * @param playerName Bakiyesi güncellenecek oyuncu
     * @param newBalance Güncellenmiş bakiye
     * @throws SQLException Bir hata oluşursa
     */

    public static void updateBalance(String playerName, double newBalance) throws SQLException {
        String sql = "UPDATE Player SET balance = ? WHERE playerName = ?";
        try (Connection connection = getConnection(); PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.setDouble(1, roundToTwoDecimals(newBalance));
            preparedStatement.setString(2, playerName);
            preparedStatement.executeUpdate();

        }
    }


    /**
     * Ortak kasanın tüm bilgilerini günceller (üyeler ve toplam bakiye).
     *
     * @param ownerName    Kasa sahibi
     * @param membersJson  Kasa üyelerini temsil eden JSON dizisi
     * @param totalBalance Yeni toplam bakiye
     * @return Güncelleme başarılıysa true, aksi halde false
     * @throws SQLException Eğer veritabanı hatası olursa
     */
    public static boolean updateVault(String ownerName, String membersJson, double totalBalance) throws SQLException {
        String sql = """
                UPDATE Vaults 
                SET members = ?, totalBalance = ? 
                WHERE ownerName = ?
                """;
        try (Connection connection = getConnection(); PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.setString(1, membersJson);
            preparedStatement.setDouble(2, roundToTwoDecimals(totalBalance));
            preparedStatement.setString(3, ownerName);
            return preparedStatement.executeUpdate() > 0;
        }
    }

    /**
     * Ortak kasanın üyelerini günceller.
     *
     * @param ownerName   Kasa sahibi
     * @param membersJson Kasa üyelerini temsil eden JSON dizisi
     * @return Güncelleme başarılıysa true, aksi halde false
     * @throws SQLException Eğer veritabanı hatası olursa
     */
    public static boolean updateVault(String ownerName, String membersJson) throws SQLException {
        String sql = """
                UPDATE Vaults 
                SET members = ? 
                WHERE ownerName = ?
                """;
        try (Connection connection = getConnection(); PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.setString(1, membersJson);
            preparedStatement.setString(2, ownerName);
            return preparedStatement.executeUpdate() > 0;
        }
    }

    /**
     * Ortak kasanın toplam bakiyesini günceller.
     *
     * @param ownerName    Kasa sahibi
     * @param totalBalance Yeni toplam bakiye
     * @return Güncelleme başarılıysa true, aksi halde false
     * @throws SQLException Eğer veritabanı hatası olursa
     */
    public static boolean updateVault(String ownerName, double totalBalance) throws SQLException {
        String sql = """
                UPDATE Vaults 
                SET totalBalance = ? 
                WHERE ownerName = ?
                """;
        try (Connection connection = getConnection(); PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.setDouble(1, roundToTwoDecimals(totalBalance));
            preparedStatement.setString(2, ownerName);
            return preparedStatement.executeUpdate() > 0;
        }
    }


    /***
     *
     * @param ownerName Kasa sahibi
     * @return Ortak Kasa üyelerini döndürür
     * @throws SQLException Bir hata oluşursa
     */

    public static Optional<String> getVaultMembers(String ownerName) throws SQLException {
        String sql = "SELECT members FROM Vaults WHERE ownerName = ?";
        try (Connection connection = getConnection(); PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.setString(1, ownerName);
            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                return Optional.of(resultSet.getString("members"));
            }
        }
        return Optional.empty();
    }


    /**
     * Bir ortak kasanın toplam bakiyesini döndürür.
     *
     * @param ownerName Kasa sahibi
     * @return Toplam bakiye (2 ondalık basamağa yuvarlanmış)
     * @throws SQLException Eğer veritabanı hatası olursa
     */
    public static double getVaultTotalBalance(String ownerName) throws SQLException {
        String sql = "SELECT totalBalance FROM Vaults WHERE ownerName = ?";
        try (Connection connection = getConnection(); PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.setString(1, ownerName);
            ResultSet resultSet = preparedStatement.executeQuery();

            if (resultSet.next()) {
                double totalBalance = resultSet.getDouble("totalBalance");
                return roundToTwoDecimals(totalBalance);
            }
        }
        return 0.0;
    }


    /***
     *
     * @param ownerName Kasa sahibi
     * @return Ortak kasa olup olmadığını kontrol eder
     * @throws SQLException Bir hata oluşursa
     */

    public static boolean vaultExists(String ownerName) throws SQLException {
        String sql = "SELECT COUNT(*) FROM Vaults WHERE ownerName = ?";
        try (Connection connection = getConnection(); PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.setString(1, ownerName);
            ResultSet resultSet = preparedStatement.executeQuery();
            return resultSet.next() && resultSet.getInt(1) > 0;
        }
    }


}
