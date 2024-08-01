package dev.nachwahl.lobby.hologram;

import dev.nachwahl.lobby.Lobby;
import dev.nachwahl.lobby.language.Language;
import lombok.Getter;
import me.filoghost.holographicdisplays.api.HolographicDisplaysAPI;
import me.filoghost.holographicdisplays.api.Position;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;


public class HologramAPI {


    @Getter
    private HolographicDisplaysAPI api;
    private FileConfiguration dataFile;

    @Getter
    private ArrayList<Hologram> holograms;
    @Getter
    private HashMap<String, Hologram> customHolograms;

    private final Lobby plugin;

    public HologramAPI(Lobby plugin) {
        this.plugin = plugin;
        if (Bukkit.getPluginManager().isPluginEnabled("HolographicDisplays")) {
            api = HolographicDisplaysAPI.get(plugin);
            holograms = new ArrayList<>();
            customHolograms = new HashMap<>();
        }
    }

    public void loadData() {
        if (holograms.size() > 0) clearHolograms();

        File file = new File(plugin.getDataFolder() + File.separator + "holograms.yml");
        if (!file.exists()) {
            file = createFile();
        }
        dataFile = YamlConfiguration.loadConfiguration(file);
        ConfigurationSection sec = dataFile.getConfigurationSection("holograms");

        assert sec != null;
        for (String key : sec.getKeys(false)) {
            String[] location = dataFile.getString("holograms." + key + ".location").split("/");
            String[] englishText = dataFile.getString("holograms." + key + ".english").split(";");
            String[] germanText = dataFile.getString("holograms." + key + ".german").split(";");
            World world = Bukkit.getWorld(location[0]);

            if (world == null) return;

            holograms.add(new Hologram(Position.of(world, Double.parseDouble(location[1]), Double.parseDouble(location[2]), Double.parseDouble(location[3])), englishText, germanText));
        }
    }

    private void saveFile() throws IOException {
        File file = new File(plugin.getDataFolder() + File.separator + "holograms.yml");
        dataFile.save(file);
        dataFile = YamlConfiguration.loadConfiguration(file);
    }

    private File createFile() {
        File file = new File(plugin.getDataFolder() + File.separator + "holograms.yml");
        if (!file.exists()) {
            try {
                file.createNewFile();
                dataFile = YamlConfiguration.loadConfiguration(file);
                dataFile.set("holograms.example.location", "world/10/100/10");
                dataFile.set("holograms.example.english", "Test Hologram;");
                dataFile.set("holograms.example.german", "Test Hologram in deutsch;");
                saveFile();
                return file;
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    private void clearHolograms() {
        for (Hologram hologram : holograms) {
            hologram.delete();
        }
        holograms = new ArrayList<>();
    }

    public void showHolograms(Player player) {
        showHolograms(player, Lobby.getInstance().getLanguageAPI().getLanguage(player));
    }

    public void showHolograms(Player player, Language language) {
        for (Hologram hologram : holograms) {
            hologram.setPlayer(player, language);
        }
        for (Hologram hologram : customHolograms.values()) {
            hologram.setPlayer(player, language);
        }
    }

    public void hideHolograms(Player player) {
        for (Hologram hologram : holograms) {
            hologram.removePlayer(player);
        }
        for (Hologram hologram : customHolograms.values()) {
            hologram.removePlayer(player);
        }
    }

    public void removeHologram(Position location) {
        for (Hologram hologram : customHolograms.values()) {
            if (hologram.getLocation().equals(location)) hologram.delete();
        }
    }

    public void removeHologram(String id) {
        customHolograms.get(id).delete();
    }

    public void addHologram(String id, Hologram hologram) {
        customHolograms.put(id, hologram);
        for (Player player : Bukkit.getOnlinePlayers()) {
            hologram.setPlayer(player, Lobby.getInstance().getLanguageAPI().getLanguage(player));
        }
    }

    public Hologram getHologram(String id) {
        return customHolograms.get(id);
    }
}
