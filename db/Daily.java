package org.example.listeners.db;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.Instant;
import java.time.temporal.ChronoUnit;

public class Daily extends ListenerAdapter {
    private static final Logger logger = LoggerFactory.getLogger(Daily.class);

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

    @Override
    public void onSlashCommandInteraction(SlashCommandInteractionEvent event) {
        String command = event.getName();

        if (command.equalsIgnoreCase("daily")) {
            String userId = event.getUser().getId();

            try {
                if(BankCreate.hasAccount(userId)) {
                    int amount = (int)(Math.random()*251) + 50;

                    try {
                        if (canClaimDaily(userId)) {

                            updateLastDailyTimestamp(userId);
                            // Code to give coins to the user
                            logger.info("User {} claimed daily reward.", userId);
                            int userBalance = DBSetup.getBalanceFromDatabase(userId);
                            int updatedBalance = userBalance + amount;
                            DBSetup.updateBalanceInDatabase(userId, updatedBalance);

                            event.reply("You've claimed your daily reward of " + amount + " coins! :coin:").setEphemeral(false).queue();
                        } else {
                            Instant lastClaimTime = getLastDailyTimestamp(userId);
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

    private boolean canClaimDaily(String userId) throws SQLException {
        Instant lastClaimTime = getLastDailyTimestamp(userId);
        Instant now = Instant.now();
        return ChronoUnit.HOURS.between(lastClaimTime, now) >= 24;
    }

    private Instant getLastDailyTimestamp(String userId) throws SQLException {
        String SQL_QUERY = "SELECT last_daily FROM eco_table WHERE user_id = ?";

        try (Connection con = ds.getConnection();
             PreparedStatement pst = con.prepareStatement(SQL_QUERY)) {
            pst.setString(1, userId);
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

    private void updateLastDailyTimestamp(String userId) throws SQLException {
        String SQL_UPDATE = "UPDATE eco_table SET last_daily = NOW() WHERE user_id = ?";

        try (Connection con = ds.getConnection();
             PreparedStatement pst = con.prepareStatement(SQL_UPDATE)) {
            pst.setString(1, userId);
            pst.executeUpdate();
        }
    }
}
