package org.danizen.solrconfig;

import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.apache.solr.client.solrj.SolrClient;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.request.CollectionAdminRequest;
import org.apache.solr.client.solrj.response.CollectionAdminResponse;

public class CleanUpTask {

  private static final Logger logger = LoggerFactory.getLogger(CleanUpTask.class);
  
  public static void removeConfigSet() {
    SolrConfig config = SolrConfig.getInstance();
    String znodePath = "/configs/"+config.getConfigName();
    try {
      config.getZkClient().clean(znodePath);
    } catch (Exception e) { 
      logger.warn("error during cleanup", e);
    }
  }
  
  public static void removeCollection() {
    SolrConfig config = SolrConfig.getInstance();
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
}

