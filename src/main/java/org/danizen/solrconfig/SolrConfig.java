package org.danizen.solrconfig;

import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;

import org.apache.solr.client.solrj.SolrClient;
import org.apache.solr.client.solrj.impl.CloudSolrClient;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;

public class SolrConfig {
  
  private static final Logger logger = LoggerFactory.getLogger(SolrConfig.class);
  
  // constructor is private so no one else can create it
  private SolrConfig() {
  }
  
  // there's a single instance of this class
  private static SolrConfig instance = new SolrConfig();
  
  // and a public method allows access to this method 
  public static SolrConfig getInstance() {
    return instance;
  }
  
  // mostly for testing in a single JVM
  public static void newInstance() {
    instance = new SolrConfig();
  }
  
  // the attributes are more like a struct than a POJO
  private TestMethod method = TestMethod.EMBEDDED;
  private Path path = Paths.get(".");
  private String zkhost = null;
  private String zkroot = null;
  private Path xmloutpath = null;
  
  private boolean initClient = true;
  private SolrClient client = null;
  
  public TestMethod getTestMethod() {
    return method;
  }

  public void setTestMethod(TestMethod method) {
    this.method = method;
  }
  
  public void setTestMethod(String method) {
    this.method = TestMethod.valueOf(method.toUpperCase());
  }

  public Path getPath() {
    return path;
  }
  
  public void setPath(Path path) {
    this.path = path;
  }
  
  public void setPath(String path) {
    this.path = Paths.get(path);
  }

  public String getZkHost() {
    return zkhost;
  }
  
  public void setZkHost(String zkhost) {
    this.zkhost = zkhost;
  }

  public String getZkRoot() {
    return zkroot;
  }

  public void setZkRoot(String zkroot) {
    this.zkroot = zkroot;
  }

  public Path getXmlOutPath() {
    return xmloutpath;
  }

  public void setXmlOutPath(Path xmloutpath) {
    this.xmloutpath = xmloutpath;
  }

  public Path getSolrConfigPath() {
    return path.resolve("solrconfig.xml");
  }
  
  public Path getSchemaPath() {
    return path.resolve("schema.xml");
  }
  
  public SolrClient getSolrClient() {
    if (this.initClient) {
      this.initClient = false;
      this.client = createSolrClient();
    }
    return this.client;
  }
  
  public SolrClient createSolrClient() {
    List<String> zkHostList = new ArrayList<String>(Arrays.asList(getZkHost().split(", *")));
    return new CloudSolrClient(zkHostList, getZkRoot());
  }
  
  // but it does know how to load defaults from an properties file
  public void loadDefaults(InputStream defaults) throws IOException {
    Properties p = new Properties();
    p.load(defaults);
    String v = null;
    if ((v = p.getProperty("method")) != null)
      this.method = TestMethod.valueOf(v.toUpperCase());
    if ((v = p.getProperty("zkhost")) != null)
      this.zkhost = v;
    if ((v = p.getProperty("zkroot")) != null)
      this.zkroot = v;
  }
  
  // and it knows a canonical path to that
  public void loadDefaults() {
    Path userhome = Paths.get(System.getProperty("user.home"));
    Path defaultConfigFile = userhome.resolve(".solrconfigtest");    
    if (Files.exists(defaultConfigFile)) {
      try {
        this.loadDefaults(Files.newInputStream(defaultConfigFile));
      } catch (IOException e) {
        logger.error("invalid format or I/O error reading "+defaultConfigFile);
      }
    }
  }
}
