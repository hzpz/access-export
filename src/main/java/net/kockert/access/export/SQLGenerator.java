package net.kockert.access.export;

import com.healthmarketscience.jackcess.Index;
import com.healthmarketscience.jackcess.Table;

public interface SQLGenerator {

    String createTable(Table table);

    String createIndex(Index index);

}
