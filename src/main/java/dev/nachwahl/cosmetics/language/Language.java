package dev.nachwahl.cosmetics.language;

import lombok.Getter;

public enum Language {
    GERMAN("de_DE"),
    ENGLISH("en_EN");

    @Getter
    private final String lang;

    Language(String lang) {
        this.lang = lang;
    }
}