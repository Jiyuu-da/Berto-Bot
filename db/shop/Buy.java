//package org.example.listeners.db.shop;
//
//import com.zaxxer.hikari.HikariConfig;
//import com.zaxxer.hikari.HikariDataSource;
//import net.dv8tion.jda.api.EmbedBuilder;
//import net.dv8tion.jda.api.entities.User;
//import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
//import net.dv8tion.jda.api.hooks.ListenerAdapter;
//import net.dv8tion.jda.api.interactions.commands.OptionMapping;
//import org.example.listeners.constants;
//import org.example.listeners.db.BankCreate;
//import org.example.listeners.db.DBSetup;
//import org.example.listeners.db.shop.Items;
//
//import java.sql.SQLException;
//import java.time.Instant;
//import java.time.temporal.ChronoUnit;
//
//public class Buy extends ListenerAdapter {
//    private static HikariConfig config = new HikariConfig();
//    private static HikariDataSource ds;
//
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
//        String command = event.getName();
//        OptionMapping itemOption = event.getOption("item");
//        String item = itemOption.getAsString();
//        User user = event.getUser();
//        String userID = event.getUser().getId();
//
//       if(command.equalsIgnoreCase("buy")) {
//           try {
//               if(BankCreate.hasAccount(userID)) {
//                   switch (item) {
//                       case "shield":
//                           Instant now = Instant.now();
//                           Instant shieldExpiry = Items.getShieldExpiry(userID);
//                           long remainingMinutes = ChronoUnit.MINUTES.between(now, shieldExpiry);
//
//                           long hoursRemaining = remainingMinutes / 60;
//                           long minutesRemaining = remainingMinutes % 60;
//                           if(Items.hasShield(user) == 1) {
//
//
//                               event.reply("You already have a shield for "+ hoursRemaining + "h " + minutesRemaining + "m").setEphemeral(true).queue();
//                           } else {
//                               if(DBSetup.getBalanceFromDatabase(userID) >= 120) {
//                                   Instant shieldExpiryTimestamp = Instant.now().plus(17, ChronoUnit.HOURS);
//                                   Items.activateShield(user, shieldExpiryTimestamp);
//                                   int reducedBalance = DBSetup.getBalanceFromDatabase(userID) - 120;
//                                   DBSetup.updateBalanceInDatabase(userID, reducedBalance);
//
//
//                                   event.reply("Shield activated!").setEphemeral(true).queue();
//                               } else {
//                                   int difference = 120 - DBSetup.getBalanceFromDatabase(userID);
//                                   event.reply("You are missing " + difference + " coins :coin: to buy the shield").setEphemeral(true).queue();
//                               }
//                           }
//
//                           break;
//                       default:
//                           event.reply("Invalid Item").setEphemeral(true).queue();
//                   }
//
//
//               } else {
//                   event.reply("This command is under maintenance you do not have the required perms").setEphemeral(true).queue();
//               }
//           } catch (SQLException e) {
//               throw new RuntimeException(e);
//           }
//       }
//        super.onSlashCommandInteraction(event);
//    }
//}
