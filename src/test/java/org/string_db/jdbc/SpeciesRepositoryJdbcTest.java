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

package org.string_db.jdbc;

import com.google.common.collect.ImmutableList;
import org.junit.Test;

import java.util.List;
import java.util.Map;

import static org.junit.Assert.*;

/**
 * @author Milan Simonovic <milan.simonovic@imls.uzh.ch>
 */
public class SpeciesRepositoryJdbcTest {

    static final SpeciesRepositoryJdbc repo = CachedTestAppContext.speciesRepositoryJdbc;

    @Test
    public void testLoadCoreSpeciesIds() throws Exception {
        final List<Integer> speciesIds = repo.loadCoreSpeciesIds();
        assertTrue(speciesIds.toString(), speciesIds.size() > 5);
        assertTrue(speciesIds.toString(), speciesIds.containsAll(ImmutableList.of(3702, 9606, 10090, 511145)));
        assertFalse(speciesIds.toString(), speciesIds.contains(272634));
    }

    @Test
    public void testLoadSpeciesIds() throws Exception {
        final List<Integer> speciesIds = repo.loadSpeciesIds();
        assertTrue(speciesIds.toString(), speciesIds.size() > 5);
        assertTrue(speciesIds.toString(), speciesIds.containsAll(ImmutableList.of(3702, 9606, 10090, 272634, 511145)));
    }

    @Test
    public void test_speciesName() throws Exception {
        assertEquals("Escherichia coli K 12 substr  MG1655", repo.loadSpeciesName(511145));
    }

    @Test
    public void testLoadSpeciesNames() throws Exception {
        final Map<Integer, String> names = repo.loadSpeciesNames();
        assertTrue(names.toString(), names.size() > 5);
        assertEquals("Homo sapiens", repo.loadSpeciesName(9606));

    }
}
