package org.danizen.solrconfig;

public class EmbeddedSolrServerTester extends BaseTester {

  public EmbeddedSolrServerTester(Config config) {
    super(config);
  }

  @Override
  public void test() {
    testLocal();
  }
}
