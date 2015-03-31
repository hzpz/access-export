/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */

package net.kockert.access.export;

import com.healthmarketscience.jackcess.Index;
import com.healthmarketscience.jackcess.Table;

public interface SQLGenerator {

    String createTable(Table table);

    String createIndex(Index index);

}
