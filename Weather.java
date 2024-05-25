package org.example.listeners;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class Weather extends ListenerAdapter {

    @Override
    public void onSlashCommandInteraction(SlashCommandInteractionEvent event) {
        String command = event.getName();

        if (command.equalsIgnoreCase("weather")) {
            OptionMapping option = event.getOption("city");
            String city = option.getAsString();

            String apiKey = "d1715ae6486692fb4b753af67304c4d1";
            String weatherEndPoint = "http://api.openweathermap.org/data/2.5/weather";

            User user = event.getUser();

            EmbedBuilder embed = new EmbedBuilder();
            embed.setTitle("Weather");
            embed.setColor(constants.color);
            embed.setFooter("Requested by " + user.getEffectiveName(), user.getAvatarUrl());



            try {
                // Make API request
                String apiUrl = weatherEndPoint + "?q=" + city + "&appid=" + apiKey + "&units=metric";
                JSONObject json = getJsonResponse(apiUrl);


                // Parse and display weather info
                String description = json.getJSONArray("weather").getJSONObject(0).getString("description");

                if(description.equalsIgnoreCase("broken clouds")) {
                    description = "Partly Cloudy";
                }
                int temperature = (int)Math.round(json.getJSONObject("main").getDouble("temp"));
                String iconCode = json.getJSONArray("weather").getJSONObject(0).getString("icon");
                String iconUrl = "http://openweathermap.org/img/w/" + iconCode + ".png";
                String temp = String.valueOf(temperature);
                int fahrenheit = (int)Math.round(temperature*1.8) + 32;
                String fahr = String.valueOf(fahrenheit);


                embed.addField("City: ", city.substring(0,1).toUpperCase() + city.substring(1), false);
                embed.addField("Description: ", description.substring(0,1).toUpperCase() + description.substring(1), false);
                embed.addField("Temperature: ", temp+"°C / " + fahr + "°F", false);
                embed.setThumbnail(iconUrl); // Set the thumbnail to the weather icon

                event.replyEmbeds(embed.build()).queue();

            } catch (Exception e) {
                event.reply("An error occurred while fetching weather data.");
                e.printStackTrace();
            }
        }
        super.onSlashCommandInteraction(event);
    }

    private JSONObject getJsonResponse(String apiUrl) throws Exception {
        URL url = new URL(apiUrl);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
        String line;
        StringBuilder response = new StringBuilder();
        while ((line = reader.readLine()) != null) {
            response.append(line);
        }
        reader.close();
        return new JSONObject(response.toString());
    }
}
