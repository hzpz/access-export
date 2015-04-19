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

    private boolean primaryKey;
    private List<ColumnStub> columns;

    public IndexStub(boolean primaryKey) {
        this.primaryKey = primaryKey;
        this.columns = new ArrayList<>();
    }

    public void addColumn(com.healthmarketscience.jackcess.Column column) {
        columns.add(new ColumnStub(column));
    }

    @Override
    public Table getTable() {
        return null;
    }

    @Override
    public String getName() {
        return null;
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
        return false;
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

}
