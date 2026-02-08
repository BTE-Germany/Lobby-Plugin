package dev.nachwahl.lobby.quests;

import dev.nachwahl.lobby.LobbyPlugin;
import dev.nachwahl.lobby.quests.car.CarQuest;
import dev.nachwahl.lobby.quests.mine.MineQuest;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class Timer {

    private int period;

    private int runnable;

    private final Player player;

    private final Quest quest;


    public Timer(Player player, int period, Quest quest) {
        this.player = player;
        this.period = period;
        this.runnable = 0;
        this.quest = quest;
    }

    public void start() {
        this.runnable = Bukkit.getScheduler().scheduleSyncRepeatingTask(LobbyPlugin.getInstance(), () -> {
            this.player.spigot().sendMessage(ChatMessageType.ACTION_BAR, new TextComponent(ChatColor.WHITE + getTimeLeft()));
            this.period = this.period - 1;
            if (this.period < 0) {
                this.player.spigot().sendMessage(ChatMessageType.ACTION_BAR, new TextComponent(ChatColor.DARK_RED + "Zeit ist abgelaufen"));
                Bukkit.getScheduler().cancelTask(this.runnable);
                if (this.quest instanceof MineQuest) {
                    MineQuest mineQuest = (MineQuest) quest;
                    mineQuest.stopQuest();
                } else if (this.quest instanceof CarQuest) {
                    CarQuest carQuest = (CarQuest) quest;
                    carQuest.stopQuest();
                }
            }
        }, 0, 20);
    }

    private String getTimeLeft() {
        long sec = this.period;
        long min = sec / 60;
        long h = min / 60;
        long day = h / 24;
        min %= 60;
        sec %= 60;
        h %= 24;
        if (day == 0) {
            if (h == 0) {
                if (min == 0) {
                    return ("Remaining Time: " + sec + " Sec");
                } else {
                    return ("Remaining Time: " + min + " Min, " + sec + " Sec");
                }
            } else {
                return ("Remaining Time: " + h + " Std, " + min + " Min, " + sec + " Sec");
            }
        } else {
            return ("Remaining Time: " + day + " Day(s), " + h + " Std, " + min + " Min, " + sec + " Sec");
        }

    }
}
