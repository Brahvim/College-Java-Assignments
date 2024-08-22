package com.brahvim.college_assignments;

public enum AppExitCode {

    OKAY(),
    UNKNOWN_FLAG_PASSED(), // <- I hated this idea till I realized that VERSIONING APPS is a thing!
                           // (NEW VERSIONS may come with NEW FLAGS!)
    INVALID_CONFIG_FILE();

}
