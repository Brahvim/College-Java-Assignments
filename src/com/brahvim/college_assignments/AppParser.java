package com.brahvim.college_assignments;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.EnumMap;
import java.util.Map;
import java.util.Queue;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.BooleanSupplier;

import com.brahvim.college_assignments.enums.AppConfigEntry;
import com.brahvim.college_assignments.enums.AppExitCode;
import com.brahvim.college_assignments.enums.AppFlag;
import com.brahvim.college_assignments.enums.AppParserFlag;

public class AppParser {

    private AppParser() {
        throw new IllegalAccessError();
    }

    public static Map<AppConfigEntry, String> beginAllParsing(final String[] p_args) {
        final AtomicBoolean parsingOver = App.startApologyThread(1000, "(Still parsing flags! Please wait...)");

        final Map<AppFlag, String> flags = new EnumMap<>(AppFlag.class);
        final Queue<AppParserFlag> parserFlags = new ArrayDeque<>(p_args.length);
        final Map<AppConfigEntry, String> configuration = new EnumMap<>(AppConfigEntry.class);

        if (!configuration.containsKey(AppConfigEntry.SQL_PATH))
            configuration.put(AppConfigEntry.SQL_PATH, "");

        if (!configuration.containsKey(AppConfigEntry.CONF_PATH))
            configuration.put(

                    AppConfigEntry.CONF_PATH,
                    Path.of(".", ".env").toString() // "Platform independence". LOL.

            );

        // Parse flags in:
        for (final var s : p_args)
            AppParser.flagParserIteration(s, flags, parserFlags, configuration);

        if (!flags.containsKey(AppFlag.IGNORE_CONF)) {
            if (AppParser.readConfigFile(configuration)) {
                AppParser.endConfigFileErrors();
                App.exit(AppExitCode.INVALID_CONFIG_FILE);
            }

            if (flags.containsKey(AppFlag.CHECK_CONF_AND_EXIT))
                System.exit(AppExitCode.OKAY.ordinal());
        }

        synchronized (parsingOver) {
            parsingOver.set(true);
        }

        return configuration;
    }

    /**
     * Reads the config file and stores it in the given
     * {@linkplain Map Map&lt;String,String&gt;}.
     * 
     * <hr>
     * </hr>
     * 
     * If a mapping exists and maps to an empty string - a {@code ""} - then it'll
     * be skipped.
     * 
     * <hr>
     * </hr>
     * 
     * The parser does in fact, allow comments starting with the {@code #}
     * character. They may start either mid-line or at the beginning of one.
     * 
     * <hr>
     * </hr>
     */
    public static boolean readConfigFile(final Map<AppConfigEntry, String> p_config) {
        final String filePath = p_config.get(AppConfigEntry.CONF_PATH);
        System.out.printf("Using config file found at `%s`.%n", filePath);

        // These class's `close()` method recursively close the innermost `Closeable`:
        try (final BufferedReader reader = new BufferedReader(new FileReader(filePath))) {

            String line = null;
            boolean hasErrors = false;

            // NOTE: If you have to use `continue`, **SET `line` TO `null`!**

            for (int i = 0; reader.ready();) {

                if (line == null)
                    line = reader.readLine();

                final int commentStart = line.indexOf('#');

                switch (commentStart) {

                    case -1 -> {
                        // Nothing!
                        // DON'T remove this case!
                        // It's here to ensure that `default` isn't executed!
                    }

                    case 0 -> {
                        line = null;
                        continue;
                    }

                    default -> {
                        line = line.substring(0, commentStart - 1);
                    }

                }

                final int separatorId = line.indexOf('=');

                if (separatorId == -1) {
                    if (!(line.isBlank() || (commentStart == 0))) {
                        if (!hasErrors) {
                            hasErrors = true;
                            AppParser.beginConfigFileErrors();
                        }

                        System.out.printf("\t(Line content: \"%s\".)%n", line);
                        System.out.printf("- Line `%d`: Missing `=`.%n", i + 1);
                    }

                    ++i;
                    line = null;
                    continue;
                }

                final String value = line.substring(separatorId + 1).trim();
                final String entryName = line.substring(0, separatorId).trim();
                final AppConfigEntry configEntry = AppConfigEntry.valueOf(entryName);

                // If the value from the config file is empty:
                if ("".equals(value)) { // If there is no value, leave it `null`!
                    line = null;
                    continue;
                }

                final String status = configEntry.getChecker().apply(value);
                if (status != null) {
                    if (!hasErrors) {
                        hasErrors = true;
                        AppParser.beginConfigFileErrors();
                    }

                    System.out.print("- ");
                    System.out.println(status);
                }

                p_config.put(configEntry, value);
                line = null;
                ++i;
            }

            return hasErrors;

        } catch (final IOException e) {
            System.out.printf("Config file not found at path `%s`.%n", filePath);
            return false;
        } catch (final SecurityException e) {
            System.out.printf("Process disallowed from reading config file at `%s`.%n", filePath);
            return false;
        }
    }

    private static void flagParserIteration(

            final String p_argString,
            final Map<AppFlag, String> p_flags,
            final Queue<AppParserFlag> p_parserFlags,
            final Map<AppConfigEntry, String> p_configuration

    ) {
        final int length = p_argString.length();
        final AppParserFlag polledParserFlag = p_parserFlags.poll();

        final var stages = new ArrayList<BooleanSupplier>(2);
        stages.add(() -> AppParser.processParserFlags(p_argString, polledParserFlag, p_configuration));
        stages.add(() -> AppParser.processAppFlags(length, p_argString, p_flags, p_parserFlags));

        for (final var s : stages)
            if (!s.getAsBoolean())
                break;
    }

    private static void endConfigFileErrors() {
        System.out.println("\n====END OF CONFIG FILE ERRORS.====\n");
    }

    private static void beginConfigFileErrors() {
        System.out.println("\n=======CONFIG FILE ERRORS!=======\n");
    }

    // You, sir, are allowed to ask for skipping this iteration.
    // That's why you return a `boolean`. Say `true` to skip.
    private static boolean processParserFlags(

            final String p_argString,
            final AppParserFlag p_polledParserFlag,
            final Map<AppConfigEntry, String> p_config

    ) {
        if (p_polledParserFlag == null)
            return true;

        switch (p_polledParserFlag) {

            case READ_SQL_PATH -> {
                if (!p_argString.endsWith(".sql"))
                    App.exit(AppExitCode.SQL_PATH_INVALID);

                if (!new File(p_argString).exists())
                    App.exit(AppExitCode.SQL_PATH_INVALID);

                final String toAdd = String.format("\"%s\", ", p_argString);
                final String oldValue = p_config.get(AppConfigEntry.SQL_PATH);
                p_config.put(AppConfigEntry.SQL_PATH, oldValue.concat(toAdd));

                return false;
            }

            case READ_CONF_PATH -> {
                if (new File(p_argString).exists())
                    p_config.put(AppConfigEntry.CONF_PATH, p_argString);
                else
                    App.exit(AppExitCode.CONF_PATH_INVALID);
                return false;
            }

            case READ_CONF_ENTRY -> {
                final AppConfigEntry e = AppConfigEntry.valueOf(p_argString);

                if (e == null)
                    App.exit(AppExitCode.ENTRY_INVALID);
                else
                    p_config.put(e, "");
                return false;
            }

        }

        return true;
    }

    private static boolean processAppFlags(

            final int p_length,
            final String p_argString,
            final Map<AppFlag, String> p_flags,
            final Queue<AppParserFlag> p_parserFlags

    ) {
        AppFlag parsedFlag = null;

        switch (p_length) {
            case 0 -> {
                break;
            }

            case 1 -> {
                parsedFlag = AppFlag.identifierToFlag(p_argString.charAt(0));
            }

            default -> {
                for (int i = 0; i < p_length; ++i) {
                    final char c = p_argString.charAt(i);

                    switch (c) {
                        case '-' -> {
                            continue;
                        }

                        // Couldn't use `enum`s around here! <Sigh>...:
                        case 'r' -> p_parserFlags.add(AppParserFlag.READ_SQL_PATH);
                        case 'f' -> p_parserFlags.add(AppParserFlag.READ_CONF_PATH);
                        case 'x' -> p_parserFlags.add(AppParserFlag.READ_CONF_ENTRY);
                        // `default` is just... breaking out, I guess:
                    }

                    parsedFlag = AppFlag.identifierToFlag(c);
                }
            } // End of `default` block.
        } // End of `switch`.

        if (parsedFlag == null)
            App.exit(AppExitCode.UNKNOWN_FLAG_PASSED);

        p_flags.put(parsedFlag, "");
        return true;
    }

}
