package org.example.listeners;

import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

public class nerd extends ping {
    @Override
    public void onMessageReceived(MessageReceivedEvent event) {
        String message = event.getMessage().getContentRaw();
        if(message.length() > 240) {
            event.getChannel().sendMessage(":nerd:").queue();
        }
        super.onMessageReceived(event);
    }
}
