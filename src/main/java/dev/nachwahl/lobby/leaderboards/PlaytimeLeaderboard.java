package dev.nachwahl.lobby.leaderboards;

import dev.nachwahl.lobby.Lobby;
import dev.nachwahl.lobby.language.Language;
import dev.nachwahl.lobby.plan.user.User;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

public class PlaytimeLeaderboard extends Leaderboard{


    public PlaytimeLeaderboard(Lobby lobby, String loc) throws SQLException {
        super(lobby, loc);
    }

    @Override
    public void load() {
        if(getInstance().getPlanQuery()==null) return;

        ArrayList<User> users = getInstance().getPlanQuery().getTopPlaytimeOnAllServers(TimeUnit.DAYS.toMillis(30L),0);
        ArrayList<String> germanText = new ArrayList<>();
        ArrayList<String> englishText = new ArrayList<>();

        germanText.add(getInstance().getLanguageAPI().getMessage(Language.GERMAN,"leaderboard.playtime", Placeholder.parsed("time","30")).toString());
        germanText.add("");
        englishText.add(getInstance().getLanguageAPI().getMessage(Language.ENGLISH,"leaderboard.playtime", Placeholder.parsed("time","30")).toString());
        englishText.add("");

        for (int i = 1; i <= users.size(); i++) {
            User user = users.get(i-1);

            germanText.add(i+". "+user.getPlayer()+": "+user.getPlaytimeReadable());
            englishText.add(i+". "+user.getPlayer()+": "+user.getPlaytimeReadable());
        }

        getHologram().setText(englishText,germanText);
    }
}
