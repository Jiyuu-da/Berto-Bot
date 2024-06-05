//package org.example.listeners;
//
//import net.dv8tion.jda.api.entities.User;
//import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
//import net.dv8tion.jda.api.hooks.ListenerAdapter;
//import com.zaxxer.hikari.HikariConfig;
//import com.zaxxer.hikari.HikariDataSource;
//import net.dv8tion.jda.api.EmbedBuilder;
//import net.dv8tion.jda.api.interactions.commands.OptionMapping;
//import net.dv8tion.jda.internal.entities.UserImpl;
//import org.example.listeners.db.BankCreate;
//import org.example.listeners.db.DBSetup;
//
//import java.sql.SQLException;
//import java.util.Random;
//import java.util.concurrent.Executors;
//import java.util.concurrent.ScheduledExecutorService;
//import java.util.concurrent.TimeUnit;
//
//public class Roulette extends ListenerAdapter {
//    private String user_id;
//    private double user_bal;
//    private static HikariConfig config = new HikariConfig();
//    private static HikariDataSource ds;
//    static {
//        config.setJdbcUrl("jdbc:mysql://localhost:3306/economy");
//        config.setUsername("root");
//        config.setPassword("K1r4root");
//        config.setDriverClassName("com.mysql.cj.jdbc.Driver");
//
//        config.addDataSourceProperty("cachePrepStmts", "true");
//        config.addDataSourceProperty("prepStmtCacheSize", "250");
//        config.addDataSourceProperty("prepStmtCacheSqlLimit", "2048");
//        ds = new HikariDataSource(config);
//    }
//    @Override
//    public void onSlashCommandInteraction(SlashCommandInteractionEvent event) {
//
//        String command = event.getName();
//        String userID = event.getUser().getId();
//
//        OptionMapping optionBet = event.getOption("bet");
//        OptionMapping optionNum = event.getOption("number");
//        OptionMapping optionType = event.getOption("type");
//
//        final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
//
//
//        int bet = optionBet.getAsInt();
//        int chosenNum = optionNum != null ? optionNum.getAsInt() : -1; // default value for no number
//        String type = optionType != null ? optionType.getAsString() : null;
//
//        System.out.println("num value " + chosenNum);
//        System.out.println("type value " + type);
//
//        if(command.equalsIgnoreCase("roulette")) {
//            try {
//                if(BankCreate.hasAccount(userID)) {
//                    User user = event.getUser();
//
//                    int userBalance = DBSetup.getBalanceFromDatabase(userID);
//                    int diff = bet - userBalance;
//
//                    if(bet > 0) {
//                        if(bet <= userBalance) {
//
//
//                            int[] allNums = new int[36];
//                            for (int i = 0; i < 36; i++) {
//                                allNums[i] = i + 1;
//                            }
//
//                            Random random = new Random();
//                            int ballNum = random.nextInt(36) + 1;
//
//                            String color = getColor(ballNum);
//
//                            String even_or_odd = isEven(ballNum);
//
//                            boolean first_12 = ballNum < 13;
//                            boolean second_12 = ballNum >=13 && ballNum < 25;
//                            boolean third_12 = ballNum >=25 ;
//
//                            int multiplier = getMultiplier(type);
//
//                            boolean win = (type == null) ? winWithNumber(chosenNum, ballNum) : win(type, color, even_or_odd, first_12, second_12, third_12);
//
//
//                            EmbedBuilder spinningEmbed = new EmbedBuilder();
//                            spinningEmbed.setTitle("Roulette");
//                            spinningEmbed.setColor(constants.color);
//                            spinningEmbed.setImage("https://media.tenor.com/AQQVuTU0ZjcAAAAj/casino.gif");
//                            event.replyEmbeds(spinningEmbed.build()).queue();
//
//                            scheduler.schedule((new Runnable() {
//                                @Override
//                                public void run() {
//
//                                    String redEmoteID = "<:red:1246820495320420394>";
//                                    String blackEmoteID = "<:black:1246820011385946176>";
//
//                                    EmbedBuilder stillEmbed = new EmbedBuilder();
//                                    stillEmbed.setTitle("Roulette");
////                                    stillEmbed.setDescription("<:red:1246820495320420394>");
//                                    stillEmbed.setImage("https://cdn.discordapp.com/attachments/1246118669306822708/1246869241177833563/still_roulette.png?ex=665df4f8&is=665ca378&hm=b9acd3a582f88b29f6783d50a2d59b6dca576ccab33bc369893e4d1a1325704e&");
//                                    stillEmbed.addField("Player", event.getUser().getAsMention(), true);
//                                    stillEmbed.addField("Bet", bet + " :coin:", true);
//
//                                    if(type!=null) {
//                                        if(color.equalsIgnoreCase("red")) {
//                                            stillEmbed.setDescription(redEmoteID + " " + ballNum);
//                                        }
//                                        else {
//                                            stillEmbed.setDescription(blackEmoteID + " " + ballNum);
//                                        }
//                                        stillEmbed.addField("On", type, true);
//                                    } else {
//                                        stillEmbed.addField("On", String.valueOf(chosenNum), true);
//                                    }
//
//
//                                    int updatedAmount;
//                                    if(win) {
//                                        try {
//                                            updatedAmount = (userBalance + multiplier * bet) ;
//                                            DBSetup.updateBalanceInDatabase(userID, updatedAmount);
//
//                                            stillEmbed.setColor(constants.WIN_COLOR);
//                                            stillEmbed.addField("Result", "WON " + multiplier * bet + "!! :coin:", true);
//                                        } catch (SQLException e) {
//                                            throw new RuntimeException(e);
//                                        }
//                                    } else {
//                                        try {
//                                            updatedAmount = userBalance - bet;
//                                            DBSetup.updateBalanceInDatabase(userID, updatedAmount);
//
//                                            stillEmbed.setColor(constants.LOST_COLOR);
//                                            stillEmbed.addField("Result", "Lost " + bet + " :coin:", true);
//                                        } catch (SQLException e) {
//                                            throw new RuntimeException(e);
//                                        }
//
//                                    }
//                                    event.getHook().editOriginalEmbeds(stillEmbed.build()).queue();
//                                }
//                            }), 5, TimeUnit.SECONDS);
//
//
//                        }
//                        else {
//                            event.reply("You do not have sufficient balance, missing "+
//                                    diff+ " coins :coin:").setEphemeral(true).queue();
//                        }
//                    } else {
//                        event.reply("Invalid Input").setEphemeral(true).queue();
//                    }
//                } else {
//                    event.reply("You do not have a bank account");
//                }
//            } catch (SQLException e) {
//                throw new RuntimeException(e);
//            }
//        }
//
//        super.onSlashCommandInteraction(event);
//    }
//    public static String isEven(int num) {
//        if(num % 2 == 0) {
//            return "even";
//        }
//        return "odd";
//    }
//    public static int getMultiplier(String type) {
//        if(type==null) return 36;
//        switch(type.toLowerCase()) {
//            case "even":
//                return 2;
//            case "odd":
//                return 2;
//            case "red":
//                return 2;
//            case "black":
//                return 2;
//            case "first_12":
//                return 3;
//            case "second_12":
//                return 3;
//            case "third_12":
//                return 3;
//            default:
//                return 1;
//        }
//    }
//
//    public static String getColor(int ballNum) {
//        int[] redNums = {1, 3, 5, 7, 9, 12, 14, 16, 18, 19, 21, 23, 25, 27, 30, 32, 34, 36};
//        for (int i : redNums) {
//            if (ballNum == i) {
//                return "red";
//            }
//        }
//        return "black";
//    }
//
//    public static boolean win(String betType, String color, String even_or_odd, boolean first_12, boolean second_12, boolean third_12) {
//        if(betType.equalsIgnoreCase("red") || betType.equalsIgnoreCase("black")) {
//            return betType.equalsIgnoreCase(color);
//        }
//        else if (betType.equalsIgnoreCase("even") || betType.equalsIgnoreCase("odd")){
//            return betType.equalsIgnoreCase(even_or_odd);
//        }
//        else if(betType.equalsIgnoreCase("1-12")) {
//            if(first_12) {
//                return true;
//            } else {
//                return false;
//            }
//        } else if (betType.equalsIgnoreCase("13-24")) {
//            if(second_12) {
//                return true;
//            } else {
//                return false;
//            }
//        } else if (betType.equalsIgnoreCase("25-36")) {
//            if(third_12) {
//                return true;
//            } else {
//                return false;
//            }
//        }
//        return false;
//    }
//
//    public static boolean winWithNumber(int chosenNum, int ballNum) {
//        return ballNum == chosenNum;
//    }
//}


package org.example.listeners;

import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import org.example.listeners.db.BankCreate;
import org.example.listeners.db.DBSetup;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Random;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class Roulette extends ListenerAdapter {
    private static final String DB_URL = "jdbc:sqlite:your_database_path.db";

    @Override
    public void onSlashCommandInteraction(SlashCommandInteractionEvent event) {

        String command = event.getName();
        String userID = event.getUser().getId();

        OptionMapping optionBet = event.getOption("bet");
        OptionMapping optionNum = event.getOption("number");
        OptionMapping optionType = event.getOption("type");

        final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);

        int bet = optionBet.getAsInt();
        int chosenNum = optionNum != null ? optionNum.getAsInt() : -1; // default value for no number
        String type = optionType != null ? optionType.getAsString() : null;

        if (command.equalsIgnoreCase("roulette")) {
            try (Connection connection = DriverManager.getConnection(DB_URL)) {
                if (BankCreate.hasAccount(userID)) {
                    User user = event.getUser();

                    int userBalance = DBSetup.getBalanceFromDatabase(userID);
                    int diff = bet - userBalance;

                    if (bet > 0) {
                        if (bet <= userBalance) {

                            int[] allNums = new int[36];
                            for (int i = 0; i < 36; i++) {
                                allNums[i] = i + 1;
                            }

                            Random random = new Random();
                            int ballNum = random.nextInt(36) + 1;

                            String color = getColor(ballNum);

                            String even_or_odd = isEven(ballNum);

                            boolean first_12 = ballNum < 13;
                            boolean second_12 = ballNum >= 13 && ballNum < 25;
                            boolean third_12 = ballNum >= 25;

                            int multiplier = getMultiplier(type);

                            boolean win = (type == null) ? winWithNumber(chosenNum, ballNum) : win(type, color, even_or_odd, first_12, second_12, third_12);

                            EmbedBuilder spinningEmbed = new EmbedBuilder();
                            spinningEmbed.setTitle("Roulette");
                            spinningEmbed.setColor(constants.color);
                            spinningEmbed.setImage("https://media.tenor.com/AQQVuTU0ZjcAAAAj/casino.gif");
                            event.replyEmbeds(spinningEmbed.build()).queue();

                            scheduler.schedule(new Runnable() {
                                @Override
                                public void run() {

                                    String redEmoteID = "<:red:1246820495320420394>";
                                    String blackEmoteID = "<:black:1246820011385946176>";

                                    EmbedBuilder stillEmbed = new EmbedBuilder();
                                    stillEmbed.setTitle("Roulette");
                                    stillEmbed.setImage("https://cdn.discordapp.com/attachments/1246118669306822708/1246869241177833563/still_roulette.png?ex=665df4f8&is=665ca378&hm=b9acd3a582f88b29f6783d50a2d59b6dca576ccab33bc369893e4d1a1325704e&");
                                    stillEmbed.addField("Player", event.getUser().getAsMention(), true);
                                    stillEmbed.addField("Bet", bet + " :coin:", true);

                                    if (type != null) {
                                        if (color.equalsIgnoreCase("red")) {
                                            stillEmbed.setDescription(redEmoteID + " " + ballNum);
                                        } else {
                                            stillEmbed.setDescription(blackEmoteID + " " + ballNum);
                                        }
                                        stillEmbed.addField("On", type, true);
                                    } else {
                                        stillEmbed.addField("On", String.valueOf(chosenNum), true);
                                    }

                                    int updatedAmount;
                                    if (win) {
                                        try {
                                            updatedAmount = (userBalance + multiplier * bet);
                                            DBSetup.updateBalanceInDatabase(userID, updatedAmount);

                                            stillEmbed.setColor(constants.WIN_COLOR);
                                            stillEmbed.addField("Result", "WON " + multiplier * bet + "!! :coin:", true);
                                        } catch (SQLException e) {
                                            throw new RuntimeException(e);
                                        }
                                    } else {
                                        try {
                                            updatedAmount = userBalance - bet;
                                            DBSetup.updateBalanceInDatabase(userID, updatedAmount);

                                            stillEmbed.setColor(constants.LOST_COLOR);
                                            stillEmbed.addField("Result", "Lost " + bet + " :coin:", true);
                                        } catch (SQLException e) {
                                            throw new RuntimeException(e);
                                        }

                                    }
                                    event.getHook().editOriginalEmbeds(stillEmbed.build()).queue();
                                }
                            }, 5, TimeUnit.SECONDS);

                        } else {
                            event.reply("You do not have sufficient balance, missing " +
                                    diff + " coins :coin:").setEphemeral(true).queue();
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
        if (num % 2 == 0) {
            return "even";
        }
        return "odd";
    }

    public static int getMultiplier(String type) {
        if (type == null) return 36;
        switch (type.toLowerCase()) {
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

    public static String getColor(int ballNum) {
        int[] redNums = {1, 3, 5, 7, 9, 12, 14, 16, 18, 19, 21, 23, 25, 27, 30, 32, 34, 36};
        for (int i : redNums) {
            if (ballNum == i) {
                return "red";
            }
        }
        return "black";
    }

    public static boolean win(String betType, String color, String even_or_odd, boolean first_12, boolean second_12, boolean third_12) {
        if (betType.equalsIgnoreCase("red") || betType.equalsIgnoreCase("black")) {
            return betType.equalsIgnoreCase(color);
        } else if (betType.equalsIgnoreCase("even") || betType.equalsIgnoreCase("odd")) {
            return betType.equalsIgnoreCase(even_or_odd);
        } else if (betType.equalsIgnoreCase("1-12")) {
            return first_12;
        } else if (betType.equalsIgnoreCase("13-24")) {
            return second_12;
        } else if (betType.equalsIgnoreCase("25-36")) {
            return third_12;
        }
        return false;
    }

    public static boolean winWithNumber(int chosenNum, int ballNum) {
        return ballNum == chosenNum;
    }
}
