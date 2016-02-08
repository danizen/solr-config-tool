package org.danizen.solrconfig;

import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.apache.solr.client.solrj.SolrClient;
import org.apache.solr.client.solrj.impl.CloudSolrClient;
import org.apache.solr.client.solrj.impl.HttpSolrClient;
import org.apache.solr.common.cloud.OnReconnect;
import org.apache.solr.common.cloud.SolrZkClient;
import org.apache.solr.common.util.NamedList;

import java.nio.file.Path;

import static org.danizen.solrconfig.SolrConfigOption.*;

public class SolrConfig {
  
  // constructor is private so no one else can create it
  private SolrConfig() {
  }
  
  // there's a single instance of this class
  private static SolrConfig instance = new SolrConfig();
  
  // and a public method allows access to this method 
  public static SolrConfig getInstance() {
    return instance;
  }
  
  // Is this too subtle? - it should be as clear as "Autowired" Spring notation, right...?
  private Path path = Paths.get(System.getProperty(CONFDIR.getPropertyName(), "."));
  private String zkhost = System.getProperty(ZKHOST.getPropertyName(), "127.0.0.1:9983");
  private String zkroot = System.getProperty(ZKROOT.getPropertyName(), null);
  private String configName = System.getProperty(CONFNAME.getPropertyName(), null);
  private String collectionName = System.getProperty(COLLECTION.getPropertyName(), null);
  private Boolean cleanup = Boolean.valueOf(System.getProperty(CLEANUP.getPropertyName(), "true"));
  private Boolean reloadCollection = Boolean.valueOf(System.getProperty(RELOAD.getPropertyName(), "false"));
  private String solrurl = System.getProperty(SOLRURL.getPropertyName(), null);
  
  private SolrClient client = null;
  private SolrZkClient zkClient = null;
  
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
    if (getSolrURL() == null) {
      List<String> zkHostList = new ArrayList<String>(Arrays.asList(getZkHost().split(", *")));
      return new CloudSolrClient(zkHostList, getZkRoot());
    } else {
      return new HttpSolrClient(getSolrURL());
    }
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

  public boolean isCleanupEnabled() {
    return cleanup;
  }
	
  public void setCleanupEnabled(boolean cleanup) {
    this.cleanup = cleanup;
  }

  public String getSolrURL() {
    return solrurl;
  }

  public void setSolrURL(String solrurl) {
    this.solrurl = solrurl;
  }

  public boolean getReloadCollection() {
    return reloadCollection;
  }

  public void setReloadCollection(boolean reloadCollection) {
    this.reloadCollection = reloadCollection;
  }

}
