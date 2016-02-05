package org.danizen.solrconfig.unittest;

import java.nio.file.Paths;
import static org.junit.Assert.*;
import org.junit.Test;
import org.apache.commons.cli.ParseException;

import org.danizen.solrconfig.CLI;
import org.danizen.solrconfig.SolrConfig;
import org.danizen.solrconfig.TestMethod;


public class CLITest {
  
  @Test
  public void testCloudOptions() throws ParseException {
    String[] args = { 
        "-config", "whatever", 
        "-xmlout", "noob.xml",
        "-zkhost", "65.6.22.127:8983; 65.6.22.128:8983",
        "-zkroot", "/solr1"
    };
    SolrConfig.newInstance();
    CLI cli = new CLI();
    boolean isvalid = cli.validateOptions(cli.parseOptions(args));
    assertTrue("validates command-line options", isvalid);
    
    SolrConfig config = SolrConfig.getInstance();
    assertEquals(Paths.get("whatever"), config.getPath());
    assertEquals(Paths.get("noob.xml"), config.getXmlOutPath());
    assertEquals(TestMethod.CLOUD, config.getTestMethod());
    assertEquals("65.6.22.127:8983; 65.6.22.128:8983", config.getZkHost());
    assertEquals("/solr1", config.getZkRoot());
  }
  
  @Test
  public void testZkHostRequired() throws ParseException {
    String[] args = {};
    SolrConfig.newInstance();
    CLI cli = new CLI();
    boolean isvalid = cli.validateOptions(cli.parseOptions(args));
    assertFalse("requires something more", isvalid);
  }
  
}
