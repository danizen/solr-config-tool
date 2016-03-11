package org.danizen.solrconfig.unittest;

import java.net.URL;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class ResourceUtils {
  private static final Logger logger = LoggerFactory.getLogger(ResourceUtils.class);
  
  private static final String OS = System.getProperty("os.name").toLowerCase();
  
  public static String getResourceSubdir(String resourceName) {
    URL url = ResourceUtils.class.getResource(resourceName); 
    logger.info("the path is "+url.getPath());    
    String path = url.getPath();
    if (OS.indexOf("win") >= 0)
    	path = path.substring(1);
    return path;
  }

}
