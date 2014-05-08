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

import com.google.common.collect.ImmutableSet;
import org.junit.Test;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import static org.junit.Assert.*;
import static org.string_db.EvidenceType.*;

/**
 * @author Milan Simonovic <milan.simonovic@imls.uzh.ch>
 */
public class StringDbScoresTest {
    Map<Integer, String> scoreTypes = new HashMap<Integer, String>();

    {
        scoreTypes.put(1, "neighbourhood");
        scoreTypes.put(2, "neighbourhood_transferred");
        scoreTypes.put(3, "fusion");
        scoreTypes.put(4, "cooccurrence");
        scoreTypes.put(5, "homology");
        scoreTypes.put(6, "coexpression");
        scoreTypes.put(8/*just for testing*/, "coexpression_transferred");
        scoreTypes.put(7/*just for testing*/, "experimental");
        scoreTypes.put(9, "experimental_transferred");
        scoreTypes.put(10, "database");
        scoreTypes.put(11, "database_transferred");
        scoreTypes.put(12, "textmining");
        scoreTypes.put(13, "textmining_transferred");
        scoreTypes.put(14, "wrongly_spelled_evidence");
    }

    public static final int PROTEIN_A = 1;
    public static final int PROTEIN_B = 2;
    @SuppressWarnings("deprecated")
    static final StringDbScores ALL_SCORES = new StringDbScores(PROTEIN_A, PROTEIN_B, 10, 20, 30, 40, 50, 60, 80, 70, 90, 100, 110, 120, 130);

    @Test
    public void testEquals() throws Exception {
        assertEquals(EvidenceType.COEXPRESSION, EvidenceType.COEXPRESSION);
        @SuppressWarnings("deprecated")
        final StringDbScores expected = new StringDbScores(PROTEIN_A, PROTEIN_B, 10, 20, 30, 40, 50, 60, 80, 70, 90, 100, 110, 120, 130);

        assertEquals(expected, ALL_SCORES);
        assertEquals(expected.hashCode(), ALL_SCORES.hashCode());

        @SuppressWarnings("deprecated")
        final StringDbScores notExpected = new StringDbScores(PROTEIN_A, PROTEIN_B, null, 20, 30, 40, 50, 60, 80, 70, 90, 100, 110, 120, 130);
        assertFalse(notExpected.equals(ALL_SCORES));
        assertFalse(notExpected.hashCode() == ALL_SCORES.hashCode());
    }

    @Test
    public void test_mapping() throws Exception {
        StringDbScores scores = new StringDbScores(PROTEIN_A, PROTEIN_B, scoreTypes, new Integer[][]{{12, 718}});
        assertEquals(StringDbScores.builder(PROTEIN_A, PROTEIN_B).with(TEXTMINING, 718).build(), scores);

        scores = new StringDbScores(PROTEIN_A, PROTEIN_B, scoreTypes, new Integer[][]{{7, 676}, {12, 718}});
        assertEquals(StringDbScores.builder(PROTEIN_A, PROTEIN_B).with(TEXTMINING, 718).with(EXPERIMENTAL, 676).build(), scores);

        scores = new StringDbScores(PROTEIN_A, PROTEIN_B, scoreTypes, new Integer[][]{{1, 10}, {2, 20}, {3, 30}, {4, 40}, {5, 50}, {6, 60},
                {7, 70}, {8, 80}, {9, 90}, {10, 100}, {11, 110}, {12, 120}, {13, 130}});
        assertEquals(ALL_SCORES, scores);
    }

    @Test(expected = IllegalArgumentException.class)
    public void typesIncomplete() throws Exception {
        new StringDbScores(PROTEIN_A, PROTEIN_B, scoreTypes, new Integer[][]{{/*no such score*/115, 718}});
    }

    @Test(expected = IllegalArgumentException.class)
    public void scoreValueNegative() throws Exception {
        new StringDbScores(PROTEIN_A, PROTEIN_B, scoreTypes, new Integer[][]{{1, -1}});
    }

    @Test(expected = IllegalArgumentException.class)
    public void scoreValueOutOfRange() throws Exception {
        new StringDbScores(PROTEIN_A, PROTEIN_B, scoreTypes, new Integer[][]{{1, 1001}});
    }

    @Test(expected = IllegalArgumentException.class)
    public void badTypeName() throws Exception {
        new StringDbScores(PROTEIN_A, PROTEIN_B, scoreTypes, new Integer[][]{{14, 100}});
    }

    @Test
    public void testIsTransferred() throws Exception {
        final Set<EvidenceType> transferred = ImmutableSet.of(NEIGHBOURHOOD_TRANSFERRED, COEXPRESSION_TRANSFERRED,
                EXPERIMENTAL_TRANSFERRED, DATABASE_TRANSFERRED, TEXTMINING_TRANSFERRED, COMBINED_TRANSFERRED);

        for (EvidenceType type : EvidenceType.values()) {
            if (transferred.contains(type) && !type.isTransferred()) {
                fail(type + " is transferred!");
            }
            if (!transferred.contains(type) && type.isTransferred()) {
                fail(type + " is not transferred!");
            }

        }
    }

    @Test
    public void testCalculateTransferredScore() throws Exception {
//from db: 97382;3694;83748;368;"{{7,53},{9,327},{11,99},{13,85}}"
        final StringDbScores scores = StringDbScores.builder(97382, 83748).with(COEXPRESSION_TRANSFERRED, 53)
                .with(EXPERIMENTAL_TRANSFERRED, 327)
                .with(DATABASE_TRANSFERRED, 99)
                .with(TEXTMINING_TRANSFERRED, 85)
                .build();
        final Integer transferredScore = scores.getTransferredScore();
        assertEquals(368, transferredScore.intValue());
    }
}
