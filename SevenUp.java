package org.example.listeners;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
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

                                int outcome1 = (int) (Math.random() * (12-2) + 2);
                                int outcome2 = (int) (Math.random() * (12-2) + 2);
                                int result = outcome1 + outcome2;


                                if(result < 7 && result!=7) {
                                    if(num < 7) {
                                        int updatedAmount = (int)(userBalance + 1.5*bet);
                                        DBSetup.updateBalanceInDatabase(userID, updatedAmount);
                                        event.reply(user.getAsMention() + "You picked **7 Down**, it was **7 Down** You Won! You now have " + updatedAmount+" coins :coin:").setEphemeral(false).queue();
                                    }
                                    else {
                                        int updatedAmount = userBalance - bet;
                                        DBSetup.updateBalanceInDatabase(userID, updatedAmount);
                                        event.reply(user.getAsMention() + "You picked **7 Up**, it was **7 Down** You Lost... You now have "+ updatedAmount+" coins :coin:").setEphemeral(false).queue();

                                    }
                                } else if (result > 7 && result!=7) {
                                    if(num > 7) {
                                        int updatedAmount = (int)(userBalance + 1.5*bet);
                                        DBSetup.updateBalanceInDatabase(userID, updatedAmount);
                                        event.reply(user.getAsMention() + "You picked **7 Up**, it was **7 Up** You Won! You now have " + updatedAmount+" coins :coin:").setEphemeral(false).queue();
                                    }
                                    else {
                                        int updatedAmount = userBalance - bet;
                                        DBSetup.updateBalanceInDatabase(userID, updatedAmount);
                                        event.reply(user.getAsMention() + "You picked **7 Down**, it was **7 Up** You Lost... You now have " + updatedAmount+" coins :coin:").setEphemeral(false).queue();

                                    }
                                }
                                else {
                                    if(num==7) {
                                        int updatedAmount = (int)(userBalance + 3*bet);
                                        DBSetup.updateBalanceInDatabase(userID, updatedAmount);
                                        event.reply(user.getAsMention() + "You picked **7**, it was **7** You Won! You now have " + updatedAmount+" coins :coin:").setEphemeral(false).queue();
                                    }
                                    else {
                                        int updatedAmount = userBalance - bet;
                                        DBSetup.updateBalanceInDatabase(userID, updatedAmount);
                                        event.reply(user.getAsMention() + "You picked **7**, it was + " +result+ " You Lost... You now have " + updatedAmount+" coins :coin:").setEphemeral(false).queue();

                                    }
                                }
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
