package dev.nachwahl.lobby.utils;

import dev.nachwahl.lobby.Lobby;
import lombok.Getter;
import org.bukkit.Location;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.List;

public class MiniGameBlockUtil {

    @Getter
    private FileConfiguration dataFile;
    private final Lobby plugin;

    public MiniGameBlockUtil(Lobby plugin) {
        this.plugin = plugin;

        loadData();

    }

    public void loadData() {
        File file = new File(plugin.getDataFolder() + File.separator + "minigameblocks.yml");
        if (!file.exists()) {
            file = createFile();
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
        File file = new File(plugin.getDataFolder() + File.separator + "minigameblocks.yml");
        dataFile.save(file);
        dataFile = YamlConfiguration.loadConfiguration(file);
    }

    private File createFile() {
        File file = new File(plugin.getDataFolder() + File.separator + "minigameblocks.yml");
        if (!file.exists()) {
            try {
                file.createNewFile();
                dataFile = YamlConfiguration.loadConfiguration(file);
                saveFile();
                return file;
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    public static void setGameTitleHoverTexts(@org.jetbrains.annotations.NotNull String game) {
        for (String s : Lobby.getInstance().getMiniGameBlockUtil().getList(game.toLowerCase())) {
            Location loc = Lobby.getInstance().getLocationAPI().parseLocation(s);
            Location locHD = new Location(loc.getWorld(), loc.getBlockX() + 0.5, loc.getBlockY() + 3.5, loc.getBlockZ() + 0.5);
            de.oliver.fancyholograms.api.HologramManager manager = de.oliver.fancyholograms.api.FancyHologramsPlugin.get().getHologramManager();
            de.oliver.fancyholograms.api.data.TextHologramData data =
                new de.oliver.fancyholograms.api.data.TextHologramData(game + "_" + locHD.getBlockX() + "-" + locHD.getBlockZ(),
                locHD);
            data.setPersistent(false);
            data.addLine("§9§l" + game);
            manager.addHologram(manager.create(data));
        }
    }

    public static void setGameTitleHoverText(String game, @org.jetbrains.annotations.NotNull Location loc) {
        Location locHD = new Location(loc.getWorld(), loc.getBlockX() + 0.5, loc.getBlockY() + 3.5, loc.getBlockZ() + 0.5);
        de.oliver.fancyholograms.api.HologramManager manager = de.oliver.fancyholograms.api.FancyHologramsPlugin.get().getHologramManager();
        de.oliver.fancyholograms.api.data.TextHologramData data =
            new de.oliver.fancyholograms.api.data.TextHologramData(game + "_" + locHD.getBlockX() + "-" + locHD.getBlockZ(),
                locHD);
        data.addLine("§9§l" + game);
        data.setPersistent(false);
        manager.addHologram(manager.create(data));
    }


    public static void deleteHologram(String game, @org.jetbrains.annotations.NotNull Location locHD) {
        de.oliver.fancyholograms.api.HologramManager manager = de.oliver.fancyholograms.api.FancyHologramsPlugin.get().getHologramManager();
        java.util.Optional<de.oliver.fancyholograms.api.hologram.Hologram> hologram =
            manager.getHologram(game + "_" + locHD.getBlockX() + "-" + locHD.getBlockZ());
        hologram.ifPresent(manager::removeHologram);
    }

    public static void reloadHolograms() {
        String[] games = {"TicTacToe", "Connect4", "UNO", "BattleShip", "RockPaperScissors"};
        for (String s : games) setGameTitleHoverTexts(s);
    }

}
