package com.brahvim.college_assignments;

public enum AppExitCode {

    OKAY(),
    UNKNOWN_FLAG_PASSED(), // <- I hated this idea till I realized that VERSIONING APPS is a thing!
                           // (NEW VERSIONS may come with NEW FLAGS!)
    INVALID_CONFIG_FILE();

    // region Class stuff.
    private final int exitCode;

    private class AppExitCodeStatics {

        private static int enumCount = 0;

    }

    private AppExitCode() {
        this.exitCode = super.ordinal();
    }

    public int getExitCode() {
        return this.exitCode;
    }
    // endregion

}
