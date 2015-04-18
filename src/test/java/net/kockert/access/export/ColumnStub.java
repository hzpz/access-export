package net.kockert.access.export;

import com.healthmarketscience.jackcess.*;
import com.healthmarketscience.jackcess.complex.ComplexColumnInfo;
import com.healthmarketscience.jackcess.complex.ComplexValue;
import com.healthmarketscience.jackcess.util.ColumnValidator;

import java.io.IOException;
import java.sql.SQLException;
import java.util.Map;

/**
 * Stub implementation for {@link Column} to be used in tests.
 */
public class ColumnStub implements Column {

    private String name;
    private DataType type;

    public ColumnStub(String name, DataType type) {
        this.name = name;
        this.type = type;
    }

    @Override
    public Table getTable() {
        return null;
    }

    @Override
    public Database getDatabase() {
        return null;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public boolean isVariableLength() {
        return false;
    }

    @Override
    public boolean isAutoNumber() {
        return false;
    }

    @Override
    public int getColumnIndex() {
        return 0;
    }

    @Override
    public DataType getType() {
        return type;
    }

    @Override
    public int getSQLType() throws SQLException {
        return 0;
    }

    @Override
    public boolean isCompressedUnicode() {
        return false;
    }

    @Override
    public byte getPrecision() {
        return 0;
    }

    @Override
    public byte getScale() {
        return 0;
    }

    @Override
    public short getLength() {
        return 0;
    }

    @Override
    public short getLengthInUnits() {
        return 0;
    }

    @Override
    public boolean isAppendOnly() {
        return false;
    }

    @Override
    public boolean isHyperlink() {
        return false;
    }

    @Override
    public boolean isCalculated() {
        return false;
    }

    @Override
    public ComplexColumnInfo<? extends ComplexValue> getComplexInfo() {
        return null;
    }

    @Override
    public PropertyMap getProperties() throws IOException {
        return null;
    }

    @Override
    public Column getVersionHistoryColumn() {
        return null;
    }

    @Override
    public ColumnValidator getColumnValidator() {
        return null;
    }

    @Override
    public void setColumnValidator(ColumnValidator newValidator) {

    }

    @Override
    public Object setRowValue(Object[] rowArray, Object value) {
        return null;
    }

    @Override
    public Object setRowValue(Map<String, Object> rowMap, Object value) {
        return null;
    }

    @Override
    public Object getRowValue(Object[] rowArray) {
        return null;
    }

    @Override
    public Object getRowValue(Map<String, ?> rowMap) {
        return null;
    }

}
