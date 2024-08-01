package dev.nachwahl.lobby.quests.mine;

import dev.nachwahl.lobby.quests.Arena;
import dev.nachwahl.lobby.quests.Block;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.World;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

public class MineArena extends Arena {

    private List<VeinLoader> veinLoaders;

    public MineArena(Block startBlock, int radius) {
        super(startBlock, radius);
        this.veinLoaders = new ArrayList<>();
    }


    public List<VeinLoader> getVeinLoaders() {
        return veinLoaders;
    }

    public void setVeinLoaders(List<VeinLoader> veins) {
        this.veinLoaders = veins;
    }

    public void addVeinLoader(VeinLoader vein) {
        this.veinLoaders.add(vein);
    }

    public void removeVein(VeinLoader vein) {
        this.veinLoaders.remove(vein);
    }

    /*public HashMap<String, Integer> getMaxBlockCount(){
        return this.maxBlockCount;
    }*/

    public void initNewVeinsMap() {
        int startX = this.getStartBlock().getX();
        int startY = this.getStartBlock().getY();
        int startZ = this.getStartBlock().getZ();
        int radius = this.getRadius();
        int[] c1 = {startX - radius, startY - radius, startZ - radius};
        int[] c2 = {startX + radius, startY + radius, startZ + radius};
        HashMap<Block, VeinLoader.VeinLoaderType> veinLoaderTypeHashMap = new HashMap<>();
        World world = Bukkit.getWorld(this.getStartBlock().getWorld());
        for (int i = c1[0]; i <= c2[0]; i++) {
            for (int i1 = c1[1]; i1 <= c2[1]; i1++) {
                for (int i2 = c1[2]; i2 <= c2[2]; i2++) {
                    assert world != null;
                    Material material = world.getBlockAt(i, i1, i2).getType();
                    switch (material) {
                        case LIME_WOOL:
                            veinLoaderTypeHashMap.put(new Block(i, i1, i2, world.getName()), VeinLoader.VeinLoaderType.LIME);
                            break;
                        case YELLOW_WOOL:
                            veinLoaderTypeHashMap.put(new Block(i, i1, i2, world.getName()), VeinLoader.VeinLoaderType.YELLOW);
                            break;
                        case RED_WOOL:
                            veinLoaderTypeHashMap.put(new Block(i, i1, i2, world.getName()), VeinLoader.VeinLoaderType.RED);
                            break;
                    }
                }
            }
        }
        List<VeinLoader> veinLoaderList = new ArrayList<>();
        List<Block> alreadyAddedBlock = new ArrayList<>();
        for (Block block : veinLoaderTypeHashMap.keySet()) {
            if (!doesBlockAlreadyExist(block, alreadyAddedBlock)) {
                VeinLoader veinLoader = new VeinLoader(veinLoaderTypeHashMap.get(block));
                List<Block> blocks = getNearbyVeins(block, veinLoaderTypeHashMap.get(block), alreadyAddedBlock);
                veinLoader.setBlocks(blocks);
                //veinLoader.loadVein();
                veinLoaderList.add(veinLoader);
            }
        }
        System.out.println("Added Blocks " + alreadyAddedBlock.size());
        System.out.println("Veins: " + veinLoaderList.size());
        System.out.println("OreBlocksCount " + veinLoaderTypeHashMap.keySet().size());
        int count = 0;
        for (VeinLoader veinLoader : veinLoaderList) {
            count = count + veinLoader.getBlocks().size();
        }
        System.out.println("getVeinBlocks() " + count);
        this.setVeinLoaders(veinLoaderList);

    }


    private List<Block> getNearbyVeins(Block block, VeinLoader.VeinLoaderType veinLoaderType, List<Block> alreadyAddedBlock) {
        List<Block> blocks = new ArrayList<>();
        blocks.add(block);
        alreadyAddedBlock.add(block);
        int startX = block.getX();
        int startY = block.getY();
        int startZ = block.getZ();
        int radius = 1;
        int[] c1 = {startX - radius, startY - radius, startZ - radius};
        int[] c2 = {startX + radius, startY + radius, startZ + radius};
        World world = Bukkit.getWorld(block.getWorld());
        for (int i = c1[0]; i <= c2[0]; i++) {
            for (int i1 = c1[1]; i1 <= c2[1]; i1++) {
                for (int i2 = c1[2]; i2 <= c2[2]; i2++) {
                    assert world != null;
                    if (!doesBlockAlreadyExist(new Block(i, i1, i2, world.getName()), alreadyAddedBlock)) {
                        org.bukkit.block.Block block1 = world.getBlockAt(i, i1, i2);
                        Block block2 = new Block(i, i1, i2, world.getName());
                        switch (block1.getType()) {
                            case LIME_WOOL:
                                if (veinLoaderType.equals(VeinLoader.VeinLoaderType.LIME)) {
                                    blocks.addAll(Objects.requireNonNull(getNearbyVeins(block2, VeinLoader.VeinLoaderType.LIME, alreadyAddedBlock)));
                                }
                                break;
                            case YELLOW_WOOL:
                                if (veinLoaderType.equals(VeinLoader.VeinLoaderType.YELLOW)) {
                                    blocks.addAll(Objects.requireNonNull(getNearbyVeins(block2, VeinLoader.VeinLoaderType.YELLOW, alreadyAddedBlock)));
                                }
                                break;
                            case RED_WOOL:
                                if (veinLoaderType.equals(VeinLoader.VeinLoaderType.RED)) {
                                    blocks.addAll(Objects.requireNonNull(getNearbyVeins(block2, VeinLoader.VeinLoaderType.RED, alreadyAddedBlock)));
                                }
                                break;
                        }
                    }
                }
            }
        }
        return blocks;
    }

    private boolean doesBlockAlreadyExist(Block block, List<Block> alreadyAddedBlocks) {
        for (Block block1 : alreadyAddedBlocks) {
            if (block1.getX() == block.getX() && block1.getY() == block.getY() && block1.getZ() == block.getZ() && block1.getWorld().equals(block.getWorld())) {
                return true;
            }
        }
        return false;
    }
}
