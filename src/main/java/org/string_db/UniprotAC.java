/*
 * Copyright 2014 University of Zürich, SIB, and others.
 *
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements. The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.string_db;

import java.io.Serializable;
import java.util.regex.Pattern;

/**
 * The class is immutable => @ThreadSafe.
 *
 * @author Milan Simonovic <milan.simonovic@imls.uzh.ch>
 * @see <a href="http://web.expasy.org/docs/userman.html#AC_line">AC format</a>
 */
public class UniprotAC implements Serializable {

    private static final Pattern STARTS_WITH_OPQ_FORMAT = Pattern.compile("[O,P,Q][0-9][A-Z,0-9][A-Z,0-9][A-Z,0-9][0-9]");
    private static final Pattern OTHER_FORMAT = Pattern.compile("[A-N,R-Z][0-9][A-Z][A-Z,0-9][A-Z,0-9][0-9]");

    private String ac;

    public UniprotAC(String ac) {
        isValidUniprotAC(ac);
        this.ac = ac;
    }

    /**
     * Accession numbers consist of 6 alphanumerical characters in the following format:
     * <table>
     * <tr>
     * <th>1</th><th>2</th><th>3</th><th>4</th><th>5</th><th>6</th>
     * </tr>
     * <tr>
     * <td>[A-N,R-Z]</td><td>[0-9]</td><td>[A-Z]</td><td>[A-Z, 0-9]</td><td>[A-Z, 0-9]</td><td>[0-9]</td>
     * </tr>
     * <tr>
     * <td>[O,P,Q]</td><td>[0-9]</td><td>[A-Z, 0-9]</td><td>[A-Z, 0-9]</td><td>[A-Z, 0-9]</td><td>[0-9]</td>
     * </tr>
     * </table>
     *
     * @see <a href="http://web.expasy.org/docs/userman.html#AC_line">AC format</a>
     */
    public static void isValidUniprotAC(String ac) {
        if (ac == null) {
            throw new IllegalArgumentException("null AC");
        }
        if (ac.length() != 6) {
            throw new IllegalArgumentException("AC must be exactly 6 chars long, not: " + ac.length());
        }
        if (STARTS_WITH_OPQ_FORMAT.matcher(ac).matches() || OTHER_FORMAT.matcher(ac).matches()) {
            return;
        }
        throw new IllegalArgumentException("illegal AC format: " + ac);
    }

    @Override
    public String toString() {
        return ac;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof UniprotAC)) return false;

        UniprotAC uniprotId = (UniprotAC) o;

        if (!ac.equals(uniprotId.ac)) return false;

        return true;
    }

    @Override
    public int hashCode() {
        return ac.hashCode();
    }
}
