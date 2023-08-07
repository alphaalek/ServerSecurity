package me.alek.serversecurity.lang;

import me.alek.serversecurity.lang.locales.DanishRepresentor;
import me.alek.serversecurity.lang.locales.EnglishRepresentor;

import java.util.*;

public enum Locale {

    DANISH(DanishRepresentor.values(), "da", "danish"),

    ENGLISH(EnglishRepresentor.values(), "en", "english");

    private final LocaleMessageRepresentor[] langs;
    private final String[] abbrevs;

    private static final Map<String, Locale> localeLookup = new HashMap<>();

    static {
        for (Locale locale : Locale.values()) {
            for (String abbrev : locale.getAbbreviations()) {
                localeLookup.put(abbrev, locale);
            }
        }
    }

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

    public static List<String> getAllAbbreviations() {
        List<String> abbrevs = new ArrayList<>();

        for (Locale locale : Locale.values()) {
            abbrevs.addAll(Arrays.asList(locale.getAbbreviations()));
        }
        return abbrevs;
    }

    public static boolean hasLocale(String localeString) {
        return localeLookup.containsKey(localeString.toLowerCase());
    }

    public static Locale fromString(String localeString) {
        return localeLookup.getOrDefault(localeString.toLowerCase(), Locale.ENGLISH);
    }

}
