package org.danizen.solrconfig.tests;

import java.nio.file.Files;
import java.nio.file.Path;

import org.junit.Test;
import static org.junit.Assert.*;

import org.danizen.solrconfig.SolrConfig;


public class ConfigDirExists {
  private SolrConfig config = SolrConfig.getInstance();
  
  @Test
  public void test() {
    Path path = config.getPath();
    assertTrue(
        String.format("\"%s\" exists", path), 
        Files.exists(path));
    assertTrue(
        String.format("\"%s\" is a directory", path),
        Files.isDirectory(path));
  }
}
