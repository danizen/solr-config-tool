package org.danizen.solrconfig.tests;

import java.nio.file.Files;
import org.junit.Test;
import static org.junit.Assert.*;

import org.danizen.solrconfig.SolrConfig;


public class SolrConfigExists {
  private SolrConfig config = SolrConfig.getInstance();
  
  @Test
  public void test() {    
    assertTrue(
        String.format("\"%s\" exists", config.getSolrConfigPath()),
        Files.exists(config.getSolrConfigPath()));
    assertTrue(
        String.format("\"%s\" is a file", config.getSolrConfigPath()),
        Files.isRegularFile(config.getSolrConfigPath()));
  }
}
