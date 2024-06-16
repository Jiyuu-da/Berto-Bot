package org.example.listeners.db;

import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.example.listeners.coinflip;
import org.slf4j.Logger;
import java.sql.SQLException;
import org.slf4j.LoggerFactory;


public class CheckBalance extends ListenerAdapter {
    private static final Logger logger = LoggerFactory.getLogger(coinflip.class);

    private String user_id;
    private double user_bal;
    @Override
    public void onSlashCommandInteraction(SlashCommandInteractionEvent event) {
        String command = event.getName();
        String userID = event.getUser().getId();
        if (command.equalsIgnoreCase("check")) {
            try {
                if (event.getOption("user") != null) {
                    String givenUserID = event.getOption("user").getAsUser().getId();
                    long userBalance = DBSetup.getBalanceFromDatabase(givenUserID);
                    event.reply(event.getOption("user").getAsUser().getAsMention() + " has " + userBalance + " coins :coin:")
                            .setEphemeral(false).queue();
                } else {
                    long userBalance = DBSetup.getBalanceFromDatabase(userID);
                    event.reply("You have " + userBalance + " coins :coin:").setEphemeral(true).queue();
                }
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }
        super.onSlashCommandInteraction(event);
    }
    }

