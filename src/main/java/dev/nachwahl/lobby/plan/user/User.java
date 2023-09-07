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
        long pt = playtime;
        if(pt<60000) {
            return "<1min";
        }

        StringBuilder sb = new StringBuilder();

        // Weeks
        if (pt >= 604800000) {
            int days = (int) (pt / 604800000);
            sb.append(days).append("w ");
            pt -= days * 604800000;
        }
        // Days
        if (pt >= 86400000) {
            int days = (int) (pt / 86400000);
            sb.append(days).append("d ");
            pt -= days * 86400000;
        }

        // Hours
        if (pt >= 3600000) {
            int hours = (int) (pt / 3600000);
            sb.append(hours).append("h ");
            pt -= hours * 3600000;
        }

        // Minutes
        if (pt >= 60000) {
            int minutes = (int) (pt / 60000);
            sb.append(minutes).append("min ");
            pt -= minutes * 60000;
        }

        return sb.toString();
    }
}
