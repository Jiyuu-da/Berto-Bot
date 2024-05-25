package org.example.listeners;

import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

public class ping extends ListenerAdapter {
    @Override
    public void onSlashCommandInteraction(SlashCommandInteractionEvent event) {
        String command = event.getName();
        if(command.equalsIgnoreCase("ping")) {
            String user = event.getUser().getAsMention();
            event.reply("pong").queue();
        }
        super.onSlashCommandInteraction(event);
    }
}
