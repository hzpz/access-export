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

    public static void main(String[] args) {
        CommandLineParameters clp = new CommandLineParameters();
        JCommander jCommander = new JCommander(clp);
        jCommander.parse(args);

        if (clp.helpRequested() || clp.insufficientParameters()) {
            jCommander.usage();
            System.exit(EXIT_STATUS_INVALID_USAGE);
        }

        try {
            Path sourceFile = getSourceFile(clp);
            Path targetFile = getTargetFile(clp);
            try (Database database = openSourceDatabase(sourceFile.toFile());
                 Connection jdbcConnection = openTargetDatabase(targetFile)) {
                Exporter exporter = new Exporter(database, clp.getTablesToExport());
                exporter.export(jdbcConnection);
            }
        } catch (SystemExitException e) {
            System.err.println(e.getMessage());
            System.exit(e.getStatusCode());
        } catch (IOException | SQLException e) {
            System.err.println("Error during export: " + e.getMessage());
        }
    }

    private static Path getSourceFile(CommandLineParameters clp) throws SystemExitException {
        Path sourceFile = Paths.get(clp.getParameters().get(0));
        if (Files.notExists(sourceFile)) {
            throw new SystemExitException("'" + sourceFile + "' does not exist", EXIT_STATUS_SOURCE_DOES_NOT_EXIST);
        }
        return sourceFile;
    }

    private static Path getTargetFile(CommandLineParameters clp) throws SystemExitException {
        Path targetFile = Paths.get(clp.getParameters().get(1));
        if (Files.exists(targetFile)) {
            throw new SystemExitException("'" + targetFile + "' does already exist", EXIT_STATUS_TARGET_DOES_ALREADY_EXIST);
        }
        return targetFile;
    }

    private static Database openSourceDatabase(File databaseFile) throws SystemExitException {
        Database database;
        try {
            database = new DatabaseBuilder(databaseFile).setReadOnly(true).open();
        } catch (IOException e) {
            throw new SystemExitException("Error opening the source database", e, EXIT_STATUS_ERROR_OPENING_SOURCE);
        }
        return database;
    }

    private static Connection openTargetDatabase(Path databaseFile) throws SystemExitException {
        Connection jdbcConnection;
        try {
            jdbcConnection = DriverManager.getConnection("jdbc:sqlite:" + databaseFile);
        } catch (SQLException e) {
            throw new SystemExitException("Error opening the target database", e, EXIT_STATUS_ERROR_OPENING_TARGET);
        }
        return jdbcConnection;
    }

    /**
     * Indicates an exception that must result in a call to {@link System#exit(int)}.
     */
    private static class SystemExitException extends Exception {

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
