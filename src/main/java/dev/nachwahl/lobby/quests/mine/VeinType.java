package dev.nachwahl.lobby.quests.mine;

public enum VeinType {
    STONE(0),
    COAL(1),
    IRON(2),
    GOLD(3);

    public final int i;
    VeinType(int i){
        this.i = i;
    }

    public int getValue(){
        return i;
    }
}
