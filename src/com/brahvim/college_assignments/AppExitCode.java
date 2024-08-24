package com.brahvim.college_assignments;

public enum AppExitCode {

    OKAY(),
    FLAG_PARSING_ERROR(),
    PATH_NOT_SPECIFIED(), // ...Need to be
    ENTRY_NOT_SPECIFIED(), // specific enough...
    UNKNOWN_FLAG_PASSED(), // <- I hated this idea till I realized that VERSIONING APPS is a thing!
                           // (NEW VERSIONS may come with NEW FLAGS!)
    INVALID_CONFIG_FILE();

}
