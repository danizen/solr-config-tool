package org.danizen.solrconfig.unittest;

import org.junit.Test;
import org.junit.BeforeClass;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.danizen.solrconfig.SolrConfig;
import org.danizen.solrconfig.tests.*;


public class NoConfigDirTest {
  private static final Logger logger = LoggerFactory.getLogger(NoConfigDirTest.class);
  
  @BeforeClass
  public static void setUp() {
    SolrConfig.newInstance();
    SolrConfig config = SolrConfig.getInstance();
        
    config.setPath("/nosuchdirectory");
    
    logger.info(String.format("SolrConfig path \"%s\"", config.getPath()));    
  }
   
  @Test(expected=AssertionError.class)
  public void testConfigDirectoryDoesNotExist() {
    new ConfigDirExists().test();
  }
  
  @Test(expected=AssertionError.class)
  public void testSchemaIsMissing() {
    new SchemaExists().test();
  }
  
  @Test(expected=AssertionError.class)
  public void testSolrConfigIsMissing() {
    new SolrConfigExists().test();
  }
}
