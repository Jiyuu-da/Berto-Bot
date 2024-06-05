package org.example.listeners.db;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class DBSetup {
    private static final String JDBC_URL = "jdbc:sqlite:economy.db";

    public static List<Economy> fetchData() throws SQLException {
        String SQL_QUERY = "SELECT * FROM eco_table";
        List<Economy> users = new ArrayList<>();

        try (Connection con = DriverManager.getConnection(JDBC_URL);
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

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(JDBC_URL);
    }

    public static int getBalanceFromDatabase(String userId) throws SQLException {
        int balance = 0;
        String SQL_QUERY = "SELECT user_bal FROM eco_table WHERE user_id = ?";

        try (Connection con = DriverManager.getConnection(JDBC_URL);
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

        try (Connection con = DriverManager.getConnection(JDBC_URL);
             PreparedStatement pst = con.prepareStatement(SQL_UPDATE)) {
            pst.setInt(1, updatedBalance);
            pst.setString(2, userId);
            pst.executeUpdate();
        }
    }

    public static List<Economy> getSortedBalance() throws SQLException {
        String SQL_SORT = "SELECT user_bal, user_id FROM eco_table ORDER BY user_bal DESC LIMIT 10";
        List<Economy> users = new ArrayList<>();

        try (Connection con = DriverManager.getConnection(JDBC_URL);
             PreparedStatement pst = con.prepareStatement(SQL_SORT);
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
}

