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

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * A thin wrapper around STRINGDB's protein_external_id. Better to have a type to constrain
 * {@code java.lang.String} values.
 * <p/>
 * The class is immutable => @ThreadSafe.
 *
 * @author Milan Simonovic <milan.simonovic@imls.uzh.ch>
 */
public class ProteinExternalId {
    private static final Pattern pattern = Pattern.compile("(\\d+)\\.(.+)", Pattern.DOTALL);
    private final String externalId;

    public ProteinExternalId(String externalId) {
        if (!pattern.matcher(externalId).matches()) {
            throw new ExceptionInInitializerError("illegal external id: " + externalId);
        }
        this.externalId = externalId;
    }

    public Integer getSpeciesId() {
        final Matcher matcher = pattern.matcher(externalId);
        matcher.matches();
        return Integer.valueOf(matcher.group(1));
    }

    @Override
    public String toString() {
        return externalId;
    }

    @Override
    public int hashCode() {
        return externalId.hashCode();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ProteinExternalId that = (ProteinExternalId) o;

        if (!externalId.equals(that.externalId)) return false;

        return true;
    }
}
