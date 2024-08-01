package dev.nachwahl.lobby.quests.mine;

import dev.nachwahl.lobby.quests.Block;
import org.bukkit.Bukkit;
import org.bukkit.Material;

import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.List;

public class VeinLoader {

    private List<Block> blocks;

    private VeinLoaderType veinLoaderType;

    public VeinLoader(VeinLoaderType veinLoaderType) {
        this.veinLoaderType = veinLoaderType;
    }

    public VeinLoaderType getVeinLoaderType() {
        return veinLoaderType;
    }

    public List<Block> getBlocks() {
        return blocks;
    }

    public List<org.bukkit.block.Block> getBlocksAsBukkitBlock() {
        List<org.bukkit.block.Block> bukkitBlocks = new ArrayList<>();
        for (Block block : getBlocks()) {
            bukkitBlocks.add(Bukkit.getWorld(block.getWorld()).getBlockAt(block.getX(), block.getY(), block.getZ()));
        }
        return bukkitBlocks;
    }

    public void setVeinLoaderType(VeinLoaderType veinLoaderType) {
        this.veinLoaderType = veinLoaderType;
    }

    public void setBlocks(List<Block> blocks) {
        this.blocks = blocks;
    }

    public void addBlock(Block block) {
        this.blocks.add(block);
    }

    public void removeBlock(Block block) {
        this.blocks.remove(block);
    }


    public Result loadVein() {
        SecureRandom secureRandom = new SecureRandom();
        int i = secureRandom.nextInt(100 + 1);
        if (this.getVeinLoaderType().equals(VeinLoaderType.LIME)) {
            if (i <= 40) {
                return setBlocksInVeinMaterial(this.getBlocksAsBukkitBlock(), VeinType.STONE);
            } else if (i <= 90) {
                return setBlocksInVeinMaterial(this.getBlocksAsBukkitBlock(), VeinType.COAL);
            } else {
                return setBlocksInVeinMaterial(this.getBlocksAsBukkitBlock(), VeinType.IRON);
            }
        } else if (this.getVeinLoaderType().equals(VeinLoaderType.YELLOW)) {
            if (i <= 30) {
                return setBlocksInVeinMaterial(this.getBlocksAsBukkitBlock(), VeinType.STONE);
            } else if (i <= 80) {
                return setBlocksInVeinMaterial(this.getBlocksAsBukkitBlock(), VeinType.IRON);
            } else {
                return setBlocksInVeinMaterial(this.getBlocksAsBukkitBlock(), VeinType.COAL);
            }
        } else if (this.getVeinLoaderType().equals(VeinLoaderType.RED)) {
            if (i <= 20) {
                return setBlocksInVeinMaterial(this.getBlocksAsBukkitBlock(), VeinType.STONE);
            } else if (i <= 90) {
                return setBlocksInVeinMaterial(this.getBlocksAsBukkitBlock(), VeinType.GOLD);
            } else {
                return setBlocksInVeinMaterial(this.getBlocksAsBukkitBlock(), VeinType.IRON);
            }
        }
        return null;
    }

    private Result setBlocksInVeinMaterial(List<org.bukkit.block.Block> blocks, VeinType veinType) {
        if (veinType.equals(VeinType.STONE)) {
            for (org.bukkit.block.Block block : blocks) {
                block.setType(Material.STONE);
            }
        } else if (veinType.equals(VeinType.COAL)) {
            for (org.bukkit.block.Block block : blocks) {
                block.setType(Material.COAL_ORE);
            }
        } else if (veinType.equals(VeinType.IRON)) {
            for (org.bukkit.block.Block block : blocks) {
                block.setType(Material.IRON_ORE);
            }
        } else if (veinType.equals(VeinType.GOLD)) {
            for (org.bukkit.block.Block block : blocks) {
                block.setType(Material.GOLD_ORE);
            }
        }
        return new Result(veinType, blocks.size());
    }


    public enum VeinLoaderType {
        LIME,
        YELLOW,
        RED;
    }

    public class Result {
        private VeinType veinType;
        private int count;

        public Result(VeinType veinType, int count) {
            this.veinType = veinType;
            this.count = count;
        }

        public VeinType getVeinType() {
            return veinType;
        }

        public int getCount() {
            return count;
        }
    }
}
