package org.danizen.solrconfig.unittest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.junit.Test;
import static org.junit.Assert.*;
import java.io.IOException;

import org.danizen.solrconfig.SolrConfig;
import org.danizen.solrconfig.TestMethod;

public class SolrConfigUnitTest {
  
  Logger logger = LoggerFactory.getLogger(SolrConfigUnitTest.class);
  
  @Test
  public void testIsSingleton() {
    SolrConfig config1 = SolrConfig.getInstance();
    SolrConfig config2 = SolrConfig.getInstance();
    assertSame(config1, config2);
  }
  
  @Test
  public void testMethodIgnoresCase() {
    SolrConfig config = SolrConfig.getInstance();
    config.setTestMethod("cloud");
    assertEquals(TestMethod.CLOUD, config.getTestMethod());
        
    config.setTestMethod("EMBEDDED");
    assertEquals(TestMethod.EMBEDDED, config.getTestMethod());
  }

  @Test(expected=IllegalArgumentException.class)
  public void testMethodValidates() {
    SolrConfig config = SolrConfig.getInstance();
    config.setTestMethod("embeded");    // intentionally mispelled
  }
  
  @Test
  public void testCloudInProperties() throws IOException {
    SolrConfig.newInstance();
    SolrConfig config = SolrConfig.getInstance();
    
    config.loadDefaults(this.getClass().getResourceAsStream("cloud.properties"));
    
    assertEquals(TestMethod.CLOUD, config.getTestMethod());
    assertEquals("65.6.22.127:8983; 65.6.22.128:8983", config.getZkHost());
    assertEquals("/solr1", config.getZkRoot());   
  }

}
