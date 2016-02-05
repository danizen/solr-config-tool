package org.danizen.solrconfig.tests;

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
import org.apache.solr.client.solrj.response.CollectionAdminResponse;

import org.danizen.solrconfig.SolrConfig;
import org.danizen.solrconfig.TestMethod;

public class CanReloadCollection {
  private SolrConfig config = SolrConfig.getInstance();

  @Before
  public void setUp() throws Exception {
    assumeTrue(Files.exists(config.getPath()));
    assumeTrue(Files.exists(config.getSolrConfigPath()));
    assumeTrue(Files.exists(config.getSchemaPath()));
    assumeThat(config.getTestMethod(), is(equalTo(TestMethod.CLOUD)));
    assumeTrue(config.getReloadCollection());
  }
 
  @Test
  public void test() throws IOException, SolrServerException {
    SolrClient client = config.getSolrClient();
    String collectionName = config.getCollectionName();
    assertNotNull(collectionName);
    
    CollectionAdminResponse response = new CollectionAdminResponse();
    CollectionAdminRequest.Reload request = new CollectionAdminRequest.Reload();
    request.setCollectionName(collectionName);
    response.setResponse(client.request(request));
    assertTrue(response.isSuccess());
  }
}
