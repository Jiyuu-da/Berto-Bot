package org.example.listeners.db;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import org.apache.http.Consts;
import org.example.listeners.constants;
import org.sqlite.core.DB;

import javax.swing.text.html.Option;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.Duration;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Random;

public class Steal extends ListenerAdapter {
    @Override
    public void onSlashCommandInteraction(SlashCommandInteractionEvent event) {
        String command = event.getName();
        String stealerID = event.getUser().getId();


        if(command.equalsIgnoreCase("steal")) {
            OptionMapping optionUser = event.getOption("user");
            User innocent = optionUser.getAsUser();
            String innocentID = innocent.getId();

            try {
                if(BankCreate.hasAccount(stealerID) && BankCreate.hasAccount(innocentID)) {
                    if(!stealerID.equalsIgnoreCase(innocentID)) {
                        if(canSteal(stealerID)) {
                            long innocentBal = DBSetup.getBalanceFromDatabase(innocentID);
                            long stealerBal = DBSetup.getBalanceFromDatabase(stealerID);

                            Random rd = new Random();
                            int chance = rd.nextInt(100) + 1;

                            EmbedBuilder embed = new EmbedBuilder();


                            double portion = rd.nextInt(10, 21) / 100.0;


                            if(chance <=5) {
                                long amountStolen = (long)(portion*innocentBal);

                                long newInnocentBal = Math.max(innocentBal - amountStolen, 0);
                                long newStealerBal = stealerBal + amountStolen;


                                DBSetup.updateBalanceInDatabase(innocentID, newInnocentBal);
                                DBSetup.updateBalanceInDatabase(stealerID, newStealerBal);

                                embed.setDescription("You successfully stole " + amountStolen + " :coin: from " + innocent.getAsMention());
                                embed.setColor(constants.WIN_COLOR);
                                event.replyEmbeds(embed.build()).queue();
                            }
                            else {
                                updateLastStealTimeStamp(stealerID);
                                long amountFined = (long)(portion*stealerBal);
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
                                int rdidx = rd.nextInt(roasts.length);
                                embed.setDescription(roasts[rdidx] + " You were fined " + amountFined + " :coin: ");
                                embed.setColor(constants.LOST_COLOR);
                                event.replyEmbeds(embed.build()).queue();

                                long newInnocentBal = innocentBal + amountFined;
                                long newStealerBal = Math.max(stealerBal - amountFined, 0);

                                DBSetup.updateBalanceInDatabase(innocentID, newInnocentBal);
                                DBSetup.updateBalanceInDatabase(stealerID, newStealerBal);
                            }
                        } else {
                            Duration remainingTime = getRemainingTime(stealerID);
                            event.reply("You can steal again in " + formatDuration(remainingTime)).setEphemeral(true).queue();
                        }
                    } else {
                        event.reply("Can't steal from self.").setEphemeral(true).queue();
                    }
                } else {
                    event.reply("One of you doesn't have an account").setEphemeral(true).queue();
                }
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }
    }


    private boolean canSteal(String stealerID) {
        Instant lastClaimTime = getLastStealTimeStamp(stealerID);
        Instant now = Instant.now();
        return ChronoUnit.HOURS.between(lastClaimTime, now) >= 15;
    }

    private Instant getLastStealTimeStamp(String stealerID) {
        String SQL_QUERY = "SELECT last_steal FROM eco_table WHERE user_id = ?";

        try (Connection con = DBSetup.getConnection();
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


    private Duration getRemainingTime(String stealerID) {
        Instant lastClaimTime = getLastStealTimeStamp(stealerID);
        Instant now = Instant.now();
        long hoursSinceLastSteal = ChronoUnit.HOURS.between(lastClaimTime, now);
        long hoursRemaining = 15 - hoursSinceLastSteal;
        return Duration.ofHours(hoursRemaining);
    }
    private String formatDuration(Duration duration) {
        long hours = duration.toHours();
        long minutes = duration.minusHours(hours).toMinutes();
        return hours + " hours and " + minutes + " minutes";
    }
    private void updateLastStealTimeStamp(String stealerID) throws SQLException {
        String SQL_UPDATE = "UPDATE eco_table SET last_steal = CURRENT_TIMESTAMP WHERE user_id = ?";

        try (Connection con = DBSetup.getConnection();
             PreparedStatement pst = con.prepareStatement(SQL_UPDATE)) {
            pst.setString(1, stealerID);
            pst.executeUpdate();
        }
    }
}


