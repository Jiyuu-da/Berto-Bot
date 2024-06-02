package org.example.listeners;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.User;
import org.example.listeners.db.BankCreate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import org.example.listeners.db.DBSetup;

public class coinflip extends ListenerAdapter {
    private static final Logger logger = LoggerFactory.getLogger(coinflip.class);

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

    @Override
    public void onSlashCommandInteraction(SlashCommandInteractionEvent event) {
        String command = event.getName();

        if (command.equalsIgnoreCase("cointoss")) {
            String userID = event.getUser().getId();
            String user = event.getUser().getAsMention();

            OptionMapping typeOption = event.getOption("type");
            OptionMapping betOption = event.getOption("bet");

            String type = typeOption.getAsString().toLowerCase();
            int bet = betOption.getAsInt();

            EmbedBuilder embed = new EmbedBuilder();


            try {
                if (BankCreate.hasAccount(userID)) {
                    try {
                        if (bet > 0) {
                            int userBalance = DBSetup.getBalanceFromDatabase(userID);
                            int difference = bet - userBalance;
                            if ((bet <= userBalance)) {

                                User user_e = event.getUser();

                                boolean won;
                                double random = Math.random();
                                String outcome;
                                if (random < 0.5) {
                                    outcome = "heads";
                                } else {
                                    outcome = "tails";
                                }

                                if (outcome.equalsIgnoreCase(type)) {
                                    won = true;
                                } else {
                                    won = false;
                                }

                                int updatedBalance;
                                if (won) {
                                    updatedBalance = userBalance + bet;
                                    DBSetup.updateBalanceInDatabase(userID, updatedBalance);
                                    logger.info("Amount updated : {}", userID);

                                    embed.setTitle("Coinflip");
                                    embed.setDescription(user + " You Won and doubled your bet, you now have " + (userBalance + bet) + " coins :coin:");
                                    embed.setColor(constants.WIN_COLOR);
                                    event.replyEmbeds(embed.build()).queue();

                                } else {
                                    updatedBalance = userBalance - bet;
                                    DBSetup.updateBalanceInDatabase(userID, updatedBalance);

                                    embed.setTitle("Coinflip");
                                    embed.setDescription(user + " You Lost.. you now have " + (userBalance - bet) + " coins :coin:");
                                    embed.setColor(constants.LOST_COLOR);
                                    event.replyEmbeds(embed.build()).queue();

                                    event.reply(user + " You Lost.. you now have " + (userBalance - bet) + " coins :coin:").setEphemeral(false).queue();

                                }
                            } else {
                                event.reply("You do not have sufficient balance missing " + difference + " coins :coin:").setEphemeral(true).queue();
                            }
                        } else {
                            event.reply("Invalid Bet amount").setEphemeral(true).queue();
                        }
                    } catch (SQLException e) {
                        throw new RuntimeException(e);
                    }

                } else {
                    event.reply("You do not have a bank account, create one by /bankcreate").setEphemeral(true).queue();
                }

                super.onSlashCommandInteraction(event);
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }
    }
}


