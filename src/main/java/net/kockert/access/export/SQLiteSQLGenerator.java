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
        boolean hasSinglePrimaryKeyColumn = primaryKeyColumns.size() == 1;

        String tableName = table.getName();
        stmtBuilder.append("CREATE TABLE ");
        stmtBuilder.append(createStringConstant(tableName));
        stmtBuilder.append(" (");

        List<? extends Column> columns = table.getColumns();
        for (Iterator<? extends Column> iterator = columns.iterator(); iterator.hasNext(); ) {
            Column column = iterator.next();

            stmtBuilder.append(createStringConstant(column.getName()));
            stmtBuilder.append(" ");
            // see https://www.sqlite.org/datatype3.html
            switch (column.getType()) {
                /* Blob */
                case BINARY:
                case OLE:
                    stmtBuilder.append("BLOB");
                    break;

                /* Integers */
                case BOOLEAN:
                case BYTE:
                case INT:
                case LONG:
                case MONEY:
                /* SQLite does not have a dedicated type for storing timestamps and the
                 * default behaviour of the driver is to store the date as Unix Time */
                case SHORT_DATE_TIME:
                    stmtBuilder.append("INTEGER");
                    break;

                /* Floating point */
                case DOUBLE:
                case FLOAT:
                case NUMERIC:
                    stmtBuilder.append("REAL");
                    break;

                /* Strings */
                case TEXT:
                case GUID:
                case MEMO:
                    stmtBuilder.append("TEXT");
                    break;

                default:
                    throw new IllegalArgumentException("Unsupported data type: " + column.getType());
            }

            if (hasSinglePrimaryKeyColumn && primaryKeyColumns.get(0).getColumn().equals(column)) {
                stmtBuilder.append(" PRIMARY KEY");
            }

            if (iterator.hasNext()) {
                stmtBuilder.append(", ");
            }
        }

        for (Relationship relationship : relationships) {
            if (!relationship.getToTable().equals(table)) {
                continue;
            }

            stmtBuilder.append(", FOREIGN KEY(");
            for (Iterator<Column> iterator = relationship.getToColumns().iterator(); iterator.hasNext(); ) {
                Column foreignKeyColumn = iterator.next();
                stmtBuilder.append(foreignKeyColumn.getName());
                if (iterator.hasNext()) {
                    stmtBuilder.append(", ");
                }
            }
            stmtBuilder.append(") REFERENCES ");
            stmtBuilder.append(relationship.getFromTable().getName());
            stmtBuilder.append("(");
            for (Iterator<Column> iterator = relationship.getFromColumns().iterator(); iterator.hasNext(); ) {
                Column referencedColumn = iterator.next();
                stmtBuilder.append(referencedColumn.getName());
                if (iterator.hasNext()) {
                    stmtBuilder.append(", ");
                }
            }
            stmtBuilder.append(")");
        }

        stmtBuilder.append(")");

        return stmtBuilder.toString();
    }

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
     * @see <a href="http://www.sqlite.org/lang_expr.html>SQL As Understood By SQLite</a>
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
