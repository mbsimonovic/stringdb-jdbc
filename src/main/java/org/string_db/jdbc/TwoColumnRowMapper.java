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

import java.util.Collections;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * A class to convert a set of rows to a map, where rows have two columns.
 * The first row column is used as a key. The second column can either directly
 * be used as a value, or some kind of aggregation can be performed
 * (sum, number of unique values, collect all values into a set, etc).
 * <p/>
 *
 *
 *
 * @param <F> first column type
 * @param <S> second column type
 * @param <R> result type
 * @author Milan Simonovic <milan.simonovic@imls.uzh.ch>
 */
public abstract class TwoColumnRowMapper<F, S, R> {
    protected abstract void addToMap(F firstColumn, S secondColumn, Map<F, R> map);

    public static <F, S> TwoColumnRowMapper<F, S, S> uniqueValMapper() {
        return new UniqueValTwoColumnRowMapper<>();
    }

    /**
     * @param <F> first column type
     * @param <S> second column type
     * @return a mapper that groups all <code>S</code> values for each <code>F</code>
     */
    public static <F, S> TwoColumnRowMapper<F, S, Set<S>> multiValMapper() {
        return new MultiValTwoColumnRowMapper<>();
    }

    //hide the implementation for now..
    private static class MultiValTwoColumnRowMapper<K, V> extends TwoColumnRowMapper<K, V, Set<V>> {
        @Override
        public void addToMap(K firstColumn, V secondColumn, Map<K, Set<V>> r) {
            if (!r.containsKey(firstColumn)) {
                r.put(firstColumn, Collections.<V>singleton(secondColumn));
            } else {
                final Set set = (Set) r.get(firstColumn);
                if (!(set instanceof HashSet)) {
                    r.put(firstColumn, new HashSet<V>(set));
                }
                ((Set) r.get(firstColumn)).add(secondColumn);
            }
        }
    }

    private static class UniqueValTwoColumnRowMapper<K, V> extends TwoColumnRowMapper<K, V, V> {
        @Override
        public void addToMap(K firstColumn, V secondColumn, Map<K, V> map) {
            map.put(firstColumn, secondColumn);
        }
    }

}

