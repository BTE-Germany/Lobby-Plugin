package dev.nachwahl.lobby.quests;

import org.bukkit.Material;

public class Block {
    private int x;
    private int y;
    private int z;
    private String world;

    private Material material;

    public Block(int x, int y, int z, String world) {
        this.x = x;
        this.y = y;
        this.z = z;
        this.world = world;
    }

    public Block(int x, int y, int z, String world, Material material) {
        this.x = x;
        this.y = y;
        this.z = z;
        this.world = world;
        this.material = material;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public int getZ() {
        return z;
    }

    public Material getMaterial() {
        return material;
    }

    public void setMaterial(Material material) {
        this.material = material;
    }

    public String getWorld() {
        return world;
    }

    public boolean equalsBukkitBlockLocation(org.bukkit.block.Block block) {
        return this.getX() == block.getX() && this.getY() == block.getY() && this.getZ() == block.getZ() && this.getWorld().equals(block.getWorld().getName());
    }

    @Override
    public String toString() {
        return "x=" + this.x + " y=" + this.y + " z=" + this.z + " ";
    }
}
