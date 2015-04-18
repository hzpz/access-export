package net.kockert.access.export;

import com.healthmarketscience.jackcess.*;
import com.healthmarketscience.jackcess.util.ErrorHandler;

import java.io.IOException;
import java.util.*;

/**
 * Stub implementation for {@link Table} to be used in tests.
 */
public class TableStub implements Table {

    private String name;
    private List<? extends Index> indexes;
    private List<ColumnStub> columns;

    public TableStub(String name) {
        this.name = name;
        indexes = Collections.emptyList();
        columns = new ArrayList<>();
    }

    public void addColumn(String name, DataType type) {
        columns.add(new ColumnStub(name, type));
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public boolean isHidden() {
        return false;
    }

    @Override
    public boolean isSystem() {
        return false;
    }

    @Override
    public int getColumnCount() {
        return 0;
    }

    @Override
    public Database getDatabase() {
        return null;
    }

    @Override
    public ErrorHandler getErrorHandler() {
        return null;
    }

    @Override
    public void setErrorHandler(ErrorHandler newErrorHandler) {

    }

    @Override
    public List<? extends Column> getColumns() {
        return columns;
    }

    @Override
    public Column getColumn(String name) {
        return null;
    }

    @Override
    public PropertyMap getProperties() throws IOException {
        return null;
    }

    @Override
    public List<? extends Index> getIndexes() {
        return indexes;
    }

    @Override
    public Index getIndex(String name) {
        return null;
    }

    @Override
    public Index getPrimaryKeyIndex() {
        return null;
    }

    @Override
    public Index getForeignKeyIndex(Table otherTable) {
        return null;
    }

    @Override
    public Object[] asRow(Map<String, ?> rowMap) {
        return new Object[0];
    }

    @Override
    public Object[] asUpdateRow(Map<String, ?> rowMap) {
        return new Object[0];
    }

    @Override
    public int getRowCount() {
        return 0;
    }

    @Override
    public Object[] addRow(Object... row) throws IOException {
        return new Object[0];
    }

    @Override
    public <M extends Map<String, Object>> M addRowFromMap(M row) throws IOException {
        return null;
    }

    @Override
    public List<? extends Object[]> addRows(List<? extends Object[]> rows) throws IOException {
        return null;
    }

    @Override
    public <M extends Map<String, Object>> List<M> addRowsFromMaps(List<M> rows) throws IOException {
        return null;
    }

    @Override
    public Row updateRow(Row row) throws IOException {
        return null;
    }

    @Override
    public Row deleteRow(Row row) throws IOException {
        return null;
    }

    @Override
    public Iterator<Row> iterator() {
        return null;
    }

    @Override
    public void reset() {

    }

    @Override
    public Row getNextRow() throws IOException {
        return null;
    }

    @Override
    public Cursor getDefaultCursor() {
        return null;
    }

    @Override
    public CursorBuilder newCursor() {
        return null;
    }

}
