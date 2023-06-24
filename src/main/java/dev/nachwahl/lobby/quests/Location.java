package dev.nachwahl.lobby.quests;

public class Location {
    private double x;
    private double y;
    private double z;
    private float yaw;
    private float pitch;
    private String world;

    public Location(double x, double y, double z, float yaw, float pitch, String world){
        this.x = x;
        this.y = y;
        this.z = z;
        this.yaw = yaw;
        this.pitch = pitch;
        this.world = world;
    }

    public double getX() {
        return x;
    }

    public double getY() {
        return y;
    }

    public double getZ() {
        return z;
    }

    public float getPitch() {
        return pitch;
    }

    public float getYaw() {
        return yaw;
    }

    public String getWorld() {
        return world;
    }
}
