package dev.astatic.database;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.jetbrains.annotations.NotNull;
import org.json.JSONArray;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Optional;

public class DatabaseManage {
    private static HikariDataSource dataSource;

    public static void databaseConnect(String dbPath) {
        try {
            if (!dbPath.startsWith("jdbc:sqlite:")) {
                dbPath = "jdbc:sqlite:" + dbPath;
            }

            HikariConfig config = getHikariConfig(dbPath);

            dataSource = new HikariDataSource(config);

            createTables();
        } catch (Exception e) {

            throw new RuntimeException("Veritabanına bağlanırken bir hata oluştu: " + e.getMessage(), e);
        }
    }

    private static @NotNull HikariConfig getHikariConfig(String dbPath) {
        HikariConfig config = new HikariConfig();
        config.setJdbcUrl(dbPath);

        config.setMaximumPoolSize(8);       // Maksimum 8 bağlantı
        config.setMinimumIdle(4);          // Minimum 4 bağlantı
        config.setIdleTimeout(30000);      // Boşta kalma süresi: 30 saniye
        config.setMaxLifetime(1800000);    // Maksimum yaşam süresi: 30 dakika
        config.setConnectionTimeout(10000); // Bağlantı süresi: 10 saniye
        return config;
    }


    private static void createTables() throws SQLException {
        String playerTable = """
                CREATE TABLE IF NOT EXISTS Player (
                    id         INTEGER PRIMARY KEY AUTOINCREMENT,
                    playerName TEXT    NOT NULL UNIQUE,
                    balance    REAL    NOT NULL DEFAULT 0.0
                );
                """;

        String vaultTable = """
                CREATE TABLE IF NOT EXISTS Vaults (
                    vaultId      INTEGER PRIMARY KEY AUTOINCREMENT,
                    ownerName    TEXT    NOT NULL UNIQUE,
                    members      TEXT,
                    totalBalance REAL    NOT NULL DEFAULT 0.0
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

    /*** Veritabanı bağlantısını kapat
     *
     * @throws SQLException bağlantı kapanmazsa
     */

    public static void closeConnection() throws SQLException {
        if (dataSource != null) {
            dataSource.close();
        }
    }


    /*** Oyuncunun bakiyesini döndürür
     *
     * @param playerName Oyuncu
     * @return Oyuncunun bakiyesi
     * @throws SQLException Veritabanı hatası olursa
     */

    public static double getBalance(String playerName) throws SQLException {
        String sql = "SELECT balance FROM Player WHERE playerName = ?";
        try (Connection connection = getConnection(); PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.setString(1, playerName);
            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                return resultSet.getDouble("balance");
            }
        }
        return 0.0;
    }

    /*** Oyuncu oyuna ilk girişinde veritabanına bakiye kaydı yapma
     *
     * @param playerName hesap oluşturulacak Oyuncunun adı
     * @return Hesap oluşturulursa true oluşturulmazsa false
     * @throws SQLException veritabanı hatalarına karşı
     */
    public static boolean createPlayerAccount(String playerName) throws SQLException {
        String sql = "INSERT INTO Player (playerName, balance) VALUES (?, 0.0)";

        try (Connection connection = getConnection(); PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.setString(1, playerName);
            return preparedStatement.executeUpdate() > 0; // Kayıt oluşturulduysa `true` döner
        }
    }


    /*** Oyuncunun sunucuda kayıtlı olup olmadığını döndür
     *
     * @param playerName Kontrol edilecek oyuncunun adı
     * @return Eğer kayıt varsa true yoksa false döner
     * @throws SQLException Veritabanı hatalarına karşı
     */

    public static boolean isPlayerRegistered(String playerName) throws SQLException {
        String sql = "SELECT COUNT(*) FROM Player WHERE playerName = ?";

        try (Connection connection = getConnection(); PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.setString(1, playerName);
            ResultSet resultSet = preparedStatement.executeQuery();

            // Eğer kayıt varsa `true`, yoksa `false` döner
            return resultSet.next() && resultSet.getInt(1) > 0;
        }
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
            preparedStatement.setDouble(1, newBalance);
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
                 UPDATE Vaults\s
                 SET members = ?, totalBalance = ?\s
                 WHERE ownerName = ?
                \s""";
        try (Connection connection = getConnection(); PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.setString(1, membersJson);
            preparedStatement.setDouble(2, totalBalance);
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
                 UPDATE Vaults\s
                 SET members = ?\s
                 WHERE ownerName = ?
                \s""";
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
                 UPDATE Vaults\s
                 SET totalBalance = ?\s
                 WHERE ownerName = ?
                \s""";
        try (Connection connection = getConnection(); PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.setDouble(1, totalBalance);
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
     * @return Toplam bakiye
     * @throws SQLException Eğer veritabanı hatası olursa
     */
    public static double getVaultTotalBalance(String ownerName) throws SQLException {
        String sql = "SELECT totalBalance FROM Vaults WHERE ownerName = ?";
        try (Connection connection = getConnection(); PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.setString(1, ownerName);
            ResultSet resultSet = preparedStatement.executeQuery();

            if (resultSet.next()) {
                return resultSet.getDouble("totalBalance");
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

    /*** Oyuncu bir kasanın sahibi mi
     *
     * @param playerName Oyuncu
     * @return Oyuncu bir kasanın sahibiyse true döner
     * @throws SQLException Veritabanı hatası olursa
     */

    public static boolean isVaultOwner(String playerName) throws SQLException {
        String sql = "SELECT COUNT(*) FROM Vaults WHERE ownerName = ?";
        try (Connection connection = DatabaseManage.getConnection(); PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.setString(1, playerName);
            ResultSet resultSet = preparedStatement.executeQuery();
            return resultSet.next() && resultSet.getInt(1) > 0;
        }
    }

    public static boolean isVaultMember(String playerName) throws SQLException {
        String sql = "SELECT members FROM Vaults";
        try (Connection connection = getConnection(); PreparedStatement preparedStatement = connection.prepareStatement(sql); ResultSet resultSet = preparedStatement.executeQuery()) {

            while (resultSet.next()) {
                String membersJson = resultSet.getString("members");
                JSONArray membersArray = new JSONArray(membersJson);

                for (int i = 0; i < membersArray.length(); i++) {
                    if (membersArray.getString(i).equals(playerName)) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    public static String getPlayerVaultRole(String playerName) throws SQLException {
        if (isVaultOwner(playerName)) {
            return "owner";
        } else if (isVaultMember(playerName)) {
            return "member";
        }
        return "none";
    }


    /*** Oyuncu herhangi bir kasada üye olup olmadığını kontrol eder
     *
     * @param playerName Oyuncu
     * @return Oyuncu herhangi bir kasada üye olup olmadığını kontrol eder
     * @throws SQLException herhangi bir hatada
     */

    public static boolean isPlayerInAnyVault(String playerName) throws SQLException {
        String sql = "SELECT members FROM Vaults";
        try (Connection connection = getConnection(); PreparedStatement preparedStatement = connection.prepareStatement(sql); ResultSet resultSet = preparedStatement.executeQuery()) {

            while (resultSet.next()) {
                String membersJson = resultSet.getString("members");
                JSONArray membersArray = new JSONArray(membersJson);

                for (int i = 0; i < membersArray.length(); i++) {
                    if (membersArray.getString(i).equals(playerName)) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    /*** Oyuncunun bulunduğu kasanın ID sini döndürür
     *
     * @param playerName Oyuncu
     * @return Oyuncunun bulunduğu kasanın ID sini döndürür
     * @throws SQLException herhangi bir hatada
     */

    public static Optional<Integer> getVaultId(String playerName) throws SQLException {
        String sql = "SELECT vaultId, members FROM Vaults";
        try (Connection connection = DatabaseManage.getConnection(); PreparedStatement preparedStatement = connection.prepareStatement(sql); ResultSet resultSet = preparedStatement.executeQuery()) {

            while (resultSet.next()) {
                String membersJson = resultSet.getString("members");
                JSONArray membersArray = new JSONArray(membersJson);

                for (int i = 0; i < membersArray.length(); i++) {
                    if (membersArray.getString(i).equals(playerName)) {
                        return Optional.of(resultSet.getInt("vaultId"));
                    }
                }
            }
        }
        return Optional.empty();
    }

    /*** Oyuncunun bulunduğu kasanın sahibini döndürür
     *
     * @param playerName Oyuncu
     * @return Oyuncunun bulunduğu kasanın sahibini döndürür
     * @throws SQLException herhangi bir hatada
     */

    public static Optional<String> getVaultOwner(String playerName) throws SQLException {
        String sql = "SELECT ownerName, members FROM Vaults";
        try (Connection connection = DatabaseManage.getConnection(); PreparedStatement preparedStatement = connection.prepareStatement(sql); ResultSet resultSet = preparedStatement.executeQuery()) {

            while (resultSet.next()) {
                String membersJson = resultSet.getString("members");
                JSONArray membersArray = new JSONArray(membersJson);

                for (int i = 0; i < membersArray.length(); i++) {
                    if (membersArray.getString(i).equals(playerName)) {
                        return Optional.of(resultSet.getString("ownerName"));
                    }
                }
            }
        }
        return Optional.empty();
    }

    /***
     *
     * @param ownerName Vault'u silinecek oyuncu
     * @return Eğer vault silindiyse true döner silinmediyse false döner
     * @throws SQLException Veritabanı hatası olursa
     */

    public static boolean deleteVault(String ownerName) throws SQLException {
        String sql = "DELETE FROM Vaults WHERE ownerName = ?";
        try (Connection connection = getConnection(); PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.setString(1, ownerName);
            return preparedStatement.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Vault silinirken bir hata oluştu: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    /***
     *
     * @param owner Kasa sahibi
     * @param amount Eklenecek miktar
     * @return Kasaya bakiye ekler
     * @throws SQLException
     */

    public static boolean addBalanceToVault(String owner, double amount) throws SQLException {
        String sql = "UPDATE Vaults SET totalBalance = totalBalance + ? WHERE ownerName = ?";

        if (amount <= 0) {
            return false;
        }

        try (Connection connection = getConnection(); PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.setDouble(1, amount);
            preparedStatement.setString(2, owner);
            return preparedStatement.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public static boolean addMemberToVault(String ownerName, String newMember) throws SQLException {
        Optional<String> membersJson = getVaultMembers(ownerName);

        List<String> members = new ArrayList<>();
        if (membersJson.isPresent()) {
            JSONArray membersArray = new JSONArray(membersJson.get());
            for (int i = 0; i < membersArray.length(); i++) {
                members.add(membersArray.getString(i));
            }
        }

        if (members.contains(newMember)) {
            return false;
        }

        members.add(newMember);

        JSONArray updatedMembersJson = new JSONArray(members);

        return updateVault(ownerName, updatedMembersJson.toString());
    }


    public static boolean removeMemberFromVault(String ownerName, String memberToRemove) throws SQLException {
        Optional<String> membersJson = getVaultMembers(ownerName);

        if (membersJson.isEmpty()) {
            return false;
        }

        // JSON'dan üyeleri listeye çevir
        JSONArray membersArray = new JSONArray(membersJson.get());
        boolean memberRemoved = false;

        // Üyeyi bul ve kaldır
        for (int i = 0; i < membersArray.length(); i++) {
            if (membersArray.getString(i).equals(memberToRemove)) {
                membersArray.remove(i);
                memberRemoved = true;
                break;
            }
        }

        if (!memberRemoved) {
            return false;
        }

        // Üyeleri tekrar JSON olarak güncelle
        String updatedMembersJson = membersArray.toString();

        // Veritabanında üyeleri güncelle
        return updateVault(ownerName, updatedMembersJson);
    }


    public static boolean createVault(String ownerName) throws SQLException {

        if (vaultExists(ownerName) || isVaultMember(ownerName)) {
            return false;
        }

        String sql = "INSERT INTO Vaults (ownerName, members, totalBalance) VALUES (?, ?, ?)";
        try (Connection connection = getConnection(); PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.setString(1, ownerName); // Vault sahibi
            preparedStatement.setString(2, "[]"); // Başlangıçta üye yok
            preparedStatement.setDouble(3, 0.0); // Başlangıç bakiyesi 0.0
            return preparedStatement.executeUpdate() > 0;
        }
    }

    /*** Parametre olarak girilen oyuncu isminin sahip olduğu kasada kaç tane oyuncu olduğunu döndürür
     *
     * @param ownerName Kasa sahibi
     * @return Parametre olarak girilen oyuncu isminin sahip olduğu kasada kaç tane oyuncu olduğunu döndürür
     * @throws SQLException Veritabanı hatasına karşı
     */

    public static int getVaultMemberCount(String ownerName) throws SQLException {
        String sql = "SELECT members FROM Vaults WHERE ownerName = ?";
        try (Connection connection = getConnection(); PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.setString(1, ownerName);
            ResultSet resultSet = preparedStatement.executeQuery();

            if (resultSet.next()) {
                String membersJson = resultSet.getString("members");
                JSONArray membersArray = new JSONArray(membersJson);
                return membersArray.length();
            }
        }
        return 0; // Üyeler yoksa 0 döner
    }



    /*** Sayıları düzgün bir şekilde formatlar (YALNIZCA OYUNCUYA PARA DEĞERİ GÖSTERİRKEN KULLANILMALIDIR).
     *
     * @param value Sayı
     * @return Formatlanmış düzgün görünen sayı
     */

    public static String formatNumber(double value) {
        DecimalFormatSymbols symbols = new DecimalFormatSymbols(Locale.getDefault());
        symbols.setGroupingSeparator('.');
        symbols.setDecimalSeparator(',');

        DecimalFormat decimalFormat = new DecimalFormat("#,##0.00", symbols);

        return decimalFormat.format(value);
    }

    public static List<String> getAllVaultOwnersAndMembers() throws SQLException {
        List<String> vaultOwnersAndMembers = new ArrayList<>();

        String sql = "SELECT ownerName, members FROM Vaults";
        try (Connection connection = DatabaseManage.getConnection(); PreparedStatement preparedStatement = connection.prepareStatement(sql); ResultSet resultSet = preparedStatement.executeQuery()) {

            while (resultSet.next()) {
                vaultOwnersAndMembers.add(resultSet.getString("ownerName"));

                String membersJson = resultSet.getString("members");
                JSONArray membersArray = new JSONArray(membersJson);

                for (int i = 0; i < membersArray.length(); i++) {
                    vaultOwnersAndMembers.add(membersArray.getString(i));
                }
            }
        }

        return vaultOwnersAndMembers;
    }

    /***
     *
     * @param limit Kaç oyuncu alınacağını belirler
     * @return belirlenen limite göre en çok parası olan oyuncuları sıralar
     * @throws SQLException Veritabanı hatalarına karşı
     */

    public static List<String> getTopBalanceList(int limit) throws SQLException {
        String sql = "SELECT playerName, balance FROM Player ORDER BY balance DESC LIMIT ?";
        List<String> topPlayers = new ArrayList<>();

        try (Connection connection = getConnection(); PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.setInt(1, limit);

            ResultSet resultSet = preparedStatement.executeQuery();

            while (resultSet.next()) {
                String playerName = resultSet.getString("playerName");
                double balance = resultSet.getDouble("balance");
                topPlayers.add(playerName + " - " + formatNumber(balance));
            }
        }

        return topPlayers;
    }

    /*** En çok bakiye bulunan kasaları verir
     *
     * @param limit maximum kaç kasa dönecek
     * @return En çok bakiyesi bulunan kasaları sahipleriyle birlikte döner
     * @throws SQLException veritabanı hatasına karşı
     */

    public static List<String> getTopVaultBalanceList(int limit) throws SQLException {
        String sql = "SELECT ownerName, totalBalance FROM Vaults ORDER BY totalBalance DESC LIMIT ?";
        List<String> topVaults = new ArrayList<>();

        try (Connection connection = getConnection(); PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.setInt(1, limit); // Kaç vault alınacağı belirleniyor (örneğin: 10)

            ResultSet resultSet = preparedStatement.executeQuery();

            while (resultSet.next()) {
                String ownerName = resultSet.getString("ownerName");
                double totalBalance = resultSet.getDouble("totalBalance");
                topVaults.add(ownerName + " - " + formatNumber(totalBalance));
            }
        }

        return topVaults;
    }


    public static List<String> parseMembers(String membersJson) {
        JSONArray membersArray = new JSONArray(membersJson);
        List<String> members = new ArrayList<>();
        for (int i = 0; i < membersArray.length(); i++) {
            members.add(membersArray.getString(i));
        }
        return members;
    }



}
