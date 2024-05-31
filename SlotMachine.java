package org.example.listeners;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import org.example.listeners.db.BankCreate;
import org.example.listeners.db.DBSetup;

import java.sql.SQLException;
import java.util.Arrays;
import java.util.Random;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class SlotMachine extends ListenerAdapter {
    private String user_id;
    private double user_bal;
    private static HikariConfig config = new HikariConfig();
    private static HikariDataSource ds;

    static {
        config.setJdbcUrl("jdbc:mysql://localhost:3306/economy");
        config.setUsername("root");
        config.setPassword("K1r4root");
        config.setDriverClassName("com.mysql.cj.jdbc.Driver");

        config.addDataSourceProperty("cachePrepStmts", "true");
        config.addDataSourceProperty("prepStmtCacheSize", "250");
        config.addDataSourceProperty("prepStmtCacheSqlLimit", "2048");
        ds = new HikariDataSource(config);
    }

    @Override
    public void onSlashCommandInteraction(SlashCommandInteractionEvent event) {
        final int ANIMATION_DURATION = 3000; // Duration of the animation in milliseconds
        final int ANIMATION_INTERVAL = 300; // Interval between color changes


        String command = event.getName();
        String userID = event.getUser().getId();

        OptionMapping optionBet = event.getOption("bet");
        int bet = optionBet.getAsInt();

        if(command.equalsIgnoreCase("slot")) {

            try {
                if(BankCreate.hasAccount(userID)) {
                    try {
                        int userBalance = DBSetup.getBalanceFromDatabase(userID);
                        int difference = bet - userBalance;

                        if(bet > 0) {
                            if(bet<= userBalance) {
                                User user = event.getUser();


                                EmbedBuilder embed = new EmbedBuilder();
                                embed.setTitle("Slot Machine");
                                embed.setColor(constants.color);
                                embed.setFooter("Requested by " + user.getEffectiveName(), user.getAvatarUrl());

                                String[][] slots = {
                                        {"", "", ""},
                                        {"", "", ""},
                                        {"", "", ""}

                                };

                                String[] colors = {"red", "orange",
                                        "yellow", "brown",
                                        "green","purple","blue"
                                };

                                int[] numsToColor = {1, 2, 3, 4, 5, 6, 7};

                                Random random = new Random();


                                ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);

                                // Task to update slot colors randomly
                                Runnable animationTask = new Runnable() {
                                    @Override
                                    public void run() {
                                        for (int i = 0; i < slots.length; i++) {
                                            for (int j = 0; j < slots[i].length; j++) {
                                                int randomIndex = random.nextInt(colors.length);
                                                slots[i][j] = String.format(":%s_square:", colors[randomIndex]);
                                            }
                                        }

                                        StringBuilder slotField = new StringBuilder();
                                        for (int i = 0; i < slots.length; i++) {
                                            for (int j = 0; j < slots[i].length; j++) {
                                                slotField.append(slots[i][j]).append(" ");
                                            }
                                            slotField.append("\n");
                                        }

                                        embed.clearFields();
                                        embed.addField("", slotField.toString(), false);
                                        event.getHook().editOriginalEmbeds(embed.build()).queue();
                                    }
                                };

                                // Start the animation
                                event.deferReply().queue();
                                for (int i = 0; i < ANIMATION_DURATION / ANIMATION_INTERVAL; i++) {
                                    scheduler.schedule(animationTask, i * ANIMATION_INTERVAL, TimeUnit.MILLISECONDS);
                                }

                                scheduler.schedule(new Runnable() {
                                    @Override
                                    public void run() {

                                        for(int i=0; i<slots.length; i++) {
                                            for(int j=0; j<slots[i].length; j++) {
                                                int randomIdx = random.nextInt(slots.length);
                                                slots[i][j] = String.format(":%s_square:", colors[randomIdx]);
                                            }
                                        }

                                        StringBuilder slotField = new StringBuilder();
                                        for (int i = 0; i < slots.length; i++) {
                                            for (int j = 0; j < slots[i].length; j++) {
                                                slotField.append(slots[i][j]).append(" ");
                                            }
                                            slotField.append("\n");
                                        }

                                        boolean win = false;

                                        for(int i=0; i<slots.length; i++) {
                                            if(slots[i][0].equals(slots[i][1]) && slots[i][1].equals(slots[i][2])) {
                                                win = true;
                                                break;
                                            }
                                        }

                                        embed.clearFields();
                                        embed.addField("", slotField.toString(), false);
                                        embed.addField("Player", user.getAsMention(), true);
                                        embed.addField("Bet", String.valueOf(bet) + " :coin:", true);


                                        if(win) {
                                            embed.addField("Result", "WON " + (int)(2.5*bet) + "!! :coin:", true);
                                            int updatedAmount = (int)(userBalance + 2.5*bet);
                                            try {
                                                DBSetup.updateBalanceInDatabase(userID, updatedAmount);
                                            } catch (SQLException e) {
                                                throw new RuntimeException(e);
                                            }
                                        }
                                        else {
                                            embed.addField("Result", "Lost " + bet + " :coin:", true);
                                        }

                                        event.getHook().editOriginalEmbeds(embed.build()).queue();
                                    }
                                }, ANIMATION_DURATION, TimeUnit.MILLISECONDS);
                            } else {
                                event.reply("You do not have sufficient balance, missing "+
                                        difference+ " coins :coin:").setEphemeral(true).queue();
                            }
                        } else {
                            event.reply("Invalid Input").setEphemeral(true).queue();
                        }
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                } else {
                    event.reply("You do not have a bank account, create one by /bankcreate").setEphemeral(true).queue();
                }
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }
        super.onSlashCommandInteraction(event);
    }
}
