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
            getGermanHologram().forceHideHologram(player);
            getEnglischHologram().forceShowHologram(player);
        } else if (language == Language.GERMAN) {
            getEnglischHologram().forceHideHologram(player);
            getGermanHologram().forceShowHologram(player);
        }
    }

    public void updateHolograms() {
        de.oliver.fancyholograms.api.hologram.@org.jetbrains.annotations.NotNull Hologram germanHologram = getOrCreateGermanHologram();
        de.oliver.fancyholograms.api.hologram.@org.jetbrains.annotations.NotNull Hologram englishHologram = getOrCreateEnglischHologram();

        ((de.oliver.fancyholograms.api.data.TextHologramData) germanHologram.getData()).setText(germanText);
        germanHologram.forceUpdate();
        ((de.oliver.fancyholograms.api.data.TextHologramData) englishHologram.getData()).setText(englishText);
        englishHologram.forceUpdate();
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

    private de.oliver.fancyholograms.api.hologram.@org.jetbrains.annotations.NotNull Hologram getOrCreateGermanHologram() {
        if (getGermanHologram() == null) {
            de.oliver.fancyholograms.api.data.TextHologramData hologramData = new de.oliver.fancyholograms.api.data.TextHologramData(getId() +
                "_GER", getLocation());
            hologramData.setVisibility(de.oliver.fancyholograms.api.data.property.Visibility.MANUAL);
            hologramData.setPersistent(false);
            manager.addHologram(manager.create(hologramData));
        }
        return getGermanHologram();
    }

    private de.oliver.fancyholograms.api.hologram.@org.jetbrains.annotations.NotNull Hologram getOrCreateEnglischHologram() {
        if (getEnglischHologram() == null) {
            de.oliver.fancyholograms.api.data.TextHologramData hologramData = new de.oliver.fancyholograms.api.data.TextHologramData(getId() + "_EN", getLocation());
            hologramData.setVisibility(de.oliver.fancyholograms.api.data.property.Visibility.MANUAL);
            hologramData.setPersistent(false);
            manager.addHologram(manager.create(hologramData));
        }
        return getGermanHologram();
    }

    private de.oliver.fancyholograms.api.hologram.Hologram getGermanHologram() {
        return manager.getHologram(getId() + "_GER").orElse(null);
    }

    private de.oliver.fancyholograms.api.hologram.Hologram getEnglischHologram() {
        return manager.getHologram(getId() + "_EN").orElse(null);
    }
}
