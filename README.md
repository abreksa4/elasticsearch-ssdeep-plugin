# ElasticSearch SSDeep Script Plugin

The following plugin allows the usage of native scripts which compare ssdeep 
hashes between documents generating a score between 0 and 100 allowing 
identification of similar documents according to the SSDeep algorithm.

## Usage

### Installation

`mvn clean package` 

This will build the project and put the distributable package in the 
*target/releases/elasticsearch-ssdeep-plugin-${version}.zip* file.  This can then
be installed in Elasticsearch to become available.

`bin/plugin install file:///<path_to_.zip>`

This will install the plugin into ElasticSearch, it will become available for use 
the next time ElasticSearch is restarted.

You can remove the plugin with the following command.

`bin/plugin remove elasticsearch-ssdeep-plugin`

### Querying

In the directory *examples* there are two examples which show using the script as
part of a function query to find documents with a similarity between a known SSDeep 
hash.  The file *ssdeep_search.json* shows the simplest use case finding all documents
which have a non-zero matching value to a known hash.  The document 
*optimized_ssdeep_search.json* shows some optimization techniques which can filter 
out documents before running them through the SSDeep script which may be expensive
on large data sets.  Experimentation with your data set may be necessary to determine
whether the optimizations outlined in that sample query will improve performance for
your use case.  Eliminating matches based on block size alone may be faster than 
eliminating matches based on block size and chunk matches.

## Requirements

* Java 1.8
* ElasticSearch 2.3.4
* phishme/jssdeep Java SSDeep library. 
