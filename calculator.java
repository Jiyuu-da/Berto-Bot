package org.example.listeners;

import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;

public class calculator extends ListenerAdapter {
    @Override
    public void onMessageReceived(@NotNull MessageReceivedEvent event) {

        String[] message = event.getMessage().getContentRaw().split(" ");


        if(message[0].equalsIgnoreCase("!calculate") && message.length==1){
            event.getChannel().sendMessage("You need to Specify add or sub ").queue();
        } else if (message[0].equalsIgnoreCase("!calculate") && message[1].equalsIgnoreCase("add")){
            double num1 = Integer.parseInt(message[2]);
            double num2 = Integer.parseInt(message[3]);
            event.getChannel().sendMessage((num1) +  " + " + (num2) + " = " + (num1+num2)).queue();

        } else if (message[0].equalsIgnoreCase("!calculate") && message[1].equalsIgnoreCase("sub")){
            double num1 = Integer.parseInt(message[2]);
            double num2 = Integer.parseInt(message[3]);
            event.getChannel().sendMessage((num1) +  " - " + (num2) + " = " + (num1-num2)).queue();
        } else if (message[0].equalsIgnoreCase("!calculate") && message[1].equalsIgnoreCase("mul")) {
            double num1 = Integer.parseInt(message[2]);
            double num2 = Integer.parseInt(message[3]);
            event.getChannel().sendMessage((num1) + " * " + (num2) + " = " + (num1 * num2)).queue();
        }  else if (message[0].equalsIgnoreCase("!calculate") && message[1].equalsIgnoreCase("div")) {
            double num1 = Integer.parseInt(message[2]);
            double num2 = Integer.parseInt(message[3]);
            event.getChannel().sendMessage((num1) + " / " + (num2) + " = " + (num1 / num2)).queue();
        }
    }
}



