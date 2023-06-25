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

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
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

    public static void setHoverText(String game){
        for(String s : Lobby.getInstance().getMiniGameBlockUtil().getList(game.toLowerCase())) {
            List<Double> loc = parse(s);
            Location locHD = new Location(Bukkit.getWorld("lobby3"), loc.get(0)+0.5, loc.get(1)+3.5,loc.get(2)+0.5);
            Hologram hologram = Lobby.getInstance().getHologramAPI().getApi().createHologram(locHD);
            hologram.getLines().appendText("§6§l"+game);
        }
    }

    public static List<Double> parse(String location){
        String[] locs = location.split(",");
        List<Double> locs1 = new ArrayList<>();
        for(String s : locs){
            locs1.add(Double.parseDouble(s));
        }
        return locs1;
    }

    public static void deleteHologram(Location locHD) {
        for (Hologram h : Lobby.getInstance().getHologramAPI().getApi().getHolograms()) {
            Location hloc = h.getPosition().toLocation();
            if(hloc.getBlockX()==locHD.getBlockX()&&hloc.getBlockY()==locHD.getBlockY()&&hloc.getBlockZ()==locHD.getBlockZ()){
                h.delete();
            }
        }
    }

}
