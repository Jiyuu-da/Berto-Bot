
package org.example.listeners;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.User;
import org.example.Main;
import org.example.listeners.db.BankCreate;
import org.example.listeners.db.DBSetup;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class coinflip extends ListenerAdapter {
    private static final Logger logger = LoggerFactory.getLogger(coinflip.class);

    private static final String DB_URL = "jdbc:sqlite:economy.db";

    @Override
    public void onSlashCommandInteraction(SlashCommandInteractionEvent event) {
        String command = event.getName();
        String userID = event.getUser().getId();

        if(Main.maintenance && !userID.equals("576834455306633216")) {
            event.reply("BertoBot is under maintenance.").setEphemeral(true).queue();
            return;
        }

        if (command.equalsIgnoreCase("cointoss")) {
            String user = event.getUser().getAsMention();

            OptionMapping typeOption = event.getOption("type");
            OptionMapping betOption = event.getOption("bet");

            String type = typeOption.getAsString().toLowerCase();
            long bet = betOption.getAsLong();

            EmbedBuilder embed = new EmbedBuilder();

            try (Connection connection = DriverManager.getConnection(DB_URL)) {
                if (BankCreate.hasAccount(userID)) {
                    if (bet > 0) {
                        long userBalance = DBSetup.getBalanceFromDatabase(userID);
                        long difference = bet - userBalance;
                        if (bet <= userBalance) {

                            User user_e = event.getUser();

                            boolean won;
                            double random = Math.random();
                            String outcome;
                            if (random < 0.5) {
                                outcome = "heads";
                            } else {
                                outcome = "tails";
                            }

                            won = outcome.equalsIgnoreCase(type);

                            long updatedBalance;
                            if (won) {
                                updatedBalance = userBalance + bet;
                                DBSetup.updateBalanceInDatabase(userID, updatedBalance);
                                logger.info("Amount updated : {}", userID);

                                embed.setTitle("Coinflip");
                                embed.setDescription(user + " You Won and doubled your bet, you now have " + updatedBalance + " coins :coin:");
                                embed.setColor(constants.WIN_COLOR);
                                event.replyEmbeds(embed.build()).queue();

                            } else {
                                updatedBalance = userBalance - bet;
                                DBSetup.updateBalanceInDatabase(userID, updatedBalance);

                                embed.setTitle("Coinflip");
                                embed.setDescription(user + " You Lost.. you now have " + updatedBalance + " coins :coin:");
                                embed.setColor(constants.LOST_COLOR);
                                event.replyEmbeds(embed.build()).queue();
                            }
                        } else {
                            event.reply("You do not have sufficient balance, missing " + difference + " coins :coin:").setEphemeral(true).queue();
                        }
                    } else {
                        event.reply("Invalid Bet amount").setEphemeral(true).queue();
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
