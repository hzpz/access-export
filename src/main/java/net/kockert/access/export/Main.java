/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */

package net.kockert.access.export;

import com.beust.jcommander.JCommander;
import com.healthmarketscience.jackcess.Database;
import com.healthmarketscience.jackcess.DatabaseBuilder;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class Main {

    private static final int EXIT_STATUS_INVALID_USAGE = 1;
    private static final int EXIT_STATUS_SOURCE_DOES_NOT_EXIST = 2;
    private static final int EXIT_STATUS_TARGET_DOES_ALREADY_EXIST = 3;
    private static final int EXIT_STATUS_ERROR_OPENING_SOURCE = 4;
    private static final int EXIT_STATUS_ERROR_OPENING_TARGET = 5;
    private static final int EXIT_STATUS_ERROR_DURING_EXPORT = 6;
    private static final int EXIT_STATUS_TARGET_DIR_DOES_NOT_EXIST = 7;
    private static final int EXIT_STATUS_TARGET_DIR_IS_NO_DIRECTORY = 8;

    public static void main(String[] args) {
        Main main = new Main(systemExitException -> {
            System.err.println(systemExitException.getMessage());
            System.exit(systemExitException.getStatusCode());
        });
        main.run(args);
    }

    private SystemExitHandler systemExitHandler;

    public Main(SystemExitHandler systemExitHandler) {
        this.systemExitHandler = systemExitHandler;
    }

    public void run(String... args) {
        CommandLineParameters clp = new CommandLineParameters();
        JCommander jCommander = new JCommander(clp);
        jCommander.parse(args);

        if (clp.helpRequested() || clp.insufficientParameters()) {
            StringBuilder usage = new StringBuilder();
            jCommander.usage(usage);
            systemExitHandler.handle(new SystemExitException(usage.toString(), EXIT_STATUS_INVALID_USAGE));
        }

        try {
            switch (clp.getFormat()) {
                case SQLITE:
                    jdbcExport(clp);
                    break;
                case CSV:
                    csvExport(clp);
                    break;
            }
        } catch (SystemExitException e) {
            systemExitHandler.handle(e);
        } catch (IOException | SQLException e) {
            systemExitHandler.handle(new SystemExitException("Error during export", e, EXIT_STATUS_ERROR_DURING_EXPORT));
        }
    }

    private void csvExport(CommandLineParameters clp) throws SystemExitException, IOException {
        Path sourceFile = getSourceFile(clp);
        Path targetDir = getTargetDir(clp);
        try (Database database = openSourceDatabase(sourceFile.toFile())) {
            CSVExporter exporter = new CSVExporter(database);
            if (clp.hasTablesToExport()) {
                exporter.export(targetDir, clp.getTablesToExport());
            } else {
                exporter.export(targetDir);
            }
        }
    }

    private void jdbcExport(CommandLineParameters clp) throws SystemExitException, IOException, SQLException {
        Path sourceFile = getSourceFile(clp);
        Path targetFile = getTargetFile(clp);
        try (Database database = openSourceDatabase(sourceFile.toFile());
             Connection jdbcConnection = openTargetDatabase(targetFile)) {
            Exporter exporter = new Exporter(database, clp.getTablesToExport());
            exporter.export(jdbcConnection);
        }
    }

    private Path getSourceFile(CommandLineParameters clp) throws SystemExitException {
        Path sourceFile = Paths.get(clp.getParameters().get(0));
        if (Files.notExists(sourceFile)) {
            throw new SystemExitException("'" + sourceFile + "' does not exist", EXIT_STATUS_SOURCE_DOES_NOT_EXIST);
        }
        return sourceFile;
    }

    private Path getTargetFile(CommandLineParameters clp) throws SystemExitException {
        Path targetFile = Paths.get(clp.getParameters().get(1));
        if (Files.exists(targetFile)) {
            throw new SystemExitException("'" + targetFile + "' does already exist", EXIT_STATUS_TARGET_DOES_ALREADY_EXIST);
        }
        return targetFile;
    }

    private Path getTargetDir(CommandLineParameters clp) throws SystemExitException {
        Path targetDir = Paths.get(clp.getParameters().get(1));
        if (Files.notExists(targetDir)) {
            throw new SystemExitException("'" + targetDir + "' does not exist", EXIT_STATUS_TARGET_DIR_DOES_NOT_EXIST);
        }
        if (!Files.isDirectory(targetDir)) {
            throw new SystemExitException("'" + targetDir + "' is not a directory", EXIT_STATUS_TARGET_DIR_IS_NO_DIRECTORY);
        }
        return targetDir;
    }

    private Database openSourceDatabase(File databaseFile) throws SystemExitException {
        Database database;
        try {
            database = new DatabaseBuilder(databaseFile).setReadOnly(true).open();
        } catch (IOException e) {
            throw new SystemExitException("Error opening the source database", e, EXIT_STATUS_ERROR_OPENING_SOURCE);
        }
        return database;
    }

    private Connection openTargetDatabase(Path databaseFile) throws SystemExitException {
        Connection jdbcConnection;
        try {
            jdbcConnection = DriverManager.getConnection("jdbc:sqlite:" + databaseFile);
        } catch (SQLException e) {
            throw new SystemExitException("Error opening the target database", e, EXIT_STATUS_ERROR_OPENING_TARGET);
        }
        return jdbcConnection;
    }

    /**
     * Handles {@link SystemExitException}s
     */
    interface SystemExitHandler {

        void handle(SystemExitException systemExitException);

    }

    /**
     * Indicates an exception that must result in a call to {@link System#exit(int)}.
     */
    static class SystemExitException extends Exception {

        private final int statusCode;

        public SystemExitException(String message, Throwable cause, int statusCode) {
            super(message + ": " + cause.getMessage(), cause);
            this.statusCode = statusCode;
        }

        public SystemExitException(String message, int statusCode) {
            super(message);
            this.statusCode = statusCode;
        }

        public int getStatusCode() {
            return statusCode;
        }
    }

}
