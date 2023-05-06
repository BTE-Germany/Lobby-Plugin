package dev.nachwahl.lobby.utils;

import dev.nachwahl.lobby.Lobby;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.Bukkit;
import org.bukkit.GameRule;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitScheduler;

import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;

public class RealTime {

    private static final double SECONDS_TO_TICKS_FACTOR = 1_000d / Math.pow(60d, 2d);
    private static final long FIRST_FULL_MOON_SINCE_EPOCH = 1814400; //22 Jan 1970
    private static final double SECONDS_IN_MOON_CYCLE = 2551392; //29.53 days
    private static final int MOON_PHASE_GAME_COUNT = 8;
    private static final int MOON_PHASE_GAME_ADVANCE = 24000;
    private static final int MOON_PHASE_GAME_CYCLE = MOON_PHASE_GAME_ADVANCE * MOON_PHASE_GAME_COUNT;

    @Getter @Setter
    private String timezone;
    @Getter @Setter
    private int updateInterval;
    @Getter
    private long time;
    @Getter
    private World world;

    public RealTime(String timezone,int updateInterval,World world) {
        this.timezone = timezone;
        this.updateInterval = updateInterval;
        this.world = world;

        this.world.setGameRule(GameRule.DO_DAYLIGHT_CYCLE,false);

        runUpdate();
    }

    private void runUpdate() {
       Bukkit.getScheduler().scheduleSyncRepeatingTask(Lobby.getInstance(), new Runnable() {
           @Override
           public void run() {
               time = getTime();
               for(Player player: Bukkit.getOnlinePlayers()) {
                   Lobby.getInstance().getUserSettingsAPI().getBooleanSetting(player,"realTime",(result) -> {
                       if(result) {
                           player.setPlayerTime(time,false);
                       }else {
                           player.resetPlayerTime();
                       }
                   });
               }
           }
       },0, 20L *updateInterval);
    }
    public long getTime() {
        ZonedDateTime dateTime = getDateTime();

        double secondsSinceFullMoon = (dateTime.toEpochSecond() - FIRST_FULL_MOON_SINCE_EPOCH) % SECONDS_IN_MOON_CYCLE;
        double moonPhase = secondsSinceFullMoon / SECONDS_IN_MOON_CYCLE;
        long epochOffsetAdjustedSeconds = dateTime.toEpochSecond() + dateTime.getOffset().getTotalSeconds();
        long secondsInDay = epochOffsetAdjustedSeconds % 86400;
        int secondsInDayOverflowAdjusted = overflow(18_000 + (int) (secondsInDay * SECONDS_TO_TICKS_FACTOR), 24_000);
        long secondsInYear = epochOffsetAdjustedSeconds % 31536000;
        long baseFullTime = (long) Math.floor((secondsInYear * SECONDS_TO_TICKS_FACTOR) / (double) MOON_PHASE_GAME_CYCLE) * MOON_PHASE_GAME_CYCLE;
        return baseFullTime + secondsInDayOverflowAdjusted + Math.round(moonPhase * MOON_PHASE_GAME_COUNT) * MOON_PHASE_GAME_ADVANCE;
    }

    public ZonedDateTime getDateTime(){
        return ZonedDateTime.ofInstant(Instant.now() /* in UTC */, ZoneId.of(timezone));
    }

    public static int overflow(int value, int at) {
        while (value > at) {
            value -= at;
        }
        return value;
    }

}
