package dev.nachwahl.lobby.leaderboards;

import dev.nachwahl.lobby.Lobby;
import dev.nachwahl.lobby.language.Language;
import dev.nachwahl.lobby.plan.user.User;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

public class PlaytimeLeaderboard extends Leaderboard {


    public PlaytimeLeaderboard(Lobby lobby, String loc) throws SQLException {
        super(lobby, loc);
    }

    @Override
    public void load() {
        if (getInstance().getPlanQuery() == null) {
            Bukkit.getLogger().info("[Lobby] PlaytimeLeaderboard not initialized because Plan is not loaded.");
            return;
        }

        ArrayList<User> users = getInstance().getPlanQuery().getTopPlaytimeOnAllServers(TimeUnit.DAYS.toMillis(30L), 0);
        ArrayList<String> germanText = new ArrayList<>();
        ArrayList<String> englishText = new ArrayList<>();
        germanText.add(ChatColor.translateAlternateColorCodes('&', LegacyComponentSerializer.legacyAmpersand().serialize(getInstance().getLanguageAPI().getMessage(Language.GERMAN, "leaderboard.playtime", Placeholder.parsed("time", "30")))));
        germanText.add("");
        englishText.add(ChatColor.translateAlternateColorCodes('&', LegacyComponentSerializer.legacyAmpersand().serialize(getInstance().getLanguageAPI().getMessage(Language.ENGLISH, "leaderboard.playtime", Placeholder.parsed("time", "30")))));
        englishText.add("");

        for (int i = 1; i <= users.size(); i++) {
            User user = users.get(i - 1);

            germanText.add(ChatColor.GOLD + "" + i + ChatColor.DARK_GRAY + ". " + ChatColor.AQUA + user.getPlayer() + ChatColor.DARK_GRAY + ": " + ChatColor.GREEN + user.getPlaytimeReadable());
            englishText.add(ChatColor.GOLD + "" + i + ChatColor.DARK_GRAY + ". " + ChatColor.AQUA + user.getPlayer() + ChatColor.DARK_GRAY + ": " + ChatColor.GREEN + user.getPlaytimeReadable());
        }
        System.out.println("set text playtime");
        getHologram().setText(englishText, germanText);
    }
}
