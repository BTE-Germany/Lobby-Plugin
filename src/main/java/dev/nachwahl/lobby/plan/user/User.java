package dev.nachwahl.lobby.plan.user;

import lombok.Getter;
import lombok.Setter;

import java.util.concurrent.TimeUnit;

public class User implements Comparable<User>{
    @Getter @Setter
    private String player;
    @Getter @Setter
            private long playtime;

    public User(String player, long playtime) {
        this.player = player;
        this.playtime = playtime;
    }
    @Override
    public int compareTo(User user) {
        return (int)(user.getPlaytime()- this.playtime);
    }

    public String getPlaytimeReadable() {
       return String.format("%d h, %d m",
                TimeUnit.MILLISECONDS.toHours(playtime),
                TimeUnit.MILLISECONDS.toMinutes(playtime) - TimeUnit.MINUTES.toMinutes(TimeUnit.MILLISECONDS.toHours(playtime))
        );
    }
}
