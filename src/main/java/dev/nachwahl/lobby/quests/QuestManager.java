package dev.nachwahl.lobby.quests;

import dev.nachwahl.lobby.Lobby;
import dev.nachwahl.lobby.quests.car.CarArena;
import dev.nachwahl.lobby.quests.mine.MineArena;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class QuestManager {
    private List<Quest> quests;

    public QuestManager(){
        this.quests = new ArrayList<>();
    }

    public List<Quest> getQuests() {
        return quests;
    }

    public void addQuest(Quest quest){
        this.quests.add(quest);
    }

    public void removeQuest(Quest quest){
        this.quests.remove(quest);
    }

    public boolean isPlayerInQuest(Player player){
        for(Quest quest : quests){
            if(quest.getPlayer().equals(player)){
                return true;
            }
        }
        return false;
    }

    public Quest getQuestFromPlayer(Player player){
        for(Quest quest : quests){
            if(quest.getPlayer() == null){
               continue;
            }
            if(quest.getPlayer().equals(player)){
                return quest;
            }
        }
        return null;
    }

    public void setPools(){
        Pool pool = new Pool(QuestType.MINE);
        MineArena mineArena = new MineArena(new Block(0, 100, 0, "world"), 32);
        mineArena.setArenaStatus(Arena.ArenaStatus.INITIALISING);
        long startTime = System.currentTimeMillis();
        mineArena.initNewVeinsMap();
        long finishTime = System.currentTimeMillis();
        System.out.println("TIME: "+(finishTime-startTime)+"ms");



        pool.addArena(mineArena);

        System.out.println("Added Arena to Pool");

        System.out.println(Arrays.toString(pool.getArenas().toArray()));

        mineArena.setArenaStatus(Arena.ArenaStatus.WAITING);



        Pool pool1 = new Pool(QuestType.CAR);

        CarArena carArena = new CarArena(new Block(0, 150, 0, "world"), 64,
                new Block(-9, 151, 22, "world"),
                new Block(-9, 151, 13, "world"),
                new Block(-9, 151, 4, "world"),
                new Block(-9, 151, -5, "world"));

        carArena.setArenaStatus(Arena.ArenaStatus.WAITING);

        pool1.addArena(carArena);

        Lobby.getInstance().getPoolManager().addPool(pool1);

        Lobby.getInstance().getPoolManager().addPool(pool);


    }



}
