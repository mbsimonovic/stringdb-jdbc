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

import org.springframework.jdbc.core.JdbcTemplate;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * @author Milan Simonovic <milan.simonovic@imls.uzh.ch>
 */
//@RunWith(SpringJUnit4ClassRunner.class )
public class JdbcTemplateTest {

    @org.junit.Test
    public void test_jdbcTemplateOpen() throws Exception {
        final JdbcTemplate jdbcTemplate = CachedTestAppContext.ctx.getBean(JdbcTemplate.class);
        final Integer numSpecies = jdbcTemplate.queryForObject("select count(*) from items.species", Integer.class);
        assertTrue("must have some species: " + numSpecies, numSpecies > 0);
        final String name = jdbcTemplate.queryForObject("select compact_name from items.species where species_id = 511145", String.class);
        assertEquals("Escherichia coli K12_MG1655", name);
    }
}
