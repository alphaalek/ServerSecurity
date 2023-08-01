package me.alek.serversecurity.lang;

import me.alek.serversecurity.lang.locales.DanishRepresentor;
import me.alek.serversecurity.lang.locales.EnglishRepresentor;

public enum Locale {

    DANISH(DanishRepresentor.values(), "da"),

    ENGLISH(EnglishRepresentor.values(), "en");

    private final LocaleMessageRepresentor[] langs;
    private final String[] abbrevs;

    Locale(LocaleMessageRepresentor[] langs, String... abbrevs) {
        this.langs = langs;
        this.abbrevs = abbrevs;
    }

    public LocaleMessageRepresentor[] getLangs() {
        return langs;
    }

    public String[] getAbbreviations() {
        return abbrevs;
    }

    public static Locale fromString(String localeString) {

        // loop through the different languages and check what is configured to be used
        for (Locale locale : Locale.values()) {

            for (String abbrev : locale.getAbbreviations()) {

                if (localeString.equalsIgnoreCase(abbrev)) {
                    return locale;
                }
            }
        }
        // no valid locale, so just use english
        return ENGLISH;
    }

}
