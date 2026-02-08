package dev.nachwahl.lobby.quests.car;

import dev.nachwahl.lobby.LobbyPlugin;
import dev.nachwahl.lobby.quests.*;
import org.bukkit.Location;
import org.bukkit.*;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class CarQuest extends Quest {
    public static String prefix = ChatColor.DARK_GRAY + "[" + ChatColor.YELLOW + "CarQuest" + ChatColor.DARK_GRAY + "] " + ChatColor.GRAY;
    private CarArena carArena;
    private Player player;

    private List<org.bukkit.block.Block> placedBlocks;

    public CarQuest() {
        super();
        this.placedBlocks = new ArrayList<>();


        LobbyPlugin.getInstance().getQuestManager().addQuest(this);
    }

    public void setCarArena(CarArena carArena) {
        this.carArena = carArena;
    }

    public CarArena getCarArena() {
        return carArena;
    }

    public List<org.bukkit.block.Block> getPlacedBlocks() {
        return placedBlocks;
    }

    public void addBlock(org.bukkit.block.Block block) {
        placedBlocks.add(block);
    }

    @Override
    public void startQuest(Player player) {
        this.player = player;
        super.startQuest(player);
        this.player.setGameMode(GameMode.SURVIVAL);
        dev.nachwahl.lobby.quests.Location location = this.getCarArena().getSpawnDealer();
        this.player.teleport(new Location(Bukkit.getWorld(location.getWorld()), location.getX(), location.getY(), location.getZ(), location.getYaw(), location.getPitch()));
        //this.player.sendMessage(prefix+"Bei CarQuest geht es darum, dein ausgewählte Auto so gut es geht in der vorgegebenen Zeit nachzubauen. Viel Erfolg!");
        this.player.sendMessage(prefix + "Bei CarQuest hast Du zunächst die Auswahl, Dir ein´s der vier Auto´s auszuwählen, welches Du dann in einer vorgegebenen Zeit nachbauen musst. Drücke den jeweiligen Knopf" +
                " vor dem Auto, um dieses auszuwählen.");
        //new Timer(player, 60*5, this).start();
    }

    @Override
    public void stopQuest() {
        this.carArena.setArenaStatus(Arena.ArenaStatus.FINISHING);
        this.player.sendMessage(prefix + ChatColor.DARK_RED + "Die Runde ist nun zu Ende!");
        String value = String.valueOf(this.carArena.compareCarToOriginal());
        this.player.sendMessage(prefix + "Du hast " + value.substring(0, Math.min(5, value.length())) + "% Genauigkeit erreicht!");
        resetQuest();
        super.stopQuest();
    }

    @Override
    public void resetQuest() {
        this.carArena.setArenaStatus(Arena.ArenaStatus.RESTARTING);
        for (org.bukkit.block.Block block : this.carArena.getAllPlacedBlocks()) {
            block.setType(Material.AIR, false);
        }
        this.carArena.setArenaStatus(Arena.ArenaStatus.WAITING);
        this.carArena.setFree(true);
        LobbyPlugin.getInstance().getQuestManager().removeQuest(this);
        Player player;
        if ((player = Queue.getNextPlayerInQueue(QuestType.MINE)) != null) {
            Quest quest = LobbyPlugin.getInstance().getPoolManager().getFreeQuest(QuestType.MINE);
            if (quest != null) {
                if (quest instanceof CarQuest) {
                    CarQuest carQuest = (CarQuest) quest;
                    carQuest.startQuest(player);
                }
            }
        }
    }

    public void nextStage(int car) {
        this.player.sendMessage(prefix + "Du hast Dich für Auto " + car + " entschieden! Jetzt geht es zum nachbauen.");
        //handle Car Copy
        Block startBlock = this.carArena.getStartBlock();
        this.carArena.setCopyFirsStep(new Block(startBlock.getX() - 2 - (car * 8), startBlock.getY() + 12, startBlock.getZ() - 7, startBlock.getWorld()));
        this.carArena.setCopySecondStep(new Block(startBlock.getX() - 2 - (car * 8), startBlock.getY() + 12, startBlock.getZ() - 2, startBlock.getWorld()));
        this.carArena.setCopyThirdStep(new Block(startBlock.getX() - 2 - (car * 8), startBlock.getY() + 12, startBlock.getZ() + 3, startBlock.getWorld()));
        this.carArena.setCopyFourthStep(new Block(startBlock.getX() - 2 - (car * 8), startBlock.getY() + 12, startBlock.getZ() + 8, startBlock.getWorld()));
        this.carArena.setCopyFinalStep(new Block(startBlock.getX() - 2 - (car * 8), startBlock.getY() + 12, startBlock.getZ() + 13, startBlock.getWorld()));

        this.carArena.copyPreBuildCarsToProductionHall();

        this.player.setGameMode(GameMode.CREATIVE);


        dev.nachwahl.lobby.quests.Location location = this.getCarArena().getSpawnProductionHall();
        this.player.teleport(new Location(Bukkit.getWorld(location.getWorld()), location.getX(), location.getY(), location.getZ(), location.getYaw(), location.getPitch()));

        new Timer(player, 60, this).start();

    }
}
