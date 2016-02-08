package org.danizen.solrconfig.unittest;

import org.junit.Test;
import org.junit.BeforeClass;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.danizen.solrconfig.SolrConfig;
import org.danizen.solrconfig.tests.*;


public class MissingSchemaTest {

  private static final Logger logger = LoggerFactory.getLogger(MissingSchemaTest.class);
  
  @BeforeClass
  public static void setUp() {
    SolrConfig config = SolrConfig.getInstance();
    String configPath = ResourceUtils.getResourceSubdir("/missingschema");
    config.setPath(configPath);
    
    logger.info(String.format("SolrConfig path \"%s\"", configPath));    
  }
  
  @Test
  public void testSolrConfigOK() {
    new SolrConfigExists().test();
  }
  
  @Test
  public void testXmlFilesAreValid() throws Exception {
    new XmlFilesAreValid().test();
  }
  
  @Test(expected=AssertionError.class)
  public void testSchemaMissing() {
    new SchemaExists().test();
  }
}
