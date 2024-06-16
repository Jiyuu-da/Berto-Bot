//package org.example.listeners;
//
//import java.lang.reflect.Array;
//import java.util.ArrayList;
//import java.util.Random;
//import java.util.Scanner;
//
///*
//    1. Build and Shuffle Deck
//    2. Give dealer 2 cards
//    3. Give player 2 cards
//    4. Handle the case of Ace (reduce to 10 if sum exceeds 21)
//    5. Draw more cards for both dealer and player
//    6. Decide winner
// */
//public class BlackJack {
//    static class Card {
//        String value;
//        String type;
//
//        Card(String value, String type) {
//            this.value = value;
//            this.type = type;
//        }
//        public String toString() {
//            return value + " " + type;
//        }
//
//        public int getValue() {
//            if("AJQK".contains(value)) {
//                if(value == "A") {
//                    return 11;
//                }
//                return 10;
//            }
//            return Integer.parseInt(value);
//        }
//
//        public boolean isAce() {
//            return value == "A";
//        }
//    }
//    Scanner sc = new Scanner(System.in);
//    static ArrayList<Card> deck;
//    Random rd = new Random();
//
//    static Card hiddenCard;
//    static ArrayList<Card> dealerHand;
//    static int dealerSum;
//    static int dealerAceCount;
//
//    static ArrayList<Card> playerHand;
//    static int playerSum;
//    static int playerAceCount;
//    static Card card;
//
//
//    public void buildDeck() {
//        deck = new ArrayList<>();
//        String values[] = {"A", "2", "3", "4", "5", "6", "7", "8", "9", "10", "J", "Q", "K"};
//        String type[] = {"<:heartsN:1250077294895038535>", "<:diamonds:1249796154498224149>", "<:spades:1249792484733878332>", "<:clubs:1249790755103576246>"};
//
//        for(int i=0; i<values.length; i++) {
//            for(int j=0; j<type.length; j++) {
//                Card card = new Card(values[i], type[j]);
//                deck.add(card);
//            }
//        }
//
////        System.out.println("Build deck :");
////        System.out.println(deck);
//    }
//
//    public void shuffleDeck() {
//        for(int i=0; i<deck.size(); i++) {
//            int j = rd.nextInt(deck.size());
//            Card currCard = deck.get(i);
//            Card randomCard = deck.get(j);
//            deck.set(i, randomCard);
//            deck.set(j, currCard);
//        }
//
////        System.out.println("Shuffled Deck :");
////        System.out.println(deck);
//    }
//
//    public void startGame() {
//        //Handle the deck
//        buildDeck();
//        shuffleDeck();
//
//        //Give dealer 2 cards
//        dealerHand = new ArrayList<>();
//        dealerSum = 0;
//        dealerAceCount = 0;
//
//
//        hiddenCard = deck.remove(deck.size() - 1);
//        dealerSum += hiddenCard.getValue();
//        dealerAceCount += hiddenCard.isAce() ? 1 : 0;
//        dealerHand.add(hiddenCard);
//
//        card = deck.remove(deck.size() - 1);
//        dealerSum += card.getValue();
//        dealerAceCount += card.isAce() ? 1 : 0;
//        dealerHand.add(card);
//
//        System.out.println("Dealer : ");
//        System.out.print("?-?  ");
//        System.out.print(card + " Total = ");
//        System.out.println(dealerSum);
//
//
//        //Give player 2 cards
//
//        playerHand = new ArrayList<>();
//        playerSum = 0;
//        playerAceCount = 0;
//
//        for(int i=0; i<2; i++) {
//            card = deck.remove(deck.size() - 1);
//            playerHand.add(card);
//            playerSum += card.getValue();
//            playerAceCount += card.isAce() ? 1 : 0;
//        }
//
//        System.out.println("Player : ");
//        System.out.print(playerHand + " Total = ");
//        System.out.println(playerSum);
//
//        //Draw player cards till 21
//        while(playerSum < 21) {
//            System.out.println("Hit / Stay");
//            String choice = sc.nextLine();
//
//            if(choice.equalsIgnoreCase("hit")) {
//                card = deck.remove(deck.size() - 1);
//                playerSum += card.getValue();
//                playerAceCount += card.isAce() ? 1 : 0;
//                playerHand.add(card);
//
//                reducePlayerSum();
//
//                System.out.println("Player");
//                System.out.print(playerHand + " Total = ");
//                System.out.println(playerSum);
//
//            } else {
//                System.out.println("Player");
//                System.out.print(playerHand + " Total = ");
//                System.out.println(playerSum);
//                break;
//            }
//        }
//
//        //Draw dealer cards till 16
//        while(dealerSum < 17) {
//
//            card = deck.remove(deck.size() - 1);
//            dealerSum += card.getValue();
//            dealerAceCount += card.isAce() ? 1 : 0;
//            dealerHand.add(card);
//
//            reduceDealerSum();
//        }
//
//        System.out.println("Dealer");
//        System.out.print(dealerHand + " Total = ");
//        System.out.println(dealerSum);
//
//        System.out.println(winner() + " WON THE GAME");
////
//
//    }
//
//    public static String winner() {
//        if(playerSum > 21) {
//            return "dealer";
//        } else if (dealerSum > 21) {
//            return "player";
//        } else if(playerSum > dealerSum) {
//            return "player";
//        } else if (playerSum == dealerSum) {
//            return "tie";
//        } else {
//            return "dealer";
//        }
//    }
//
//    public static int reducePlayerSum() {
//        if(playerSum > 21 && playerAceCount > 0) {
//            playerSum -= 10;
//            playerAceCount -= 1;
//        }
//        return playerSum;
//    }
//
//    public static int reduceDealerSum() {
//        if(dealerSum > 21 && dealerAceCount > 0) {
//            dealerSum -= 10;
//            dealerAceCount -= 1;
//        }
//        return dealerSum;
//    }
//
//    public static void dealerHit() {
//        while(BlackJack.dealerSum < 17) {
//            BlackJack.card = BlackJack.deck.remove(BlackJack.deck.size() - 1);
//            BlackJack.dealerSum += BlackJack.card.getValue();
//            BlackJack.dealerAceCount += BlackJack.card.isAce() ? 1 : 0;
//            BlackJack.dealerHand.add(BlackJack.card);
//
//            BlackJack.reduceDealerSum();
//        }
//    }
//    public static void playerHit() {
//        card = deck.remove(deck.size() - 1);
//        playerSum += card.getValue();
//        playerAceCount += card.isAce() ? 1 : 0;
//        playerHand.add(card);
//
//        reducePlayerSum();
//    }
//}
