package com.brahvim.college_assignments.enums;

import java.util.HashMap;
import java.util.Map;

public enum AppFlag {

    CONF_PATH('f'),
    RUN_SCRIPT('r'),
    IGNORE_CONF('i'),
    IGNORE_ENTRY('x'),
    CHECK_CONF_AND_EXIT('c')
    /*  */;

    // region Class stuff.
    private static final Map<Character, AppFlag> IDENTIFIERS_MAP = new HashMap<>(AppFlag.values().length);

    private final char identifier;

    static {
        for (final var e : AppFlag.values())
            AppFlag.IDENTIFIERS_MAP.put(e.getIdentifier(), e);
    }

    private AppFlag(final char p_identifier) {
        this.identifier = p_identifier;
    }

    public char getIdentifier() {
        return this.identifier;
    }

    public static AppFlag identifierToFlag(final char p_identifier) {
        return AppFlag.IDENTIFIERS_MAP.get(p_identifier);
    }
    // endregion

}
