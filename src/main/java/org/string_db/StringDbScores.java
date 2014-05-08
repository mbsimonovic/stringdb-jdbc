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

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * A programmer-friendly interface to STRING-DB's the {@code evidence_scores} column
 * from the {@code network.node_node_links} table. It's got only one additional method
 * {@link #getTransferredScore()} } to calculate one combined score for all transferred
 * channels (used only for PSICQUIC currently).
 * <p/>
 * <p/>
 * It's a value class, {@link #equals(Object)}  (and {@link #hashCode()}) is based on
 * {@link #scores} equality.
 * <p/>
 * The class is @Immutable => @TheadSafe.
 * <p/>
 *
 * @author Milan Simonovic <milan.simonovic@imls.uzh.ch>
 * @see <a href='http://www.ncbi.nlm.nih.gov/pubmed/15608232'>von Mering, et al Nucleic Acids Res. 2005</a>
 */
public final class StringDbScores {
    protected final Integer proteinA;
    protected final Integer proteinB;
    protected final Map<EvidenceType, Integer> scores;
    /**
     * a 'prior' that has been added to account for the probability
     * that two randomly picked proteins are interacting.
     */
    protected final Double prior = 0.063;

    private StringDbScores(Integer proteinA, Integer proteinB, Map<EvidenceType, Integer> scores) {
        this.proteinA = proteinA;
        this.proteinB = proteinB;
        this.scores = scores;
    }

    /**
     * @param proteinA
     * @param proteinB
     * @param scoreTypes              network.score_types_user_friendly table map
     * @param scoresFromNodeNodeLinks evidence_scores column from network.node_node_links
     */
    public StringDbScores(Integer proteinA, Integer proteinB, Map<Integer, String> scoreTypes, Integer[][] scoresFromNodeNodeLinks) {
        Map<EvidenceType, Integer> tmpscores = new HashMap<EvidenceType, Integer>();
        for (Integer[] score : scoresFromNodeNodeLinks) {
            final Integer key = score[0];
            if (!scoreTypes.containsKey(key)) {
                throw new IllegalArgumentException("no such score: " + key + ", only allowed: " + scoreTypes);
            }
            if (score[1] < 0 || score[1] > 1000) {
                throw new IllegalArgumentException("invalid score value: " + score[1]);
            }
            tmpscores.put(EvidenceType.valueOf(scoreTypes.get(key).trim().toUpperCase()), score[1]);
        }
        this.scores = Collections.unmodifiableMap(tmpscores);
        this.proteinA = proteinA;
        this.proteinB = proteinB;
    }

    /**
     * @deprecated use {@link org.string_db.StringDbScores.Builder}
     */
    StringDbScores(Integer proteinA, Integer proteinB, Integer neighbourhood, Integer neighbourhoodTransferred, Integer fusion, Integer cooccurrence, Integer homology,
                   Integer coexpresion, Integer coexpressionTransferred, Integer experimental, Integer experimentalTransferred,
                   Integer database, Integer databaseTransferred, Integer textmining, Integer textminingTransferred) {
        this.proteinA = proteinA;
        this.proteinB = proteinB;

        Map<EvidenceType, Integer> tmpscores = new HashMap<EvidenceType, Integer>();
        tmpscores.put(EvidenceType.NEIGHBOURHOOD, neighbourhood);
        tmpscores.put(EvidenceType.NEIGHBOURHOOD_TRANSFERRED, neighbourhoodTransferred);
        tmpscores.put(EvidenceType.FUSION, fusion);
        tmpscores.put(EvidenceType.COOCCURRENCE, cooccurrence);
        tmpscores.put(EvidenceType.HOMOLOGY, homology);
        tmpscores.put(EvidenceType.COEXPRESSION, coexpresion);
        tmpscores.put(EvidenceType.COEXPRESSION_TRANSFERRED, coexpressionTransferred);
        tmpscores.put(EvidenceType.EXPERIMENTAL, experimental);
        tmpscores.put(EvidenceType.EXPERIMENTAL_TRANSFERRED, experimentalTransferred);
        tmpscores.put(EvidenceType.DATABASE, database);
        tmpscores.put(EvidenceType.DATABASE_TRANSFERRED, databaseTransferred);
        tmpscores.put(EvidenceType.TEXTMINING, textmining);
        tmpscores.put(EvidenceType.TEXTMINING_TRANSFERRED, textminingTransferred);
        this.scores = Collections.unmodifiableMap(tmpscores);
    }

    /**
     * @param evidenceType
     * @return null if score doesn't exist
     */
    public Integer get(EvidenceType evidenceType) {
        return scores.get(evidenceType);
    }

    public Integer getProteinA() {
        return proteinA;
    }

    public Integer getProteinB() {
        return proteinB;
    }

    /**
     * A builder for creating immutable {@code StringDbScores} instances.
     */
    public static class Builder {
        protected Integer proteinA;
        protected Integer proteinB;
        protected final HashMap<EvidenceType, Integer> scores = new HashMap<EvidenceType, Integer>();

        public Builder(Integer proteinA, Integer proteinB) {
            this.proteinA = proteinA;
            this.proteinB = proteinB;
        }

        public Builder with(EvidenceType evidenceType, Integer score) {
            scores.put(evidenceType, score);
            return this;
        }

        public StringDbScores build() {
            return new StringDbScores(proteinA, proteinB, Collections.unmodifiableMap(scores));
        }
    }

    /**
     * @return Returns a new {@link Builder}
     */
    public static Builder builder(Integer proteinA, Integer proteinB) {
        return new Builder(proteinA, proteinB);
    }

    /**
     * @param type
     * @return true if score for {@code type} is greater than 0
     */
    private boolean hasScore(EvidenceType type) {
        if (!scores.containsKey(type)) return false;
        return scores.get(type) > 0;
    }

    public boolean hasTransferred() {
        for (EvidenceType type : EvidenceType.TRANSFERRED) {
            if (hasScore(type)) return true;
        }
        return false;
    }

    /**
     * Calculate combined score for all transferred channels.
     * <p/>
     * From the <a href='http://string-db.org/help/topic/org.string-db.docs/ch04.html#d0e366'>FAQ</a>:
     * <pre>
     * How are scores computed?
     * The combined score are computed by combining the probabilities from the
     * different evidence channels, correcting for the probability of randomly
     * observing an interaction. For a more detailed description please see
     * von Mering, et al Nucleic Acids Res. 2005
     * </pre>
     * <p>
     * To combine the scores we add the probabilities for each of the channels.
     * To each channel, a 'prior' has been added to account for the probability
     * that two randomly picked proteins are interacting. Before combing the
     * channels the 'prior' has to be removed and then added back again to
     * the combined score. Here is how the combined score is computed for an interaction.
     * </p>
     * <ol>
     * <li>
     * For each of the scores for the individual channels (s_i) remove the prior (p=0.063): <br/>
     * s^{no prior}_i := (s_i-p)/(1-p)
     * </li>
     * <li>
     * Combine the scores of the channels:<br/>
     * s^{no prior}_{tot} := 1 - \prod_i (1 - s^{no prior}_i)
     * </li>
     * <li>
     * Add the prior back (once):<br/>
     * s_{tot} := s^{no prior}_{tot} + p * (1-s^{no prior}_{tot})
     * </li>
     * </ol>
     * <p/>
     * In R code it would be something like this:
     * <pre>
     * > s_exp = 0.621
     * > s_txt = 0.585
     * > p = 0.063
     * > s_exp_nop = (s_exp-p) / (1-p)
     * > s_txt_nop = (s_txt-p) / (1-p)
     * > s_tot_nop = 1 - (1 - s_exp_nop) * (1 - s_txt_nop)
     * > s_tot = s_tot_nop + p * (1 - s_tot_nop)
     * </pre>
     * <p>
     * Also, although it appears that it is not the case here, homology correction
     * are applied to the co-occurrence and text-mining scores.
     * <pre>
     * Effective co-occurrence score = co-occurrence score * (1 - homology score)
     * effective text-mining score = text-mining score * (1 - homology score)
     * </pre>
     * </p>
     *
     * @return combined transferred score
     */
    public Integer getTransferredScore() {
        final double total_no_prior =
                1.0d -
                        (1.0d - removePrior(EvidenceType.COEXPRESSION_TRANSFERRED))
                                * (1.0d - removePrior(EvidenceType.DATABASE_TRANSFERRED))
                                * (1.0d - removePrior(EvidenceType.EXPERIMENTAL_TRANSFERRED))
                                * (1.0d - removePrior(EvidenceType.TEXTMINING_TRANSFERRED));

        final double total_prior = total_no_prior + prior * (1 - total_no_prior);
        Integer score = (int) (total_prior * 1000);
        if (score > 1000 || score < 0) {
            throw new RuntimeException("illegal score for: " + toString() + ", transferred score: " + score);
        }
        return score;
    }

    private double removePrior(EvidenceType transferred) {
        if (hasScore(transferred)) {
            final Integer score = get(transferred);
            if (score <= prior * 1000)
                return 0;
            final double normalized = score / 1000.0d;
            final double score_no_prior = (normalized - prior) / (1 - prior);
            return score_no_prior < 0 ? 0 : score_no_prior;
        }
        return 0;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        StringDbScores that = (StringDbScores) o;

        if (!proteinA.equals(that.proteinA)) return false;
        if (!proteinB.equals(that.proteinB)) return false;
        if (!scores.equals(that.scores)) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = proteinA.hashCode();
        result = 31 * result + proteinB.hashCode();
        result = 31 * result + scores.hashCode();
        return result;
    }

    @Override
    public String toString() {
        return "StringDbScores{" +
                "proteinA=" + proteinA +
                ", proteinB=" + proteinB +
                ", scores=" + scores +
                '}';
    }

    public Integer getNeighbourhood() {
        return get(EvidenceType.NEIGHBOURHOOD);
    }

    public Integer getNeighbourhoodTransferred() {
        return get(EvidenceType.NEIGHBOURHOOD_TRANSFERRED);
    }

    public Integer getFusion() {
        return get(EvidenceType.FUSION);
    }

    public Integer getCooccurrence() {
        return get(EvidenceType.COOCCURRENCE);
    }

    public Integer getHomology() {
        return get(EvidenceType.HOMOLOGY);
    }

    public Integer getCoexpresion() {
        return get(EvidenceType.COEXPRESSION);
    }

    public Integer getCoexpressionTransferred() {
        return get(EvidenceType.COEXPRESSION_TRANSFERRED);
    }

    public Integer getExperimental() {
        return get(EvidenceType.EXPERIMENTAL);
    }

    public Integer getExperimentalTransferred() {
        return get(EvidenceType.EXPERIMENTAL_TRANSFERRED);
    }

    public Integer getDatabase() {
        return get(EvidenceType.DATABASE);
    }

    public Integer getDatabaseTransferred() {
        return get(EvidenceType.DATABASE_TRANSFERRED);
    }

    public Integer getTextmining() {
        return get(EvidenceType.TEXTMINING);
    }

    public Integer getTextminingTransferred() {
        return get(EvidenceType.TEXTMINING_TRANSFERRED);
    }
}
