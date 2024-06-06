package org.example.listeners;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import org.example.listeners.db.BankCreate;
import org.example.listeners.db.DBSetup;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class SevenUp extends ListenerAdapter {
    private static final String DB_URL = "jdbc:sqlite:economy.db";

    @Override
    public void onSlashCommandInteraction(SlashCommandInteractionEvent event) {
        String command = event.getName();
        String userID = event.getUser().getId();

        if (command.equalsIgnoreCase("7up7down")) {
            final double MULTIPLIER_NON_SEVEN = 2.4;
            final double MULTIPLIER_SEVEN = 6;


            OptionMapping optionBet = event.getOption("bet");
            OptionMapping optionNum = event.getOption("sum");

            if (optionNum == null) {
                event.reply("You need to specify 'bet' and 'sum'").setEphemeral(true).queue();
                return;
            }

            String sum = optionNum.getAsString();
            String displaySum;

            if (sum.equalsIgnoreCase("down")) {
                displaySum = "7 down";
            } else if (sum.equalsIgnoreCase("up")) {
                displaySum = "7 up";
            } else {
                displaySum = "7";
            }

            int num = 0;
            int bet = optionBet.getAsInt();

            EmbedBuilder embed = new EmbedBuilder();
            try (Connection connection = DriverManager.getConnection(DB_URL)) {
                if (BankCreate.hasAccount(userID)) {
                    try {
                        if (sum.equalsIgnoreCase("down")) {
                            num = 5;
                        } else if (sum.equalsIgnoreCase("up")) {
                            num = 10;
                        } else if (sum.equalsIgnoreCase("7")) {
                            num = 7;
                        }
                        int userBalance = DBSetup.getBalanceFromDatabase(userID);
                        int diff = bet - userBalance;
                        if (bet > 0) {
                            if (bet <= userBalance) {
                                User user = event.getUser();

                                int outcome1 = (int) (Math.random() * 6 + 1);
                                int outcome2 = (int) (Math.random() * 6 + 1);
                                int result = outcome1 + outcome2;

                                boolean win;

                                if ((result > 7 && num > 7) || (result < 7 && num < 7) || (result == 7 && num == 7)) {
                                    win = true;
                                } else {
                                    win = false;
                                }

                                String displayResult;

                                if (result > 7) {
                                    displayResult = "7 up";
                                } else if (result < 7) {
                                    displayResult = "7 down";
                                } else {
                                    displayResult = "7";
                                }

                                int updatedAmount;

                                if (win) {
                                    if (result == 7) {
                                        updatedAmount = (int) (userBalance + MULTIPLIER_SEVEN * bet);
                                        DBSetup.updateBalanceInDatabase(userID, updatedAmount);
                                    } else {
                                        updatedAmount = (int) (userBalance + MULTIPLIER_NON_SEVEN * bet);
                                        DBSetup.updateBalanceInDatabase(userID, updatedAmount);
                                    }

                                    embed.setColor(constants.WIN_COLOR);
                                    embed.setTitle("7 up 7 Down");
                                    embed.setDescription(user.getAsMention() + " You picked **" + displaySum + "**, it was **" + displayResult + "** You Won! You now have " + updatedAmount + " coins :coin:");
                                    event.replyEmbeds(embed.build()).queue();

                                } else {
                                    updatedAmount = userBalance - bet;
                                    DBSetup.updateBalanceInDatabase(userID, updatedAmount);

                                    embed.setColor(constants.LOST_COLOR);
                                    embed.setTitle("7 up 7 Down");
                                    embed.setDescription(user.getAsMention() + " You picked **" + displaySum + "**, it was **" + displayResult + "** You Lost... You now have " + updatedAmount + " coins :coin:");
                                    event.replyEmbeds(embed.build()).queue();
                                }

                            } else {
                                event.reply("You do not have sufficient balance, missing " +
                                        diff + " coins :coin:").setEphemeral(true).queue();
                            }
                        } else {
                            event.reply("Invalid Input").setEphemeral(true).queue();
                        }
                    } catch (SQLException e) {
                        throw new RuntimeException(e);
                    }
                } else {
                    event.reply("You do not have a bank account");
                }
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }
    }
}

