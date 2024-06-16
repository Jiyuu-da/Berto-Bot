//package org.example.listeners.db;
//
//import com.zaxxer.hikari.HikariConfig;
//import com.zaxxer.hikari.HikariDataSource;
//import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
//import net.dv8tion.jda.api.hooks.ListenerAdapter;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//
//import java.sql.Connection;
//import java.sql.PreparedStatement;
//import java.sql.ResultSet;
//import java.sql.SQLException;
//import java.time.Instant;
//import java.time.temporal.ChronoUnit;
//import java.util.Locale;
//
//public class Work extends ListenerAdapter {
//    private static final Logger logger = LoggerFactory.getLogger(Work.class);
//
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
//    @Override
//    public void onSlashCommandInteraction(SlashCommandInteractionEvent event) {
//        String command = event.getName();
//        String userID = event.getUser().getId();
//        final int MAX_AMOUNT = 60;
//
//        if(command.equalsIgnoreCase("work")) {
//            try {
//                if(BankCreate.hasAccount(userID)) {
//                    String[] workOptions = {
//                            "Waiter", "Plumber", "Manager",  "Doctor", "Engineer","Teacher", "Artist", "Writer", "Chef", "Lawyer", "Accountant","Architect", "Step Sister ;)",
//                            "Police Officer", "Firefighter", "Pilot","Nurse", "Scientist", "Athlete","Musician", "Photographer", "Actor", "Farmer","Electrician", "Director"
//                    };
//
//                    int rd = (int)(Math.random()*workOptions.length);
//                    int amount = (int)(Math.random()*MAX_AMOUNT - 10) + 10;
//                    String work = workOptions[rd];
//
//                    if(canClaimWork(userID)) {
//                        updateLastWorkTimestamp(userID);
//                        logger.info("User {} claimed daily reward.", userID);
//                        int userBalance = DBSetup.getBalanceFromDatabase(userID);
//                        int updatedBalance = userBalance + amount;
//                        DBSetup.updateBalanceInDatabase(userID, updatedBalance);
//
//                        if("aeiouAEIOU".contains(work.substring(0,1).toLowerCase())) {
//                            event.reply("You worked as an " + work + " and earned " + amount + " coins :coin:").setEphemeral(false).queue();
//                        } else {
//                            event.reply("You worked as a " + work + " and earned " + amount + " coins :coin:").setEphemeral(false).queue();
//
//                        }
//                    } else {
//                        Instant currentTime = Instant.now();
//                        Instant nextClaimTime = getLastWorkTimestamp(userID).plus(3, ChronoUnit.HOURS);
//                        long remainingMinutes = ChronoUnit.MINUTES.between(currentTime, nextClaimTime);
//
//                        long hoursRemaining = remainingMinutes / 60;
//                        long minutesRemaining = remainingMinutes % 60;
//                        event.reply("You can work again in " + hoursRemaining + "h " + minutesRemaining + "m").setEphemeral(true).queue();
//                    }
//
//                } else {
//                    event.reply("You do not have a bank account, create one by /bankcreate").setEphemeral(true).queue();
//
//                }
//            } catch (SQLException e) {
//                throw new RuntimeException(e);
//            }
//        }
//        super.onSlashCommandInteraction(event);
//    }
//
//    private boolean canClaimWork(String userId) throws SQLException {
//        Instant lastClaimTime = getLastWorkTimestamp(userId);
//        Instant now = Instant.now();
//        return ChronoUnit.HOURS.between(lastClaimTime, now) >= 3;
//    }
//    private Instant getLastWorkTimestamp(String userId) throws SQLException {
//        String SQL_QUERY = "SELECT last_work FROM eco_table WHERE user_id = ?";
//
//        try (Connection con = ds.getConnection();
//             PreparedStatement pst = con.prepareStatement(SQL_QUERY)) {
//            pst.setString(1, userId);
//            try (ResultSet rs = pst.executeQuery()) {
//                if (rs.next()) {
//                    return rs.getTimestamp("last_work").toInstant();
//                }
//            }
//        } catch (SQLException e) {
//            throw new RuntimeException(e);
//        }
//
//        return Instant.MIN;
//    }
//    private void updateLastWorkTimestamp(String userId) throws SQLException {
//        String SQL_UPDATE = "UPDATE eco_table SET last_work = NOW() WHERE user_id = ?";
//
//        try (Connection con = ds.getConnection();
//             PreparedStatement pst = con.prepareStatement(SQL_UPDATE)) {
//            pst.setString(1, userId);
//            pst.executeUpdate();
//        }
//    }
//}
package org.example.listeners;

import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.example.Main;
import org.example.listeners.db.BankCreate;
import org.example.listeners.db.DBSetup;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.Instant;
import java.time.temporal.ChronoUnit;



public class Work extends ListenerAdapter {
    private static final Logger logger = LoggerFactory.getLogger(Work.class);

    @Override
    public void onSlashCommandInteraction(SlashCommandInteractionEvent event) {
        String command = event.getName();
        String userID = event.getUser().getId();

        if(Main.maintenance && !userID.equals("576834455306633216")) {
            event.reply("BertoBot is under maintenance.").setEphemeral(true).queue();
            return;
        }

        final long MAX_AMOUNT = 60;

        if(command.equalsIgnoreCase("work")) {
            logger.info("Received /work command from user {}", userID);
            try (Connection connection = SqliteTest.getConnection()) {
                if(BankCreate.hasAccount(userID)) {
                    String[] workOptions = {
                            "Waiter", "Plumber", "Manager",  "Doctor", "Engineer","Teacher", "Artist", "Writer", "Chef", "Lawyer", "Accountant","Architect", "Bartender",
                            "Police Officer", "Firefighter", "Pilot","Nurse", "Scientist", "Athlete","Musician", "Photographer", "Actor", "Farmer","Electrician", "Director"
                    };

                    int rd = (int)(Math.random()*workOptions.length);
                    long amount = (long)(Math.random()*MAX_AMOUNT - 10) + 10;
                    String work = workOptions[rd];

                    if(canClaimWork(userID, connection)) {
                        updateLastWorkTimestamp(userID, connection);
                        logger.info("User {} claimed daily reward.", userID);
                        long userBalance = DBSetup.getBalanceFromDatabase(userID);
                        long updatedBalance = userBalance + amount;
                        DBSetup.updateBalanceInDatabase(userID, updatedBalance);

                        if("aeiouAEIOU".contains(work.substring(0,1).toLowerCase())) {
                            event.reply("You worked as an " + work + " and earned " + amount + " coins :coin:").setEphemeral(false).queue();
                        } else {
                            event.reply("You worked as a " + work + " and earned " + amount + " coins :coin:").setEphemeral(false).queue();

                        }
                    } else {
                        Instant currentTime = Instant.now();
                        Instant nextClaimTime = getLastWorkTimestamp(userID, connection).plus(3, ChronoUnit.HOURS);
                        long remainingMinutes = ChronoUnit.MINUTES.between(currentTime, nextClaimTime);

                        long hoursRemaining = remainingMinutes / 60;
                        long minutesRemaining = remainingMinutes % 60;
                        event.reply("You can work again in " + hoursRemaining + "h " + minutesRemaining + "m").setEphemeral(true).queue();
                    }

                } else {
                    event.reply("You do not have a bank account, create one by /bankcreate").setEphemeral(true).queue();

                }
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }
        super.onSlashCommandInteraction(event);
    }

    private boolean canClaimWork(String userId, Connection connection) throws SQLException {
        Instant lastClaimTime = getLastWorkTimestamp(userId, connection);
        Instant now = Instant.now();
        return ChronoUnit.HOURS.between(lastClaimTime, now) >= 3;
    }

    private Instant getLastWorkTimestamp(String userId, Connection connection) throws SQLException {
        String SQL_QUERY = "SELECT last_work FROM eco_table WHERE user_id = ?";

        try (PreparedStatement pst = connection.prepareStatement(SQL_QUERY)) {
            pst.setString(1, userId);
            try (ResultSet rs = pst.executeQuery()) {
                if (rs.next()) {
                    Timestamp lastWorkTimestamp = rs.getTimestamp("last_work");
                    if (lastWorkTimestamp != null) {
                        return lastWorkTimestamp.toInstant();
                    }
                }
            }
        }

        return Instant.MIN;
    }

    private void updateLastWorkTimestamp(String userId, Connection connection) throws SQLException {
        String SQL_UPDATE = "UPDATE eco_table SET last_work = ? WHERE user_id = ?";

        try (PreparedStatement pst = connection.prepareStatement(SQL_UPDATE)) {
            pst.setTimestamp(1, Timestamp.from(Instant.now()));
            pst.setString(2, userId);
            pst.executeUpdate();
        }
    }
}
