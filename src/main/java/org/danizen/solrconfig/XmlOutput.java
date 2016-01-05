package org.danizen.solrconfig;

import java.nio.file.Path;

import org.junit.runner.notification.RunListener;

public class XmlOutput extends RunListener {
  
  private Path path = null;
  
  public XmlOutput(Path path) {
    this.path = path;
  }

}
