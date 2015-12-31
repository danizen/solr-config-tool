package org.danizen.solrconfig;

public class SolrCloudTester extends BaseTester {

  public SolrCloudTester(Config config) {
    super(config);
  }

  @Override
  public void test() {
    testLocal();
  }
}
