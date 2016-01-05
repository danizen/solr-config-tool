package org.danizen.solrconfig.tests;

import static org.junit.Assert.*;
import static org.junit.Assume.*;
import static org.hamcrest.CoreMatchers.*;

import java.nio.file.Files;

import org.junit.Before;
import org.junit.Test;

import org.danizen.solrconfig.SolrConfig;
import org.danizen.solrconfig.TestMethod;

public class CanUpConfig {
  
  private SolrConfig config = SolrConfig.getInstance();

  @Before
  public void setUp() throws Exception {
    assumeTrue(Files.exists(config.getPath()));
    assumeTrue(Files.exists(config.getSolrConfigPath()));
    assumeTrue(Files.exists(config.getSchemaPath()));
    assumeThat(config.getTestMethod(), is(equalTo(TestMethod.CLOUD)));
  }

  @Test
  public void test() {
    fail("Not yet implemented");
  }

}
