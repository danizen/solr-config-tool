package org.danizen.solrconfig.tests;

import java.util.List;
import java.io.IOException;

import static org.junit.Assert.*;
import static org.junit.Assume.*;
import static org.hamcrest.CoreMatchers.*;

import java.nio.file.Files;

import org.junit.Before;
import org.junit.Test;

import org.apache.solr.common.cloud.SolrZkClient;
import org.apache.solr.common.cloud.ZkConfigManager;
import org.apache.solr.common.cloud.ZkStateReader;
import org.apache.commons.lang3.RandomStringUtils;

import org.danizen.solrconfig.SolrConfig;

public class CanUpConfig {
  
  private SolrConfig config = SolrConfig.getInstance();

  @Before
  public void setUp() throws Exception {
    assumeTrue(Files.exists(config.getPath()));
    assumeTrue(Files.exists(config.getSolrConfigPath()));
    assumeTrue(Files.exists(config.getSchemaPath()));
  }

  public String newConfigName(ZkConfigManager configManager) throws IOException {
    List<String> configSets = configManager.listConfigs();
    String newConfigName = null;
    boolean matches = true;
    while (matches) {
      newConfigName = RandomStringUtils.randomAlphabetic(8);
      matches = false;
      for (String configName : configSets) {
        if (newConfigName.equalsIgnoreCase(configName)) {
          matches = true;
          break;
        }
      }
    }
    return newConfigName;
  }

  @Test
  public void test() throws IOException {
    SolrZkClient zkClient = config.getZkClient();
    ZkConfigManager configManager = new ZkConfigManager(zkClient);
    String confName = config.getConfigName();
    if (confName == null) {
      confName = newConfigName(configManager);
      config.setConfigName(confName);
    }
    configManager.uploadConfigDir(config.getPath(), confName);
  }

}
