package org.example.listeners;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.interactions.components.buttons.Button;
import org.example.Main;
import org.example.listeners.db.BankCreate;
import org.example.listeners.db.DBSetup;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.*;

//CHECK CASE FOR 2 ACES
//CHECK CASE FOR AN INTIAL BLACKJACK

class bj {
    ArrayList<Card> deck;
    Random rd = new Random();
    int win = 0;
    Card hiddenCard;
    ArrayList<Card> dealerHand;
    int dealerSum;
    int dealerAceCount;

    ArrayList<Card> playerHand;
    int playerSum;
    int playerAceCount;
    Card card;
    String userIDObj;
    long betObj;
    EmbedBuilder embedObj = new EmbedBuilder();

    public bj(String userID, long bet) {
        this.userIDObj = userID;
        this.betObj = bet;
    }
    static class Card {
        String value;
        String type;

        Card(String value, String type) {
            this.value = value;
            this.type = type;
        }
        public String toString() {
            return value + " " + type;
        }

        public int getValue() {
            if("AJQK".contains(value)) {
                if(value == "A") {
                    return 11;
                }
                return 10;
            }
            return Integer.parseInt(value);
        }

        public boolean isAce() {
            return value == "A";
        }
    }


    public void buildDeck() {
        deck = new ArrayList<>();
        String values[] = {"A", "2", "3", "4", "5", "6", "7", "8", "9", "10", "J", "Q", "K"};
        String type[] = {"<:heartsN:1250077294895038535>", "<:diamonds:1249796154498224149>", "<:spades:1249792484733878332>", "<:clubs:1249790755103576246>"};

        for(int i=0; i<values.length; i++) {
            for(int j=0; j<type.length; j++) {
                Card card = new Card(values[i], type[j]);
                deck.add(card);
            }
        }
    }

    public void shuffleDeck() {
        for(int i=0; i<deck.size(); i++) {
            int j = rd.nextInt(deck.size());
            Card currCard = deck.get(i);
            Card randomCard = deck.get(j);
            deck.set(i, randomCard);
            deck.set(j, currCard);
        }
    }

    public String winner() {
        if(playerSum > 21) {
            return "dealer";
        } else if (dealerSum > 21) {
            return "player";
        } else if(playerSum > dealerSum) {
            return "player";
        } else if (playerSum == dealerSum) {
            return "tie";
        } else {
            return "dealer";
        }
    }

    public int reducePlayerSum() {
        if(playerSum > 21 && playerAceCount > 0) {
            playerSum -= 10;
            playerAceCount -= 1;
        }
        return playerSum;
    }

    public int reduceDealerSum() {
        if(dealerSum > 21 && dealerAceCount > 0) {
            dealerSum -= 10;
            dealerAceCount -= 1;
        }
        return dealerSum;
    }

    public void dealerHit() {
        while(dealerSum < 17) {
            card = deck.remove(deck.size() - 1);
            dealerSum += card.getValue();
            dealerAceCount += card.isAce() ? 1 : 0;
            dealerHand.add(card);

            reduceDealerSum();
        }
    }

    public void playerHit() {
        card = deck.remove(deck.size() - 1);
        playerSum += card.getValue();
        playerAceCount += card.isAce() ? 1 : 0;
        playerHand.add(card);

        reducePlayerSum();
    }
}
public class BlackJ extends ListenerAdapter {
    private static final String DB_URL = "jdbc:sqlite:economy.db";
    private static final Map<String, bj> activeGames = new HashMap<>();

    @Override
    public void onSlashCommandInteraction(SlashCommandInteractionEvent event) {
        String command = event.getName();
        String userID = event.getUser().getId();

        if(command.equalsIgnoreCase("blackjack")) {
                OptionMapping optionBet = event.getOption("bet");
                long bet = optionBet.getAsLong();

                bj game = activeGames.get(userID);

                if(!(game==null)) {
                    event.reply("You have an existing game running").setEphemeral(true).queue();
                    return;
                }

                long userBal;
                try {
                    if(BankCreate.hasAccount(userID)) {
                        try {
                            userBal = DBSetup.getBalanceFromDatabase(userID);
                        } catch (SQLException e) {
                            throw new RuntimeException(e);
                        }
                        if(bet > 0) {
                            try {
                                if(bet <= DBSetup.getBalanceFromDatabase(userID)) {
                                    game = new bj(userID, bet);

                                    game.buildDeck();
                                    game.shuffleDeck();

                                    //Dealer first 2 cards
                                    game.dealerHand = new ArrayList<>();
                                    game.dealerSum = 0;
                                    game.dealerAceCount = 0;

                                    game.hiddenCard = game.deck.remove(game.deck.size() - 1);
                                    game.dealerSum += game.hiddenCard.getValue();
                                    game.dealerAceCount += game.hiddenCard.isAce() ? 1 : 0;
                                    game.dealerHand.add(game.hiddenCard);

                                    game.card = game.deck.remove(game.deck.size() - 1);
                                    game.dealerSum += game.card.getValue();
                                    game.dealerAceCount += game.card.isAce() ? 1 : 0;
                                    game.dealerHand.add(game.card);

                                    game.reduceDealerSum();

                                    //Player first 2 cards

                                    game.playerHand = new ArrayList<>();
                                    game.playerSum = 0;
                                    game.playerAceCount = 0;

                                    for(int i=0; i<2; i++) {
                                        game.card = game.deck.remove(game.deck.size() - 1);
                                        game.playerHand.add(game.card);
                                        game.playerSum += game.card.getValue();
                                        game.playerAceCount += game.card.isAce() ? 1 : 0;
                                    }
                                    game.reducePlayerSum();
                                    activeGames.put(userID, game);
                                    handleInitialBlackjack(userID);

                                    game.embedObj.clearFields();
                                    game.embedObj.setTitle("BlackJack");
                                    game.embedObj.setColor(constants.color);

                                    // Dealer's hand and value
                                    String dealerHandDisplay = "<:cardback:1249804378831851630>  " + String.valueOf(game.dealerHand.get(1));
                                    game.embedObj.addField("Dealer", dealerHandDisplay + " =  **" + (int)(game.dealerSum - game.dealerHand.get(0).getValue()) + "**", false);  // This can be false to place it on a new line

                                    // Player's hand and value
                                    String playerHandDisplay = formatHand(game.playerHand);
                                    game.embedObj.addField("Player", playerHandDisplay + " =  **" + game.playerSum + "**", false);  // This can be false to place it on a new line

                                    if(game.playerSum == 21) {
                                        displayFinalState(userID);
                                        event.replyEmbeds(game.embedObj.build()).queue();
                                    } else {
                                        event.replyEmbeds(game.embedObj.build())
                                                .addActionRow(Button.primary("hit", "Hit"), Button.danger("stand", "Stand"))
                                                .queue();
                                    }
                                } else {
                                    event.reply("You do not sufficient balance missing " + (bet - userBal)+ " :coin:").setEphemeral(true).queue();
                                }
                            } catch (SQLException e) {
                                throw new RuntimeException(e);
                            }
                        } else {
                            event.reply("Invalid bet").setEphemeral(true).queue();
                        }
                    } else {
                        event.reply("You do not have a bank account, create one with /bankcreate").setEphemeral(true).queue();
                    }
                } catch (SQLException e) {
                    throw new RuntimeException(e);
                }
        }
    }
    @Override
    public void onButtonInteraction(ButtonInteractionEvent event) {
        String buttonID = event.getButton().getId();
        String userID = event.getUser().getId();

        bj game = activeGames.get(userID);

        if(game == null) {
            event.reply("not your game").setEphemeral(true).queue();
            return;
        }

        if(buttonID.equals("hit")) {
            if(game.playerSum < 21) {
                game.playerHit();
                String playerHandDisplay = formatHand(game.playerHand);
                String dealerHandDisplay = "<:cardback:1249804378831851630>  " + String.valueOf(game.dealerHand.get(1));

                game.embedObj.clearFields();
                game.embedObj.setTitle("BlackJack");
                game.embedObj.addField("Dealer", dealerHandDisplay + " =  **" + (int)(game.dealerSum - game.dealerHand.get(0).getValue()) + "**", false);  // This can be false to place it on a new line
                game.embedObj.addField("Player", playerHandDisplay + " =  **" + game.playerSum + "**", false);  // This can be false to place it on a new line
                game.embedObj.setColor(constants.color);

                System.out.println(game.playerHand);

                event.deferEdit().queue();
                event.getHook().editOriginalEmbeds(game.embedObj.build()).queue();

                if(game.playerSum >= 21) {
                    displayFinalState(userID);
                    event.deferEdit().queue();
                    event.getHook().editOriginalComponents().queue();
                    event.getHook().editOriginalEmbeds(game.embedObj.build()).queue();
                    return;
                }
            }
        } else if(buttonID.equals("stand")) {

            game.dealerHit();
            displayFinalState(userID);

            event.deferEdit().queue();
            event.getHook().editOriginalComponents().queue();
            event.getHook().editOriginalEmbeds(game.embedObj.build()).queue();
        }

    }

    private void displayFinalState(String userID) {
        bj game = activeGames.get(userID);
        String winner = game.winner();


        String playerHandDisplay = formatHand(game.playerHand);
        String dealerHandDisplay = formatHand(game.dealerHand);

        game.embedObj.clearFields();
        game.embedObj.setTitle("BlackJack");
        game.embedObj.addField("Dealer", dealerHandDisplay + " =  **" + game.dealerSum + "**", false);  // This can be false to place it on a new line
        game.embedObj.addField("Player", playerHandDisplay + " =  **" + game.playerSum + "**", false);  // This can be false to place it on a new line


        switch (winner.toLowerCase()) {
            case "player":
                if (game.playerSum == 21) {
                    game.embedObj.addField("Blackjack!", " Won " + (int)(2.5 * game.betObj) + " :coin:", false);
                    game.embedObj.setColor(constants.WIN_COLOR);
                    game.win = 21;
                } else {
                    game.embedObj.addField("Dealer Busted", " Won " + game.betObj + " :coin:", false);
                    game.embedObj.setColor(constants.WIN_COLOR);
                    game.win = 1;
                }
                break;
            case "dealer":
                if (game.dealerSum == 21) {
                    game.embedObj.addField("Dealer Blackjack", "Lost " + game.betObj + " :coin:", false);
                    game.embedObj.setColor(constants.LOST_COLOR);
                    game.win = 0;
                } else {
                    game.embedObj.addField("Dealer Won", "Lost " + game.betObj + " :coin:", false);
                    game.embedObj.setColor(constants.LOST_COLOR);
                    game.win = 0;
                }
                break;
            default:
                game.embedObj.addField("Tied", "Returned " + game.betObj + " :coin:", false);
                game.embedObj.setColor(constants.color);
                game.win = -1;
                break;
        }

        updateBalance(userID);
        activeGames.remove(userID);

    }

    public String formatHand(ArrayList<bj.Card> hand) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < hand.size(); i++) {
            sb.append(hand.get(i).toString());
            if (i != hand.size() - 1) {
                sb.append("  ");
            }
        }
        return sb.toString();
    }

    private void updateBalance(String userID) {
        bj game = activeGames.get(userID);
        try (Connection connection = DriverManager.getConnection(DB_URL)) {
            long userBal = DBSetup.getBalanceFromDatabase(game.userIDObj);

            if (game.win == 1) {
                DBSetup.updateBalanceInDatabase(game.userIDObj, userBal + game.betObj);
            } else if (game.win == 0) {
                DBSetup.updateBalanceInDatabase(game.userIDObj, userBal - game.betObj);
            } else if (game.win == -1){
                DBSetup.updateBalanceInDatabase(game.userIDObj, userBal);
            } else if (game.win == 21) {
                DBSetup.updateBalanceInDatabase(game.userIDObj, (long)(userBal + (game.betObj * (2.5 - 1))));
            }
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }
    public void handleInitialBlackjack(String userID) {
        bj game = activeGames.get(userID);
        if ((game.dealerSum == 21 && game.dealerHand.size() == 2) ||
                (game.playerSum == 21 && game.playerHand.size() == 2) ||
                (game.dealerSum == 21 && game.playerSum == 21 && game.dealerHand.size() == 2 && game.playerHand.size() ==2)) {
            displayFinalState(userID);
        }
    }
}

