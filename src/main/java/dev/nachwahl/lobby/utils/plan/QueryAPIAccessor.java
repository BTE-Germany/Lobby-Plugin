package dev.nachwahl.lobby.utils.plan;

import com.djrapitops.plan.query.QueryService;
import dev.nachwahl.lobby.utils.plan.user.User;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.sql.ResultSet;
import java.util.*;
import java.util.stream.Collectors;

public class QueryAPIAccessor {

    @Getter
    private final QueryService queryService;

    public QueryAPIAccessor(QueryService queryService) {
        this.queryService = queryService;

    }

    public long getPlaytimeOnAllServers(UUID player, long ago) {
        long now = System.currentTimeMillis();
        Set<UUID> serverUUIDs = queryService.getCommonQueries()
                .fetchServerUUIDs();

        long playtime = 0;
        for (UUID serverUUID : serverUUIDs) {
            playtime += queryService.getCommonQueries().fetchPlaytime(
                    player, serverUUID, ago, now
            );
        }
        return playtime;
    }

    public ArrayList<User> getTopPlaytimeOnAllServers(long ago, int length) {
        ArrayList<User> users = new ArrayList<>();
        queryService.query("SELECT uuid,name FROM plan_users", statement -> {
            try (ResultSet set = statement.executeQuery()) {
                while (set.next()) {
                    users.add(new User(set.getString("name"),getPlaytimeOnAllServers(UUID.fromString(set.getString("uuid")),ago)));
                }
            }
            return null;
        });
        Collections.sort(users);
        if(length<=0) {
            return  users;
        }
        return (ArrayList<User>) users.subList(0,length);
    }
}
