package org.danizen.solrconfig.tests;

import java.util.ArrayList;
import java.util.List;
import java.io.IOException;

import static org.junit.Assert.*;
import static org.junit.Assume.*;
import static org.hamcrest.CoreMatchers.*;

import java.nio.file.Files;

import org.junit.Before;
import org.junit.Test;

import org.apache.solr.client.solrj.SolrClient;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.request.CollectionAdminRequest;
import org.apache.solr.client.solrj.request.CollectionAdminRequest.Create;
import org.apache.solr.client.solrj.response.CollectionAdminResponse;
import org.apache.solr.common.cloud.ZkConfigManager;
import org.apache.solr.common.util.NamedList;
import org.apache.commons.lang3.RandomStringUtils;

import org.danizen.solrconfig.SolrConfig;
import org.danizen.solrconfig.CleanUpTask;
import org.danizen.solrconfig.TestMethod;

public class CanCreateCollection {

  private SolrConfig config = SolrConfig.getInstance();

  @Before
  public void setUp() throws Exception {
    assumeTrue(Files.exists(config.getPath()));
    assumeTrue(Files.exists(config.getSolrConfigPath()));
    assumeTrue(Files.exists(config.getSchemaPath()));
    assumeThat(config.getTestMethod(), is(equalTo(TestMethod.CLOUD)));
  }

  public String newCollectionName(SolrClient client) throws IOException, SolrServerException  {
    NamedList<Object> response = client.request(new CollectionAdminRequest.List());
    System.out.println(config.formatResponse(response));
    NamedList<Object> header = (NamedList<Object>) response.get("responseHeader");
    assertThat((Integer)header.get("status"), is(equalTo(0)));
    List<String> collections = (List<String>) response.get("collections");
    
    String newName = null;
    boolean matches = true;
    
    while (matches) {
      newName = RandomStringUtils.randomAlphabetic(8);
      matches = false;
      for (String collectionName : collections) {
        if (newName.equalsIgnoreCase(collectionName)) {
          matches = true;
          break;
        }
      }
    }
    return newName;
  }
  
  @Test
  public void test() throws IOException, SolrServerException {
    SolrClient client = config.getSolrClient();
    final String collectionName = newCollectionName(client);
    System.out.println("will create new collection "+collectionName);
    config.setCollectionName(collectionName);
    
    CollectionAdminRequest.Create request = new CollectionAdminRequest.Create();
    request.setConfigName(config.getConfigName());
    request.setCollectionName(collectionName);
    request.setNumShards(1);
    request.setReplicationFactor(1);
    
    CollectionAdminResponse response = new CollectionAdminResponse();
    response.setResponse(client.request(request));
    assertTrue(response.isSuccess());
  }
}
