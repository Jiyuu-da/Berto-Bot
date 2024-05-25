package org.example.listeners;
import net.dv8tion.jda.api.EmbedBuilder;

import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

import java.sql.Date;
import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

public class Help extends ListenerAdapter {
    @Override
    public void onSlashCommandInteraction(SlashCommandInteractionEvent event) {
        String command = event.getName();

        if(command.equalsIgnoreCase("help")) {

            EmbedBuilder embed = new EmbedBuilder();
            embed.setTitle("Help");
            embed.setThumbnail("https://i.pinimg.com/736x/47/4b/02/474b0216a482f49d28fb8dd9e761465d.jpg");
            embed.addField("/avatar", "shows a user's avatar", false);
            embed.addField("/afk", "set an afk reason", false);
            embed.addField("/bankcreate", "create a bank account", false);
            embed.addField("/buy", "buy items from the shop", false);
            embed.addField("/cointoss", "toss a coin", false);
            embed.addField("/fish", "try catching a fish", false);
            embed.addField("/8ball", "ask the magic 8 ball a question", false);
            embed.addField("/piglatin", "convert a sentence into piglatin", false);
            embed.addField("/ping", "get ponged", false);
            embed.addField("/say", "make the bot say somethiing", false);
            embed.addField("/shop", "have a tour of the shop", false);
            embed.addField("/steal", "steal coins from someone", false);
            embed.addField("/weather", "check a city's current weather", false);
            embed.addField("/work", "work as someone", false);

            embed.setColor(constants.color);
            embed.setFooter("Requested by " + event.getUser().getEffectiveName(), event.getUser().getAvatarUrl());
            event.replyEmbeds(embed.build()).queue();


        }
        super.onSlashCommandInteraction(event);
    }
}


