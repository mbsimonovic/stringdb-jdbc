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
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;
import org.springframework.jdbc.datasource.DriverManagerDataSource;

import javax.sql.DataSource;

/**
 * @author Milan Simonovic <milan.simonovic@imls.uzh.ch>
 * @see <a href="</>http://docs.spring.io/spring/docs/4.0.x/javadoc-api/org/springframework/context/annotation/PropertySource.html">PropertySource</a>
 */
@Configuration
@PropertySource("file:/opt/stringdb/jdbc-v1.0.properties")
//use profiles? http://spring.io/blog/2011/02/14/spring-3-1-m1-introducing-profile/
public class DriverDataSourceConfig implements DataSourceConfig {
    @Autowired
    Environment env;

    @Override
    @Bean //need to repeat the annotation
    public DataSource dataSource() {
        if (!env.containsProperty("jdbc.url")) {
            throw new ExceptionInInitializerError("missing property 'jdbc.url' " + env);
        }
        return new DriverManagerDataSource(env.getProperty("jdbc.url"),
                env.getProperty("jdbc.username"),
                env.getProperty("jdbc.password"));
    }
}
