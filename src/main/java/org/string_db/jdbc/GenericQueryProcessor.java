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
import org.springframework.jdbc.core.RowCallbackHandler;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.stereotype.Component;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Milan Simonovic <milan.simonovic@imls.uzh.ch>
 */
@Component
public class GenericQueryProcessor {
    @Autowired
    protected JdbcTemplate jdbcTemplate;

    @Autowired
    protected NamedParameterJdbcTemplate namedParameterJdbcTemplate;

    /**
     * Query database for two columns and collect results in a map.
     * <p/>
     * <em>Warning</em>: possible SQL injection
     *
     * @param firstColumn  name (escaped if SQL keyword)
     * @param secondColumn name (escaped if SQL keyword)
     * @param table        name
     * @param rowMapper    items collector
     * @param <K>          first column type
     * @param <V>          second column type
     * @param <R>          type for aggregated values from the second column
     * @return
     * @throws org.springframework.dao.DataAccessException if there is any problem executing the query
     */
    public <K, V, R> Map<K, R> selectTwoColumns(String firstColumn,
                                                String secondColumn,
                                                String table,
                                                final TwoColumnRowMapper<K, V, R> rowMapper) {
        final String query = String.format("SELECT %s, %s FROM %s ", firstColumn, secondColumn, table);
        final Map<K, R> r = new HashMap<>();
        jdbcTemplate.query(query, new RowCallbackHandler() {
            @Override
            public void processRow(ResultSet resultSet) throws SQLException {
                rowMapper.addToMap((K) resultSet.getObject(1), (V) resultSet.getObject(2), r);
            }
        });
        return r;
    }

    /**
     * Query database for two columns and collect results in a map.
     * <p/>
     *
     * <em>Warning</em>: possible SQL injection
     *
     * @param firstColumn  name (escaped if SQL keyword)
     * @param secondColumn name (escaped if SQL keyword)
     * @param table        name
     * @param rowMapper    items collector
     * @param filter       query criteria
     * @param parameters   named params defined in <code>filter</code>
     * @param <K>          first column type
     * @param <V>          second column type
     * @param <R>          type for aggregated values from the second column
     * @return
     * @throws org.springframework.dao.DataAccessException if there is any problem executing the query
     */
    public <K, V, R> Map<K, R> selectTwoColumns(String firstColumn,
                                                String secondColumn,
                                                String table,
                                                final TwoColumnRowMapper<K, V, R> rowMapper,
                                                String filter,
                                                SqlParameterSource parameters) {
        final String query = String.format("SELECT %s, %s FROM %s WHERE %s", firstColumn, secondColumn, table, filter);
        final Map<K, R> r = new HashMap<>();
        namedParameterJdbcTemplate.query(query, parameters, new RowCallbackHandler() {
            @Override
            public void processRow(ResultSet resultSet) throws SQLException {
                rowMapper.addToMap((K) resultSet.getObject(1), (V) resultSet.getObject(2), r);
            }
        });

        return r;
    }
}
