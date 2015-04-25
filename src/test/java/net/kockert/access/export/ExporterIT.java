/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */

package net.kockert.access.export;

import com.healthmarketscience.jackcess.*;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.sql.*;
import java.util.Date;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.assertThat;

public class ExporterIT {

    @Test
    public void shouldExportArbitraryDataTypes() throws IOException, SQLException, ClassNotFoundException {
        byte[] bytes = new byte[]{0x1, 0x2, 0x3, 0x4};
        float floaty = 0.123f;
        Date now = new Date();
        BigDecimal bigDecimal = new BigDecimal("3.21");
        boolean bool = true;
        String text = "Text";

        String tableName = "TestTable";
        String columnNameBinary = "binary";
        String columnNameFloat = "float";
        String columnNameDateTime = "datetime";
        String columnNameMoney = "money";
        String columnNameBoolean = "boolean";
        String columnNameText = "text";

        File databaseFile = File.createTempFile("access2003-", ".mdb");
        databaseFile.deleteOnExit();
        File sqliteFile = File.createTempFile("export-", ".sqlite");
        sqliteFile.deleteOnExit();

        Database database = DatabaseBuilder.create(Database.FileFormat.V2003, databaseFile);
        TableBuilder tableBuilder = new TableBuilder(tableName);
        tableBuilder.addColumn(new ColumnBuilder(columnNameBinary).setType(DataType.BINARY));
        tableBuilder.addColumn(new ColumnBuilder(columnNameFloat).setType(DataType.FLOAT));
        tableBuilder.addColumn(new ColumnBuilder(columnNameDateTime).setType(DataType.SHORT_DATE_TIME));
        tableBuilder.addColumn(new ColumnBuilder(columnNameMoney).setType(DataType.MONEY));
        tableBuilder.addColumn(new ColumnBuilder(columnNameBoolean).setType(DataType.BOOLEAN));
        tableBuilder.addColumn(new ColumnBuilder(columnNameText).setType(DataType.TEXT));
        Table table = tableBuilder.toTable(database);
        table.addRow(bytes, floaty, now, bigDecimal, bool, text);

        final Exporter exporter = new Exporter(database);
        final Connection jdbcConnection = DriverManager.getConnection("jdbc:sqlite:" + sqliteFile);
        exporter.export(jdbcConnection);

        Statement statement = jdbcConnection.createStatement();
        ResultSet resultSet = statement.executeQuery("SELECT * FROM " + tableName);
        while (resultSet.next()) {
            assertThat(resultSet.getBytes(columnNameBinary), equalTo(bytes));
            assertThat(resultSet.getFloat(columnNameFloat), equalTo(floaty));
            assertThat(resultSet.getLong(columnNameDateTime), equalTo(now.getTime()));
            assertThat(resultSet.getBigDecimal(columnNameMoney), equalTo(bigDecimal));
            assertThat(resultSet.getBoolean(columnNameBoolean), equalTo(bool));
            assertThat(resultSet.getString(columnNameText), equalTo(text));
        }

        database.close();
        jdbcConnection.close();
    }

    @Test
    public void shouldFilterDuplicateIndex() throws IOException, SQLException {
        String tableName = "TestTable";
        String columnName = "TestColumn";
        String indexName = "TestIndex";

        File databaseFile = File.createTempFile("access2003-", ".mdb");
        databaseFile.deleteOnExit();
        File sqliteFile = File.createTempFile("export-", ".sqlite");
        sqliteFile.deleteOnExit();

        Database database = DatabaseBuilder.create(Database.FileFormat.V2003, databaseFile);
        TableBuilder tableBuilder = new TableBuilder(tableName);
        tableBuilder.addColumn(new ColumnBuilder(columnName).setType(DataType.INT));
        tableBuilder.addIndex(new IndexBuilder(indexName).addColumns(columnName));
        tableBuilder.addIndex(new IndexBuilder(indexName + "Duplicate").addColumns(columnName));
        Table table = tableBuilder.toTable(database);
        table.addRow(1);

        final Exporter exporter = new Exporter(database);
        final Connection jdbcConnection = DriverManager.getConnection("jdbc:sqlite:" + sqliteFile);
        exporter.export(jdbcConnection);

        // fyi: the SQLite driver has a bug which results in an exception on the next line if the table has more than
        // one index, see https://bitbucket.org/xerial/sqlite-jdbc/issue/134/metadatagetindexinfo-throws-exception
        ResultSet indexInfo = jdbcConnection.getMetaData().getIndexInfo(jdbcConnection.getCatalog(), null, tableName, false, true);
        int indexCount = 0;
        while (indexInfo.next()) {
            indexCount++;
        }

        assertThat(indexCount, equalTo(1));
    }
}
