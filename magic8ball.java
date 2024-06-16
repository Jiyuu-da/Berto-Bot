package org.example.listeners;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import org.example.Main;


public class magic8ball extends ListenerAdapter {

    @Override
    public void onSlashCommandInteraction(SlashCommandInteractionEvent event) {
        String command = event.getName();
        String userID = event.getUser().getId();

        if(Main.maintenance && !userID.equals("576834455306633216")) {
            event.reply("BertoBot is under maintenance.").setEphemeral(true).queue();
            return;
        }

        if (command.equalsIgnoreCase("8ball")) {
            OptionMapping option = event.getOption("question");
            String msg = option.getAsString();

            String[] ansArray = {
                    "http://www.redkid.net/generator/8ball/newsign.php?line1=As+I+&line2=see+it%2C&line3=Yes.&Shake+Me=Shake+Me",
                    "http://www.redkid.net/generator/8ball/newsign.php?line1=Ask&line2=Again&line3=Later&Shake+Me=Shake+Me",
                    "http://www.redkid.net/generator/8ball/newsign.php?line1=Cannot&line2=predict&line3=now&Shake+Me=Shake+Me",
                    "http://www.redkid.net/generator/8ball/newsign.php?line1=Don%27t&line2=count&line3=on+it&Shake+Me=Shake+Me",
                    "http://www.redkid.net/generator/8ball/newsign.php?line1=Decidedly&line2=so&line3=&Shake+Me=Shake+Me",
                    "http://www.redkid.net/generator/8ball/newsign.php?line1=Most&line2=likely&line3=&Shake+Me=Shake+Me",
                    "http://www.redkid.net/generator/8ball/newsign.php?line1=My+reply&line2=is&line3=no&Shake+Me=Shake+Me",
                    "http://www.redkid.net/generator/8ball/newsign.php?line1=Outlook+&line2=not+so&line3=good&Shake+Me=Shake+Me",
                    "http://www.redkid.net/generator/8ball/newsign.php?line1=I+don%27t&line2=think+&line3=so&Shake+Me=Shake+Me",
                    "http://www.redkid.net/generator/8ball/newsign.php?line1=Very&line2=doubtfu&line3=l&Shake+Me=Shake+Me",
                    "http://www.redkid.net/generator/8ball/newsign.php?line1=Without&line2=a&line3=doubt&Shake+Me=Shake+Me",
                    "http://www.redkid.net/generator/8ball/newsign.php?line1=&line2=Yes.&line3=&Shake+Me=Shake+Me",
                    "http://www.redkid.net/generator/8ball/newsign.php?line1=You+may&line2=relay&line3=on+it&Shake+Me=Shake+Me",
                    "http://www.redkid.net/generator/8ball/newsign.php?line1=Probably&line2=not&line3=&Shake+Me=Shake+Me",
                    "http://www.redkid.net/generator/8ball/newsign.php?line1=Reply+&line2=Hazy.&line3=&Shake+Me=Shake+Me",
                    "http://www.redkid.net/generator/8ball/newsign.php?line1=I+don%27t&line2=think&line3=so&Shake+Me=Shake+Me"
            };
            int num = (int) (Math.random() * 6 + 1);
            String ans ;
            User user = event.getUser();
            EmbedBuilder embed = new EmbedBuilder();
            embed.setTitle("Magic 8 Ball");
            embed.setThumbnail(event.getMember().getAvatarUrl());
            embed.setColor(constants.color);
            embed.setFooter("Requested by " + user.getEffectiveName(), user.getAvatarUrl());



            switch (num) {
                case 1:
                    ans = ansArray[1];
                    embed.addField("Question: "+msg,"",false);
                    embed.setImage(ans);

                    break;
                case 2:
                    ans = ansArray[2];
                    embed.addField("Question: "+msg,"",false);
                    embed.setImage(ans);

                    break;
                case 3:
                    ans = ansArray[3];
                    embed.addField("Question: "+msg,"",false);
                    embed.setImage(ans);

                    break;
                case 4:
                    ans = ansArray[4];
                    embed.addField("Question: "+msg,"",false);
                    embed.setImage(ans);

                    break;
                case 5:
                    ans = ansArray[5];
                    embed.addField("Question: "+msg,"",false);
                    embed.setImage(ans);

                    break;
                case 6:
                    ans = ansArray[6];
                    embed.addField("Question: "+msg,"",false);
                    embed.setImage(ans);

                    break;

            }
            event.replyEmbeds(embed.build()).queue();

        }
        super.onSlashCommandInteraction(event);
    }
}



