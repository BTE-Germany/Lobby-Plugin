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

    private static final String ENGLISCH_HOLOGRAM_ENDING = "_EN";
    private static final String GERMAN_HOLOGRAM_ENDING = "_GER";
    private final HologramManager manager = FancyHologramsPlugin.get().getHologramManager();

    public Hologram(Location location, List<String> englishText, List<String> germanText, String id) {
        this.location = location;
        this.englishText = englishText;
        this.germanText = germanText;
        players = new ArrayList<>();
        this.id = id;

        createHologramWhenNotExists(null, ENGLISCH_HOLOGRAM_ENDING, englishText);
        createHologramWhenNotExists(null, GERMAN_HOLOGRAM_ENDING, germanText);
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
            englischHologram.forceShowHologram(player);
            germanHologram.forceHideHologram(player);
        } else if (language == Language.GERMAN) {
            germanHologram.forceShowHologram(player);
            englischHologram.forceHideHologram(player);
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
        de.oliver.fancyholograms.api.hologram.Hologram hologram = getEnglischHologram();
        if (hologram != null) {
            manager.removeHologram(hologram);
        }

        hologram = getGermanHologram();
        if (hologram != null) {
            manager.removeHologram(hologram);
        }
    }

    public void setText(List<String> englishText, List<String> germanText) {
        this.englishText = englishText;
        this.germanText = germanText;
        updateHolograms();
    }

    private de.oliver.fancyholograms.api.hologram.@NotNull Hologram getOrCreateGermanHologram() {
        return createHologramWhenNotExists(getGermanHologram(), GERMAN_HOLOGRAM_ENDING, germanText);
    }

    private de.oliver.fancyholograms.api.hologram.@NotNull Hologram getOrCreateEnglischHologram() {
        return createHologramWhenNotExists(getEnglischHologram(), ENGLISCH_HOLOGRAM_ENDING, englishText);
    }

    private de.oliver.fancyholograms.api.hologram.@NotNull Hologram createHologramWhenNotExists(de.oliver.fancyholograms.api.hologram.Hologram hologram, String ending, List<String> text) {
        de.oliver.fancyholograms.api.hologram.Hologram iHologram = hologram;
        if (hologram == null) {
            TextHologramData hologramData = new TextHologramData(getId() + ending, getLocation());
            hologramData.setVisibility(Visibility.MANUAL);
            hologramData.setPersistent(false);
            hologramData.setText(text);
            iHologram = manager.create(hologramData);
            manager.addHologram(iHologram);
        }
        return iHologram;
    }

    private de.oliver.fancyholograms.api.hologram.@Nullable Hologram getGermanHologram() {
        return manager.getHologram(getId() + GERMAN_HOLOGRAM_ENDING).orElse(null);
    }

    private de.oliver.fancyholograms.api.hologram.@Nullable Hologram getEnglischHologram() {
        return manager.getHologram(getId() + ENGLISCH_HOLOGRAM_ENDING).orElse(null);
    }
}
