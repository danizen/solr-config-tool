package org.danizen.solrconfig;

import org.junit.runner.Description;
import org.junit.runner.Result;
import org.junit.runner.notification.Failure;
import org.junit.runner.notification.RunListener;


public class ConsoleOutput extends RunListener {
  
  private boolean failed;

  public void testRunStarted(Description desc) {
    SolrConfig config = SolrConfig.getInstance();
    System.out.println("Testing solr configuration in "+config.getPath());
    System.out.println();    
  }
 
  public void testStarted(Description desc) {
    this.failed = false;
  }

  public void testFinished(Description desc) {
    if (!failed) {
      System.out.println(String.format("%-60s [PASS]", desc.getDisplayName()));
    }
  }
  
  public void testFailure(Failure failure) {
    failed = true;
    Description desc = failure.getDescription();
    System.out.println(String.format("%-60s [FAIL]", desc.getDisplayName()));   
  }
  
  public void testAssumptionFailure(Failure failure) {
    failed = true;
    Description desc = failure.getDescription();
    System.out.println(String.format("%-60s [SKIP]", desc.getDisplayName()));      
  }
  
  public void testRunFinished(Result result) {
    System.out.println();
    System.out.println(String.format("Completed in %d milliseconds.", result.getRunTime()));
    System.out.println(String.format("%d out of %d tests failed.", result.getFailureCount(), result.getRunCount()));
  }
  
}
