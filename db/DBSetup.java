package org.example.listeners.db;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class DBSetup {
    private String user_id;
    private double user_bal;
    private static HikariConfig config = new HikariConfig();
    private static HikariDataSource ds;

    static {
        config.setJdbcUrl("jdbc:mysql://localhost:3306/economy");
        config.setUsername("root");
        config.setPassword("K1r4root");
        config.setDriverClassName("com.mysql.cj.jdbc.Driver");

        config.addDataSourceProperty("cachePrepStmts", "true");
        config.addDataSourceProperty("prepStmtCacheSize", "250");
        config.addDataSourceProperty("prepStmtCacheSqlLimit", "2048");
        ds = new HikariDataSource(config);
    }

    public static List<Economy> fetchData() throws SQLException {
        String SQL_QUERY = "SELECT * FROM eco_table";
        List<Economy> users = new ArrayList<>();

        try (Connection con = ds.getConnection();
             PreparedStatement pst = con.prepareStatement(SQL_QUERY);
             ResultSet rs = pst.executeQuery()) {

            while (rs.next()) {
                Economy economy = new Economy();
                economy.setUser_id(rs.getString("user_id"));
                economy.setUser_bal(rs.getDouble("user_bal"));

                users.add(economy);
            }
        }

        return users;
    }

    public DBSetup(String user_id, double user_bal) {
        this.user_id = user_id;
        this.user_bal = user_bal;
    }

    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    public double getUser_bal() {
        return user_bal;
    }

    public void setUser_bal(double user_bal) {
        this.user_bal = user_bal;
    }

    public static Connection getConnection() throws SQLException {
        return ds.getConnection();
    }

    public static int getBalanceFromDatabase(String userId) throws SQLException {
        int balance = 0;
        String SQL_QUERY = "SELECT user_bal FROM eco_table WHERE user_id = ?";

        try (Connection con = ds.getConnection();
             PreparedStatement pst = con.prepareStatement(SQL_QUERY)) {
            pst.setString(1, userId);
            try (ResultSet rs = pst.executeQuery()) {
                if (rs.next()) {
                    balance = rs.getInt("user_bal");
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        return balance;
    }

    public static void updateBalanceInDatabase(String userId, int updatedBalance) throws SQLException {
        String SQL_UPDATE = "UPDATE eco_table SET user_bal = ? WHERE user_id = ?";

        try (Connection con = ds.getConnection();
             PreparedStatement pst = con.prepareStatement(SQL_UPDATE)) {
            pst.setInt(1, updatedBalance);
            pst.setString(2, userId);
            pst.executeUpdate();
        }
    }

}