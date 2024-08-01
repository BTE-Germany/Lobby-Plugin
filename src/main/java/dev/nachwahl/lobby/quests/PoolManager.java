package dev.nachwahl.lobby.quests;

import java.util.ArrayList;
import java.util.List;

public class PoolManager {
    private List<Pool> pools;

    public PoolManager() {
        this.pools = new ArrayList<>();
    }

    public void setPools(List<Pool> pools) {
        this.pools = pools;
    }

    public List<Pool> getPools() {
        return pools;
    }

    public void addPool(Pool pool) {
        this.pools.add(pool);
    }

    public void removePool(Pool pool) {
        this.pools.remove(pool);
    }

    public Quest getFreeQuest(QuestType questType) {
        for (Pool pool : pools) {
            if (pool.getQuestType().equals(questType)) {
                return pool.getFreeQuest();
            }
        }
        return null;
    }
}
