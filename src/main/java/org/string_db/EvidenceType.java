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

import java.util.HashSet;
import java.util.Set;

/**
 * Enum representing score types from the {@code network.score_types_user_friendly} table.
 *
 * @author Milan Simonovic <milan.simonovic@imls.uzh.ch>
 */
public enum EvidenceType {
    NEIGHBOURHOOD,
    NEIGHBOURHOOD_TRANSFERRED,
    FUSION,
    COOCCURRENCE,
    HOMOLOGY,
    COEXPRESSION,
    COEXPRESSION_TRANSFERRED,
    EXPERIMENTAL,
    EXPERIMENTAL_TRANSFERRED,
    DATABASE,
    DATABASE_TRANSFERRED,
    TEXTMINING,
    TEXTMINING_TRANSFERRED,
    /**
     * not an actual STRING-DB score, can be used for grouping all transferred scores into one
     */
    COMBINED_TRANSFERRED;

    final static Set<EvidenceType> TRANSFERRED = new HashSet<EvidenceType>();

    static {
        for (EvidenceType evidenceType : EvidenceType.values()) {
            if (evidenceType.isTransferred())
                TRANSFERRED.add(evidenceType);
        }
    }

    /**
     * @return true if {@code this} is a transferred evidence type
     */
    public boolean isTransferred() {
        return this.name().endsWith("_TRANSFERRED");
    }

}
