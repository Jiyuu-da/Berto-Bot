package org.example.listeners.db;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import org.example.listeners.db.shop.Items;

import javax.swing.text.html.Option;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.Instant;
import java.time.temporal.ChronoUnit;

public class Steal extends ListenerAdapter {
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
        String stealerID = event.getUser().getId();

        OptionMapping optionUser = event.getOption("user");
        User innocent = optionUser.getAsUser();
        String innocentID = innocent.getId();


        if(command.equalsIgnoreCase("steal")) {
            try {
                if(BankCreate.hasAccount(stealerID)) {
                    if(BankCreate.hasAccount(innocentID)) {
                        if(canSteal(stealerID)){
                            if(Items.hasShield(innocent) == 0) {
                                if(DBSetup.getBalanceFromDatabase(stealerID) > 200) {
                                    int winningChance = (int)(Math.random()*100) + 1;
                                    double amountChance = (Math.random()*26) + 14;
                                    if(winningChance <= 0) {
                                        int amountStolen = (int)Math.round((amountChance/100)*DBSetup.getBalanceFromDatabase(innocentID));

                                        int updatedBalance = amountStolen + DBSetup.getBalanceFromDatabase(stealerID);
                                        int reducedBalanceForInnocent = DBSetup.getBalanceFromDatabase(innocentID) - amountStolen;

                                        updateLastStealTimeStamp(stealerID);
                                        DBSetup.updateBalanceInDatabase(stealerID, updatedBalance);
                                        DBSetup.updateBalanceInDatabase(innocentID, reducedBalanceForInnocent);

                                        event.reply("You successfully stole " + amountStolen + " coins :coin: from " + innocent.getAsMention()).setEphemeral(false).queue();
                                    } else {
                                        int roastChance = (int)(Math.random()*3) + 1;
                                        String[] roasts = {
                                                "Looks like someone's criminal career was just a short-lived adventure.",
                                                "When you try to rob a place without checking if it's open or closed...",
                                                "Note to self: 'How to Rob 101' doesn't have a chapter on 'Getting Caught Immediately.'",
                                                "Breaking news: Local aspiring robber discovers the '1-Star Rating' for his escape plan.",
                                                "When your disguise is a nametag that says 'Hello, I'm Not a Robber.'",
                                                "They say crime doesn't pay, and apparently, it also doesn't account for security cameras.",
                                                "That moment when your 'Getaway Vehicle' is actually just a kid's tricycle.",
                                                "Turns out robbing a police station isn't the best way to avoid getting caught.",
                                                "When you realize your 'black mask' is just permanent marker and not actually inconspicuous.",
                                                "Remember, folks, practice makes perfect, but apparently, not in the art of robbing.",
                                        };
                                        String roastGiven = roasts[roastChance];
                                        int amountFined = (int)Math.round((amountChance/100)*DBSetup.getBalanceFromDatabase(stealerID));
                                        int reducedBalanceForStealer = DBSetup.getBalanceFromDatabase(stealerID) - amountFined;

                                        updateLastStealTimeStamp(stealerID);
                                        DBSetup.updateBalanceInDatabase(stealerID, reducedBalanceForStealer);
                                        event.reply(roastGiven +"You tried to steal from " + innocent.getAsMention() + " and were fined " + amountFined + " coins :coin:").setEphemeral(false).queue();
                                    }
                                } else {
                                    event.reply("You need at least 200 coins :coin: to use /steal").setEphemeral(true).queue();
                                }
                            } else {
                                Instant now = Instant.now();
                                Instant shieldExpiry = Items.getShieldExpiry(innocentID);
                                long remainingMinutes = ChronoUnit.MINUTES.between(now, shieldExpiry);

                                long hoursRemaining = remainingMinutes / 60;
                                long minutesRemaining = remainingMinutes % 60;
                                event.reply(innocent.getAsMention() + " has a shield for "+hoursRemaining+"h "+minutesRemaining+"m you cannot steal from them.").setEphemeral(false).queue();
                            }

                        } else {
                            Instant currentTime = Instant.now();
                            Instant nextClaimTime = getLastStealTimeStamp(stealerID).plus(15, ChronoUnit.HOURS);
                            long remainingMinutes = ChronoUnit.MINUTES.between(currentTime, nextClaimTime);

                            long hoursRemaining = remainingMinutes / 60;
                            long minutesRemaining = remainingMinutes % 60;
                            event.reply("You need to wait " + (hoursRemaining) + "h " + minutesRemaining+"m before stealing again.").setEphemeral(true).queue();
                        }

                    } else {
                        event.reply(innocent.getEffectiveName() + " does not have a bank account, you can not steal from them.").setEphemeral(true).queue();
                    }

                } else {
                    event.reply("This command is under maintenance, you do not have the perms to use it").setEphemeral(true).queue();
                }
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }
        super.onSlashCommandInteraction(event);
    }


    private boolean canSteal(String stealerID) {
        Instant lastClaimTime = getLastStealTimeStamp(stealerID);
        Instant now = Instant.now();
        return ChronoUnit.HOURS.between(lastClaimTime, now) >= 15;
    }

    private Instant getLastStealTimeStamp(String stealerID) {
        String SQL_QUERY = "SELECT last_steal FROM eco_table WHERE user_id = ?";

        try (Connection con = ds.getConnection();
             PreparedStatement pst = con.prepareStatement(SQL_QUERY)) {
            pst.setString(1, stealerID);
            try (ResultSet rs = pst.executeQuery()) {
                if (rs.next()) {
                    return rs.getTimestamp("last_steal").toInstant();
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        return Instant.MIN;
    }

    private void updateLastStealTimeStamp(String stealerID) throws SQLException {
        String SQL_UPDATE = "UPDATE eco_table SET last_steal = NOW() WHERE user_id = ?";

        try (Connection con = ds.getConnection();
             PreparedStatement pst = con.prepareStatement(SQL_UPDATE)) {
            pst.setString(1, stealerID);
            pst.executeUpdate();
        }
    }
}
