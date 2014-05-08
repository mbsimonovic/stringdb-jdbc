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

import com.google.common.base.Joiner;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowCallbackHandler;

import java.io.*;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * After failing to create Postgres dumps that HSQLDB would accept
 * verbatim, here's a simple class that can export data from PostgreSQL
 * into HSQLDB-compatible dumps.
 *
 * @author Milan Simonovic <milan.simonovic@imls.uzh.ch>
 * @see <a href="http://www.postgresql.org/docs/9.3/static/catalog-pg-class.html">pg_class</a>
 * @see <a href="http://www.postgresql.org/docs/current/static/catalog-pg-index.html">pg_index</a>
 * @see <a href="http://stackoverflow.com/questions/2204058/show-which-columns-an-index-is-on-in-postgresql">stackoverflow q 2204058</a>
 * @see <a href="http://www.hsqldb.org/doc/guide/sqlgeneral-chapt.html">HSQLDB syntax</a>
 * @see <a href="http://isocra.com/articles/db2sql.java?phpMyAdmin=zUe3nb4m8CyqM%2C1oOhgUrqY4g1c">db2sql</a>
 */
public class Postgres2HSQLDB {
    final JdbcTemplate jdbcTemplate;

    public Postgres2HSQLDB(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    private final String selectIndexDef =
            "SELECT pg_get_indexdef(i.indexrelid) \n" +
                    "FROM   pg_class c, pg_namespace n, pg_index i, pg_class t\n" +
                    "WHERE\n" +
                    "       c.relnamespace = n.oid \n" +
                    "   AND c.oid = i.indexrelid \n" +
                    "   AND i.indrelid   = t.oid\n" +
                    "   AND c.relkind = 'i'\n" +
                    "   AND n.nspname = ?\n" +
                    "   AND t.relname = ?";


    public static void main(final String[] args) throws FileNotFoundException, UnsupportedEncodingException, SQLException {
        final ApplicationContext ctx = new AnnotationConfigApplicationContext(DbConfig.class, DriverDataSourceConfig.class);
        final JdbcTemplate jdbcTemplate = ctx.getBean(JdbcTemplate.class);
        final Postgres2HSQLDB converter = new Postgres2HSQLDB(jdbcTemplate);

        converter.dumpTable("items", "species", "species_id in (882, 3702, 4932, 7227, 9606, 10090, 272634, 511145)", "hsql-data.sql");
        // skip checksum (not used) and annotation_word_vectors columns (postgresql specific data type - tsvector)
        converter.dumpTable("items", "proteins", "species_id in (511145)", "hsql-data.sql",
                new String[]{"protein_id", "protein_external_id", "species_id", "annotation", "preferred_name"});
        //m.pneumoniae has fewest names:
        converter.dumpTable("items", "proteins_names", "species_id in (272634)", "hsql-data.sql",
                new String[]{"protein_id", "protein_name", "species_id", /*escape keyword*/"\"source\""});
    }

    private void dumpTable(String schema, String table, String filter, String fileName, String[] columnsToInclude) {
        final StringBuilder result = new StringBuilder();
        result.append("\n-- " + schema + "." + table + " DATA:\n");
        dumpTableData(schema, table, filter, result, columnsToInclude);
        result.append("\n-- indices after the data:\n");
        dumpIndices(schema, table, result);
        result.append("\n-- END of  " + schema + "." + table + "\n");
        appendToFile(result, fileName);
    }

    public void dumpTable(String schema, String table, String filter, String fileName) throws FileNotFoundException, UnsupportedEncodingException {
        dumpTable(schema, table, filter, fileName, null);
    }

    void dumpTableData(final String schema, final String table, String filter, final StringBuilder result, final String[] columnsToInclude) {
        final String selectedColumns = columnsToInclude != null ? Joiner.on(',').join(columnsToInclude) : "*";
        final String sql = "SELECT " + selectedColumns
                + " FROM " + schema + "." + table + (filter != null ? " WHERE " + filter : "");
        final String columns = columnsToInclude != null ? "(" + Joiner.on(", ").join(columnsToInclude) + ")" : null;

        result.append("SET SCHEMA ").append(schema).append(";\n");
        jdbcTemplate.query(sql, new RowCallbackHandler() {
                    @Override
                    public void processRow(ResultSet rs) throws SQLException {
                        while (rs.next()) {
                            result.append("INSERT INTO ").append(table);
                            if (columns != null) result.append(columns);
                            result.append(" VALUES(");
                            for (int i = 0; i < rs.getMetaData().getColumnCount(); i++) {
                                if (i > 0) {
                                    result.append(", ");
                                }
                                Object value = rs.getObject(i + 1);
                                if (value == null) {
                                    result.append("NULL");
                                } else {
                                    String outputValue = value.toString();
                                    // In a string started with ' (singlequote) use '' (two singlequotes) to create a ' (singlequote).
                                    //see http://www.hsqldb.org/doc/guide/ch09.html#expression-section
                                    //XXX use Connection.escapeString ?
                                    outputValue = outputValue.replaceAll("'", "''");
                                    result.append("'" + outputValue + "'");
                                }
                            }
                            result.append(");\n");
                        }
                    }
                }
        );
    }

    void dumpIndices(String schema, String table, final StringBuilder result) {
        /**
         index metadata be obtained from jdbc connection:
         <pre>
         final DataSource dataSource = ctx.getBean(DataSource.class);
         final Jdbc4Connection connection = (Jdbc4Connection) dataSource.getConnection();
         final DatabaseMetaData metaData = connection.getMetaData();
         final ResultSet indexInfo = metaData.getIndexInfo(connection.getCatalog(), "items", "proteins", false, false);
         while(indexInfo.next()) {
         for (int i = 0; i < indexInfo.getMetaData().getColumnCount(); i++) {
         System.out.println(indexInfo.getObject(i + 1));
         }
         }
         </pre>
         *
         */
        jdbcTemplate.query(selectIndexDef, new Object[]{schema, table}, new RowCallbackHandler() {
            @Override
            public void processRow(ResultSet rs) throws SQLException {
                String def = rs.getString(1);
                if (def.contains("USING gist")) {
                    //skip GIST..
                    return;
                }
                if (def.contains("varchar_pattern_ops")) {
                    //not supported by hsqldb
                    return;
                }
                if (def.contains("(upper(") || def.contains("(lower(")) {
                    return;
                }
                result.append(def.replace("USING btree", "")).append(";\n");

            }
        });
    }

    public void appendToFile(StringBuilder result, String fileName) {
        try (PrintWriter writer = new PrintWriter(new BufferedWriter(new FileWriter(fileName, true)))) {
            writer.append(result);
        } catch (IOException e) {
            throw new RuntimeException("failed to write to " + fileName, e);
        }
    }

}
