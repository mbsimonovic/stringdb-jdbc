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

import java.util.Map;
import java.util.Set;

/**
 * @author Milan Simonovic <milan.simonovic@imls.uzh.ch>
 */
public interface ProteinRepository {

    /**
     * @param speciesId must not be null
     * @return {protein_id -> ProteinExternalId} map of all {@code speciesId} proteins
     */
    Map<Integer, ProteinExternalId> loadExternalIds(Integer speciesId);

    /**
     * load protein names for this species
     *
     * @param speciesId
     * @return {protein_id -> Set(protein names)} map
     */
    Map<Integer, String> loadProteinPreferredNames(Integer speciesId);

    /**
     * load all protein names for this species
     *
     * @param speciesId
     * @return {protein_id -> Set(protein names)} map
     */
    Map<Integer, Set<String>> loadProteinNames(Integer speciesId);

    /**
     * load protein names only from these {@code sources}
     *
     * @param speciesId
     * @param sources
     * @return {protein_id -> Set(protein names)} map
     */
    Map<Integer, Set<String>> loadProteinNames(Integer speciesId, Set<String> sources);

    /**
     * load all protein sequences for this species
     *
     * @param speciesId
     * @return {protein_id -> sequence} map
     */
    Map<Integer, String> loadProteinSequences(Integer speciesId);

    /**
     * load unique (1:1) mapping of protein_ids-UniProt_ids  for this species
     *
     * @param speciesId
     * @return {protein_id -> UniprotAC} map
     */
    Map<Integer, UniprotAC> loadUniqueUniProtIds(Integer speciesId);

    /**
     * @param speciesId
     * @return total number of proteins for this <code>speciesId</code>
     */
    Integer count(Integer speciesId);
}
