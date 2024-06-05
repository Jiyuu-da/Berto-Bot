//package org.example.listeners;
//
//import net.dv8tion.jda.api.EmbedBuilder;
//import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
//import net.dv8tion.jda.api.hooks.ListenerAdapter;
//import com.zaxxer.hikari.HikariConfig;
//import com.zaxxer.hikari.HikariDataSource;
//import net.dv8tion.jda.api.entities.User;
//import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
//import net.dv8tion.jda.api.hooks.ListenerAdapter;
//import net.dv8tion.jda.internal.entities.UserImpl;
//import org.example.listeners.db.DBSetup;
//import org.example.listeners.db.Economy;
//
//import java.sql.SQLException;
//import java.util.ArrayList;
//import java.util.List;
//import java.util.Random;
//
//
//public class LeaderBoard extends ListenerAdapter {
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
//        if(command.equalsIgnoreCase("leaderboard")) {
//            try {
//                User user = event.getUser();
//                List<Economy> users = DBSetup.getSortedBalance();
//
//                EmbedBuilder embed = new EmbedBuilder();
//
//                StringBuilder descriptionBuilder = new StringBuilder();
//
//                int maxBalanceLength = 0;
//                int maxIdLength = 0;
//                for (Economy i : users) {
//                    String balance = String.valueOf((int) i.getUser_bal());
//                    String id = String.valueOf(i.getUser_id());
//                    maxBalanceLength = Math.max(maxBalanceLength, balance.length());
//                    maxIdLength = Math.max(maxIdLength, id.length());
//                }
//
//                int pos = 1;
//
//                for (Economy i : users) {
//                    String balance = String.valueOf((int) i.getUser_bal());
//                    String id = String.valueOf(i.getUser_id());
//
//                    balance = String.format("%" + maxBalanceLength + "s", balance);
//                    String emote;
//
//                    if(pos == 1) {
//                        emote = ":first_place:";
//                    } else if (pos==2) {
//                        emote = ":second_place:";
//                    } else if (pos == 3) {
//                        emote = ":third_place:";
//                    } else {
//                        emote = ":coin:";
//                    }
//
//                    descriptionBuilder.append(pos + ". ").append("**`").append(balance).append("`** " + emote + "\t").append("<@!").append(id).append(">").append("\n");
//                    pos +=1;
//                }
//
//                embed.setTitle("Top 10 Leaderboard :coin:");
//                embed.setDescription(String.valueOf(descriptionBuilder));
//                embed.setFooter("From all the servers.");
//                embed.setColor(constants.color);
//                event.replyEmbeds(embed.build()).queue();
//
//
//
//            } catch (SQLException e) {
//                throw new RuntimeException(e);
//            }
//        }
//        super.onSlashCommandInteraction(event);
//    }
//}

package org.example.listeners;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.entities.User;
import org.example.listeners.db.DBSetup;
import org.example.listeners.db.Economy;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.List;

public class LeaderBoard extends ListenerAdapter {

    private static final String DB_URL = "jdbc:sqlite:Files/bot/economy.db";

    @Override
    public void onSlashCommandInteraction(SlashCommandInteractionEvent event) {
        String command = event.getName();
        String userID = event.getUser().getId();

        if (command.equalsIgnoreCase("leaderboard")) {
            try (Connection connection = DriverManager.getConnection(DB_URL)) {
                User user = event.getUser();
                List<Economy> users = DBSetup.getSortedBalance();

                EmbedBuilder embed = new EmbedBuilder();
                StringBuilder descriptionBuilder = new StringBuilder();

                int maxBalanceLength = 0;
                int maxIdLength = 0;
                for (Economy i : users) {
                    String balance = String.valueOf((int) i.getUser_bal());
                    String id = String.valueOf(i.getUser_id());
                    maxBalanceLength = Math.max(maxBalanceLength, balance.length());
                    maxIdLength = Math.max(maxIdLength, id.length());
                }

                int pos = 1;
                for (Economy i : users) {
                    String balance = String.valueOf((int) i.getUser_bal());
                    String id = String.valueOf(i.getUser_id());

                    balance = String.format("%" + maxBalanceLength + "s", balance);
                    String emote;

                    if (pos == 1) {
                        emote = ":first_place:";
                    } else if (pos == 2) {
                        emote = ":second_place:";
                    } else if (pos == 3) {
                        emote = ":third_place:";
                    } else {
                        emote = ":coin:";
                    }

                    descriptionBuilder.append(pos).append(". **`").append(balance).append("`** ")
                            .append(emote).append("\t").append("<@!").append(id).append(">").append("\n");
                    pos += 1;
                }

                embed.setTitle("Top 10 Leaderboard :coin:");
                embed.setDescription(descriptionBuilder.toString());
                embed.setFooter("From all the servers.");
                embed.setColor(constants.color);
                event.replyEmbeds(embed.build()).queue();

            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }
        super.onSlashCommandInteraction(event);
    }
}
