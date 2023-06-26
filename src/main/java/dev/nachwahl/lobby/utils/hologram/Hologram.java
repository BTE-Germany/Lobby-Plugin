package dev.nachwahl.lobby.utils.hologram;

import dev.nachwahl.lobby.Lobby;
import dev.nachwahl.lobby.utils.language.Language;
import lombok.Getter;
import lombok.Setter;
import me.filoghost.holographicdisplays.api.Position;
import me.filoghost.holographicdisplays.api.hologram.VisibilitySettings;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Arrays;

public class Hologram {

    @Getter
    private Position location;
    @Getter
    private ArrayList<String> englishText;
    @Getter
    private ArrayList<String> germanText;
    @Getter @Setter
    private ArrayList<Player> players;

    private me.filoghost.holographicdisplays.api.hologram.Hologram englishHologram;
    private me.filoghost.holographicdisplays.api.hologram.Hologram germanHologram;

    public Hologram(Position location, ArrayList<String> englishText, ArrayList<String> germanText) {
        this.location = location;
        this.englishText = englishText;
        this.germanText = germanText;
        players = new ArrayList<>();

        updateHolograms();
    }

    public Hologram(Position location, String[] englishText, String[] germanText) {
        this.location = location;
        this.englishText = new ArrayList<>(Arrays.asList(englishText));
        this.germanText = new ArrayList<>(Arrays.asList(germanText));
        players = new ArrayList<>();

        updateHolograms();
    }

    public void setPlayer(Player player, Language language) {
        if(language == Language.ENGLISH) {
            germanHologram.getVisibilitySettings().setIndividualVisibility(player, VisibilitySettings.Visibility.HIDDEN);
            englishHologram.getVisibilitySettings().setIndividualVisibility(player, VisibilitySettings.Visibility.VISIBLE);
        } else if (language == Language.GERMAN) {
            englishHologram.getVisibilitySettings().setIndividualVisibility(player, VisibilitySettings.Visibility.HIDDEN);
            germanHologram.getVisibilitySettings().setIndividualVisibility(player, VisibilitySettings.Visibility.VISIBLE);
        }
    }

    public void removePlayer(Player player) {
        englishHologram.getVisibilitySettings().setIndividualVisibility(player, VisibilitySettings.Visibility.HIDDEN);
        germanHologram.getVisibilitySettings().setIndividualVisibility(player, VisibilitySettings.Visibility.HIDDEN);
    }

    public void updateHolograms() {
        englishHologram = Lobby.getInstance().getHologramAPI().getApi().createHologram(location);
        germanHologram = Lobby.getInstance().getHologramAPI().getApi().createHologram(location);
        englishHologram.getVisibilitySettings().setGlobalVisibility(VisibilitySettings.Visibility.HIDDEN);
        germanHologram.getVisibilitySettings().setGlobalVisibility(VisibilitySettings.Visibility.HIDDEN);

        for(String line: englishText) {
            englishHologram.getLines().appendText((line.startsWith("§l")? ChatColor.BOLD:"")+line);
        }
        for(String line: germanText) {
            germanHologram.getLines().appendText((line.startsWith("§l")? ChatColor.BOLD:"")+line);
        }
    }

    public void delete() {
        englishHologram.delete();
        germanHologram.delete();
    }
}
