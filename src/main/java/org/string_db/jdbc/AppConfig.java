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

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

/**
 * Global spring configuration, lists all other bean configs except for
 * {@link org.string_db.jdbc.DataSourceConfig} implementation (can be configured
 * at runtime to select appropriate db backend).
 *
 * @author Milan Simonovic <milan.simonovic@imls.uzh.ch>
 */
@Configuration
@Import({DbConfig.class, DataSourceConfig.class, SpeciesRepositoryJdbc.class, ProteinRepositoryJdbc.class, GenericQueryProcessor.class})
public class AppConfig {
}
