package org.danizen.solrconfig;

import java.util.List;
import java.io.IOException;
import java.util.LinkedList;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.apache.solr.client.solrj.SolrClient;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.request.CollectionAdminRequest;
import org.apache.solr.client.solrj.response.CollectionAdminResponse;
import org.apache.zookeeper.KeeperException;

public class CleanUpTask {

  private static List<CleanUpTask> tasks = new LinkedList<CleanUpTask>();
  private static final Logger logger = LoggerFactory.getLogger(CleanUpTask.class);
  
  public void run(SolrConfig config) {
    // DO NOTHING
  }

  public static void add(CleanUpTask task) {
    tasks.add(0, task);
  }

  public static void addRemoveConfigSet() {
    add(new CleanUpTask() {
      public void run(SolrConfig config) {
        try {
          String znodePath = "/configs/"+config.getConfigName();
          config.getZkClient().clean(znodePath);
        } catch (Exception e) { 
          logger.warn("error during cleanup", e);
        }
      }
    });
  }
  
  public static void addRemoveCollection() {
	add(new CleanUpTask() {
	  public void run(SolrConfig config) {
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
	});
  }

  public static void runAll() {
    SolrConfig config = SolrConfig.getInstance();
    if (config.getCleanUp()) {
      for (CleanUpTask task : tasks) {
        task.run(config);
      }
    }
  }
}

