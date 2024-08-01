package dev.nachwahl.lobby.quests;

import dev.nachwahl.lobby.Lobby;
import dev.nachwahl.lobby.quests.car.CarArena;
import dev.nachwahl.lobby.quests.mine.MineArena;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class QuestManager {
    private List<Quest> quests;

    public QuestManager() {
        this.quests = new ArrayList<>();
    }

    public List<Quest> getQuests() {
        return quests;
    }

    public void addQuest(Quest quest) {
        this.quests.add(quest);
    }

    public void removeQuest(Quest quest) {
        this.quests.remove(quest);
    }

    public boolean isPlayerInQuest(Player player) {
        for (Quest quest : quests) {
            if (quest.getPlayer().equals(player)) {
                return true;
            }
        }
        return false;
    }

    public Quest getQuestFromPlayer(Player player) {
        for (Quest quest : quests) {
            if (quest.getPlayer() == null) {
                continue;
            }
            if (quest.getPlayer().equals(player)) {
                return quest;
            }
        }
        return null;
    }

    public void setPools() throws SQLException {
        Pool pool = new Pool(QuestType.MINE);
        Location mineQuest0Location = Lobby.getInstance().getLocationAPI().getLocation("quests.mine.0");

        Block block = new Block((int) mineQuest0Location.getX(), (int) mineQuest0Location.getY(), (int) mineQuest0Location.getZ(), mineQuest0Location.getWorld().getName());
        MineArena mineArena = new MineArena(block, 32);
        mineArena.setArenaStatus(Arena.ArenaStatus.INITIALISING);
        long startTime = System.currentTimeMillis();
        mineArena.initNewVeinsMap();
        long finishTime = System.currentTimeMillis();
        System.out.println("TIME: " + (finishTime - startTime) + "ms");


        pool.addArena(mineArena);

        System.out.println("Added Arena to Pool");

        System.out.println(Arrays.toString(pool.getArenas().toArray()));

        mineArena.setArenaStatus(Arena.ArenaStatus.WAITING);


        Pool pool1 = new Pool(QuestType.CAR);

        Location carQuest0Location = Lobby.getInstance().getLocationAPI().getLocation("quests.car.0");

        System.out.println(carQuest0Location);
        System.out.println((int) carQuest0Location.getX() + " " + (((int) carQuest0Location.getY()) - 1) + " " + (int) carQuest0Location.getZ());

        CarArena carArena = new CarArena(new Block((int) carQuest0Location.getX(), ((int) carQuest0Location.getY()) - 1, (int) carQuest0Location.getZ(), carQuest0Location.getWorld().getName()), 64,
                new Block((int) carQuest0Location.getX() - 9, (int) carQuest0Location.getY(), (int) carQuest0Location.getZ() + 22, carQuest0Location.getWorld().getName()),
                new Block((int) carQuest0Location.getX() - 9, (int) carQuest0Location.getY(), (int) carQuest0Location.getZ() + 13, carQuest0Location.getWorld().getName()),
                new Block((int) carQuest0Location.getX() - 9, (int) carQuest0Location.getY(), (int) carQuest0Location.getZ() + 4, carQuest0Location.getWorld().getName()),
                new Block((int) carQuest0Location.getX() - 9, (int) carQuest0Location.getY(), (int) carQuest0Location.getZ() - 5, carQuest0Location.getWorld().getName()));

        carArena.setArenaStatus(Arena.ArenaStatus.WAITING);

        pool1.addArena(carArena);

        Lobby.getInstance().getPoolManager().addPool(pool1);

        Lobby.getInstance().getPoolManager().addPool(pool);


        Queue.initQueues();


    }


}
