package dev.nachwahl.lobby.language;

import lombok.Getter;

public enum Language {
    GERMAN("de_DE"),
    ENGLISH("en_EN");

    @Getter
    private final String lang;

    Language(String lang) {
        this.lang = lang;
    }

    public static Language fromString(String lang) {
        for (Language language : values()) {
            if (language.getLang().equals(lang)) {
                return language;
            }
        }
        return ENGLISH;
    }
}