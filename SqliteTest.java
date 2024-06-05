package org.example.listeners;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class SqliteTest {
    private static final String JDBC_URL = "jdbc:sqlite:/Users/chaks/bot/economy.db";

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(JDBC_URL);
    }

        public static void main(String[] args) {
            try (Connection connection = SqliteTest.getConnection()) {
                // Create a statement
                Statement statement = connection.createStatement();

                // Execute a query
                ResultSet resultSet = statement.executeQuery("SELECT * FROM eco_table");

                // Process the result set
                while (resultSet.next()) {
                    String userId = resultSet.getString("user_id");
                    int userBal = resultSet.getInt("user_bal");
                    // Fetch other columns as needed

                    // Print the fetched data
                    System.out.println("User ID: " + userId + ", User Balance: " + userBal);
                }
            } catch (SQLException e) {
                e.printStackTrace();

        }
    }
}
