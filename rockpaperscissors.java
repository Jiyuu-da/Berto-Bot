package org.example.listeners;

import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;

import java.util.Random;

public class rockpaperscissors extends ListenerAdapter {
    @Override
    public void onMessageReceived(@NotNull MessageReceivedEvent event) {
        String[] message = event.getMessage().getContentRaw().split(" ");

        Random random = new Random();
        int computerChoice = (int) (Math.random() * 3 + 1);

        if(message[0].equalsIgnoreCase("!rps") && message.length == 1) {
            event.getChannel().sendMessage("You need to tell your choice").queue();
        } else if (message[0].equalsIgnoreCase("!rps") && message[1].equalsIgnoreCase("rock")){

            if (computerChoice == 1) {
                event.getChannel().sendMessage(" Your Choice : :new_moon: \n My Choice : :new_moon:\n It's a draw" ).queue();
            } else if (computerChoice == 2) {
                event.getChannel().sendMessage(" Your Choice : :new_moon: \n My Choice : :page_facing_up:\n You Lost...").queue();
            } else if (computerChoice == 3) {
                event.getChannel().sendMessage(" Your Choice : :new_moon: \n My Choice : :scissors:\n You Won!!").queue();
            }
        }  else if (message[0].equalsIgnoreCase("!rps") && message[1].equalsIgnoreCase("paper")){

            if (computerChoice == 1) {
                event.getChannel().sendMessage(" Your Choice : :page_facing_up: \n My Choice : :new_moon:\n You Won!!" ).queue();
            } else if (computerChoice == 2) {
                event.getChannel().sendMessage(" Your Choice : :page_facing_up: \n My Choice : :page_facing_up:\n It's a draw").queue();
            } else if (computerChoice == 3) {
                event.getChannel().sendMessage(" Your Choice : :page_facing_up: \n My Choice : :scissors:\n You Lost...").queue();
            }
        }  else if (message[0].equalsIgnoreCase("!rps") && message[1].equalsIgnoreCase("scissors")){

            if (computerChoice == 1) {
                event.getChannel().sendMessage(" Your Choice : :scissors: \n My Choice : :new_moon:\n You Lost..." ).queue();
            } else if (computerChoice == 2) {
                event.getChannel().sendMessage(" Your Choice : :scissors: \n My Choice : :page_facing_up:\n You Won!!").queue();
            } else if (computerChoice == 3) {
                event.getChannel().sendMessage(" Your Choice : :scissors: \n My Choice : :scissors:\n It's a draw").queue();
            }
        }

        super.onMessageReceived(event);
    }
}


