package org.danizen.solrconfig;

import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;

import org.apache.commons.lang3.StringUtils;
import org.apache.solr.client.solrj.SolrClient;
import org.apache.solr.client.solrj.impl.CloudSolrClient;

import org.apache.solr.common.cloud.OnReconnect;
import org.apache.solr.common.cloud.SolrZkClient;
import org.apache.solr.common.util.NamedList;
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
  private TestMethod method = TestMethod.CLOUD;
  private Path path = Paths.get(".");
  private String zkhost = null;
  private String zkroot = null;
  private Path xmloutpath = null;
  private String configName = null;
  private String collectionName = null;
  
  private SolrClient client = null;
  private SolrZkClient zkClient = null;
  
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
    if (this.client == null) {
      this.client = createSolrClient();
    }
    return this.client;
  }
  
  public SolrClient createSolrClient() {
    List<String> zkHostList = new ArrayList<String>(Arrays.asList(getZkHost().split(", *")));
    return new CloudSolrClient(zkHostList, getZkRoot());
  }

  public SolrZkClient getZkClient() {
    if (this.zkClient == null) {
      this.zkClient = createZkClient();
    }
    return this.zkClient;
  }

  public SolrZkClient createZkClient() {
    SolrZkClient zkClient = new SolrZkClient(getZkHost(), 30000, 30000,
      new OnReconnect() {
        public void command() { /* DO NOTHING */}
      }
    );
    return zkClient;
  }

  // but it does know how to load defaults from an properties file
  public void loadDefaults(InputStream defaults) throws IOException {
    Properties p = new Properties();
    p.load(defaults);
    String v = null;
    // TODO: no support as yet for EmbeddedSolrServer
    //if ((v = p.getProperty("method")) != null)
    //   this.method = TestMethod.valueOf(v.toUpperCase());
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
  
  private void formatResponseGuts(StringBuffer buf, NamedList<Object> list, int indent) {
    String spaces = StringUtils.repeat(" ",  indent);
    for (int i = 0; i < list.size(); i++) {
      String name = list.getName(i);
      Object obj = list.getVal(i);
      if (!(obj instanceof NamedList)) {
        buf.append(spaces+name+"("+obj.getClass().getName()+") = "+obj.toString()+"\n");
      } else {
        buf.append(spaces+name+": \n");
        formatResponseGuts(buf, (NamedList<Object>)obj, indent+2);
      }
    }
  }
  
  public String formatResponse(NamedList<Object> response) {
    StringBuffer buf = new StringBuffer();
    formatResponseGuts(buf, response, 0);
    return buf.toString();
  }

  public String getConfigName() {
    return configName;
  }

  public void setConfigName(String configName) {
    this.configName = configName;
  }

  public String getCollectionName() {
    return collectionName;
  }

  public void setCollectionName(String collectionName) {
    this.collectionName = collectionName;
  }  
}
