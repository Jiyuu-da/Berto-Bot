package org.example.listeners;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.internal.entities.UserImpl;
import org.example.listeners.db.DBSetup;
import org.example.listeners.db.Economy;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;


public class LeaderBoard extends ListenerAdapter {
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
        String userID = event.getUser().getId();

        if(command.equalsIgnoreCase("leaderboard")) {
            try {
                User user = event.getUser();
                List<Economy> users = DBSetup.getSortedBalance();

                EmbedBuilder embed = new EmbedBuilder();

                embed.setTitle("BertoBot Leaderboard :coin:");

                StringBuilder descriptionBuilder = new StringBuilder();

                int maxBalanceLength = 0;
                int maxIdLength = 0;
                for (Economy i : users) {
                    String balance = String.valueOf((int) i.getUser_bal());
                    String id = String.valueOf(i.getUser_id());
                    maxBalanceLength = Math.max(maxBalanceLength, balance.length());
                    maxIdLength = Math.max(maxIdLength, id.length());
                }



                for (Economy i : users) {
                    String balance = String.valueOf((int) i.getUser_bal());
                    String id = String.valueOf(i.getUser_id());

                    balance = String.format("%" + maxBalanceLength + "s", balance);


                    descriptionBuilder.append("**`").append(balance).append("`** :coin:").append("<@!").append(id).append(">").append("\n");
                }


                embed.setDescription(String.valueOf(descriptionBuilder));
                embed.setFooter("Requested by " + user.getEffectiveName());
                embed.setColor(constants.color);
                event.replyEmbeds(embed.build()).queue();



            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        } else {
            event.reply("Command under maintenance").setEphemeral(true).queue();
        }
        super.onSlashCommandInteraction(event);
    }
}
