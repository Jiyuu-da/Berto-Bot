package org.example.listeners;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

import net.dv8tion.jda.api.interactions.commands.OptionMapping;


public class avatar extends ListenerAdapter {
    @Override
    public void onSlashCommandInteraction(SlashCommandInteractionEvent event) {
        String command = event.getName();

        if(command.equalsIgnoreCase("avatar")) {
            OptionMapping userO = event.getOption("user");

            User user = event.getUser();

            EmbedBuilder embed = new EmbedBuilder();
            embed.setColor(constants.color);
            embed.setFooter("Requested by " + user.getName(), user.getAvatarUrl());

            if(userO !=null) {
                embed.setTitle(userO.getAsUser().getEffectiveName() + "'s Avatar", userO.getAsUser().getAvatarUrl());

                embed.setTitle( userO.getAsUser().getEffectiveName() + "'s Avatar", userO.getAsUser().getAvatarUrl());
                embed.setImage(userO.getAsUser().getAvatarUrl());
            }
            else {
                embed.setTitle(user.getEffectiveName()+"'s Avatar", event.getUser().getAvatarUrl());
                embed.setImage(event.getUser().getAvatarUrl());
            }

            event.replyEmbeds(embed.build()).queue();

        }
        super.onSlashCommandInteraction(event);
    }

}
