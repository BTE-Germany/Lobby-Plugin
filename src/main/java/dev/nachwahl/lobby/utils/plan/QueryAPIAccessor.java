package dev.nachwahl.lobby.utils.plan;
import com.djrapitops.plan.query.QueryService;
import lombok.Getter;

public class QueryAPIAccessor {

    @Getter
    private final QueryService queryService;

    public QueryAPIAccessor(QueryService queryService) {
        this.queryService = queryService;

    }
}
