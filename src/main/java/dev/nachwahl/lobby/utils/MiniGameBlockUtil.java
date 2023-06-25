package dev.nachwahl.lobby.utils;

import dev.nachwahl.lobby.Lobby;
import lombok.Getter;
import me.filoghost.holographicdisplays.api.HolographicDisplaysAPI;
import me.filoghost.holographicdisplays.api.Position;
import me.filoghost.holographicdisplays.api.hologram.Hologram;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
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

    public static void setGameTitleHoverTexts(String game){
        for(String s : Lobby.getInstance().getMiniGameBlockUtil().getList(game.toLowerCase())) {
            Location loc = Lobby.getInstance().getLocationAPI().parseLocation(s);
            Location locHD = new Location(loc.getWorld(), loc.getBlockX()+0.5, loc.getBlockY()+3.5,loc.getBlockZ()+0.5);
            Hologram hologram = Lobby.getInstance().getHologramAPI().getApi().createHologram(locHD);
            hologram.getLines().appendText("§6§l"+game);
        }
    }

    public static void setGameTitleHoverText(String game, Location loc){
        Hologram hologram = Lobby.getInstance().getHologramAPI().getApi().createHologram(new Location(loc.getWorld(), loc.getX()+0.5, loc.getY()+3.5,loc.getZ()+0.5));
        hologram.getLines().appendText("§6§l"+game);
    }



    public static void deleteHologram(Location locHD) {
        for (Hologram h : Lobby.getInstance().getHologramAPI().getApi().getHolograms()) {
            Location hloc = h.getPosition().toLocation();
            if(hloc.getBlockX()==locHD.getBlockX()&&hloc.getBlockY()==locHD.getBlockY()&&hloc.getBlockZ()==locHD.getBlockZ()){
                h.delete();
            }
        }
    }

    public static void reloadHolograms() {
        String[] games = {"TicTacToe","Connect4","UNO","BattleShip","RockPaperScissors"};
        for(String s : games) setGameTitleHoverTexts(s);
    }

}
