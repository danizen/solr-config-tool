package org.danizen.solrconfig.tests;

import static org.junit.Assume.assumeFalse;
import static org.junit.Assume.assumeTrue;

import java.io.IOException;

import org.junit.Before;
import org.junit.Test;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.apache.solr.client.solrj.SolrClient;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.request.CollectionAdminRequest;
import org.apache.solr.client.solrj.response.CollectionAdminResponse;


import org.danizen.solrconfig.SolrConfig;

public class CleanUp {

  private static final Logger logger = LoggerFactory.getLogger(CleanUp.class);
  private SolrConfig config = SolrConfig.getInstance();

  
  @Before
  public void setUp() throws Exception {
    assumeTrue(config.getConfigName() != null);
    assumeTrue(config.getCollectionName() != null);
    assumeFalse(config.getReloadCollection());
  }
  
  public void removeConfigSet() {
    String znodePath = "/configs/"+config.getConfigName();
    try {
      config.getZkClient().clean(znodePath);
    } catch (Exception e) { 
      logger.warn("error during cleanup", e);
    }
  }
  
  public void removeCollection() {
    try {
      SolrClient client = config.getSolrClient();
      CollectionAdminRequest.Delete request = new CollectionAdminRequest.Delete();
      request.setCollectionName(config.getCollectionName());
      CollectionAdminResponse response = new CollectionAdminResponse();
      response.setResponse(client.request(request));		  		  
    } catch (IOException e) {
        logger.warn("error during cleanup", e);			
    } catch (SolrServerException e) {
        logger.warn("error during cleanup", e);			
		}	  
	}
  
  @Test
  public void test() {
    removeCollection();
    removeConfigSet();
  }
}
