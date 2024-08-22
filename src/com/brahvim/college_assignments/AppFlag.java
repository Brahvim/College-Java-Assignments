package com.brahvim.college_assignments;

import java.util.HashMap;
import java.util.Map;

public enum AppFlag {

    CHECK_CONF("-c");

    // region Class stuff.
    private static final Map<String, AppFlag> IDENTIFIERS_MAP = new HashMap<>(AppFlag.values().length);

    private final String identifier;

    static {
        for (final var e : AppFlag.values())
            AppFlag.IDENTIFIERS_MAP.put(e.getIdentifier(), e);
    }

    private AppFlag(final String p_identifier) {
        this.identifier = p_identifier;
    }

    public String getIdentifier() {
        return this.identifier;
    }

    public static AppFlag identifierToFlag(final String p_identifier) {
        return AppFlag.IDENTIFIERS_MAP.get(p_identifier);
    }
    // endregion

}
