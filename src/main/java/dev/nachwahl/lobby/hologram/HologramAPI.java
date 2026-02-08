package dev.nachwahl.lobby.hologram;

import dev.nachwahl.lobby.LobbyPlugin;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.Nullable;
import net.kyori.adventure.text.Component;
import java.util.Objects;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.UUID;

public class HologramAPI {

    public static final String FILE_NAME = "holograms.yml";
    public static final String SECTION = "holograms";
    private FileConfiguration dataFile;

    @Getter
    private ArrayList<Hologram> holograms;
    @Getter
    private final HashMap<String, Hologram> customHolograms;

    public UUID debugPlayer; // TODO Remove when Lobby Holograms works fine again

    private final LobbyPlugin plugin;

    public HologramAPI(LobbyPlugin plugin) {
        this.plugin = plugin;
            holograms = new ArrayList<>();
            customHolograms = new HashMap<>();
    }

    public void loadData() {
        if (!holograms.isEmpty()) clearHolograms();

        File file = new File(plugin.getDataFolder() + File.separator + FILE_NAME);
        if (!file.exists()) {
            file = createFile();
        }
        dataFile = YamlConfiguration.loadConfiguration(Objects.requireNonNull(file));
        ConfigurationSection sec = dataFile.getConfigurationSection(SECTION);

        assert sec != null;
        for (String key : sec.getKeys(false)) {
            String[] location = Objects.requireNonNull(dataFile.getString(SECTION + "." + key + ".location")).split("/");
            String[] englishText = Objects.requireNonNull(dataFile.getString(SECTION + "." + key + ".english")).split(";");
            String[] germanText = Objects.requireNonNull(dataFile.getString(SECTION + "." + key + ".german")).split(";");
            World world = Bukkit.getWorld(location[0]);

            if (world == null) return;

            holograms.add(new Hologram(new org.bukkit.Location(world, Double.parseDouble(location[1]), Double.parseDouble(location[2]),
                Double.parseDouble(location[3])), englishText, germanText, key));
        }
    }

    private void saveFile() throws IOException {
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
                dataFile.set(SECTION + ".example.location", "world/10/100/10");
                dataFile.set(SECTION + ".example.english", "Test Hologram;");
                dataFile.set( SECTION + ".example.german", "Test Hologram in deutsch;");
                saveFile();
                return file;
            } catch (IOException e) {
                LobbyPlugin.getInstance().getComponentLogger().error(Component.text("Error accorded while writing " + FILE_NAME + " file."), e);
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

    public void addHologram(String id, Hologram hologram) {
        customHolograms.put(id, hologram);
    }

    public Hologram getHologram(String id) {
        return customHolograms.get(id);
    }

    public void sendDebugMsg(Component component) {
        if (debugPlayer != null) {
            Player p = Bukkit.getPlayer(debugPlayer);
            if (p != null) p.sendMessage(component);
        }
    }
}
