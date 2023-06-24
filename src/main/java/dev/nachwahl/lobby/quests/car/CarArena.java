package dev.nachwahl.lobby.quests.car;


import dev.nachwahl.lobby.quests.Arena;
import dev.nachwahl.lobby.quests.Block;
import dev.nachwahl.lobby.quests.Location;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.World;

import java.util.ArrayList;
import java.util.List;

public class CarArena extends Arena {

    private Block buttonBlock1;
    private Block buttonBlock2;
    private Block buttonBlock3;
    private Block buttonBlock4;
    private Location spawnDealer;
    private Location spawnProductionHall;

    private Block firsStep;
    private Block secondStep;

    private Block thirdStep;

    private Block fourthStep;

    private Block finalStep;

    private Block copyFirsStep;
    private Block copySecondStep;

    private Block copyThirdStep;

    private Block copyFourthStep;

    private Block copyFinalStep;

    private List<org.bukkit.block.Block> allPlacedBlocks;
    
    
    public CarArena(Block startBlock, int radius, Block buttonBlock1, Block buttonBlock2, Block buttonBlock3, Block buttonBlock4) {
        super(startBlock, radius);
        this.buttonBlock1 = buttonBlock1;
        this.buttonBlock2 = buttonBlock2;
        this.buttonBlock3 = buttonBlock3;
        this.buttonBlock4 = buttonBlock4;
        this.spawnDealer = new Location(startBlock.getX()-4, startBlock.getY(), startBlock.getZ()+28.5, 180, 0, startBlock.getWorld());
        this.spawnProductionHall = new Location(startBlock.getX()-138, startBlock.getY()-1, startBlock.getZ()-2, -90, 0, startBlock.getWorld());
        this.firsStep = new Block(startBlock.getX()-137, startBlock.getY()+1, startBlock.getZ()+13, startBlock.getWorld());
        this.secondStep = new Block(startBlock.getX()-121, startBlock.getY()+1, startBlock.getZ()+13, startBlock.getWorld());
        this.thirdStep = new Block(startBlock.getX()-105, startBlock.getY()+1, startBlock.getZ()+13, startBlock.getWorld());
        this.fourthStep = new Block(startBlock.getX()-89, startBlock.getY()+1, startBlock.getZ()+13, startBlock.getWorld());
        this.finalStep = new Block(startBlock.getX()-73, startBlock.getY()+1, startBlock.getZ()+13, startBlock.getWorld());
        this.allPlacedBlocks = new ArrayList<>();
    }

    public Block getButtonBlock1() {
        return buttonBlock1;
    }

    public Block getButtonBlock2() {
        return buttonBlock2;
    }

    public Block getButtonBlock3() {
        return buttonBlock3;
    }

    public Block getButtonBlock4() {
        return buttonBlock4;
    }

    public Location getSpawnDealer() {
        return spawnDealer;
    }

    public Location getSpawnProductionHall() {
        return spawnProductionHall;
    }

    public Block getFirsStep() {
        return firsStep;
    }

    public Block getSecondStep() {
        return secondStep;
    }

    public Block getThirdStep() {
        return thirdStep;
    }

    public Block getFourthStep() {
        return fourthStep;
    }

    public Block getFinalStep() {
        return finalStep;
    }

    public Block getCopyFirsStep() {
        return copyFirsStep;
    }

    public Block getCopySecondStep() {
        return copySecondStep;
    }

    public Block getCopyThirdStep() {
        return copyThirdStep;
    }

    public Block getCopyFourthStep() {
        return copyFourthStep;
    }

    public Block getCopyFinalStep() {
        return copyFinalStep;
    }

    public void setCopyFirsStep(Block copyFirsStep) {
        this.copyFirsStep = copyFirsStep;
    }

    public void setCopySecondStep(Block copySecondStep) {
        this.copySecondStep = copySecondStep;
    }

    public void setCopyThirdStep(Block copyThirdStep) {
        this.copyThirdStep = copyThirdStep;
    }

    public void setCopyFourthStep(Block copyFourthStep) {
        this.copyFourthStep = copyFourthStep;
    }

    public void setCopyFinalStep(Block copyFinalStep) {
        this.copyFinalStep = copyFinalStep;
    }

    public List<org.bukkit.block.Block> getAllPlacedBlocks() {
        return allPlacedBlocks;
    }

    public void addAllPlacedBlock(org.bukkit.block.Block block){
        this.allPlacedBlocks.add(block);
    }

    public void copyPreBuildCarsToProductionHall(){
        moveBlock(getBlocks(this.getCopyFirsStep()), this.getCopyFirsStep(), this.getFirsStep());
        moveBlock(getBlocks(this.getCopySecondStep()), this.getCopySecondStep(), this.getSecondStep());
        moveBlock(getBlocks(this.getCopyThirdStep()), this.getCopyThirdStep(), this.getThirdStep());
        moveBlock(getBlocks(this.getCopyFourthStep()), this.getCopyFourthStep(), this.getFourthStep());
        moveBlock(getBlocks(this.getCopyFinalStep()), this.getCopyFinalStep(), this.getFinalStep());
    }

    private List<org.bukkit.block.Block> getBlocks(Block block){
        List<org.bukkit.block.Block> blocks = new ArrayList<>();
        int startX = block.getX();
        int startY = block.getY();
        int startZ = block.getZ();
        int[] c1 = {startX, startY, startZ};
        int[] c2 = {startX+6, startY+6, startZ-4};
        //System.out.println(Arrays.toString(Arrays.stream(c1).toArray()));
        //System.out.println(Arrays.toString(Arrays.stream(c2).toArray()));
        World world = Bukkit.getWorld(this.getStartBlock().getWorld());
        getBlocksInRadius(blocks, c1, c2, world);
        return blocks;
    }

    private void moveBlock(List<org.bukkit.block.Block> blocks, Block startBlock, Block moveBlock){
        int xDiff = Math.abs(startBlock.getX()) - Math.abs(moveBlock.getX());
        int yDiff = Math.abs(startBlock.getY()) - Math.abs(moveBlock.getY());
        int zDiff = startBlock.getZ() + moveBlock.getZ();
        if(startBlock.getZ() < moveBlock.getZ()){
            zDiff = moveBlock.getZ() - startBlock.getZ();
        }else if(startBlock.getZ() == moveBlock.getZ()){
            zDiff = 0;
        }
        for(org.bukkit.block.Block block : blocks){
            World world = block.getWorld();
            world.getBlockAt(block.getX()+xDiff, block.getY()-yDiff, block.getZ()+zDiff).setType(block.getType());
            world.getBlockAt(block.getX()+xDiff, block.getY()-yDiff, block.getZ()+zDiff).setBlockData(block.getBlockData());
            allPlacedBlocks.add(world.getBlockAt(block.getX()+xDiff, block.getY()-yDiff, block.getZ()+zDiff));
        }
    }

    public boolean isBlockInRadius(org.bukkit.block.Block block){
        int startX = this.getFinalStep().getX();
        int startY = this.getFinalStep().getY();
        int startZ = this.getFinalStep().getZ();
        int[] c1 = {startX-1, startY, startZ};
        int[] c2 = {startX+6, startY+6, startZ-4};
        World world = Bukkit.getWorld(this.getStartBlock().getWorld());
        for(int i = c1[0]; i <= c2[0]; i++){
            for(int i1 = c1[1]; i1 <= c2[1]; i1++){
                for(int i2 = c1[2]; i2 >= c2[2]; i2--){
                    assert world != null;
                    if(block.getX() == i && block.getY() == i1 && block.getZ() == i2) return true;
                }
            }
        }
        return false;
    }

    public double compareCarToOriginal(){
        List<org.bukkit.block.Block> origBlocks = new ArrayList<>();
        List<org.bukkit.block.Block> newBlocks = new ArrayList<>();
        int startX = this.getFourthStep().getX();
        int startY = this.getFourthStep().getY();
        int startZ = this.getFourthStep().getZ();
        int[] c1 = {startX-1, startY, startZ};
        int[] c2 = {startX+6, startY+6, startZ-4};
        World world = Bukkit.getWorld(this.getStartBlock().getWorld());
        getBlocksInRadius(origBlocks, c1, c2, world);

        startX = this.getFinalStep().getX();
        startY = this.getFinalStep().getY();
        startZ = this.getFinalStep().getZ();
        c1 = new int[]{startX - 1, startY, startZ};
        c2 = new int[]{startX + 6, startY + 6, startZ - 4};
        getBlocksInRadius(newBlocks, c1, c2, world);

        int xDiff = Math.abs(getFourthStep().getX()) - Math.abs(getFinalStep().getX());
        int yDiff = Math.abs(getFourthStep().getY()) - Math.abs(getFinalStep().getY());
        int zDiff = getFourthStep().getZ() + getFinalStep().getZ();
        if(getFourthStep().getZ() < getFinalStep().getZ()){
            zDiff = getFourthStep().getZ() - getFinalStep().getZ();
        }else if(getFourthStep().getZ() == getFinalStep().getZ()){
            zDiff = 0;
        }
        int count = 0;
        int rightBlocks = 0;
        for(org.bukkit.block.Block block : origBlocks){
            if(block.getType().equals(Material.LIGHT)) continue;
            org.bukkit.block.Block compareBlock = getBlockAtLocation(newBlocks, block.getX()+xDiff, block.getY()+yDiff, block.getZ()+zDiff);
            assert compareBlock != null;
            if(block.getState().getBlockData().getAsString().equals(compareBlock.getState().getBlockData().getAsString())){
                if(block.getType().equals(Material.AIR))continue;
                rightBlocks++;
            }else{
                if(block.getType().equals(Material.AIR))rightBlocks--;
            }
            count++;
        }
        if(rightBlocks < 0) rightBlocks = 0;
        return ((double) rightBlocks / (double) count) *100 ;
    }

    private org.bukkit.block.Block getBlockAtLocation(List<org.bukkit.block.Block> blocks, int x, int y, int z){
        for(org.bukkit.block.Block block : blocks){
            if(block.getX() == x && block.getY() == y && block.getZ() == z) return block;
        }
        return null;
    }

    private void getBlocksInRadius(List<org.bukkit.block.Block> blockList, int[] c1, int[] c2, World world) {
        for(int i = c1[0]; i <= c2[0]; i++){
            for(int i1 = c1[1]; i1 <= c2[1]; i1++){
                for(int i2 = c1[2]; i2 >= c2[2]; i2--){
                    assert world != null;
                    blockList.add(world.getBlockAt(i, i1, i2));
                }
            }
        }
    }
}
