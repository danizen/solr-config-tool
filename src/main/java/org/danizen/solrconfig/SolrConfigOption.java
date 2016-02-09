package org.danizen.solrconfig;

/**
 * Each SolrConfigOption must be setup by the tool as a Java Property,
 * or the tool won't be able to run the tests in a JVM and capture output.  
 * 
 * @author davisda4
 */
public enum SolrConfigOption {
  ZKHOST,
  ZKROOT,
  SOLRURL,
  CONFDIR,
  CONFNAME,
  COLLECTION,
  RELOAD,
  CLEANUP,
  BASICAUTH;
  
  public String toString() {
    return this.getName();
  }
  
  public String getName() {
    return this.name().toLowerCase();
  }
  
  public String getPropertyName() {
    return "solr.config.tool."+this.getName();
  }
}
