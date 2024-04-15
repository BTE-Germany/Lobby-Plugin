package dev.nachwahl.lobby.utils;

import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

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

    public static ItemStack customModel(Material material, Integer data) {
        ItemStack item = new ItemStack(material);
        ItemMeta meta = item.getItemMeta();
        meta.setCustomModelData(data);
        item.setItemMeta(meta);
        return item;
    }

}
