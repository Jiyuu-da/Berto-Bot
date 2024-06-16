//package org.example.listeners.db;
//
//import com.zaxxer.hikari.HikariConfig;
//import com.zaxxer.hikari.HikariDataSource;
//import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
//import net.dv8tion.jda.api.hooks.ListenerAdapter;
////import org.example.listeners.coinflip;
//import org.example.listeners.db.DBSetup;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//
//import java.sql.Connection;
//import java.sql.PreparedStatement;
//import java.sql.ResultSet;
//import java.sql.SQLException;
//
//public class BankCreate extends ListenerAdapter {
//    private static final Logger logger = LoggerFactory.getLogger(BankCreate.class);
//
//    private String user_id;
//    private double user_bal;
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
//
//    @Override
//    public void onSlashCommandInteraction(SlashCommandInteractionEvent event) {
//        String command = event.getName();
//        String userID = event.getUser().getId();
//        if(command.equalsIgnoreCase("bankcreate")) {
//            try {
//                if(!hasAccount(userID)) {
//                    createAccount(userID);
//                    event.reply("Account created successfully, you have 100 coins :coin:").setEphemeral(true).queue();
//                    logger.info("Account created for user ID: {}", userID);
//
//                }
//                else {
//                    logger.info("Account already exists: {}", userID);
//
//                    event.reply("You Already have an account").setEphemeral(true).queue();
//                }
//            } catch (SQLException e) {
//                throw new RuntimeException(e);
//            }
//        }
//        super.onSlashCommandInteraction(event);
//    }
//    public void createAccount(String userId) {
//        String SQL_INSERT = "INSERT into eco_table (user_id, user_bal) VALUES (?, 100)";
//
//
//        try (Connection con = ds.getConnection();
//             PreparedStatement pst = con.prepareStatement(SQL_INSERT)) {
//            pst.setString(1, userId);
//            pst.executeUpdate();
//        } catch (SQLException e) {
//            throw new RuntimeException(e);
//        }
//    }
//    public static boolean hasAccount(String userId) throws SQLException {
//        String SQL_QUERY = "SELECT COUNT(*) AS count FROM eco_table WHERE user_id = ?";
//
//        try (Connection con = ds.getConnection();
//             PreparedStatement pst = con.prepareStatement(SQL_QUERY)) {
//            pst.setString(1, userId);
//            try (ResultSet rs = pst.executeQuery()) {
//                if (rs.next()) {
//                    int count = rs.getInt("count");
//                    return count > 0;
//                }
//            }
//        } catch (SQLException e) {
//            throw new RuntimeException(e);
//        }
//
//        return false; // Default to false in case of exceptions
//    }
//}


package org.example.listeners.db;

import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.example.listeners.db.DBSetup;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class BankCreate extends ListenerAdapter {
    private static final Logger logger = LoggerFactory.getLogger(BankCreate.class);

    @Override
    public void onSlashCommandInteraction(SlashCommandInteractionEvent event) {
        String command = event.getName();
        String userID = event.getUser().getId();
        if(command.equalsIgnoreCase("bankcreate")) {
            try {
                if(!hasAccount(userID)) {
                    createAccount(userID);
                    event.reply("Account created successfully, you have 500 coins :coin:").setEphemeral(true).queue();
                    logger.info("Account created for user ID: {}", userID);

                }
                else {
                    logger.info("Account already exists: {}", userID);

                    event.reply("You Already have an account").setEphemeral(true).queue();
                }
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }
        super.onSlashCommandInteraction(event);
    }

    public void createAccount(String userId) {
        String SQL_INSERT = "INSERT INTO eco_table (user_id, user_bal) VALUES (?, 500)";


        try (Connection con = DBSetup.getConnection();
             PreparedStatement pst = con.prepareStatement(SQL_INSERT)) {
            pst.setString(1, userId);
            pst.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public static boolean hasAccount(String userId) throws SQLException {
        String SQL_QUERY = "SELECT COUNT(*) AS count FROM eco_table WHERE user_id = ?";

        try (Connection con = DBSetup.getConnection();
             PreparedStatement pst = con.prepareStatement(SQL_QUERY)) {
            pst.setString(1, userId);
            try (ResultSet rs = pst.executeQuery()) {
                if (rs.next()) {
                    int count = rs.getInt("count");
                    return count > 0;
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        return false; // Default to false in case of exceptions
    }
}

