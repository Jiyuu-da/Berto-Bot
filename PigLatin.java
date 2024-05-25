package org.example.listeners;

import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;


public class PigLatin extends ListenerAdapter {

    @Override
    public void onSlashCommandInteraction(SlashCommandInteractionEvent event) {
        String command = event.getName();

        if (command.equalsIgnoreCase("piglatin")) {
            OptionMapping option = event.getOption("sentence");

            String[] sentence = option.getAsString().toLowerCase().split(" ");
            StringBuilder pigLatinSentence = new StringBuilder();
            String regularSentence=  String.join(" ", sentence);

            for (int i = 0; i < sentence.length; i++) {
                int vowelIndex = -1;
                String prefix;
                String suffix;
                String word = sentence[i];

                for (int j = 0; j < word.length(); j++) {
                    char ch = word.charAt(j);
                    if ("aeiou".contains(String.valueOf(ch))) {
                        vowelIndex = j;
                        break;
                    }
                }
                if (vowelIndex == 0) {
                    suffix = word + "yay";
                    pigLatinSentence.append(suffix).append(" ");

                } else {
                    prefix = word.substring(0, 1);
                    suffix = word.substring(1);
                    pigLatinSentence.append(suffix).append(prefix).append("ay").append(" ");
                }
            }

            event.reply("**Your Sentence: ** " + regularSentence + "\n**Pig Latin: ** " + pigLatinSentence).setEphemeral(false).queue();
            super.onSlashCommandInteraction(event);
        }
    }
}