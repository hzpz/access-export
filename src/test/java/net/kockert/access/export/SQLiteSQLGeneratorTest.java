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

}
