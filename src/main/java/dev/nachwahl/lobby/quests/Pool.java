package dev.nachwahl.lobby.quests;

import dev.nachwahl.lobby.quests.car.CarArena;
import dev.nachwahl.lobby.quests.car.CarQuest;
import dev.nachwahl.lobby.quests.mine.MineArena;
import dev.nachwahl.lobby.quests.mine.MineQuest;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Pool {

    private final QuestType questType;

    private List<Arena> arenas;

    public Pool(QuestType questType){
        this.questType = questType;
        this.arenas = new ArrayList<>();
    }

    public QuestType getQuestType() {
        return questType;
    }

    public List<Arena> getArenas() {
        return arenas;
    }

    public void addArena(Arena arena){
        this.arenas.add(arena);
    }

    public void removeArena(Arena arena){
        this.arenas.remove(arena);
    }



    public Quest getFreeQuest(){
        System.out.println(Arrays.toString(this.arenas.toArray()));
        for(Arena arena : this.arenas){
            System.out.println("ARENA");
            if(arena.isFree()){
                try{
                    arena.setFree(false);
                    arena.setArenaStatus(Arena.ArenaStatus.INITIALISING);
                    MineQuest mineQuest = new MineQuest();
                    mineQuest.setMineArena((MineArena) arena);
                    return mineQuest;
                }catch (Exception ignored){
                }
                try{
                    arena.setFree(false);
                    arena.setArenaStatus(Arena.ArenaStatus.INITIALISING);
                    CarQuest carQuest = new CarQuest();
                    carQuest.setCarArena((CarArena) arena);
                    return carQuest;
                }catch (Exception ignored){
                }
            }
            System.out.println("Is FULL");
        }
        System.out.println("NO ARENA");
        return null;
    }


}
