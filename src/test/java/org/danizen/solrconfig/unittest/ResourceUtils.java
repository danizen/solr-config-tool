package org.danizen.solrconfig.unittest;

import java.net.URL;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ResourceUtils {
  private static final Logger logger = LoggerFactory.getLogger(ResourceUtils.class);
  
  public static String getResourceSubdir(String resourceName) {
    URL url = ResourceUtils.class.getResource(resourceName); 
    logger.info("the path is "+url.getPath());
    return url.getPath().substring(1);
  }

}
