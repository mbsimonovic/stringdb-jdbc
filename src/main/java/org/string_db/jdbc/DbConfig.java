/**
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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.*;
import org.springframework.core.env.Environment;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;

/**
 * App configuration
 *
 * @author Milan Simonovic <milan.simonovic@imls.uzh.ch>
 */
@Configuration
@PropertySource("file:/opt/stringdb/jdbc-v1.0.properties")
public class DbConfig {

    private static final Logger logger = LoggerFactory.getLogger(DbConfig.class.getCanonicalName());

    @Autowired
    Environment env;

    @Autowired
    private DataSourceConfig dataSourceConfig;

    /**
     * @return
     */
    @Bean
    @Scope("singleton")//JdbcTemplate is thread-safe so keep just one instance
    @Description("Provides the singleton JdbcTemplate")
    public JdbcTemplate jdbcTemplate() {
        return new JdbcTemplate(dataSourceConfig.dataSource());
    }

    /**
     * @return
     */
    @Bean
    @Scope("singleton")//it's thread-safe so keep just one instance
    @Description("Provides the singleton NamedParameterJdbcTemplate")
    public NamedParameterJdbcTemplate namedParameterJdbcTemplate() {
        return new NamedParameterJdbcTemplate(dataSourceConfig.dataSource());
    }
}

