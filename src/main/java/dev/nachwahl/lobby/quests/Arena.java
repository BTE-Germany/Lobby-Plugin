package dev.nachwahl.lobby.quests;


public class Arena {

    private final Block startBlock;
    private final int radius;

    private boolean isFree;

    private ArenaStatus arenaStatus;

    public Arena(Block startBlock, int radius){
        this.startBlock = startBlock;
        this.radius = radius;
        this.isFree = true;
        this.arenaStatus = ArenaStatus.INITIALISING;
    }

    public Block getStartBlock() {
        return startBlock;
    }

    public int getRadius() {
        return radius;
    }

    public boolean isFree() {
        return isFree;
    }

    public ArenaStatus getArenaStatus() {
        return arenaStatus;
    }

    public void setFree(boolean free) {
        isFree = free;
    }

    public void setArenaStatus(ArenaStatus arenaStatus) {
        this.arenaStatus = arenaStatus;
    }

    public void resetArena(){

    }

    public enum ArenaStatus{
        INITIALISING,
        WAITING,
        RUNNING,
        FINISHING,
        RESTARTING;
    }
}
