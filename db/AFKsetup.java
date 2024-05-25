package org.example.listeners.db;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import org.example.listeners.constants;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

public class AFKsetup extends ListenerAdapter {
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
    public void onMessageReceived(MessageReceivedEvent event) {
        List<User> mentionedUsers = event.getMessage().getMentions().getUsers();
        User user = event.getMember().getUser();
        String author = event.getMember().getId();

        if (event.getAuthor().isBot()) {
            return;
        }
        try {
            String afkStatus = getAFKReason(author);
            if (afkStatus != null) {
                setAFKReason(author, null);
                event.getChannel().sendMessage("AFK reason removed").queue();
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        for (User mentionedUser : mentionedUsers) {
            if(mentionedUser.isBot()) {
                return;
            }
            String mentionedUserId = mentionedUser.getId();
            try {
                String afkStatus = getAFKReason(mentionedUserId);
                if (afkStatus != null) {
                    EmbedBuilder embed = new EmbedBuilder();
                    embed.setTitle("AFK");
                    embed.setFooter("Requested by " + user.getName(), user.getAvatarUrl());
                    embed.setThumbnail(mentionedUser.getAvatarUrl());
                    embed.addField("Reason", afkStatus, true);
                    embed.setColor(constants.color);
                    event.getChannel().sendMessageEmbeds(embed.build()).queue();
                }
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }

        }
        super.onMessageReceived(event);
    }


    @Override
    public void onSlashCommandInteraction(SlashCommandInteractionEvent event) {
        String command = event.getName();
        OptionMapping optionReason = event.getOption("reason");
        String afk_reason = optionReason.getAsString();
        String userID = event.getUser().getId();

        if (command.equalsIgnoreCase("afk")) {
            try {
                if (hasAFKAccount(userID)) {
                    try {
                        setAFKReason(userID, afk_reason);
                        event.reply("AFK reason set succesfully").setEphemeral(true).queue();
                    } catch (SQLException e) {
                        throw new RuntimeException(e);
                    }
                } else {
                    createAFKAccount(userID);
                    try {
                        setAFKReason(userID, afk_reason);
                        event.reply("AFK reason set succesfully").setEphemeral(true).queue();
                    } catch (SQLException e) {
                        throw new RuntimeException(e);
                    }
                }
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }

        }
        super.onSlashCommandInteraction(event);
    }

    public static String getAFKReason(String userID) throws SQLException {
        String SQL_QUERY = "SELECT afk_reason FROM afk_table WHERE user_id = ?";
        String afk_reason = null;

        try (Connection con = ds.getConnection();
             PreparedStatement pst = con.prepareStatement(SQL_QUERY)) {
            pst.setString(1, userID);
            try (ResultSet rs = pst.executeQuery()) {
                if (rs.wasNull()) {
                    afk_reason = rs.getString("afk_reason");
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return afk_reason;
    }

    public static void setAFKReason(String userID, String afk_reason) throws SQLException {
        String SQL_QUERY = "UPDATE afk_table SET afk_reason = ? WHERE user_id = ?";

        try (Connection con = ds.getConnection();
             PreparedStatement pst = con.prepareStatement(SQL_QUERY)) {
            pst.setString(1, afk_reason);
            pst.setString(2, userID);
            pst.executeUpdate();
        }
    }

    public void createAFKAccount(String userId) {
        String SQL_INSERT = "INSERT into afk_table (user_id) VALUES (?)";

        try (Connection con = ds.getConnection();
             PreparedStatement pst = con.prepareStatement(SQL_INSERT)) {
            pst.setString(1, userId);
            pst.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public static boolean hasAFKAccount(String userId) throws SQLException {
        String SQL_QUERY = "SELECT COUNT(*) AS count FROM afk_table WHERE user_id = ?";

        try (Connection con = ds.getConnection();
             PreparedStatement pst = con.prepareStatement(SQL_QUERY)) {
            pst.setString(1, userId);
            try (ResultSet rs = pst.executeQuery()) {
                if (rs.next()) {
                    int count = rs.getInt("count");
                    return count > 0;
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return false;
    }
}

