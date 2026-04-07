package dev.nachwahl.lobby.utils;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;

public class GuiUtil {

    private static final String CHAR_REPO_NEG8 = "\uF808";
    private static final String CHAR_REPO_NEG170 = "\uF802\uF808\uF80A\uF80C";

    public static Component getCustomDataTitle(Component title, String customData) {
        return Component.text(CHAR_REPO_NEG8 + customData + CHAR_REPO_NEG170, NamedTextColor.WHITE).append(title);
    }

}
