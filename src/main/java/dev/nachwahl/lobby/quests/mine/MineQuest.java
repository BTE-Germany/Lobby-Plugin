package dev.nachwahl.lobby.quests.mine;

import dev.nachwahl.lobby.LobbyPlugin;
import dev.nachwahl.lobby.quests.*;
import org.bukkit.Location;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;

import static org.bukkit.enchantments.Enchantment.EFFICIENCY;

public class MineQuest extends Quest {

    public static String prefix = ChatColor.DARK_GRAY + "[" + ChatColor.YELLOW + "MineQuest" + ChatColor.DARK_GRAY + "] " + ChatColor.GRAY;


    private Player player;

    private HashMap<String, Integer> blockCount;

    private MineArena mineArena;

    public MineQuest() {
        super();
        this.blockCount = new HashMap<>();
        this.blockCount.put(VeinType.COAL.toString(), 0);
        this.blockCount.put(VeinType.IRON.toString(), 0);
        this.blockCount.put(VeinType.GOLD.toString(), 0);
        this.blockCount.put("MAX_" + VeinType.COAL, 0);
        this.blockCount.put("MAX_" + VeinType.IRON, 0);
        this.blockCount.put("MAX_" + VeinType.GOLD, 0);

        LobbyPlugin.getInstance().getQuestManager().addQuest(this);
    }

    public void setMineArena(MineArena mineArena) {
        this.mineArena = mineArena;
    }

    public HashMap<String, Integer> getBlockCount() {
        return blockCount;
    }

    @Override
    public void startQuest(@NotNull Player player) {
        this.player = player;
        super.startQuest(player);
        this.player.setGameMode(GameMode.SURVIVAL);
        dev.nachwahl.lobby.quests.Block block = this.mineArena.getStartBlock();
        for (VeinLoader veinLoader : this.mineArena.getVeinLoaders()) {
            VeinLoader.Result result = veinLoader.loadVein();
            if (result.getVeinType().equals(VeinType.COAL)) {
                this.blockCount.put("MAX_" + VeinType.COAL, this.blockCount.get("MAX_" + VeinType.COAL) + result.getCount());
            } else if (result.getVeinType().equals(VeinType.IRON)) {
                this.blockCount.put("MAX_" + VeinType.IRON, this.blockCount.get("MAX_" + VeinType.IRON) + result.getCount());
            } else if (result.getVeinType().equals(VeinType.GOLD)) {
                this.blockCount.put("MAX_" + VeinType.GOLD, this.blockCount.get("MAX_" + VeinType.GOLD) + result.getCount());
            }
        }
        this.player.teleport(new Location(Bukkit.getWorld(block.getWorld()), block.getX() + 0.5, block.getY(), block.getZ() + 0.5));
        ItemStack itemStack = new ItemStack(Material.NETHERITE_PICKAXE);
        ItemMeta itemMeta = itemStack.getItemMeta();
        itemMeta.addEnchant(EFFICIENCY, 4, true);
        itemStack.setItemMeta(itemMeta);
        this.player.getInventory().setItemInMainHand(itemStack);
        this.player.sendMessage(prefix + "Bei MineQuest geht es darum, so viele Erze wie möglich innerhalb einer Minute abzubauen. Viel Erfolg!");
        new Timer(player, 60, this).start();
    }


    @Override
    public void stopQuest() {
        this.mineArena.setArenaStatus(Arena.ArenaStatus.FINISHING);
        this.player.sendMessage(prefix + ChatColor.DARK_RED + "Die Runde ist nun zu Ende!");
        this.player.sendMessage(prefix + "Abgebaute Erze: " + (this.blockCount.get(VeinType.COAL.toString()) / VeinType.COAL.getValue()) + "/" + (this.blockCount.get("MAX_" + VeinType.COAL) / VeinType.COAL.getValue()) + " Kohle, "
                + (this.blockCount.get(VeinType.IRON.toString()) / VeinType.IRON.getValue()) + "/" + (this.blockCount.get("MAX_" + VeinType.IRON) / VeinType.IRON.getValue()) + " Eisen, "
                + (this.blockCount.get(VeinType.GOLD.toString()) / VeinType.GOLD.getValue()) + "/" + (this.blockCount.get("MAX_" + VeinType.GOLD) / VeinType.GOLD.getValue()) + " Gold");
        this.player.sendMessage(prefix + "Deine Punkte: " + (this.blockCount.get(VeinType.COAL.toString()) + this.blockCount.get(VeinType.IRON.toString()) + this.blockCount.get(VeinType.GOLD.toString())) + "/" + (this.blockCount.get("MAX_" + VeinType.COAL) + this.blockCount.get("MAX_" + VeinType.IRON) + this.blockCount.get("MAX_" + VeinType.GOLD)));
        resetQuest();
        super.stopQuest();
    }


    @Override
    public void resetQuest() {
        this.mineArena.setArenaStatus(Arena.ArenaStatus.RESTARTING);
        for (VeinLoader veinLoader : mineArena.getVeinLoaders()) {
            for (Block block : veinLoader.getBlocksAsBukkitBlock()) {
                switch (veinLoader.getVeinLoaderType()) {
                    case LIME:
                        block.setType(Material.LIME_WOOL);
                        break;
                    case YELLOW:
                        block.setType(Material.YELLOW_WOOL);
                        break;
                    case RED:
                        block.setType(Material.RED_WOOL);
                        break;
                }
            }
        }
        this.mineArena.setArenaStatus(Arena.ArenaStatus.WAITING);
        this.mineArena.setFree(true);
        LobbyPlugin.getInstance().getQuestManager().removeQuest(this);
        Player player;
        if ((player = Queue.getNextPlayerInQueue(QuestType.MINE)) != null) {
            Quest quest = LobbyPlugin.getInstance().getPoolManager().getFreeQuest(QuestType.MINE);
            if (quest != null) {
                if (quest instanceof MineQuest) {
                    MineQuest mineQuest = (MineQuest) quest;
                    mineQuest.startQuest(player);
                }
            }
        }
    }
}
