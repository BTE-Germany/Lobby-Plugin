package dev.nachwahl.lobby.hologram;

import dev.nachwahl.lobby.language.*;
import lombok.*;
import org.bukkit.*;
import org.bukkit.entity.*;

import java.util.*;

public class Hologram {

    @Getter
    private final Location location;
    @Getter
    private List<String> englishText;
    @Getter
    private List<String> germanText;
    @Getter
    @Setter
    private ArrayList<Player> players;
    @Getter
    private final String id;

    de.oliver.fancyholograms.api.HologramManager manager = de.oliver.fancyholograms.api.FancyHologramsPlugin.get().getHologramManager();

    public Hologram(Location location, List<String> englishText, List<String> germanText, String id) {
        this.location = location;
        this.englishText = englishText;
        this.germanText = germanText;
        players = new ArrayList<>();
        this.id = id;

        updateHolograms();
    }

    public Hologram(Location location, String[] englishText, String[] germanText, String id) {
        this(location, Arrays.asList(englishText), Arrays.asList(germanText), id);
    }

    public void setPlayer(Player player, Language language) {
        if (language == Language.ENGLISH) {
            getGermanHologram().hideHologram(player);
            getEnglischHologram().showHologram(player);
        } else if (language == Language.GERMAN) {
            getEnglischHologram().hideHologram(player);
            getGermanHologram().showHologram(player);
        }
    }

    public void removePlayer(Player player) {
        getGermanHologram().hideHologram(player);
        getEnglischHologram().hideHologram(player);
    }

    public void updateHolograms() {
        de.oliver.fancyholograms.api.data.@org.jetbrains.annotations.NotNull TextHologramData germanHologram = getOrCreateGermanHologram();
        de.oliver.fancyholograms.api.data.@org.jetbrains.annotations.NotNull TextHologramData englishHologram = getOrCreateEnglischHologram();

        germanHologram.setText(germanText);
        englishHologram.setText(englishText);
    }

    public void delete() {
        if (getEnglischHologram() != null) {
            manager.removeHologram(getEnglischHologram());
        }

        if (getGermanHologram() != null) {
            manager.removeHologram(getGermanHologram());
        }
    }

    public void setText(List<String> englishText, List<String> germanText) {
        this.englishText = englishText;
        this.germanText = germanText;
        updateHolograms();
    }

    private de.oliver.fancyholograms.api.data.@org.jetbrains.annotations.NotNull TextHologramData getOrCreateGermanHologram() {
        de.oliver.fancyholograms.api.data.TextHologramData hologramData;
        if (getGermanHologram() == null) {
            hologramData = new de.oliver.fancyholograms.api.data.TextHologramData(getId() + "_GER", getLocation());
            hologramData.setVisibility(de.oliver.fancyholograms.api.data.property.Visibility.MANUAL);
            manager.create(hologramData);
        } else {
            hologramData = (de.oliver.fancyholograms.api.data.TextHologramData) getGermanHologram().getData();
        }
        return hologramData;
    }

    private de.oliver.fancyholograms.api.data.@org.jetbrains.annotations.NotNull TextHologramData getOrCreateEnglischHologram() {
        de.oliver.fancyholograms.api.data.TextHologramData hologramData;
        if (getEnglischHologram() == null) {
            hologramData = new de.oliver.fancyholograms.api.data.TextHologramData(getId() + "_EN", getLocation());
            hologramData.setVisibility(de.oliver.fancyholograms.api.data.property.Visibility.MANUAL);
            manager.create(hologramData);
        } else {
            hologramData = (de.oliver.fancyholograms.api.data.TextHologramData) getEnglischHologram().getData();
        }
        return hologramData;
    }

    private de.oliver.fancyholograms.api.hologram.Hologram getGermanHologram() {
        return manager.getHologram(getId() + "_GER").orElse(null);
    }

    private de.oliver.fancyholograms.api.hologram.Hologram getEnglischHologram() {
        return manager.getHologram(getId() + "_EN").orElse(null);
    }
}
