//package org.example.listeners;
//
//import com.zaxxer.hikari.HikariConfig;
//import com.zaxxer.hikari.HikariDataSource;
//import net.dv8tion.jda.api.EmbedBuilder;
//import net.dv8tion.jda.api.entities.User;
//import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
//import net.dv8tion.jda.api.hooks.ListenerAdapter;
//import net.dv8tion.jda.api.interactions.commands.OptionMapping;
//import org.example.listeners.db.BankCreate;
//import org.example.listeners.db.DBSetup;
//
//import java.sql.SQLException;
//import java.util.Random;
//
//public class Fish extends ListenerAdapter {
//    private String user_id;
//    private double user_bal;
//    private static HikariConfig config = new HikariConfig();
//    private static HikariDataSource ds;
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
//        final double MULTIPLIER = 3.33;
//        String command = event.getName();
//        String userID = event.getUser().getId();
//
//        OptionMapping optionBet = event.getOption("bet");
//        int bet = optionBet.getAsInt();
//
//        if(command.equalsIgnoreCase("fish")) {
//
//            EmbedBuilder embed = new EmbedBuilder();
//
//            try {
//                if(BankCreate.hasAccount(userID)) {
//                    try {
//                        int userBalance = DBSetup.getBalanceFromDatabase(userID);
//                        int difference = bet - userBalance;
//                        if(bet > 0) {
//                            if(bet <= userBalance) {
//                                String[] objects = {"Ram Babu Buri... :poop:"," Crab... :crab:",
//                                                    "Shoe... :athletic_shoe:", "Plastic Bottle... :squeeze_bottle:",
//                                                     "Glass Bottle... :champagne:", "Tool Box... :toolbox:",
//                                                    "Bag... :shopping_bags:", "Candy Wrapper... :candy:",
//                                                    "Ball... :baseball:", "Boot... :boot:",
//                                                    "Hammer... :hammer:", "Can... :canned_food:",
//                                                    "Wooden Bat... :cricket_game:"};
//                                String[] fish = {"Salmon!! :fishing_pole_and_fish:", "Trout!! :fishing_pole_and_fish:",
//                                                "Gold Fish!! :fishing_pole_and_fish:", "Bass!! :fishing_pole_and_fish:",
//                                                "Cat Fish!! :fishing_pole_and_fish:", "Puffer Fish!! :fishing_pole_and_fish:"};
//
//                                User user = event.getUser();
//
//                                int num = (int)(Math.random()*100) + 1;
//
//                                String result;
//
//                                if(num <= 30) {
//                                    int fishChoice = (int)(Math.random()* fish.length);
//                                    result = fish[fishChoice];
//                                    int updatedAmount = (int)(userBalance + MULTIPLIER*bet);
//                                    DBSetup.updateBalanceInDatabase(userID, updatedAmount);
//
//                                    embed.setTitle("Fish");
//                                    embed.setDescription( user.getAsMention() + " You caught a " + result + " and increased your bet, you now have " + updatedAmount+ " coins :coin:");
//                                    embed.setColor(constants.WIN_COLOR);
//                                    event.replyEmbeds(embed.build()).queue();
//
//                                } else {
//                                    int objectChoice = (int)(Math.random()*objects.length);
//                                    result = objects[objectChoice];
//                                    int updatedAmount = userBalance - bet;
//                                    DBSetup.updateBalanceInDatabase(userID, updatedAmount);
//
//                                    embed.setTitle("Fish");
//                                    embed.setDescription( user.getAsMention() + " You caught a " + result + " you lost your bet, you now have " + updatedAmount + " coins :coin:");
//                                    embed.setColor(constants.LOST_COLOR);
//                                    event.replyEmbeds(embed.build()).queue();
//
////                                    event.reply( user.getAsMention() + " You caught a " + result + " you lost your bet, you now have " + updatedAmount + " coins :coin:").setEphemeral(false).queue();
//                                }
//                            } else {
//                                event.reply("You do not have sufficient balance, missing "+
//                                        difference+ " coins :coin:").setEphemeral(true).queue();
//                            }
//                        } else {
//                            event.reply("Invalid Input").setEphemeral(true).queue();
//                        }
//
//                    } catch (SQLException e) {
//                        throw new RuntimeException(e);
//                    }
//                } else {
//                    event.reply("You do not have a bank account, create one by /bankcreate").setEphemeral(true).queue();
//                }
//            } catch (SQLException e) {
//                throw new RuntimeException(e);
//            }
//
//        }
//        super.onSlashCommandInteraction(event);
//    }
//}

package org.example.listeners;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import org.example.listeners.db.BankCreate;
import org.example.listeners.db.DBSetup;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.SQLException;

public class Fish extends ListenerAdapter {
    private static final Logger logger = LoggerFactory.getLogger(Fish.class);

    @Override
    public void onSlashCommandInteraction(SlashCommandInteractionEvent event) {
        final double MULTIPLIER = 3.33;
        String command = event.getName();
        String userID = event.getUser().getId();

        OptionMapping optionBet = event.getOption("bet");
        int bet = optionBet.getAsInt();

        if (command.equalsIgnoreCase("fish")) {

            EmbedBuilder embed = new EmbedBuilder();

            try {
                if (BankCreate.hasAccount(userID)) {
                    try {
                        int userBalance = DBSetup.getBalanceFromDatabase(userID);
                        int difference = bet - userBalance;
                        if (bet > 0) {
                            if (bet <= userBalance) {
                                String[] objects = {"Ram Babu Buri... :poop:", "Crab... :crab:",
                                        "Shoe... :athletic_shoe:", "Plastic Bottle... :squeeze_bottle:",
                                        "Glass Bottle... :champagne:", "Tool Box... :toolbox:",
                                        "Bag... :shopping_bags:", "Candy Wrapper... :candy:",
                                        "Ball... :baseball:", "Boot... :boot:",
                                        "Hammer... :hammer:", "Can... :canned_food:",
                                        "Wooden Bat... :cricket_game:"};
                                String[] fish = {"Salmon!! :fishing_pole_and_fish:", "Trout!! :fishing_pole_and_fish:",
                                        "Gold Fish!! :fishing_pole_and_fish:", "Bass!! :fishing_pole_and_fish:",
                                        "Cat Fish!! :fishing_pole_and_fish:", "Puffer Fish!! :fishing_pole_and_fish:"};

                                User user = event.getUser();

                                int num = (int) (Math.random() * 100) + 1;

                                String result;

                                if (num <= 30) {
                                    int fishChoice = (int) (Math.random() * fish.length);
                                    result = fish[fishChoice];
                                    int updatedAmount = (int) (userBalance + MULTIPLIER * bet);
                                    DBSetup.updateBalanceInDatabase(userID, updatedAmount);

                                    embed.setTitle("Fish");
                                    embed.setDescription(user.getAsMention() + " You caught a " + result + " and increased your bet, you now have " + updatedAmount + " coins :coin:");
                                    embed.setColor(constants.WIN_COLOR);
                                    event.replyEmbeds(embed.build()).queue();

                                } else {
                                    int objectChoice = (int) (Math.random() * objects.length);
                                    result = objects[objectChoice];
                                    int updatedAmount = userBalance - bet;
                                    DBSetup.updateBalanceInDatabase(userID, updatedAmount);

                                    embed.setTitle("Fish");
                                    embed.setDescription(user.getAsMention() + " You caught a " + result + " you lost your bet, you now have " + updatedAmount + " coins :coin:");
                                    embed.setColor(constants.LOST_COLOR);
                                    event.replyEmbeds(embed.build()).queue();
                                }
                            } else {
                                event.reply("You do not have sufficient balance, missing " +
                                        difference + " coins :coin:").setEphemeral(true).queue();
                            }
                        } else {
                            event.reply("Invalid Input").setEphemeral(true).queue();
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
}
