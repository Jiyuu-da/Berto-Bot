package org.example.listeners;

import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import org.example.Main;


public class sayCommand extends ListenerAdapter {
    @Override
    public void onSlashCommandInteraction(SlashCommandInteractionEvent event) {
        String command = event.getName();
        String userID = event.getUser().getId();

        if(Main.maintenance && !userID.equals("576834455306633216")) {
            event.reply("BertoBot is under maintenance.").setEphemeral(true).queue();
            return;
        }

            if(command.equalsIgnoreCase("say")) {
                OptionMapping optionMap = event.getOption("message");

                String msg = optionMap.getAsString();

                event.getChannel().sendMessage(msg).queue();
                event.deferReply().setEphemeral(true).setContent("Msg sent").queue();
            }

        super.onSlashCommandInteraction(event);
    }

}




