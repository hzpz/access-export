/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */

package net.kockert.access.export;

import com.healthmarketscience.jackcess.Database;
import com.healthmarketscience.jackcess.Index;
import com.healthmarketscience.jackcess.Table;

import java.io.IOException;
import java.io.OutputStream;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class Exporter {

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
        Statement statement = jdbcConnection.createStatement();
        statement.execute(sql);
    }

    private void createTable(final Table table, final Connection jdbcConnection) throws SQLException {
        String sql = sqlGenerator.createTable(table);
        Statement statement = jdbcConnection.createStatement();
        statement.execute(sql);
    }

    private Set<String> filterTableNames() throws IOException {
        if (tablesToExport.isEmpty()) {
            return db.getTableNames();
        } else {
            return db.getTableNames().stream().filter(tablesToExport::contains).collect(Collectors.toSet());
        }
    }

    private void populateTables(final Connection jdbcConnection) {

    }

    public void export(final OutputStream outputStream) {

    }

}
