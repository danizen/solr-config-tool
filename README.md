# Solr Config Unit Test

## Description

This project allows an Apache Solr conf directory to be tested against an existing SolrCloud easily, either from the command-line or from a CI server.

## Build

The build uses maven, and the JAR will be in `target/solr-config-test-<VERSION>.jar`

## Use as a command-line

To see the command-line arguments, enter the following or build a script to call it:

```bash
java -jar solr-config-test.jar --help
```

A typical test against a SolrCloud with Zookeeper on http://localhost:2181/ would look like this:

```bash
java -jar solr-config-test.jar \
    --xmlout solr-config-test.xml \
    --zkhost http://localhost:2181 \
    <path-to-config>
```

At this point the tool takes the following actions:

* Verify the syntax of each XML file in the configuration directory.
* Make sure that both `solrconfig.xml` and `schema.xml` are present.
* Connect with the SolrCloud and upconfig to a random configuration name
* Create a collection with a random name on the SolrCloud
* Remove both the collection and the configuration
* Generate a JUnit XML report containing the results

## Configuration

The program reads a properties file in `$HOME/.solrconfigtest` that may define the following variables:

* `zkhost` - same as the `--zkhost` argument
* `zkroot` - same as the `--zkroot` argument
* `method` - same as the `--use` argument

 