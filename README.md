# Solr Config Tool

[![Build Status](https://travis-ci.org/danizen/solr-config-tool.svg?branch=master)](https://travis-ci.org/danizen/solr-config-tool.svg?branch=master)

## Description

This project allows an Apache Solr Configuration directory to be tested and deployed against an existing SolrCloud easily, either from the command-line or from a CI server.  It is possible to do this with the Solr and zkCli command line scripts.  However, Continuous Integration (CI) jobs based on these scripts do not produce easily viewable test results in de-facto standard Junit XML format.  This tool does.

## Status/Disclaimer

I wrote this, and later discovered that managed schemas work.  I am not actively working on this and do not intend to support it.  You may find it useful, and I may come back to it later.

## Build

The build uses maven, and the JAR will be in `target/solr-config-tool-<VERSION>.jar`

## Use as a command-line

To see the command-line arguments, enter the following or build a script to call it:

```bash
java -jar solr-config-tool-<VERSION>.jar help
```

## Test Job

A typical test against a SolrCloud with Zookeeper on http://localhost:2181/ would look like this:

```bash
java -jar solr-config-tool-<VERSION>.jar test \
    --xmldir test-output \
    --zkhost http://localhost:2181 \
    --confdir <path-to-config>
```

At this point the tool takes the following actions:

* Verify the syntax of each XML file in the configuration directory.
* Make sure that both `solrconfig.xml` and `schema.xml` are present.
* Connect with the SolrCloud and upconfig to a random configuration name
* Create a collection with a random name on the SolrCloud
* Remove both the collection and the configuration
* Generate a JUnit XML report containing the results

## Deploy Job

A typical deploy against a SolrCloud with ZooKeeper on http://localhost:2181/ would look like this:

```bash
java -jar solr-config-tool-<VERSION>.jar deploy \
    --xmldir test-output \
    --zkhost http://localhost:2181 \
    --confdir <path-to-config> \
    --confname <configset-in-zookeeper> \
    --collection <collection-in-solrcloud>
```

The steps here are similar:

* Verify the syntax of each XML file in the configuration directory.
* Make sure that both `solrconfig.xml` and `schema.xml` are present.
* Connect with the SolrCloud and upconfig to the specific configset name
* Reload the collection with the specific collection name
* Generate a JUnit XML report containing the results

## Remaining Issues

The following development is still to be done:


* This needs to at least smoke-test indexing by using SimplePostTool or just an update with an XML/JSON file to test indexing.
* This needs to at least smoke-test querying by using a YAML file defining queries and their expected responses in some fashion. 
* It would be nice to support custom tast classes via test-case discovery.   These would be based on a package name or class name and all would run between creating/reloading the collection and cleanup.
* In the process of getting the tests to generate XML output, I lost the `$HOME/.solrconfigtool` configuration file.   It will be restored. 
