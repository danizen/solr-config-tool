package org.danizen.solrconfig;

public class Issue {
  
  private String context;
  private String desc;
  private Throwable thrown;

  public Issue(String description, String context, Throwable throwable) {
    this.desc = description;
    this.context = context;
    this.thrown = throwable;
  }

  public Issue(String description, String context) {
    this(description, context, null);
  }
  
  public Issue(String description) {
    this(description, null, null);
  }
  
  public String getDescription() {
    return desc;
  }

  public void setDescription(String desc) {
    this.desc = desc;
  }

  public Throwable getThrowable() {
    return thrown;
  }

  public void setThrowable(Throwable thrown) {
    this.thrown = thrown;
  }

  public String getContext() {
    return context;
  }

  public void setContext(String context) {
    this.context = context;
  }

}
