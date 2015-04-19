package net.kockert.access.export;

import com.healthmarketscience.jackcess.Column;
import com.healthmarketscience.jackcess.Relationship;
import com.healthmarketscience.jackcess.Table;

import java.util.ArrayList;
import java.util.List;

/**
 * Stub implementation of {@link Relationship} to be used in tests.
 */
public class RelationshipStub implements Relationship {

    private Table fromTable;
    private List<Column> fromColumns;
    private Table toTable;
    private List<Column> toColumns;

    public RelationshipStub(Table fromTable, Table toTable) {
        this.fromTable = fromTable;
        this.fromColumns = new ArrayList<>();
        this.toTable = toTable;
        this.toColumns = new ArrayList<>();
    }

    public void addFromColumn(Column column) {
        fromColumns.add(column);
    }

    public void addToColumn(Column column) {
        toColumns.add(column);
    }

    @Override
    public String getName() {
        return null;
    }

    @Override
    public Table getFromTable() {
        return fromTable;
    }

    @Override
    public List<Column> getFromColumns() {
        return fromColumns;
    }

    @Override
    public Table getToTable() {
        return toTable;
    }

    @Override
    public List<Column> getToColumns() {
        return toColumns;
    }

    @Override
    public boolean isOneToOne() {
        return false;
    }

    @Override
    public boolean hasReferentialIntegrity() {
        return false;
    }

    @Override
    public boolean cascadeUpdates() {
        return false;
    }

    @Override
    public boolean cascadeDeletes() {
        return false;
    }

    @Override
    public boolean isLeftOuterJoin() {
        return false;
    }

    @Override
    public boolean isRightOuterJoin() {
        return false;
    }

}
