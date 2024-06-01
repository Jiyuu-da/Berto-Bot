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

public class SevenUp extends ListenerAdapter {
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
        OptionMapping optionNum = event.getOption("sum");

        String sum = optionNum.getAsString();

        int num = 0;
        int bet = optionBet.getAsInt();

        if(command.equalsIgnoreCase("7up7down")) {

            EmbedBuilder embed = new EmbedBuilder();
            try {
                if(BankCreate.hasAccount(userID)) {
                    try {
                        if(sum.equalsIgnoreCase("down")) {
                            num = 5;
                        } else if(sum.equalsIgnoreCase("up")) {
                            num = 10;
                        }
                        else if(sum.equalsIgnoreCase("7")) {
                            num = 7;
                        }
                        int userBalance = DBSetup.getBalanceFromDatabase(userID);
                        int diff = bet - userBalance;
                        if(bet>0) {
                            if(bet <= userBalance) {
                                User user = event.getUser();

                                int outcome1 = (int) (Math.random() * (6) + 1);
                                int outcome2 = (int) (Math.random() * (6) + 1);
                                int result = outcome1 + outcome2;

                                boolean win;

                                if ((result > 7 && num > 7) || (result < 7 && num < 7) || (result == 7 && num == 7)) {
                                    win = true;
                                } else {
                                    win = false;
                                }

                                String displayResult;

                                if(result > 7) {
                                    displayResult = "7 up";
                                } else if(result < 7) {
                                    displayResult = "7 down";
                                } else {
                                    displayResult = "7";
                                }

                                int updatedAmount;

                                if(win) {
                                    if(result == 7) {
                                        updatedAmount = (int)(userBalance + 3*bet);
                                        DBSetup.updateBalanceInDatabase(userID, updatedAmount);
                                    } else {
                                        updatedAmount = (int)(userBalance + 2.2*bet);
                                        DBSetup.updateBalanceInDatabase(userID, updatedAmount);
                                    }

                                    embed.setColor(constants.WIN_COLOR);
                                    embed.setTitle("7 up 7 Down");
                                    embed.setDescription(user.getAsMention() + "You picked **"+ sum + "**, it was **" +displayResult+ "** You Won! You now have " + updatedAmount+" coins :coin:");
                                    event.replyEmbeds(embed.build()).queue();

                                } else {
                                    updatedAmount = userBalance - bet;
                                    DBSetup.updateBalanceInDatabase(userID, updatedAmount);

                                    embed.setColor(constants.LOST_COLOR);
                                    embed.setTitle("7 up 7 Down");
                                    embed.setDescription(user.getAsMention() + "You picked **"+ sum + "**, it was **" +displayResult+ "** You Lost... You now have " + updatedAmount+" coins :coin:");
                                    event.replyEmbeds(embed.build()).queue();
                                }

                                System.out.println("result :" + result +"\ndisplayResult : " + displayResult + "\n" +"sum : " + sum + "\n" +"num : " + num);


                        }else {
                                event.reply("You do not have sufficient balance, missing "+
                                        diff+ " coins :coin:").setEphemeral(true).queue();
                            }
                    } else {
                            event.reply("Invalid Input").setEphemeral(true).queue();
                        }
                } catch (SQLException e) {
                        throw new RuntimeException(e);
                    }
                }
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }
    }
}
