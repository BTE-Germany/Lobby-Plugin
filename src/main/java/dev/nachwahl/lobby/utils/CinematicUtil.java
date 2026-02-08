package dev.nachwahl.lobby.utils;

import dev.nachwahl.lobby.LobbyPlugin;
import dev.nachwahl.lobby.cinematic.PathPoint;
import lombok.Getter;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class CinematicUtil {

    @Getter
    private FileConfiguration dataFile;
    private final LobbyPlugin plugin;

    public CinematicUtil(LobbyPlugin plugin) {
        this.plugin = plugin;
    }


    private void saveFile() throws IOException {
        File file = new File(plugin.getDataFolder() + File.separator + "cinematics.yml");
        dataFile.save(file);
        dataFile = YamlConfiguration.loadConfiguration(file);
    }

    public File createFile() {
        File file = new File(plugin.getDataFolder() + File.separator + "cinematics.yml");
        if (!file.exists()) {
            try {
                file.createNewFile();
                dataFile = YamlConfiguration.loadConfiguration(file);
                dataFile.set("cinematics.example.points.location", "world/10/100/10/0/0");
                dataFile.set("cinematics.example.points.speed", "world/10/100/10/0/0");
                dataFile.set("cinematics.example.points.speed", "world/10/100/10/0/0");
                saveFile();
                return file;
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    public List<PathPoint> getCinematicPath(String cinematicName) {
        File file = new File(plugin.getDataFolder() + File.separator + "cinematics.yml");
        if (!file.exists()) {
            file = createFile();
        }
        dataFile = YamlConfiguration.loadConfiguration(file);
        List<PathPoint> pathPoints = new ArrayList<>();
        String path = "cinematics." + cinematicName + ".points";

        if (dataFile.isConfigurationSection(path)) {
            ConfigurationSection pointsSection = dataFile.getConfigurationSection(path);

            for (String key : pointsSection.getKeys(false)) {
                String pointPath = path + "." + key;

                Location location = plugin.getLocationAPI().parseLocation(dataFile.getString(pointPath + ".location"));
                double ticksToPoint = dataFile.getDouble(pointPath + ".speed", 0);
                String title = dataFile.getString(pointPath + ".title",null);
                String subtitle = dataFile.getString(pointPath + ".subtitle",null);
                String chatMessage = dataFile.getString(pointPath + ".chatMessage",null);
                String soundString = dataFile.getString(pointPath + ".sound",null);
                Sound sound;
                if(soundString != null){
                    sound = Sound.valueOf(soundString);
                }else{
                    sound = null;
                }

                pathPoints.add(new PathPoint(location, ticksToPoint,title,subtitle,chatMessage,sound));
            }
        }

        return pathPoints;
    }

   public List<String> getCinematicList() {
       File file = new File(plugin.getDataFolder() + File.separator + "cinematics.yml");
       if (!file.exists()) {
           file = createFile();
       }
       dataFile = YamlConfiguration.loadConfiguration(file);
        List<String> cinematicList = new ArrayList<>();
        String path = "cinematics";
        if (dataFile.isConfigurationSection(path)) {
            ConfigurationSection pointsSection = dataFile.getConfigurationSection(path);
            for (String key : pointsSection.getKeys(false)) {
                cinematicList.add(key);
            }
        }
        return cinematicList;
    }

    public void saveCinematicPath(String cinematicName, List<PathPoint> pathPoints) {
        File file = new File(plugin.getDataFolder() + File.separator + "cinematics.yml");
        if (!file.exists()) {
            file = createFile();
        }
        dataFile = YamlConfiguration.loadConfiguration(file);
        String path = "cinematics." + cinematicName + ".points";

        for (int i = 0; i < pathPoints.size(); i++) {
            PathPoint pathPoint = pathPoints.get(i);
            String pointPath = path + "." + i;

            dataFile.set(pointPath + ".location", plugin.getLocationAPI().parseLocation(pathPoint.getLocation()));
            dataFile.set(pointPath + ".speed", pathPoint.getTicksToPoint());
        }

        try {
            saveFile();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

   public void addCinematicPathPoint(String cinematicName, PathPoint pathPoint) {
       File file = new File(plugin.getDataFolder() + File.separator + "cinematics.yml");
       if (!file.exists()) {
           file = createFile();
       }
       dataFile = YamlConfiguration.loadConfiguration(file);
       String path = "cinematics." + cinematicName + ".points";


       String pointPath = path + "." + (getCinematicPath(cinematicName).size() + 1);

       dataFile.set(pointPath + ".location", plugin.getLocationAPI().parseLocation(pathPoint.getLocation()));
       dataFile.set(pointPath + ".speed", pathPoint.getTicksToPoint());


       try {
           saveFile();
       } catch (IOException e) {
           e.printStackTrace();
       }
   }

    public void deleteCinematicPathPoint(String cinematicName, int index) {
        File file = new File(plugin.getDataFolder() + File.separator + "cinematics.yml");
        if (!file.exists()) {
            file = createFile();
        }
        dataFile = YamlConfiguration.loadConfiguration(file);
        String path = "cinematics." + cinematicName + ".points";

        dataFile.set(path + "." + index, null);

        try {
            saveFile();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void deleteCinematic(String cinematicName) {
        File file = new File(plugin.getDataFolder() + File.separator + "cinematics.yml");
        if (!file.exists()) {
            file = createFile();
        }
        dataFile = YamlConfiguration.loadConfiguration(file);
        String path = "cinematics." + cinematicName;

        dataFile.set(path, null);

        try {
            saveFile();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


}
