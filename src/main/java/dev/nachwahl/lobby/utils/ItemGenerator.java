package dev.nachwahl.lobby.utils;

import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.components.CustomModelDataComponent;

import java.util.Arrays;
import java.util.List;

public class ItemGenerator {

    public static ItemStack setMeta(String meta, ItemStack item) {
        item.editMeta((m) -> {
            Component[] ml = (Component[]) Arrays.stream(meta.split("\n")).map(Component::text).toArray();
            m.lore(List.of(ml));
        });
        return item;
    }

    public static ItemStack customModel(Material material, String data) {
        ItemStack item = new ItemStack(material);
        ItemMeta meta = item.getItemMeta();

        CustomModelDataComponent modelDataComponent = meta.getCustomModelDataComponent();
        modelDataComponent.setStrings(List.of(data));
        meta.setCustomModelDataComponent(modelDataComponent);

        item.setItemMeta(meta);
        return item;
    }

}
