package dev.nachwahl.lobby.quests;

import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class Queue {

    public static HashMap<QuestType, List<Player>> questTypeListHashMap = new HashMap<>();

    public static void addPlayerToQueue(QuestType questType, Player player) {
        List<Player> players = questTypeListHashMap.get(questType);
        players.add(player);
        questTypeListHashMap.remove(questType);
        questTypeListHashMap.put(questType, players);
    }

    public static void removePlayerFromQueue(QuestType questType, Player player) {
        List<Player> players = questTypeListHashMap.get(questType);
        players.remove(player);
        questTypeListHashMap.remove(questType);
        questTypeListHashMap.put(questType, players);
    }

    public static Player getNextPlayerInQueue(QuestType questType) {
        List<Player> players = questTypeListHashMap.get(questType);
        if (players.size() == 0) {
            return null;
        }
        Player player = players.get(0);
        removePlayerFromQueue(questType, player);
        return player;
    }

    public static void initQueues() {
        questTypeListHashMap.put(QuestType.MINE, new ArrayList<Player>());
        questTypeListHashMap.put(QuestType.CAR, new ArrayList<Player>());
    }
}
