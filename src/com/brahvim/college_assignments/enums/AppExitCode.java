package com.brahvim.college_assignments.enums;

public enum AppExitCode {

    OKAY(),
    ENTRY_INVALID(), // ...Need to be
    SQL_PATH_INVALID(), // specific enough...
    CONF_PATH_INVALID(),
    FLAG_PARSING_ERROR(), // ...Just not here!
    UNKNOWN_FLAG_PASSED(), // <- Hated this till realizing what VERSIONING was (new VERSIONS bring FLAGS)!
    INVALID_CONFIG_FILE(); // ...Never thrown...?

}
