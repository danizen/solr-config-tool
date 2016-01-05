package org.danizen.solrconfig.tests;

import static org.junit.Assert.*;

import java.nio.file.Files;

import org.danizen.solrconfig.SolrConfig;
import org.junit.Test;


public class SchemaExists {
  private SolrConfig config = SolrConfig.getInstance();
  
  @Test
  public void test() {
    assertTrue(
        String.format("\"%s\" exists", config.getSchemaPath()),
        Files.exists(config.getSchemaPath()));
    assertTrue(
        String.format("\"%s\" is a file", config.getSchemaPath()),
        Files.isRegularFile(config.getSchemaPath()));
  }
}
