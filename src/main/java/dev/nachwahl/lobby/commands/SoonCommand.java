package dev.nachwahl.lobby.commands;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.Default;
import co.aikar.commands.annotation.Dependency;
import dev.nachwahl.lobby.LobbyPlugin;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;


@CommandAlias("soon|rewards|shop")
public class SoonCommand extends BaseCommand {
    @Dependency
    private LobbyPlugin plugin;


    @Default
    public void onSoonCommand(CommandSender sender) {
        Player player = (Player) sender;
        player.sendMessage(Component.text("Coming soon...").color(NamedTextColor.RED).decorate(TextDecoration.BOLD));
    }

}
