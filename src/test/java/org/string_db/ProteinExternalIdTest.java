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

import static org.junit.Assert.assertEquals;

/**
 * @author Milan Simonovic <milan.simonovic@imls.uzh.ch>
 */
public class ProteinExternalIdTest {

    @Test
    public void test_getSpeciesId() throws Exception {
        assertEquals(new Integer(9606), new ProteinExternalId("9606.ENSP00000000233").getSpeciesId());
        assertEquals(new Integer(882), new ProteinExternalId("882.DVU0001").getSpeciesId());
        assertEquals(new Integer(882), new ProteinExternalId("882.DVU3339.1").getSpeciesId());
        assertEquals(new Integer(882), new ProteinExternalId("882.DVU3339.1.3a").getSpeciesId());
    }
}
