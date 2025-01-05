package dev.kailyn.database;

import dev.kailyn.Prefix;

import java.sql.*;

public class DatabaseConnect {

    public static void databaseConnect() {

        final var url = "jdbc:sqlite:/home/staticius/ecodb/ecosqlite.db";

        Connection connection = null;

        try {
            connection = DriverManager.getConnection(url);
            System.out.println(Prefix.getPrefix() + "Veritabanı bağlantısı kuruldu.");

            final String sql = "SELECT * FROM Player";

            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery(sql);


            while (resultSet.next()) {
                var name = resultSet.getString("Name");
                var balance = resultSet.getDouble("Balance");
                var vault = resultSet.getString("Vault");
                System.out.println(Prefix.getPrefix() + name + " " + balance + " " + vault);
            }

        } catch (SQLException e) {
            System.out.println(e.getMessage());
        } finally {
            try {
                if (connection != null) {
                    connection.close();
                    System.out.println(Prefix.getPrefix() + "Veritabanı bağlantısı kapatıldı.");
                }
            } catch (SQLException ex) {
                System.out.println(ex.getMessage());
            }
        }

    }

}
