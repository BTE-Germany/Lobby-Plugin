package dev.nachwahl.lobby.guis.botm;

import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;

class BOTMEntry {

    int month;
    int year;
    String name;
    String player1;
    String player2;
    String player3;

    public BOTMEntry(int month, int year) {
        this.month = month;
        this.year = year;
    }

    public int getMonth() {
        return month;
    }

    public void setMonth(int month) {
        this.month = month;
    }

    public int getYear() {
        return year;
    }

    public void setYear(int year) {
        this.year = year;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPlayer1() {
        return player1;
    }

    public void setPlayer1(String player1) {
        this.player1 = player1;
    }

    public String getPlayer2() {
        return player2;
    }

    public void setPlayer2(String player2) {
        this.player2 = player2;
    }

    public String getPlayer3() {
        return player3;
    }

    public void setPlayer3(String player3) {
        this.player3 = player3;
    }
}

public class EntryUtil {

    public static Map<Player, BOTMEntry> entries = new HashMap<>();

    public static void addEntry(Player player, int month, int year) {
        BOTMEntry entry = new BOTMEntry(month, year);
        entries.put(player, entry);
    }

    public static BOTMEntry getEntry(Player player) {
        return entries.get(player);
    }

}
