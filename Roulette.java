package org.example.listeners;

import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import org.example.listeners.db.BankCreate;
import org.example.listeners.db.DBSetup;

import javax.swing.text.html.Option;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.Random;

public class Roulette extends ListenerAdapter {
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

        String command = event.getName();
        String userID = event.getUser().getId();

        OptionMapping optionBet = event.getOption("bet");
        OptionMapping optionType = event.getOption("type");
        OptionMapping optionNum = event.getOption("SpecificNumber");


        int bet = optionBet.getAsInt();
        String type = optionType.getAsString();

        int chosenNum = optionNum.getAsInt();

        System.out.println("num value " + String.valueOf(chosenNum));

        if(command.equalsIgnoreCase("roulette") && userID.equalsIgnoreCase("576834455306633216")) {
            try {
                if(BankCreate.hasAccount(userID)) {
                    int userBalance = DBSetup.getBalanceFromDatabase(userID);
                    int diff = bet - userBalance;

                    if(bet > 0) {
                        if(bet <= userBalance) {


                            int[] allNums = new int[36];
                            for (int i = 0; i < 36; i++) {
                                allNums[i] = i + 1;
                            }

                            int[] redNums = {1, 3, 5, 7, 9, 12, 14, 16, 18, 19, 21, 23, 25, 27, 30, 32, 34, 36};
                            int[] blackNums = {2, 4, 6, 8, 10, 11, 13, 15, 17, 20, 22, 24, 26, 28, 29, 31, 33, 35};

                            Random random = new Random();
                            int ballNum = random.nextInt(36) + 1;

                            String color = "black";

                            for(int i : redNums) {
                                if(ballNum == i) {
                                    color = "red";
                                    break;
                                }
                            }

                            String even_or_odd = isEven(ballNum);

                            boolean first_12 = false;
                            boolean second_12 = false;
                            boolean third_12 = false;

                            int multiplier = getMultiplier(type);

                            if(ballNum < 13) {
                                first_12 = true;
                            } else if(ballNum >=13 && ballNum < 25) {
                                second_12 =true;
                            } else {
                                third_12 = true;
                            }

                            System.out.println(first_12);
                            System.out.println(second_12);

                            System.out.println(third_12);

                            boolean win = win(type, color, even_or_odd, first_12, second_12, third_12);

                            if(chosenNum != 0) {

                            }


                            System.out.println(ballNum);
                            System.out.println(color);

                            System.out.println(type);

                            EmbedBuilder embed = new EmbedBuilder();
                            embed.setTitle("Roulette");
                            embed.setColor(constants.color);
                            embed.setDescription(color + " " + ballNum);
                            embed.setImage("https://media.tenor.com/AQQVuTU0ZjcAAAAj/casino.gif");

                            if(win) {
                                embed.setFooter("You won");
                            } else {
                                embed.setFooter("You lost");
                            }
                            event.replyEmbeds(embed.build()).queue();



                        }
                        else {
                            event.reply("You do not have sufficient balance, missing "+
                                    diff+ " coins :coin:").setEphemeral(true).queue();
                        }
                    } else {
                        event.reply("Invalid Input").setEphemeral(true).queue();
                    }
                } else {
                    event.reply("You do not have a bank account");
                }
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }

        super.onSlashCommandInteraction(event);
    }
    public static String isEven(int num) {
        if(num % 2 == 0) {
            return "even";
        }
        return "odd";
    }
    public static int getMultiplier(String type) {
        switch(type.toLowerCase()) {
            case "even":
            case "odd":
            case "red":
            case "black":
                return 2;
            case "first_12":
            case "second_12":
            case "third_12":
                return 3;
            default:
                return 1;
        }
    }

    public static boolean win(String betType, String color, String even_or_odd, boolean first_12, boolean second_12, boolean third_12) {
        if(betType.equalsIgnoreCase("red") || betType.equalsIgnoreCase("black")) {
            return betType.equalsIgnoreCase(color);
        }
        else if (betType.equalsIgnoreCase("even") || betType.equalsIgnoreCase("odd")){
            return betType.equalsIgnoreCase(even_or_odd);
        }
        else if(betType.equalsIgnoreCase("1-12")) {
            if(first_12) {
                return true;
            } else {
                return false;
            }
        } else if (betType.equalsIgnoreCase("13-24")) {
            if(second_12) {
                return true;
            } else {
                return false;
            }
        } else if (betType.equalsIgnoreCase("25-36")) {
            if(third_12) {
                return true;
            } else {
                return false;
            }
        }
        return false;
    }

    public static boolean winWithNumber(int chosenNum, int ballNum) {
        return chosenNum == ballNum;
    }
}
