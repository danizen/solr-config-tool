package org.danizen.solrconfig;

import java.nio.file.Paths;
import java.util.Properties;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class Config {
  
  // enum specifies the valid methods for the test 
  public enum Method { CLOUD, EMBEDDED };

  // attributes - more like a struct than a POJO
  public Method method = Method.EMBEDDED;
  public Path path = Paths.get(".");
  public String zkhost = null;
  public String zkroot = null;

  // but it does know how to load defaults from an properties file
  public void loadDefaults(Path defaults) {
    Properties p = new Properties();
    try {
      p.load(Files.newInputStream(defaults));
    } catch (IOException e) {
      System.err.println("Invalid format or I/O error reading "+defaults);
    }
    String v = null;
    if ((v = p.getProperty("method")) != null)
      this.method = Config.Method.valueOf(v.toUpperCase());
    if ((v = p.getProperty("zkhost")) != null)
      this.zkhost = v;
    if ((v = p.getProperty("zkroot")) != null)
      this.zkroot = v;
  }
  
  // and it knows a canonical path to that
  public void loadDefaults() {
    Path userhome = Paths.get(System.getProperty("user.home"));
    Path defaultConfigFile = userhome.resolve(".solrconfigtest");
    System.out.println("default config file is \""+defaultConfigFile+"\"");
    if (Files.exists(defaultConfigFile)) {
      this.loadDefaults(defaultConfigFile);
    }
  }
}
