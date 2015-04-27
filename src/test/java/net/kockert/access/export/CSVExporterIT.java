/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */

package net.kockert.access.export;

import com.healthmarketscience.jackcess.*;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.stream.Collectors;

import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.assertThat;

public class CSVExporterIT {

    private Path targetDir;

    @Before
    public void createTargetDir() throws IOException {
        targetDir = Files.createTempDirectory("export-");
    }

    @After
    public void deleteTargetDir() throws IOException {
        Files.walk(targetDir).forEach(path -> {
            try {
                Files.delete(path);
            } catch (IOException e) {
                // ignore
            }
        });
        Files.delete(targetDir);
    }

    @Test
    public void shouldExportMultipleTablesToSeparateCSVFiles() throws IOException {
        String tableName = "TestTable";
        String columnName = "TestColumn";

        File databaseFile = File.createTempFile("access2003-", ".mdb");
        databaseFile.deleteOnExit();

        Database database = DatabaseBuilder.create(Database.FileFormat.V2003, databaseFile);
        TableBuilder tableBuilder = new TableBuilder(tableName);
        tableBuilder.addColumn(new ColumnBuilder(columnName).setType(DataType.INT));
        Table table = tableBuilder.toTable(database);
        table.addRow(1);

        TableBuilder tableBuilder2 = new TableBuilder(tableName + "2");
        tableBuilder2.addColumn(new ColumnBuilder(columnName + "2").setType(DataType.INT));
        Table table2 = tableBuilder2.toTable(database);
        table2.addRow(2);

        CSVExporter exporter = new CSVExporter(database);
        exporter.export(targetDir);

        List<Path> targetFiles = Files.walk(targetDir).filter(path -> !path.equals(targetDir)).collect(Collectors.toList());

        assertThat(targetFiles.size(), equalTo(2));

        for (Path targetFile : targetFiles) {
            String firstLine = Files.readAllLines(targetFile).get(0);
            assertThat(firstLine, containsString(columnName));
        }
    }

}
