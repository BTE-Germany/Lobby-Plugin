package dev.nachwahl.lobby.leaderboards;

import co.aikar.idb.Database;
import co.aikar.idb.DbRow;
import dev.nachwahl.cosmetics.Cosmetics;
import dev.nachwahl.lobby.Lobby;
import eu.decentsoftware.holograms.api.DHAPI;
import eu.decentsoftware.holograms.api.holograms.Hologram;
import eu.decentsoftware.holograms.api.holograms.HologramPage;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class JnRLeaderboard {

    private final Database database;
    private final ScheduledExecutorService scheduledExecutorService;
    private static final String DH_TAG = "jnr";

    public JnRLeaderboard(Lobby lobby) {
        this.database = lobby.getDatabase();
        this.scheduledExecutorService = Executors.newSingleThreadScheduledExecutor();
        this.scheduledExecutorService.scheduleAtFixedRate(this::update, 5, 30, TimeUnit.SECONDS);
    }

    public void update() {
        Hologram hologram = DHAPI.getHologram(DH_TAG);
        if (hologram == null) {
            return;
        }

        final HologramPage page = hologram.getPage(0);
        final int titleLines = 2;
        final int topCount = 10;

        while (page.getLines().size() < titleLines + topCount + 2) {
            DHAPI.addHologramLine(page, "");
        }
        page.setLine(0, "§d§lJump n Run");

        List<String> newLines;
        try {
            newLines = this.getTopScores();
        } catch (SQLException e) {
            Cosmetics.getInstance().getLogger().warning(e.toString());
            return;
        }

        for (int i = 0; i < topCount; i++) {
            if (i > newLines.size() - 1) {
                page.setLine(i + titleLines, "");
                continue;
            }
            page.setLine(i + titleLines, newLines.get(i));
        }


        page.setLine(titleLines + topCount + 1, "%bteg-lobby_own-jnr-score%");
    }

    private List<String> getTopScores() throws SQLException {
        List<DbRow> dbRows = this.database.getResults("SELECT scores.score, players.name FROM parkour_scores AS scores, parkour_players AS players WHERE scores.player = players.id AND area = \"JnR\" ORDER BY score DESC LIMIT 10");

        List<String> linesContent = new ArrayList<>();
        for (int i = 0; i < dbRows.size(); i++) {
            DbRow dbRow = dbRows.get(i);
            int rank = i + 1;
            String playerName = dbRow.getString("name");
            int score = dbRow.getInt("score");
            linesContent.add(JnRLeaderboard.getLineContent(rank, playerName, score, false));
        }

        return linesContent;
    }

    public static String getLineContent(int rank, String playerName, int score, boolean bold) {
        return "§d" + (bold ? "§l" : "") + rank
                + ". §f" + (bold ? "§l" : "") + playerName
                + ": §d" + (bold ? "§l" : "") + score;
    }

    public void cancel() {
        this.scheduledExecutorService.shutdownNow();
    }

}
