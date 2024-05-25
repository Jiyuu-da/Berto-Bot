package org.example.listeners;

import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;

public class palin extends ListenerAdapter {
    @Override
    public void onMessageReceived(@NotNull MessageReceivedEvent event) {

        String[] message = event.getMessage().getContentRaw().split(" ");

        if(message.length==1 && message[0].equalsIgnoreCase("!palin")) {
            event.getChannel().sendMessage("Enter a number to check for Palindrome").queue();
        } else if(message.length==2 && message[0].equalsIgnoreCase("!palin")) {
            String second = message[1];
            long num = Long.parseLong(second);
            long orgNum = num;
            long rev = 0;

            while(num!=0) {
                rev = rev * 10 + num % 10;
                num = num / 10;
            }

            if(rev == orgNum) {
                event.getChannel().sendMessage(orgNum + " is a Palindrome").queue();
            } else{
                event.getChannel().sendMessage(orgNum + " is NOT a Palindrome").queue();
            }
        }
        super.onMessageReceived(event);
    }
}


