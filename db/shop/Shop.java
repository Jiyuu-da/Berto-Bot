package org.example.listeners.db.shop;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import org.example.listeners.constants;

import javax.swing.text.html.Option;

public class Shop extends ListenerAdapter {
    @Override
    public void onSlashCommandInteraction(SlashCommandInteractionEvent event) {
        String command = event.getName();
        OptionMapping itemOption = event.getOption("item");
        String item = itemOption.getAsString();

        if(command.equalsIgnoreCase("shop")) {
            System.out.println("working");
            if(item.equalsIgnoreCase("shield")) {
                EmbedBuilder embed = new EmbedBuilder();
                embed.setTitle("Item");
                embed.setColor(constants.color);
                embed.addField("Name", "shield", false);
                embed.addField("Duration", "17 hours", false);
                embed.addField("Description", "Get protected from robbers for a limited time", false);
                embed.addField("Price", "120 :coin:", false);
                embed.setThumbnail("https://icones.pro/wp-content/uploads/2022/06/symbole-de-bouclier-jaune.png");
                event.replyEmbeds(embed.build()).queue();
            } else {
                event.reply("Shield is the only item in the shop right now").setEphemeral(true).queue();
            }
        }
        super.onSlashCommandInteraction(event);
    }
}
