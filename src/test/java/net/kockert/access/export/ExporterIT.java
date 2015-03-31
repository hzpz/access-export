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
    public void should() throws IOException, SQLException, ClassNotFoundException {
        byte[] bytes = new byte[]{0x1, 0x2, 0x3, 0x4};
        float floaty = 0.123f;
        Date now = new Date();
        BigDecimal bigDecimal = new BigDecimal("3.21");
        boolean bool = true;

        String tableName = "TestTable";
        String columnNameBinary = "binary";
        String columnNameFloat = "float";
        String columnNameDateTime = "datetime";
        String columnNameMoney = "money";
        String columnNameBoolean = "boolean";

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
        Table table = tableBuilder.toTable(database);
        table.addRow(bytes, floaty, now, bigDecimal, bool);

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
        }

        database.close();
        jdbcConnection.close();
    }

}
