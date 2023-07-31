package dev.nachwahl.lobby.plan;
import com.djrapitops.plan.capability.CapabilityService;
import com.djrapitops.plan.query.QueryService;
import org.bukkit.Bukkit;

import java.util.Optional;

public class PlanIntegration {

    public PlanIntegration() {
    }

    public Optional<QueryAPIAccessor> hookIntoPlan() {
        if (!areAllCapabilitiesAvailable()) return Optional.empty();
        return Optional.ofNullable(createQueryAPIAccessor());
    }

    private boolean areAllCapabilitiesAvailable() {
        CapabilityService capabilities = CapabilityService.getInstance();
        return capabilities.hasCapability("QUERY_API");
    }

    private QueryAPIAccessor createQueryAPIAccessor() {
        try {
            return new QueryAPIAccessor(QueryService.getInstance());
        } catch (IllegalStateException planIsNotEnabled) {
            Bukkit.getLogger().info("Plan ist nicht installiert.");

            return null;
        }
    }
}
