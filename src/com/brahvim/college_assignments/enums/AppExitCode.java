package com.brahvim.college_assignments.enums;

public enum AppExitCode {

    OKAY(),
    FLAG_PARSING_ERROR(),
    PATH_INVALID(), // ...Need to be
    ENTRY_INVALID(), // specific enough...
    UNKNOWN_FLAG_PASSED(), // <- I hated this idea till I realized that VERSIONING APPS is a thing!
                           // (NEW VERSIONS may come with NEW FLAGS!)
    INVALID_CONFIG_FILE();

}
