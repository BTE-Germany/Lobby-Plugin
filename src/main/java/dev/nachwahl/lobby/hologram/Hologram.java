package dev.nachwahl.lobby.hologram;

import de.oliver.fancyholograms.api.data.*;
import dev.nachwahl.lobby.language.*;
import lombok.*;
import org.bukkit.*;
import org.bukkit.entity.*;
import de.oliver.fancyholograms.api.data.property.Visibility;
import org.jetbrains.annotations.*;
import de.oliver.fancyholograms.api.HologramManager;
import de.oliver.fancyholograms.api.FancyHologramsPlugin;

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

    HologramManager manager = FancyHologramsPlugin.get().getHologramManager();

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
        de.oliver.fancyholograms.api.hologram.Hologram germanHologram = getGermanHologram();
        de.oliver.fancyholograms.api.hologram.Hologram englischHologram = getGermanHologram();

        if (germanHologram == null || englischHologram == null) {
            return;
        }

        if (language == Language.ENGLISH) {
            germanHologram.forceHideHologram(player);
            englischHologram.forceShowHologram(player);
        } else if (language == Language.GERMAN) {
            englischHologram.forceHideHologram(player);
            germanHologram.forceShowHologram(player);
        }
    }

    public void updateHolograms() {
        if (getOrCreateGermanHologram().getData() instanceof TextHologramData data) {
            data.setText(germanText);
        }
        if (getOrCreateEnglischHologram().getData() instanceof TextHologramData data) {
            data.setText(englishText);
        }
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

    private de.oliver.fancyholograms.api.hologram.@NotNull Hologram getOrCreateGermanHologram() {
        de.oliver.fancyholograms.api.hologram.Hologram hologram = getGermanHologram();
        if (hologram == null) {
            TextHologramData hologramData = new TextHologramData(getId() + "_GER", getLocation());
            hologramData.setVisibility(Visibility.MANUAL);
            hologramData.setPersistent(false);
            hologram = manager.create(hologramData);
            manager.addHologram(hologram);
        }
        return hologram;
    }

    private de.oliver.fancyholograms.api.hologram.@NotNull Hologram getOrCreateEnglischHologram() {
        de.oliver.fancyholograms.api.hologram.Hologram hologram = getEnglischHologram();
        if (hologram == null) {
            TextHologramData hologramData = new TextHologramData(getId() + "_EN", getLocation());
            hologramData.setVisibility(Visibility.MANUAL);
            hologramData.setPersistent(false);
            hologram = manager.create(hologramData);
            manager.addHologram(hologram);
        }
        return hologram;
    }

    private de.oliver.fancyholograms.api.hologram.@Nullable Hologram getGermanHologram() {
        return manager.getHologram(getId() + "_GER").orElse(null);
    }

    private de.oliver.fancyholograms.api.hologram.@Nullable Hologram getEnglischHologram() {
        return manager.getHologram(getId() + "_EN").orElse(null);
    }
}
