package net.kockert.access.export;

import com.healthmarketscience.jackcess.CursorBuilder;
import com.healthmarketscience.jackcess.Index;
import com.healthmarketscience.jackcess.Table;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Stub implemention of {@link Index} to be used in tests.
 */
public class IndexStub implements Index {

    private final Table table;
    private final String name;
    private final boolean primaryKey;
    private final boolean unique;
    private final List<Column> columns;

    private IndexStub(IndexStubBuilder builder) {
        this.table = builder.table;
        this.name = builder.name;
        this.primaryKey = builder.primaryKey;
        this.unique = builder.unique;
        this.columns = builder.columns;
    }

    @Override
    public Table getTable() {
        return table;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public boolean isPrimaryKey() {
        return primaryKey;
    }

    @Override
    public boolean isForeignKey() {
        return false;
    }

    @Override
    public List<? extends Column> getColumns() {
        return columns;
    }

    @Override
    public Index getReferencedIndex() throws IOException {
        return null;
    }

    @Override
    public boolean shouldIgnoreNulls() {
        return false;
    }

    @Override
    public boolean isUnique() {
        return unique;
    }

    @Override
    public CursorBuilder newCursor() {
        return null;
    }

    public static class ColumnStub implements Column {

        private com.healthmarketscience.jackcess.Column column;

        public ColumnStub(com.healthmarketscience.jackcess.Column column) {
            this.column = column;
        }

        @Override
        public com.healthmarketscience.jackcess.Column getColumn() {
            return column;
        }

        @Override
        public boolean isAscending() {
            return false;
        }

        @Override
        public int getColumnIndex() {
            return 0;
        }

        @Override
        public String getName() {
            return column.getName();
        }

    }

    public static class IndexStubBuilder {
        private Table table;
        private String name;
        private boolean primaryKey;
        private boolean unique;
        private List<Column> columns;

        public IndexStubBuilder() {
            this.columns = new ArrayList<>();
        }

        public IndexStubBuilder name(String name) {
            this.name = name;
            return this;
        }

        public IndexStubBuilder primaryKey() {
            this.primaryKey = true;
            return this;
        }

        public IndexStubBuilder unique() {
            this.unique = true;
            return this;
        }

        public IndexStubBuilder onTable(Table table) {
            this.table = table;
            return this;
        }

        public IndexStubBuilder onColumn(com.healthmarketscience.jackcess.Column column) {
            columns.add(new ColumnStub(column));
            return this;
        }

        public IndexStub build() {
            return new IndexStub(this);
        }
    }


}
