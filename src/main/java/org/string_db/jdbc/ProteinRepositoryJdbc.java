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

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.stereotype.Component;
import org.string_db.ProteinExternalId;
import org.string_db.ProteinRepository;

import java.util.Map;
import java.util.Set;

/**
 * @author Milan Simonovic <milan.simonovic@imls.uzh.ch>
 */
@Component
public class ProteinRepositoryJdbc implements ProteinRepository {

    @Autowired
    GenericQueryProcessor queryProcessor;

    protected final TwoColumnRowMapper<Integer, String, Set<String>> multiValSqlRowMapper = TwoColumnRowMapper.multiValMapper();
    protected TwoColumnRowMapper<Integer, String, ProteinExternalId> idExternalIdMapper = new TwoColumnRowMapper<Integer, String, ProteinExternalId>() {
        @Override
        public void addToMap(Integer firstColumn, String secondColumn, Map<Integer, ProteinExternalId> map) {
            map.put(firstColumn, new ProteinExternalId(secondColumn));
        }
    };

    @Override
    public Map<Integer, ProteinExternalId> loadExternalIds(Integer speciesId) {
        return queryProcessor.selectTwoColumns("protein_id", "protein_external_id", "items.proteins", idExternalIdMapper, "species_id = :species_id",
                new MapSqlParameterSource("species_id", speciesId));
    }

    @Override
    public Map<Integer, String> loadProteinPreferredNames(Integer speciesId) {
        return queryProcessor.selectTwoColumns(
                "protein_id", "preferred_name", "items.proteins",
                TwoColumnRowMapper.<Integer, String>uniqueValMapper(),
                "species_id = :species_id",
                new MapSqlParameterSource("species_id", speciesId));
    }

    @Override
    public Map<Integer, Set<String>> loadProteinNames(Integer speciesId) {
        return loadProteinNames(speciesId, null);
    }

    @Override
    public Map<Integer, Set<String>> loadProteinNames(Integer speciesId, Set<String> sources) {
        String filter = "species_id = :species_id ";
        MapSqlParameterSource params = new MapSqlParameterSource("species_id", speciesId);
        if (sources != null && !sources.isEmpty()) {
            filter += " AND \"source\" IN (:sources)";
            params.addValue("sources", sources);
        }
        return queryProcessor.selectTwoColumns("protein_id", "protein_name", "items.proteins_names", multiValSqlRowMapper, filter, params);
    }

    @Override
    public Map<Integer, String> loadProteinSequences(Integer speciesId) {
        return queryProcessor.selectTwoColumns("protein_id", /**/"\"sequence\"",
                "items.proteins_sequences",
                TwoColumnRowMapper.<Integer, String>uniqueValMapper(),
                " protein_id IN (select protein_id from items.proteins where species_id  = :species_id );",
                new MapSqlParameterSource("species_id", speciesId));
    }

}


