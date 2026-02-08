package dev.nachwahl.lobby.utils;

import co.aikar.commands.annotation.Dependency;
import dev.nachwahl.lobby.LobbyPlugin;
import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.jetbrains.annotations.NotNull;

public class MonthPlaceholders extends PlaceholderExpansion {

    @Dependency
    private static LobbyPlugin lobbyPlugin;

    public MonthPlaceholders(LobbyPlugin lobbyPlugin) {
        this.lobbyPlugin = lobbyPlugin;
    }

    @Override
    public @NotNull String getIdentifier() {
        return "months";
    }

    @Override
    public @NotNull String getAuthor() {
        return "Morgon";
    }

    @Override
    public @NotNull String getVersion() {
        return "1.0.0";
    }

    @Override
    public boolean canRegister() {
        return true;
    }

    @Override
    public String onPlaceholderRequest(org.bukkit.entity.Player player, String identifier) {
        return lobbyPlugin.getLanguageAPI().getMessageString(lobbyPlugin.getLanguageAPI().getLanguage(player), identifier);
    }
}
