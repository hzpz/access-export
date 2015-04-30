/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */

package net.kockert.access.export;

import com.healthmarketscience.jackcess.Database;
import com.healthmarketscience.jackcess.util.ExportUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.file.Path;
import java.util.Set;

/**
 * Exports an Access database using Jackcess' built-in {@link ExportUtil} with default settings plus header.
 */
public class CSVExporter {

    private static final Logger LOGGER = LoggerFactory.getLogger(CSVExporter.class);

    private final Database db;

    public CSVExporter(Database db) throws IOException {
        this.db = db;
    }

    /**
     * Exports the specified tables to separate CSV files.
     *
     * @param targetDir      the target directory for the CSV files
     * @param tablesToExport the tables to export
     * @throws IOException if there was an error reading the source database or writing the target files
     */
    public void export(Path targetDir, Set<String> tablesToExport) throws IOException {
        ExportUtil.Builder builder = new ExportUtil.Builder(db);
        builder.setHeader(true);
        for (String table : tablesToExport) {
            builder.setTableName(table);
            LOGGER.debug("Exporting table '" + table + "'");
            builder.exportFile(targetDir.resolve(table + "." + ExportUtil.DEFAULT_FILE_EXT).toFile());
        }
    }

    /**
     * Exports all tables to separate CSV files.
     *
     * @param targetDir the target directory for the CSV files
     * @throws IOException if there was an error reading the source database or writing the target files
     */
    public void export(Path targetDir) throws IOException {
        ExportUtil.Builder builder = new ExportUtil.Builder(db);
        builder.setHeader(true);
        LOGGER.debug("Exporting all tables");
        builder.exportAll(targetDir.toFile());
    }

}
