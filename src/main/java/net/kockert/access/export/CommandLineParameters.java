/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */

package net.kockert.access.export;

import com.beust.jcommander.Parameter;

import java.util.ArrayList;
import java.util.List;

public class CommandLineParameters {

    @Parameter(description = "<source> <target>")
    private List<String> parameters = new ArrayList<>();

    @Parameter(names = "--help", help = true, description = "display usage instructions")
    private boolean help;

    public List<String> getParameters() {
        return parameters;
    }

    public boolean helpRequested() {
        return help;
    }

    public boolean insufficientParameters() {
        return parameters.size() != 2;
    }

}
