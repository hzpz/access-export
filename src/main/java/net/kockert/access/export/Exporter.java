/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */

package net.kockert.access.export;

import com.healthmarketscience.jackcess.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.OutputStream;
import java.sql.*;
import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class Exporter {

    private static final Logger LOGGER = LoggerFactory.getLogger(Exporter.class);

    private final Database db;

    private final Set<String> tablesToExport;

    private final SQLGenerator sqlGenerator;

    public Exporter(final Database db) {
        this.db = db;
        this.tablesToExport = Collections.emptySet();
        this.sqlGenerator = new SQLiteSQLGenerator();
    }

    public void export(final Connection jdbcConnection) throws SQLException, IOException {
        boolean autoCommit = jdbcConnection.getAutoCommit();
        jdbcConnection.setAutoCommit(false);

        createTables(jdbcConnection);
        populateTables(jdbcConnection);

        jdbcConnection.commit();
        jdbcConnection.setAutoCommit(autoCommit);
    }

    private void createTables(final Connection jdbcConnection) throws SQLException, IOException {
        Set<String> tableNames = filterTableNames();

        for (String tableName : tableNames) {
            Table table = db.getTable(tableName);
            createTable(table, jdbcConnection);
            createIndexes(table, jdbcConnection);
        }
    }

    private void createIndexes(final Table table, final Connection jdbcConnection) throws SQLException {
        List<? extends Index> indexes = table.getIndexes();

        for (Index index : indexes) {
            createIndex(index, jdbcConnection);
        }
    }

    private void createIndex(final Index index, final Connection jdbcConnection) throws SQLException {
        String sql = sqlGenerator.createIndex(index);
        LOGGER.debug("Executing SQL: {}", sql);
        try (Statement statement = jdbcConnection.createStatement()) {
            statement.execute(sql);
        }
    }

    private void createTable(final Table table, final Connection jdbcConnection) throws SQLException {
        String sql = sqlGenerator.createTable(table);
        LOGGER.debug("Executing SQL: {}", sql);
        try (Statement statement = jdbcConnection.createStatement()) {
            statement.execute(sql);
        }
    }

    private Set<String> filterTableNames() throws IOException {
        if (tablesToExport.isEmpty()) {
            return db.getTableNames();
        } else {
            return db.getTableNames().stream().filter(tablesToExport::contains).collect(Collectors.toSet());
        }
    }

    private void populateTables(final Connection jdbcConnection) throws SQLException, IOException {
        Set<String> tableNames = filterTableNames();

        for (String tableName : tableNames) {
            Table table = db.getTable(tableName);
            populateTable(table, jdbcConnection);
        }

    }

    private void populateTable(final Table table, final Connection jdbcConnection) throws SQLException {
        String sql = sqlGenerator.insertIntoTable(table);
        LOGGER.debug("Prepared SQL: {}", sql);

        try (PreparedStatement preparedStatement = jdbcConnection.prepareStatement(sql)) {
            List<? extends Column> columns = table.getColumns();
            for (Row row : table) {
                bindColumnValues(row, columns, preparedStatement);
                preparedStatement.executeUpdate();
                preparedStatement.clearParameters();
            }
        }
    }

    private void bindColumnValues(final Row row, List<? extends Column> columns, final PreparedStatement preparedStatement) throws SQLException {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS"); // ISO8601
        for (int i = 0; i < columns.size(); i++) {
            Column column = columns.get(i);
            Object value = row.get(column.getName());

            if (value == null) {
                preparedStatement.setNull(i + 1, Types.NULL);
                continue;
            }

            switch (column.getType()) {
                case BOOLEAN:
                    /* SQLite does not have a dedicated type for storing booleans, convert to integer */
                    Boolean aBoolean = row.getBoolean(column.getName());
                    if (aBoolean) {
                        preparedStatement.setInt(i + 1, 1);
                    } else {
                        preparedStatement.setInt(i + 1, 0);
                    }
                    break;
                case SHORT_DATE_TIME:
                    /* SQLite does not have a dedicated type for storing timestamps, convert to text */
                    Date date = row.getDate(column.getName());
                    preparedStatement.setString(i + 1, dateFormat.format(date));
                    break;
                default:
                    preparedStatement.setObject(i + 1, row.get(column.getName()));
                    break;
            }
        }
    }

}
