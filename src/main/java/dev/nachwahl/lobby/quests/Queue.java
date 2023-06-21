package dev.nachwahl.lobby.quests;

import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class Queue  {
    private QuestType questType;

    private List<Player> players;

    public Queue(QuestType questType){
        this.questType = questType;
        this.players = new ArrayList<>();
    }
}
