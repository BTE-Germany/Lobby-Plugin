package dev.nachwahl.lobby.quests.listener;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

public class JoinListener implements Listener {
    @EventHandler
    public void onJoin(PlayerJoinEvent event){
        /*Player player = event.getPlayer();
        player.setGlowing(true);
        Scoreboard scoreboard = Bukkit.getScoreboardManager().getMainScoreboard();
        player.setScoreboard(scoreboard);
        Team team = scoreboard.getTeam(ChatColor.BLUE+"team");
        //Team team = scoreboard.registerNewTeam(ChatColor.BLUE+"team");
        team.setColor(ChatColor.BLUE);
        team.setPrefix(ChatColor.DARK_GRAY+"WASDW"+ChatColor.DARK_GRAY);
        team.addEntry(player.getName());*/
    }
}
