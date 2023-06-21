package dev.nachwahl.lobby.quests;

import dev.nachwahl.lobby.Lobby;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

public class Quest {

    private Player player;

    private List<Player> spectators;

    private ItemStack[] playerArmor;
    private ItemStack[] playerContents;

    private Location startLocation;

    public Quest(){
        this.player = null;
        this.spectators = new ArrayList<>();
    }

    public Player getPlayer() {
        return player;
    }

    public void setPlayer(Player player) {
        this.player = player;
    }

    public void setSpectators(List<Player> spectators) {
        this.spectators = spectators;
    }

    public List<Player> getSpectators() {
        return spectators;
    }

    public void addSpectator(Player player){
        this.spectators.add(player);
    }

    public void removeSpectator(Player player){
        this.spectators.remove(player);
    }

    public void setPlayerArmor(ItemStack[] playerArmor) {
        this.playerArmor = playerArmor;
    }

    public void setPlayerContents(ItemStack[] playerContents) {
        this.playerContents = playerContents;
    }

    public ItemStack[] getPlayerArmor() {
        return playerArmor;
    }

    public ItemStack[] getPlayerContents() {
        return playerContents;
    }

    public void startQuest(Player player){
        this.player = player;
        this.playerArmor = player.getInventory().getArmorContents();
        this.playerContents = player.getInventory().getContents();
        this.startLocation = player.getLocation();
        player.getInventory().clear();
    }

    public void stopQuest(){
        this.player.getInventory().clear();
        this.player.getInventory().setArmorContents(playerArmor);
        this.player.getInventory().setContents(playerContents);
        this.player.teleport(this.startLocation);
        Lobby.getInstance().getQuestManager().removeQuest(this);
    }

    public void resetQuest(){

    }
}
