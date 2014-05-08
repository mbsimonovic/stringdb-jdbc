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
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Component;
import org.string_db.SpeciesRepository;

import java.util.List;
import java.util.Map;

/**
 * @author Milan Simonovic <milan.simonovic@imls.uzh.ch>
 */
@Component
public class SpeciesRepositoryJdbc implements SpeciesRepository {

    @Autowired
    GenericQueryProcessor queryProcessor;

    @Autowired
    JdbcTemplate jdbcTemplate;

    @Autowired
    NamedParameterJdbcTemplate namedParameterJdbcTemplate;

    @Override
    public List<Integer> loadCoreSpeciesIds() {
        return namedParameterJdbcTemplate.queryForList(
                "SELECT species_id from items.species where \"type\" = :type ;",
                new MapSqlParameterSource("type", "core"),
                Integer.class);
    }

    @Override
    public List<Integer> loadSpeciesIds() {
        return jdbcTemplate.queryForList(
                "SELECT species_id from items.species;",
                Integer.class);
    }

    @Override
    public String loadSpeciesName(Integer speciesId) {
        return namedParameterJdbcTemplate.queryForObject(
                "SELECT official_name from items.species where species_id = :species_id ;",
                new MapSqlParameterSource("species_id", speciesId),
                String.class);
    }

    @Override
    public Map<Integer, String> loadSpeciesNames() {
        return queryProcessor.selectTwoColumns("species_id", "official_name", "items.species",
                TwoColumnRowMapper.<Integer, String>uniqueValMapper());
    }
}
