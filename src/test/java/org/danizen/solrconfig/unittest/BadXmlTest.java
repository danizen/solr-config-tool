package org.danizen.solrconfig.unittest;

import org.junit.Test;
import org.junit.BeforeClass;
import static org.junit.Assert.*;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xml.sax.SAXException;
import org.danizen.solrconfig.SolrConfig;
import org.danizen.solrconfig.tests.*;


public class BadXmlTest {

  private static final Logger logger = LoggerFactory.getLogger(BadXmlTest.class);
  
  @BeforeClass
  public static void setUp() {
    SolrConfig config = SolrConfig.getInstance();
    String configPath = ResourceUtils.getResourceSubdir("/badxml");
    config.setPath(configPath);
    
    logger.info(String.format("SolrConfig path \"%s\"", configPath));    
  }
  
  @Test
  public void testSchemaOK() {
    new SchemaExists().test();
  }
  
  @Test
  public void testSolrConfigOK() {
    new SolrConfigExists().test();
  }

  @Test
  public void testXmlFilesAreValid() throws Exception {
    boolean threwExpected = false;
    try {
      new XmlFilesAreValid().test();
    } catch (SAXException e) {
      String message = e.getMessage();
      if (message.indexOf("lineNumber: 6") != -1
          && message.indexOf("columnNumber: 3") != -1
          && message.indexOf("badxml.xml") != -1) {
        threwExpected = true;
      }
    }
    assertTrue(threwExpected);
  }
 

}
