package dev.nachwahl.lobby.utils;

import net.kyori.adventure.text.Component;
import org.bukkit.inventory.ItemStack;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Stream;

public class ItemGenerator {

    public static ItemStack setMeta(String meta, ItemStack item) {
        item.editMeta((m) -> {
            Component[] ml = (Component[]) Arrays.stream(meta.split("\n")).map(Component::text).toArray();
            m.lore(List.of(ml));
        });
                return item;
    }

}
