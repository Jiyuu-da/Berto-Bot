package org.example.listeners;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;

public class revstr extends ListenerAdapter {
    @Override
    public void onMessageReceived(@NotNull MessageReceivedEvent event) {

        String[] message = event.getMessage().getContentRaw().split(" ");
        if(message.length == 1 && message[0].equalsIgnoreCase("!reverse")) {
            event.getChannel().sendMessage("Enter the number to be reversed").queue();
        } else if(message.length==2 && message[0].equalsIgnoreCase("!reverse")) {
            String second = message[1];
            long num = Long.parseLong(second);
            long orgNum = num;
            long rev = 0;

            while(num!=0) {
                rev = rev*10 + num%10;
                num = num/10;
            }
            String reversed = String.valueOf(rev);

            event.getChannel().sendMessage(reversed).queue();
        }


        if(message.length == 1 && message[0].equalsIgnoreCase("!revStr")) {
            event.getChannel().sendMessage("Enter the text to be reversed").queue();
        } else if(message.length > 1 && message[0].equalsIgnoreCase("!revstr")) {
            String rev = "";
            String str = Arrays.toString(message).replace("[", "").replace("]", "").replace(",", "");

            for(int i=str.length()-1; i>7; i--) {
                rev = rev + str.charAt(i);
            }
            event.getChannel().sendMessage(rev).queue();
        }

        super.onMessageReceived(event);
    }

}



