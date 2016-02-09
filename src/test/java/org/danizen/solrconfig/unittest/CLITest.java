package org.danizen.solrconfig.unittest;

import java.nio.file.Paths;
import static org.junit.Assert.*;
import org.junit.Test;

import org.danizen.solrconfig.CLI;
import org.danizen.solrconfig.SolrConfig;


public class CLITest {
  
  @Test
  public void testCloudOptions() throws Exception {
    String[] args = {
        "test",
        "-confdir", "whatever", 
        "-xmldir", "test_output",
        "-zkhost", "65.6.22.127:8983; 65.6.22.128:8983",
        "-zkroot", "/solr1",
        "-solrurl", "http://127.0.0.1:8983/",
        "-confname", "mybigconfig",
        "-collection", "gettingstarted2",
    };
    CLI cli = new CLI();
    boolean isvalid = cli.processOptions(args);
    assertTrue("validates command-line options", isvalid);
    
    assertEquals(cli.getXmlDir(), Paths.get("test_output"));
    
    SolrConfig config = SolrConfig.getInstance();
    assertEquals(Paths.get("whatever"), config.getPath());
    assertEquals("65.6.22.127:8983; 65.6.22.128:8983", config.getZkHost());
    assertEquals("/solr1", config.getZkRoot());
    assertEquals("mybigconfig", config.getConfigName());
    assertEquals("gettingstarted2", config.getCollectionName());
    assertEquals("http://127.0.0.1:8983/", config.getSolrURL());
    assertEquals(true, config.isCleanupEnabled());
  }
  
  @Test
  public void testZkHostRequired() throws Exception {
    String[] args = {};
    CLI cli = new CLI();
    boolean isvalid = cli.processOptions(args);
    assertFalse("requires something more", isvalid);
  }
  
}
