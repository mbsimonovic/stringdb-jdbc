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

import com.google.common.collect.ImmutableSet;
import org.junit.Test;
import org.string_db.ProteinExternalId;
import org.string_db.UniprotAC;

import java.util.Map;
import java.util.Set;

import static org.junit.Assert.*;

/**
 * @author Milan Simonovic <milan.simonovic@imls.uzh.ch>
 */
public class ProteinRepositoryJdbcTest {

    final ProteinRepositoryJdbc repo = CachedTestAppContext.proteinRepositoryJdbc;

    @Test
    public void test_loadProteinExternalIds() throws Exception {
        final Map<Integer, ProteinExternalId> externalIds = repo.loadExternalIds(511145);
        assertTrue("E. coli should have ~4000 proteins " + externalIds.size(), externalIds.size() > 4000);
        assertEquals("511145.b4687", externalIds.get(4739379).toString());
    }

    @Test
    public void test_loadProteinNames() throws Exception {
        final Map<Integer, Set<String>> ids = repo.loadProteinNames(272634);
        assertTrue("MPN665 should have >10 names" + ids.get(2815672).size(), ids.get(2815672).size() > 10);
        assertTrue(ids.get(2815672).toString(), ids.get(2815672).contains("MPN665"));
        assertTrue("must have uniprot id \"P23568\": " + ids.get(2815672).toString(), ids.get(2815672).contains("P23568"));
        assertTrue("Mycoplasma pneumoniae should have ~700 proteins " + ids.size(), ids.size() > 500);
    }

    @Test
    public void test_loadRefseqIds() throws Exception {
        final Map<Integer, Set<String>> ids = repo.loadProteinNames(272634, ImmutableSet.of("Ensembl_RefSeq", "Ensembl_HGNC_RefSeq_IDs", "RefSeq"));
        assertTrue(ids.get(2815672).toString(), ids.get(2815672).contains("MPN665"));
        assertFalse("must not have uniprot id \"P23568\": " + ids.get(2815672).toString(), ids.get(2815672).contains("P23568"));
        assertTrue("Mycoplasma pneumoniae should have ~700 proteins " + ids.size(), ids.size() > 500);
    }

    @Test
    public void test_protein_sequences() throws Exception {
        final Map<Integer, String> sequences = repo.loadProteinSequences(511145);
        assertEquals(2, sequences.size());
        assertEquals("MKRISTTITTTITITTGNGAG", sequences.get(4735233));

    }

    @Test
    public void test_protein_preferred_name() throws Exception {
        final Map<Integer, String> names = repo.loadProteinPreferredNames(511145);
        assertFalse(names.isEmpty());
        assertEquals("thrA", names.get(4735233));
    }

    @Test
    public void test_loadUniqueUniProtIds() throws Exception {
        final Map<Integer, UniprotAC> ids = repo.loadUniqueUniProtIds(272634);
        assertEquals(new UniprotAC("P11311"), ids.get(2815147));
    }
}
