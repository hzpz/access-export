package net.kockert.access.export;

import com.healthmarketscience.jackcess.DataType;
import com.healthmarketscience.jackcess.Relationship;
import org.junit.Test;

import java.util.Collections;
import java.util.List;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.assertThat;

public class SQLiteSQLGeneratorTest {

    @Test
    public void shouldGenerateSQLStatementForSimpleTable() {
        String tableName = "TestTable";
        String columnName = "TestColumn";

        TableStub table = new TableStub(tableName);
        table.addColumn(columnName, DataType.INT);
        List<Relationship> relationships = Collections.emptyList();

        SQLiteSQLGenerator sqlGenerator = new SQLiteSQLGenerator();
        String sql = sqlGenerator.createTable(table, relationships);

        assertThat(sql, equalTo("CREATE TABLE '" + tableName + "' ('" + columnName + "' INTEGER)"));
    }

    @Test
    public void shouldGeneratePrimaryKeyForSinglePrimaryKeyColumn() {
        String tableName = "TestTable";
        String columnName = "TestColumn";

        TableStub table = new TableStub(tableName);
        ColumnStub column = new ColumnStub(columnName, DataType.INT);
        table.addColumn(column);
        table.addPrimaryKeyIndex(column);
        List<Relationship> relationships = Collections.emptyList();

        SQLiteSQLGenerator sqlGenerator = new SQLiteSQLGenerator();
        String sql = sqlGenerator.createTable(table, relationships);

        assertThat(sql, equalTo("CREATE TABLE '" + tableName + "' ('" + columnName + "' INTEGER PRIMARY KEY)"));
    }

    @Test
    public void shouldGeneratePrimaryKeyTableConstraintForMultiplePrimaryKeyColumns() {
        String tableName = "TestTable";
        String columnName1 = "TestColumn1";
        String columnName2 = "TestColumn2";

        TableStub table = new TableStub(tableName);
        ColumnStub column1 = new ColumnStub(columnName1, DataType.INT);
        ColumnStub column2 = new ColumnStub(columnName2, DataType.INT);
        table.addColumns(column1, column2);
        table.addPrimaryKeyIndex(column1, column2);
        List<Relationship> relationships = Collections.emptyList();

        SQLiteSQLGenerator sqlGenerator = new SQLiteSQLGenerator();
        String sql = sqlGenerator.createTable(table, relationships);

        String expectedSql =
                String.format("CREATE TABLE '%1$s' ('%2$s' INTEGER, '%3$s' INTEGER, PRIMARY KEY('%2$s', '%3$s'))",
                        tableName, columnName1, columnName2);
        assertThat(sql, equalTo(expectedSql));
    }

}
