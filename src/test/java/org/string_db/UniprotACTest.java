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

import org.junit.Test;

import static org.junit.Assert.fail;

/**
 * @author Milan Simonovic <milan.simonovic@imls.uzh.ch>
 */
public class UniprotACTest {
    @Test
    public void
    test_ac_format_valid() {
        UniprotAC.isValidUniprotAC("P12345");
        UniprotAC.isValidUniprotAC("Q1AAA9");
        UniprotAC.isValidUniprotAC("O456A1");
        UniprotAC.isValidUniprotAC("P4A123");
        UniprotAC.isValidUniprotAC("A0A022YWF9");
    }

    @Test
    public void
    test_invalid_ac_format() {
        tryToCreateUniprotACAndFailIfNoExceptionIsThrown("p12345", "all chars must be upper case");
        tryToCreateUniprotACAndFailIfNoExceptionIsThrown("A12345", "illegal format, 3rd digit must be [A-Z]");
    }

    @Test
    public void
    test_ac_must_be_6_or_10_chars_long() {
        tryToCreateUniprotACAndFailIfNoExceptionIsThrown("P1234567", "AC must be exactly 6 chars long");
        tryToCreateUniprotACAndFailIfNoExceptionIsThrown("P1234", "AC must be exactly 6 chars long");
    }

    private void tryToCreateUniprotACAndFailIfNoExceptionIsThrown(String AC, String message) {
        try {
            UniprotAC.isValidUniprotAC(AC);
            fail(message + ": " + AC);
        } catch (Exception e) {

        }
    }


}
