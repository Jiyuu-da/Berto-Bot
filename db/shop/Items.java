//package org.example.listeners.db.shop;
//
//import com.zaxxer.hikari.HikariConfig;
//import com.zaxxer.hikari.HikariDataSource;
//import net.dv8tion.jda.api.entities.User;
//import net.dv8tion.jda.api.hooks.ListenerAdapter;
//
//import java.sql.*;
//import java.time.Instant;
//import java.time.temporal.ChronoUnit;
//
//public class Items extends ListenerAdapter {
//    private static HikariConfig config = new HikariConfig();
//    private static HikariDataSource ds;
//
//    static {
//        config.setJdbcUrl("jdbc:mysql://localhost:3306/economy");
//        config.setUsername("root");
//        config.setPassword("K1r4root");
//        config.setDriverClassName("com.mysql.cj.jdbc.Driver");
//
//        config.addDataSourceProperty("cachePrepStmts", "true");
//        config.addDataSourceProperty("prepStmtCacheSize", "250");
//        config.addDataSourceProperty("prepStmtCacheSqlLimit", "2048");
//        ds = new HikariDataSource(config);
//    }
//            // *************SHIELD*******************
//            public static void activateShield(User user, Instant shieldExpiryTimestamp) {
//                String userID = user.getId();
//                String SQL_QUERY = "UPDATE eco_table SET has_shield = ?, shield_expiry = ? WHERE user_id = ?";
//
//                try (Connection con = ds.getConnection();
//                     PreparedStatement statement = con.prepareStatement(
//                             SQL_QUERY)) {
//
//                    statement.setInt(1, 1); // Set has_shield to true
//                    statement.setTimestamp(2, Timestamp.from(shieldExpiryTimestamp));
//                    statement.setString(3, userID);
//
//                    statement.executeUpdate();
//                } catch (SQLException e) {
//                    e.printStackTrace();
//                }
//            }
//
//            public static int hasShield(User user) {
//                String SQL_QUERY = "SELECT has_shield from eco_table WHERE user_id = ?";
//
//                try (Connection con = ds.getConnection();
//                     PreparedStatement pst = con.prepareStatement(SQL_QUERY)) {
//                    pst.setString(1, user.getId());
//                    try (ResultSet rs = pst.executeQuery()) {
//                        if (rs.next()) {
//                            return rs.getInt("has_shield");
//                        }
//                    }
//                } catch (SQLException e) {
//                    throw new RuntimeException(e);
//                }
//                return 0;
//            }
//            public static Instant getShieldExpiry(String userID) {
//                String SQL_QUERY = "SELECT shield_expiry FROM eco_table WHERE user_id = ?";
//
//                try (Connection con = ds.getConnection();
//                     PreparedStatement pst = con.prepareStatement(SQL_QUERY)) {
//                    pst.setString(1, userID);
//                    try (ResultSet rs = pst.executeQuery()) {
//                        if (rs.next()) {
//                            return rs.getTimestamp("shield_expiry").toInstant();
//                        }
//                    }
//                } catch (SQLException e) {
//                    throw new RuntimeException(e);
//                }
//
//                return Instant.MIN;
//            }
//
//}
