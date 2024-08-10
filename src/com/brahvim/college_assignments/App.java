package com.brahvim.college_assignments;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Scanner;

public class App {

    public enum AppExitCode {

        OK(),
        NO_PATHS_GIVEN(),
        SOURCE_NOT_FETCHED(),
        NO_DESTINATION_PATH(),
        WRITING_NOT_ALLOWED(),
        READING_NOT_ALLOWED(),
        DESTINATION_NOT_CREATED(),
        SOURCE_FILE_IS_DIRECTORY(),
        /* End of fields. */ ;

        // region The class part
        public final int exitCode;

        static class AppExitCodeEnumCounter {

            private AppExitCodeEnumCounter() {

            }

            private static int count = 0;

        }

        private AppExitCode() {
            this.exitCode = ++AppExitCode.AppExitCodeEnumCounter.count;
        }
        // endregion

    }

    private static class AppStringIds {

        int sourceStrId, destStrId;

    }

    private static class AppPermissions {

        boolean overwriteDestination;

    }

    public static void showHelp() {
        System.out.println(
                """
                        Usage: fcpy SOURCE DEST
                        ...Or: fcpy -f SOURCE DEST
                        ...Or: fcpy --help
                        ...Or: fcpy -h

                        Copies file SOURCE to path DEST, overwriting DEST if asked.

                        Please note:
                        - SOURCE and DEST must both be files.
                        - SOURCE must exist although DEST may not.
                        - If DEST exists, you will be asked for permission before overwriting it.

                        Pass -f if you are willing to overwrite file DEST with the data of SOURCE without being asked for permission.
                        """);
    }

    public static void exitWithoutOverwriting() {
        System.out.println("Overwriting disallowed, exiting.");
        App.exitWithCode(AppExitCode.OK);
    }

    public static void exitWithCode(final AppExitCode p_failure) {
        System.exit(p_failure.exitCode);
    }

    private static void performActualCopy(final String destPath, final String sourcePath, final File destFile,
            final File sourceFile) {
        try (final var is = new FileInputStream(sourceFile)) {

            App.copyGivenSource(destPath, destFile, is);

        } catch (final SecurityException e) {
            if (!sourceFile.getParentFile().canRead())
                System.out.printf(
                        "Program JVM not currently permitted to read SOURCE's parent, `%s`.%n",
                        sourceFile.getParentFile().getAbsolutePath());
            else
                System.out.printf(
                        "Program JVM not currently permitted to read SOURCE file `%s`.%n",
                        sourceFile.getAbsolutePath());
            App.exitWithCode(AppExitCode.READING_NOT_ALLOWED);
        } catch (final IOException e) {
            System.out.printf("SOURCE file `%s` could not be fetched. Does it exist?%n", sourcePath);
            App.exitWithCode(AppExitCode.SOURCE_NOT_FETCHED);
        }
    }

    private static void copyGivenSource(final String destPath, final File destFile, final FileInputStream is) {
        try (final var os = new FileOutputStream(destFile)) {
            int bytesRead;
            final byte[] buffer = new byte[4096];

            while ((bytesRead = is.read(buffer)) != -1) {
                os.write(buffer, 0, bytesRead);
                os.flush();
            }
        } catch (final SecurityException e) {
            if (!destFile.getParentFile().canWrite())
                System.out.printf(
                        "Program JVM not currently permitted to write to DEST's parent, `%s`.%n",
                        destFile.getParentFile().getAbsolutePath());
            else
                System.out.printf(
                        "Program JVM not currently permitted to write DEST file `%s`.%n",
                        destFile.getAbsolutePath());
            App.exitWithCode(AppExitCode.WRITING_NOT_ALLOWED);
        } catch (final IOException e) {
            System.out.printf("DEST file `%s` could not be created. Is there enough space?%n", destPath);
            App.exitWithCode(AppExitCode.DESTINATION_NOT_CREATED);
        }
    }

    private static void askForOverwriting(
            final AppPermissions permissions, final String destPath, final File destFile) {
        if (destFile.exists() && !permissions.overwriteDestination) {
            System.out.printf("DEST file `%s` already exists. Overwrite? [Y/n]: ", destPath);

            final Scanner sc = new Scanner(System.in);
            final String permissionAnswer = sc.nextLine();
            sc.close();

            // DO NOT rely on stack-unwinding!
            // That's bad for performance!

            // Just check, and go out that way:
            if (permissionAnswer == null)
                App.exitWithoutOverwriting();

            if (!("y".equalsIgnoreCase(permissionAnswer) || "".equals(permissionAnswer)))
                App.exitWithoutOverwriting();
        }

        permissions.overwriteDestination = true;
    }

    private static void checkParameterPresence(final AppStringIds stringIds) {
        if (stringIds.destStrId == -1) {
            if (stringIds.sourceStrId == -1) {
                System.out.println("Neither of SOURCE or DEST was provided. Exiting.");
                App.exitWithCode(AppExitCode.NO_PATHS_GIVEN);
            } else {
                System.out.println("DEST was not provided. Exiting.");
                App.exitWithCode(AppExitCode.NO_DESTINATION_PATH);
            }
        }
    }

    private static void parseParameters(final String[] p_args, final AppStringIds stringIds,
            final AppPermissions permissions) {
        for (int i = 0; i < p_args.length; i++) {
            final String s = p_args[i];

            switch (s) {
                case "-h":
                case "--help": {
                    App.showHelp();
                    App.exitWithCode(AppExitCode.OK);
                }
                    break;

                case "-f": {
                    permissions.overwriteDestination = false;
                }
                    break;

                default: {
                    if (stringIds.sourceStrId == -1)
                        stringIds.sourceStrId = i;
                    else
                        stringIds.destStrId = i;
                }
                    break;
            }
        }
    }

    public static void main(final String[] p_args) {
        if (p_args.length < 2) {
            App.showHelp();
            App.exitWithCode(AppExitCode.OK);
        }

        final AppStringIds stringIds = new AppStringIds();
        final AppPermissions permissions = new AppPermissions();

        App.parseParameters(p_args, stringIds, permissions);
        App.checkParameterPresence(stringIds);

        final String destPath = p_args[stringIds.destStrId];
        final String sourcePath = p_args[stringIds.sourceStrId];

        File destFile = new File(destPath);
        final File sourceFile = new File(sourcePath);

        if (sourceFile.isDirectory()) {
            System.out.printf(
                    "Path `%s` provided for SOURCE points to a directory and not a file. Exiting.%n",
                    sourcePath);
            App.exitWithCode(AppExitCode.SOURCE_FILE_IS_DIRECTORY);
        }

        if (destFile.isDirectory())
            destFile = new File(destFile, sourceFile.getName());

        App.askForOverwriting(permissions, destPath, destFile);
        App.performActualCopy(destPath, sourcePath, destFile, sourceFile);
    }

}
