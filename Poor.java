//package org.example.listeners;
//
//import com.zaxxer.hikari.HikariConfig;
//import com.zaxxer.hikari.HikariDataSource;
//import net.dv8tion.jda.api.entities.User;
//import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
//import net.dv8tion.jda.api.hooks.ListenerAdapter;
//import org.example.listeners.db.DBSetup;
//
//import java.util.Random;
//
//public class Poor extends ListenerAdapter {
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
//
//        if(command.equalsIgnoreCase("poor")) {
//            try {
//                User user = event.getUser();
//
//                int userBalance = DBSetup.getBalanceFromDatabase(userID);
//                Random random = new Random();
//
//                int money = (int)((Math.random()*10000) + 1);
//                int probs = (int)((Math.random()*100) + 1);
//
//                String[] cheapResponses = {"Ye le gareeb", "College ke bahar",
//                                            "Aaj tere ghar paneer aayega, ye le",
//                                            "Aur chahiye toh maang lena",
//                                          };
//                String[] goodResponses = {"Aaj russian pakki :flag_ru:", "Engineering chodh de ab :money_mouth:",
//                                          "Happy B'day :cake:", "Behen ke lode :eggplant:"
//                                        };
//
//                int responseIdxCheap = (int)(Math.random() * cheapResponses.length);
//                int responseIdxGood = (int)(Math.random() * goodResponses.length);
//
//                if(probs == 1) {
//                    if(money > 5000) {
//                        int updatedAmount = (int)(userBalance + money);
//                        DBSetup.updateBalanceInDatabase(userID, updatedAmount);
//                        event.reply( user.getAsMention() + " " + goodResponses[responseIdxGood] +" " + money+ " coins :coin:").setEphemeral(false).queue();
//                    }
//                    else {
//                        int updatedAmount = (int)(userBalance + money);
//                        DBSetup.updateBalanceInDatabase(userID, updatedAmount);
//                        event.reply( user.getAsMention() + " " + cheapResponses[responseIdxCheap] + " " + money + " coins :coin:").setEphemeral(false).queue();
//                    }
//                }
//                else {
//                    event.reply(user.getAsMention() + " bsdk ghar bhaith jaa").setEphemeral(false).queue();
//                }
//
//            } catch (Exception e) {
//                throw new RuntimeException(e);
//            }
//        }
//        super.onSlashCommandInteraction(event);
//    }

package org.example.listeners;

import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.example.listeners.db.DBSetup;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Random;

public class Poor extends ListenerAdapter {
    private static final String DB_URL = "jdbc:sqlite:your_database_path.db";

    @Override
    public void onSlashCommandInteraction(SlashCommandInteractionEvent event) {
        String command = event.getName();
        String userID = event.getUser().getId();

        if (command.equalsIgnoreCase("poor")) {
            try (Connection connection = DriverManager.getConnection(DB_URL)) {
                User user = event.getUser();

                int userBalance = DBSetup.getBalanceFromDatabase(userID);
                Random random = new Random();

                int money = random.nextInt(10000) + 1;
                int probs = random.nextInt(100) + 1;

                String[] cheapResponses = {
                        "Ye le gareeb", "College ke bahar",
                        "Aaj tere ghar paneer aayega, ye le",
                        "Aur chahiye toh maang lena"
                };
                String[] goodResponses = {
                        "Aaj russian pakki :flag_ru:", "Engineering chodh de ab :money_mouth:",
                        "Happy B'day :cake:", "Behen ke lode :eggplant:"
                };

                int responseIdxCheap = random.nextInt(cheapResponses.length);
                int responseIdxGood = random.nextInt(goodResponses.length);

                if (probs == 1) {
                    int updatedAmount = userBalance + money;
                    DBSetup.updateBalanceInDatabase(userID, updatedAmount);
                    if (money > 5000) {
                        event.reply(user.getAsMention() + " " + goodResponses[responseIdxGood] + " " + money + " coins :coin:").setEphemeral(false).queue();
                    } else {
                        event.reply(user.getAsMention() + " " + cheapResponses[responseIdxCheap] + " " + money + " coins :coin:").setEphemeral(false).queue();
                    }
                } else {
                    event.reply(user.getAsMention() + " bsdk ghar bhaith jaa").setEphemeral(false).queue();
                }
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }
        super.onSlashCommandInteraction(event);
    }
}

