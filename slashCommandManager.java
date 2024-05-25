package org.example.listeners;
import net.dv8tion.jda.api.events.guild.GuildReadyEvent;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;

import net.dv8tion.jda.api.hooks.ListenerAdapter;

import javax.swing.text.html.Option;
import java.util.ArrayList;
public class slashCommandManager extends ListenerAdapter {
    @Override
    public void onSlashCommandInteraction(SlashCommandInteractionEvent event) {
        String command = event.getName();

        if(command.equalsIgnoreCase("finish")) {
            return;
        }
        super.onSlashCommandInteraction(event);
    }

    @Override
    public void onGuildReady(GuildReadyEvent event) {
        ArrayList<CommandData> commandData = new ArrayList<>();

        OptionData cointossOption = new OptionData(OptionType.STRING, "type", "heads/ tails",true)
                .addChoice("heads", "heads")
                .addChoice("tails", "tails");
        OptionData cointossBet = new OptionData(OptionType.INTEGER, "bet", "bet your amount", true);
        commandData.add(Commands.slash("cointoss", "toss a coin").addOptions(cointossOption, cointossBet));


        OptionData sayOption = new OptionData(OptionType.STRING, "message", "type your msg here",true, true);
        commandData.add(Commands.slash("say", "make the bot say what you want").addOptions(sayOption));

        OptionData eightBallOption = new OptionData(OptionType.STRING, "question", "type your question here",true, true);
        commandData.add(Commands.slash("8ball", "ask a really important question").addOptions(eightBallOption));


        OptionData avatarOption = new OptionData(OptionType.USER, "user", "check the avatar of a user");
        commandData.add(Commands.slash("avatar", "see your avatar").addOptions(avatarOption));

        commandData.add(Commands.slash("ping", "pong"));

        OptionData fishBet = new OptionData(OptionType.INTEGER, "bet", "bet your amount", true);
        commandData.add(Commands.slash("fish", "catch a fish").addOptions(fishBet));

        OptionData pigLatinWord = new OptionData(OptionType.STRING, "sentence", "type your word here", true);
        commandData.add(Commands.slash("piglatin", "convert in piglatin").addOptions(pigLatinWord));

        OptionData city = new OptionData(OptionType.STRING, "city", "enter the city", true);
        commandData.add(Commands.slash("weather", "find how's the weather at a place").addOptions(city));

        commandData.add(Commands.slash("help", "see all the commands"));

        commandData.add(Commands.slash("bankcreate", "create a bank account"));

        OptionData userCheck = new OptionData(OptionType.USER, "user", "check the balance of a user");
        commandData.add(Commands.slash("check", "check your balance").addOptions(userCheck));

        commandData.add(Commands.slash("daily", "get a daily batch of coins"));

        OptionData afkReason = new OptionData(OptionType.STRING, "reason", "set your afk reason", true);
        commandData.add(Commands.slash("afk", "set an AFK reason").addOptions(afkReason));

        commandData.add(Commands.slash("work", "work as someone"));

        OptionData userSteal = new OptionData(OptionType.USER, "user", "provide the user to steal from", true);
        commandData.add(Commands.slash("steal", "steal from someone").addOptions(userSteal));

        OptionData userItem = new OptionData(OptionType.STRING, "item", "provide the item to buy", true)
                .addChoice("shield", "shield");
        commandData.add(Commands.slash("buy", "buy something from the shop").addOptions(userItem));

        OptionData shopItem = new OptionData(OptionType.STRING, "item", "enter item to view", true)
                .addChoice("shield", "shield");
        commandData.add(Commands.slash("shop", "have a tour of the shop").addOptions(shopItem));

        OptionData sevenBet = new OptionData(OptionType.INTEGER, "bet", "bet your amount", true);
        OptionData sevenNum = new OptionData(OptionType.STRING, "sum", "Up Down or 7", true)
                .addChoice("up", "up")
                .addChoice("down", "down")
                .addChoice("7", "7");
        commandData.add(Commands.slash("7up7down", "Play 7 up 7 down"). addOptions(sevenBet, sevenNum));


        event.getGuild().updateCommands().addCommands(commandData).queue();
        super.onGuildReady(event);
    }
}
