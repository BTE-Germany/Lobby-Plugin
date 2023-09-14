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

        for (int i = 1; i <= 10; i++) {
            if(i >= users.size()) continue;

            User user = users.get(i - 1);

            germanText.add(ChatColor.GOLD + "" + i + ChatColor.DARK_GRAY + ". " + ChatColor.AQUA + user.getPlayer() + ChatColor.DARK_GRAY + ": " + ChatColor.GREEN + getPlaytimeReadable(user.getPlaytime()));
            englishText.add(ChatColor.GOLD + "" + i + ChatColor.DARK_GRAY + ". " + ChatColor.AQUA + user.getPlayer() + ChatColor.DARK_GRAY + ": " + ChatColor.GREEN +getPlaytimeReadable(user.getPlaytime()));
        }

        // TODO
        //germanText.add(ChatColor.GOLD + "Total"+ ChatColor.DARK_GRAY + ": "  + ChatColor.GREEN + getPlaytimeReadable(getTotalPlaytime(users)));
        //englishText.add(ChatColor.GOLD + "Total" + ChatColor.DARK_GRAY + ": " + ChatColor.GREEN + getPlaytimeReadable(getTotalPlaytime(users)));

        getHologram().setText(englishText, germanText);
    }

    public long getTotalPlaytime(ArrayList<User> users) {
        long playtime = 0;
        for(User user: users) playtime+= user.getPlaytime();
        return playtime;
    }
    public String getPlaytimeReadable(long playtime) {
        long pt = playtime;
        if(pt<60000) {
            return "<1min";
        }

        StringBuilder sb = new StringBuilder();

        // Weeks
        if (pt >= 604800000) {
            int days = (int) (pt / 604800000);
            sb.append(days).append("w ");
            pt -= days * 604800000;
        }
        // Days
        if (pt >= 86400000) {
            int days = (int) (pt / 86400000);
            sb.append(days).append("d ");
            pt -= days * 86400000;
        }

        // Hours
        if (pt >= 3600000) {
            int hours = (int) (pt / 3600000);
            sb.append(hours).append("h ");
            pt -= hours * 3600000;
        }

        // Minutes
        if (pt >= 60000) {
            int minutes = (int) (pt / 60000);
            sb.append(minutes).append("min ");
            pt -= minutes * 60000;
        }

        return sb.toString();
    }
}
