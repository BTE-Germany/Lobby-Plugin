package dev.nachwahl.lobby.cinematic;

import org.bukkit.Location;
import org.bukkit.Sound;

public class PathPoint {

    private Location location;

    private double ticksToPoint;

    private String title;
    private String subtitle;
    private String chatMessage;
    private Sound sound;

    public PathPoint(Location location, double ticksToPoint) {
        this.location = location;
        this.ticksToPoint = ticksToPoint;
    }

    public PathPoint(Location location, double ticksToPoint, String title, String subtitle, String chatMessage, Sound sound) {
        this.location = location;
        this.ticksToPoint = ticksToPoint;
        this.title = title;
        this.subtitle = subtitle;
        this.chatMessage = chatMessage;
        this.sound = sound;
    }

    public Location getLocation() {
        return location;
    }

    public void setLocation(Location location) {
        this.location = location;
    }

    public double getTicksToPoint() {
        return ticksToPoint;
    }

    public void setTicksToPoint(double ticksToPoint) {
        this.ticksToPoint = ticksToPoint;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getSubtitle() {
        return subtitle;
    }

    public void setSubtitle(String subtitle) {
        this.subtitle = subtitle;
    }

    public String getChatMessage() {
        return chatMessage;
    }

    public void setChatMessage(String chatMessage) {
        this.chatMessage = chatMessage;
    }

    public Sound getSound() {
        return sound;
    }

    public void setSound(Sound sound) {
        this.sound = sound;
    }
}
