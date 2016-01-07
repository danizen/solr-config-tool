package org.danizen.solrconfig;

import java.util.List;
import java.util.LinkedList;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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

  public static void cleanZnode(final String znodePath) {
    add(new CleanUpTask() {
      public void run(SolrConfig config) {
        try {
          config.getZkClient().clean(znodePath);
        } catch (Exception e) { 
          logger.warn("error during cleanup", e);
        }
      }
    });
  }

  public static void runAll() {
    SolrConfig config = SolrConfig.getInstance();
    for (CleanUpTask task : tasks) {
      task.run(config);
    } 
  }
}

