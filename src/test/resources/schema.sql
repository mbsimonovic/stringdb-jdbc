--
-- Exported from PostgreSQK using RazorSQL
-- @author Milan Simonovic
--
CREATE SCHEMA items;
SET SCHEMA items;

CREATE TABLE funccats (
	funccat_id VARCHAR(5) NOT NULL,
	funccat_description VARCHAR(100) NOT NULL
);
CREATE TABLE genes (
	gene_id INTEGER NOT NULL,
	gene_external_id VARCHAR(100) NOT NULL,
	start_position_on_contig INTEGER NOT NULL,
	end_position_on_contig INTEGER NOT NULL,
	protein_size INTEGER NOT NULL
);
CREATE TABLE genes_proteins (
	protein_id INTEGER NOT NULL,
	gene_id INTEGER NOT NULL
);
CREATE TABLE meshterms (
	mesh_id INTEGER NOT NULL,
	description VARCHAR NOT NULL
);
CREATE TABLE orthgroups (
	orthgroup_id INTEGER NOT NULL,
	orthgroup_external_id VARCHAR(20) NOT NULL,
	description VARCHAR(1000) NOT NULL,
	protein_count INTEGER NOT NULL,
	species_count INTEGER NOT NULL
);
CREATE TABLE orthgroups_funccats (
	orthgroup_id INTEGER NOT NULL,
	funccat_id VARCHAR(5) NOT NULL
);
CREATE TABLE orthgroups_species (
	orthgroup_id INTEGER NOT NULL,
	species_id INTEGER NOT NULL,
	"count" INTEGER NOT NULL
);
CREATE TABLE protein_image_match (
	protein_id INTEGER NOT NULL,
	image_id VARCHAR(60) NOT NULL,
	"identity" DECIMAL(21,6),
	"source" VARCHAR(10) NOT NULL,
	start_position_on_protein INTEGER,
	end_position_on_protein INTEGER,
	annotation VARCHAR(50)
);
CREATE TABLE proteins (
	protein_id INTEGER NOT NULL,
	protein_external_id VARCHAR(50) NOT NULL,
	species_id INTEGER NOT NULL,
	protein_checksum VARCHAR(16),
	protein_size INTEGER,
	annotation VARCHAR(600) NOT NULL,
	preferred_name VARCHAR(50) NOT NULL,
	annotation_word_vectors VARCHAR
);
CREATE TABLE proteins_meshterms (
	mesh_id INTEGER NOT NULL,
	protein_id INTEGER NOT NULL
);
CREATE TABLE proteins_names (
	protein_name VARCHAR(60) NOT NULL,
	protein_id INTEGER NOT NULL,
	species_id INTEGER NOT NULL,
	"source" VARCHAR(100) NOT NULL,
	is_preferred_name BOOLEAN,
	linkout VARCHAR(15)
);
CREATE TABLE proteins_orthgroups (
	orthgroup_id INTEGER NOT NULL,
	protein_id INTEGER NOT NULL,
	protein_external_id VARCHAR(50) NOT NULL,
	species_id INTEGER NOT NULL,
	start_position INTEGER NOT NULL,
	end_position INTEGER NOT NULL,
	preferred_name VARCHAR(50) NOT NULL,
	protein_annotation VARCHAR(100) NOT NULL,
	preferred_linkout_url VARCHAR(150)
);
CREATE TABLE proteins_sequences (
	protein_id INTEGER NOT NULL,
	"sequence" VARCHAR NOT NULL
);
CREATE TABLE proteins_smartlinkouts (
	protein_id INTEGER NOT NULL,
	protein_size INTEGER NOT NULL,
	smart_url VARCHAR(2000) NOT NULL
);
CREATE TABLE runs (
	run_id INTEGER NOT NULL,
	species_id INTEGER NOT NULL,
	contig_id VARCHAR(50) NOT NULL
);
CREATE TABLE runs_genes_proteins (
	run_id INTEGER NOT NULL,
	gene_id INTEGER NOT NULL,
	protein_id INTEGER NOT NULL,
	start_position_on_contig INTEGER NOT NULL,
	end_position_on_contig INTEGER NOT NULL,
	preferred_name VARCHAR(50) NOT NULL,
	annotation VARCHAR(100) NOT NULL
);
CREATE TABLE runs_orthgroups (
	run_id INTEGER NOT NULL,
	orthgroup_id INTEGER NOT NULL
);
CREATE TABLE species (
	species_id INTEGER NOT NULL,
	official_name VARCHAR(100) NOT NULL,
	compact_name VARCHAR(100) NOT NULL,
	kingdom VARCHAR(15) NOT NULL,
	"type" VARCHAR(10) NOT NULL
);
CREATE TABLE species_names (
	species_id INTEGER,
	species_name VARCHAR,
	official_name VARCHAR,
	is_string_species BOOLEAN
);
CREATE TABLE species_nodes (
	species_id INTEGER NOT NULL,
	species_name VARCHAR NOT NULL,
	"position" INTEGER NOT NULL,
	"size" INTEGER NOT NULL
);
