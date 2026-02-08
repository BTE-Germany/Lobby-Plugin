package dev.nachwahl.lobby.utils;

import de.oliver.fancyholograms.api.FancyHologramsPlugin;
import de.oliver.fancyholograms.api.HologramManager;
import de.oliver.fancyholograms.api.data.TextHologramData;
import de.oliver.fancyholograms.api.hologram.Hologram;
import dev.nachwahl.lobby.LobbyPlugin;
import lombok.Getter;
import net.kyori.adventure.text.Component;
import org.bukkit.Location;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

public class MiniGameBlockUtil {

    public static final String FILE_NAME = "minigameblocks.yml";
    @Getter
    private FileConfiguration dataFile;
    private final LobbyPlugin plugin;

    public static final String FORMATTING_CODE = "<blue><bold>";

    public MiniGameBlockUtil(LobbyPlugin plugin) {
        this.plugin = plugin;

        loadData();

    }

    public void loadData() {
        File file = new File(plugin.getDataFolder() + File.separator + FILE_NAME);
        if (!file.exists()) {
            file = createFile();
            if (file == null) {
                plugin.getComponentLogger().error(Component.text("Error while creating " + FILE_NAME + " file."));
                return;
            }
        }
        dataFile = YamlConfiguration.loadConfiguration(file);
    }

    public List<String> getList(String path) {
        return dataFile.getStringList(path);
    }

    public Location getLocation(String path) {
        return plugin.getLocationAPI().parseLocation(dataFile.getString(path));
    }

    public void saveFile() throws IOException {
        File file = new File(plugin.getDataFolder() + File.separator + FILE_NAME);
        dataFile.save(file);
        dataFile = YamlConfiguration.loadConfiguration(file);
    }

    private @Nullable File createFile() {
        File file = new File(plugin.getDataFolder() + File.separator + FILE_NAME);
        if (!file.exists()) {
            try {
                if (!file.createNewFile()) return null;
                dataFile = YamlConfiguration.loadConfiguration(file);
                saveFile();
                return file;
            } catch (IOException e) {
                LobbyPlugin.getInstance().getComponentLogger().error(Component.text("Error accorded while writing " + FILE_NAME + " file."), e);
            }
        }
        return null;
    }

    public static void setGameTitleHoverTexts(@NotNull String game) {
        for (String s : LobbyPlugin.getInstance().getMiniGameBlockUtil().getList(game.toLowerCase())) {
            Location loc = LobbyPlugin.getInstance().getLocationAPI().parseLocation(s);
            setGameTitleHoverText(game, loc);
        }
    }

    public static void setGameTitleHoverText(String game, @NotNull Location loc) {
        Location locHD = new Location(loc.getWorld(), loc.getBlockX() + 0.5, loc.getBlockY() + 3.5, loc.getBlockZ() + 0.5);
        HologramManager manager = FancyHologramsPlugin.get().getHologramManager();
        TextHologramData data = new TextHologramData(game + "_" + locHD.getBlockX() + "-" + locHD.getBlockZ(), locHD);
        data.setText(Collections.singletonList(FORMATTING_CODE + game));
        data.setPersistent(false);
        manager.addHologram(manager.create(data));
    }

    public static void deleteHologram(String game, @NotNull Location locHD) {
        HologramManager manager = FancyHologramsPlugin.get().getHologramManager();
        Optional<Hologram> hologram = manager.getHologram(game + "_" + locHD.getBlockX() + "-" + locHD.getBlockZ());
        hologram.ifPresent(manager::removeHologram);
    }

    public static void reloadHolograms() {
        String[] games = {"TicTacToe", "Connect4", "UNO", "BattleShip", "RockPaperScissors"};
        for (String s : games) setGameTitleHoverTexts(s);
    }
}
