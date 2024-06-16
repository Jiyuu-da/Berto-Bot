package org.example.listeners;

import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import org.example.listeners.db.DBSetup;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class SetCoins extends ListenerAdapter {
    private static final String DB_URL = "jdbc:sqlite:economy.db";

    @Override
    public void onSlashCommandInteraction(SlashCommandInteractionEvent event) {

        String command = event.getName();
        String kiraID = event.getUser().getId();

        if(command.equalsIgnoreCase("set_coins")) {
            if(kiraID.equalsIgnoreCase("576834455306633216")) {

                OptionMapping optionUser = event.getOption("user");
                OptionMapping optionAmount = event.getOption("amount");

                long amount = optionAmount.getAsLong();
                String userID = optionUser.getAsUser().getId();

                try(Connection connection = DriverManager.getConnection(DB_URL)) {
                    DBSetup.updateBalanceInDatabase(userID, amount);
                    event.reply("coins set to " + amount + " :coin:").setEphemeral(true).queue();
                } catch (SQLException e) {
                    throw new RuntimeException(e);
                }
            } else {
                event.reply("you do not have the permission to use this command").setEphemeral(true).queue();
            }
        }
    }
}
