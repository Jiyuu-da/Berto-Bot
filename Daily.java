package org.example.listeners;

import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.example.listeners.db.BankCreate;
import org.example.listeners.db.DBSetup;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.example.Main;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.Instant;
import java.time.temporal.ChronoUnit;

public class Daily extends ListenerAdapter {
    private static final Logger logger = LoggerFactory.getLogger(Daily.class);

    @Override
    public void onSlashCommandInteraction(SlashCommandInteractionEvent event) {
        String command = event.getName();
        String userID = event.getUser().getId();

        if(Main.maintenance && !userID.equals("576834455306633216")) {
            event.reply("BertoBot is under maintenance.").setEphemeral(true).queue();
            return;
        }

        final long MAX_AMOUNT = 200;

        if (command.equalsIgnoreCase("daily")) {


            try {
                if (BankCreate.hasAccount(userID)) {
                    long amount = (long)(Math.random() * MAX_AMOUNT - 10) + 10;

                    try {
                        if (canClaimDaily(userID)) {
                            updateLastDailyTimestamp(userID);
                            // Code to give coins to the user
                            logger.info("User {} claimed daily reward.", userID);
                            long userBalance = DBSetup.getBalanceFromDatabase(userID);
                            long updatedBalance = userBalance + amount;
                            DBSetup.updateBalanceInDatabase(userID, updatedBalance);

                            event.reply("You've claimed your daily reward of " + amount + " coins! :coin:").setEphemeral(false).queue();
                        } else {
                            Instant lastClaimTime = getLastDailyTimestamp(userID);
                            Instant now = Instant.now();
                            long remainingTime = ChronoUnit.HOURS.between(lastClaimTime, now);
                            event.reply("You've already claimed your daily reward. You can claim again in " + (24 - remainingTime) + " hours.").setEphemeral(true).queue();
                        }
                    } catch (SQLException e) {
                        throw new RuntimeException(e);
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

    private boolean canClaimDaily(String userID) throws SQLException {
        Instant lastClaimTime = getLastDailyTimestamp(userID);
        Instant now = Instant.now();
        return ChronoUnit.HOURS.between(lastClaimTime, now) >= 24;
    }

    private Instant getLastDailyTimestamp(String userID) throws SQLException {
        String SQL_QUERY = "SELECT last_daily FROM eco_table WHERE user_id = ?";

        try (Connection con = DBSetup.getConnection();
             PreparedStatement pst = con.prepareStatement(SQL_QUERY)) {
            pst.setString(1, userID);
            try (ResultSet rs = pst.executeQuery()) {
                if (rs.next()) {
                    return rs.getTimestamp("last_daily").toInstant();
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        return Instant.MIN;
    }

    private void updateLastDailyTimestamp(String userID) throws SQLException {
        String SQL_UPDATE = "UPDATE eco_table SET last_daily = CURRENT_TIMESTAMP WHERE user_id = ?";

        try (Connection con = DBSetup.getConnection();
             PreparedStatement pst = con.prepareStatement(SQL_UPDATE)) {
            pst.setString(1, userID);
            pst.executeUpdate();
        }
    }
}
