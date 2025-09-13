package dev.nachwahl.lobby.utils;

import co.aikar.commands.annotation.Dependency;
import dev.nachwahl.lobby.Lobby;
import dev.nachwahl.lobby.language.LanguageAPI;
import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.jetbrains.annotations.NotNull;

public class MonthPlaceholders extends PlaceholderExpansion {

    @Dependency
    private static Lobby lobby;

    public MonthPlaceholders(Lobby lobby) {
        this.lobby = lobby;
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
       return lobby.getLanguageAPI().getMessageString(lobby.getLanguageAPI().getLanguage(player), identifier);
    }
}
