/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */

package net.kockert.access.export;

import com.healthmarketscience.jackcess.Column;
import com.healthmarketscience.jackcess.Index;
import com.healthmarketscience.jackcess.Relationship;
import com.healthmarketscience.jackcess.Table;

import java.util.Collections;
import java.util.Iterator;
import java.util.List;

public class SQLiteSQLGenerator implements SQLGenerator {

    @Override
    public String createTable(Table table, List<Relationship> relationships) {
        final StringBuilder stmtBuilder = new StringBuilder();

        List<? extends Index.Column> primaryKeyColumns = getPrimaryKeyColumns(table);

        String tableName = table.getName();
        stmtBuilder.append("CREATE TABLE ");
        stmtBuilder.append(createStringConstant(tableName));
        stmtBuilder.append(" (");

        List<? extends Column> columns = table.getColumns();
        for (Iterator<? extends Column> iterator = columns.iterator(); iterator.hasNext(); ) {
            Column column = iterator.next();

            stmtBuilder.append(createStringConstant(column.getName()));
            stmtBuilder.append(" ");
            stmtBuilder.append(mapDatatype(column));

            if (isPrimaryKeyColumn(primaryKeyColumns, column)) {
                stmtBuilder.append(" PRIMARY KEY");
            }

            if (iterator.hasNext()) {
                stmtBuilder.append(", ");
            }
        }

        if (hasMultiplePrimaryKeyColumns(primaryKeyColumns)) {
            stmtBuilder.append(", ");
            stmtBuilder.append(createPrimaryKeyTableConstraint(primaryKeyColumns));
        }

        for (Relationship relationship : relationships) {
            if (!relationship.getToTable().equals(table)) {
                continue;
            }

            stmtBuilder.append(", ");
            stmtBuilder.append(createForeignKeyTableConstraint(relationship));
        }

        stmtBuilder.append(")");

        return stmtBuilder.toString();
    }

    /**
     * Maps the type of the given column to an SQLite datatype.
     *
     * @param column the column
     * @return an SQLite datatype
     * @see <a href="https://www.sqlite.org/datatype3.html">Datatypes in SQLite</a>
     */
    private String mapDatatype(Column column) {
        switch (column.getType()) {
            /* Blob */
            case BINARY:
            case OLE:
                return "BLOB";

            /* Integers */
            case BOOLEAN:
            case BYTE:
            case INT:
            case LONG:
            case MONEY:
            /* SQLite does not have a dedicated type for storing timestamps and the
             * default behaviour of the driver is to store the date as Unix Time */
            case SHORT_DATE_TIME:
                return "INTEGER";

            /* Floating point */
            case DOUBLE:
            case FLOAT:
            case NUMERIC:
                return "REAL";

            /* Strings */
            case TEXT:
            case GUID:
            case MEMO:
                return "TEXT";

            default:
                throw new IllegalArgumentException("Unsupported data type: " + column.getType());
        }
    }

    /**
     * Returns {@code true} if the primary key consists solely of the given column.
     *
     * @param primaryKeyColumns the columns of the primary key
     * @param column the column
     * @return {@code true} if the column is the only column of the primary key, otherwise {@code false}
     */
    private boolean isPrimaryKeyColumn(List<? extends Index.Column> primaryKeyColumns, Column column) {
        return primaryKeyColumns.size() == 1 && primaryKeyColumns.get(0).getColumn().equals(column);
    }

    /**
     * Returns {@code true} if the primary key consists of more than one column.
     *
     * @param primaryKeyColumns the columns of the primary key
     * @return {@code true} if the primary key consists of more than one column, otherwise {@code false}
     */
    private boolean hasMultiplePrimaryKeyColumns(List<? extends Index.Column> primaryKeyColumns) {
        return primaryKeyColumns.size() > 1;
    }

    /**
     * Creates a primary key table constraint for multiple columns, e.g.
     * <pre>
     * PRIMARY KEY('column1', 'column2')
     * </pre>
     *
     * @param primaryKeyColumns the columns of the primary key
     * @return a primary key table contraint
     * @see <a href="https://www.sqlite.org/syntax/table-constraint.html">SQLite table-contraint</a>
     */
    private String createPrimaryKeyTableConstraint(List<? extends Index.Column> primaryKeyColumns) {
        final StringBuilder stmtBuilder = new StringBuilder();

        stmtBuilder.append("PRIMARY KEY(");
        for (Iterator<? extends Index.Column> iterator = primaryKeyColumns.iterator(); iterator.hasNext(); ) {
            stmtBuilder.append(createStringConstant(iterator.next().getName()));
            if (iterator.hasNext()) {
                stmtBuilder.append(", ");
            }
        }
        stmtBuilder.append(")");

        return stmtBuilder.toString();
    }

    /**
     * Creates a foreign key table constraint, e.g.
     * <pre>
     * FOREIGN KEY('otherTableId') REFERENCES otherTable('id')
     * </pre>
     *
     * @param relationship the relationship from the Access database
     * @return a foreign key constraint
     * @see <a href="https://www.sqlite.org/syntax/table-constraint.html">SQLite table-contraint</a>
     * @see <a href="https://www.sqlite.org/syntax/foreign-key-clause.html">SQLite foreign-key-clause</a>
     */
    private String createForeignKeyTableConstraint(Relationship relationship) {
        final StringBuilder stmtBuilder = new StringBuilder();

        stmtBuilder.append("FOREIGN KEY(");
        for (Iterator<Column> iterator = relationship.getToColumns().iterator(); iterator.hasNext(); ) {
            Column foreignKeyColumn = iterator.next();
            stmtBuilder.append(createStringConstant(foreignKeyColumn.getName()));
            if (iterator.hasNext()) {
                stmtBuilder.append(", ");
            }
        }
        stmtBuilder.append(") REFERENCES ");
        stmtBuilder.append(relationship.getFromTable().getName());
        stmtBuilder.append("(");
        for (Iterator<Column> iterator = relationship.getFromColumns().iterator(); iterator.hasNext(); ) {
            Column referencedColumn = iterator.next();
            stmtBuilder.append(createStringConstant(referencedColumn.getName()));
            if (iterator.hasNext()) {
                stmtBuilder.append(", ");
            }
        }
        stmtBuilder.append(")");

        return stmtBuilder.toString();
    }

    /**
     * Searches for a primary key index on the given table and returns the columns that were used for the index key.
     * If the table does not have a primary key index the returned list will be empty.
     *
     * @param table the table on which to search for a primary key index
     * @return the list of columns used for the index key or an empty list, if no primary key index exists
     */
    private List<? extends Index.Column> getPrimaryKeyColumns(Table table) {
        for (Index index : table.getIndexes()) {
            if (index.isPrimaryKey()) {
                return index.getColumns();
            }
        }

        return Collections.emptyList();
    }

    /**
     * Creates a string constant for SQLite.
     *
     * @param string the string to create a constant from
     * @return the string as a SQLite string constant
     * @see <a href="http://www.sqlite.org/lang_expr.html">SQL As Understood By SQLite</a>
     */
    private String createStringConstant(String string) {
        return "'" + string.replace("'", "''") + "'";
    }

    @Override
    public String createIndex(Index index) {
        List<? extends Index.Column> columns = index.getColumns();

        final StringBuilder stmtBuilder = new StringBuilder();

        final String tableName = index.getTable().getName();
        final String indexName = tableName + "_" + index.getName();

        stmtBuilder.append("CREATE ");
        if (index.isUnique()) {
            stmtBuilder.append("UNIQUE ");
        }
        stmtBuilder.append("INDEX ");
        stmtBuilder.append(createStringConstant(indexName));
        stmtBuilder.append(" ON ");
        stmtBuilder.append(createStringConstant(tableName));
        stmtBuilder.append(" (");

        for (Iterator<? extends Index.Column> iterator = columns.iterator(); iterator.hasNext(); ) {
            Index.Column column = iterator.next();
            stmtBuilder.append(createStringConstant(column.getName()));
            if (iterator.hasNext())
                stmtBuilder.append(", ");
        }
        stmtBuilder.append(")");

        return stmtBuilder.toString();
    }

    @Override
    public String insertIntoTable(Table table) {
        final StringBuilder stmtBuilder = new StringBuilder();

        stmtBuilder.append("INSERT INTO ");
        stmtBuilder.append(createStringConstant(table.getName()));
        stmtBuilder.append(" (");

        final List<? extends Column> columns = table.getColumns();

        for (Iterator<? extends Column> iterator = columns.iterator(); iterator.hasNext(); ) {
            Column column = iterator.next();
            stmtBuilder.append(createStringConstant(column.getName()));
            if (iterator.hasNext()) {
                stmtBuilder.append(", ");
            }
        }

        stmtBuilder.append(") VALUES (");

        for (Iterator<? extends Column> iterator = columns.iterator(); iterator.hasNext(); ) {
            iterator.next();
            stmtBuilder.append("?");
            if (iterator.hasNext()) {
                stmtBuilder.append(", ");
            }
        }

        stmtBuilder.append(")");

        return stmtBuilder.toString();
    }

}
