package org.example.listeners;

import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import org.example.Main;
import org.example.listeners.db.BankCreate;
import org.example.listeners.db.DBSetup;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class MoneyTransfer extends ListenerAdapter {
    public static final String DB_URL = "jdbc:sqlite:economy.db";
    @Override
    public void onSlashCommandInteraction(SlashCommandInteractionEvent event) {

        String command = event.getName();
        String userID = event.getUser().getId();

        if(Main.maintenance && !userID.equals("576834455306633216")) {
            event.reply("BertoBot is under maintenance.").setEphemeral(true).queue();
            return;
        }

        if(command.equalsIgnoreCase("transfer")) {
            OptionMapping receiver = event.getOption("user");
            OptionMapping optionAmount = event.getOption("amount");

            User sender = event.getUser();

            String receiverID = receiver.getAsUser().getId();
            String senderID = sender.getId();

            long amount = optionAmount.getAsLong();


            if(receiver == null) {
                event.reply("please provide a user").setEphemeral(true).queue();
                return;
            }

            try(Connection connection = DriverManager.getConnection(DB_URL)) {
                if(BankCreate.hasAccount(senderID) && BankCreate.hasAccount(receiverID)) {
                    if(amount > 0) {
                        if(amount <= DBSetup.getBalanceFromDatabase(senderID)) {
                            if(!receiverID.equalsIgnoreCase(senderID)) {
                                long senderBal = DBSetup.getBalanceFromDatabase(senderID);
                                long receiverBal = DBSetup.getBalanceFromDatabase(receiverID);

                                DBSetup.updateBalanceInDatabase(senderID, senderBal - amount);
                                DBSetup.updateBalanceInDatabase(receiverID, receiverBal + amount);

                                event.reply(sender.getEffectiveName() + " transferred " + amount + " :coin: to " + receiver.getAsUser().getAsMention()).setEphemeral(false).queue();

                            } else {
                                event.reply("Can't transfer to self").setEphemeral(true).queue();
                            }
                        } else {
                            event.reply("You do not have sufficient balance").setEphemeral(true).queue();
                        }
                    } else {
                        event.reply("invalid amount").setEphemeral(true).queue();
                    }
                } else {
                    event.reply("One of you don't have an account").setEphemeral(true).queue();
                }
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }

        }
        super.onSlashCommandInteraction(event);
    }
}
