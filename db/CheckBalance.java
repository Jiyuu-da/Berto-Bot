package org.example.listeners.db;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import org.example.listeners.coinflip;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.SQLException;

public class CheckBalance extends ListenerAdapter {
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
        String userID = event.getUser().getId();

        OptionMapping userOption = event.getOption("user");

        if(command.equalsIgnoreCase("check")) {

            if(userOption!=null) {
                User givenUserAsUser = userOption.getAsUser();
                String givenUser = userOption.getAsUser().getId();
                try {
                    int userBalance = DBSetup.getBalanceFromDatabase(givenUser);
                    event.reply(givenUserAsUser.getAsMention() + " has " + userBalance + " coins :coin:").setEphemeral(false).queue();
                } catch (SQLException e) {
                    throw new RuntimeException(e);
                }
            } else {
                try {
                    if (BankCreate.hasAccount(userID)) {
                        try {
                            int userBalance = DBSetup.getBalanceFromDatabase(userID);
                            event.reply("You have " + userBalance + " coins :coin:").setEphemeral(true).queue();
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
        }
        super.onSlashCommandInteraction(event);
    }
}
