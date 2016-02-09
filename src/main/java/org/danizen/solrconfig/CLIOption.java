package org.danizen.solrconfig;

/**
 * These options affect how the Test plan is built and/or how results are formatted,
 * and do not need to be persisted/loaded into Java properties.  
 * 
 * @author davisda4
 */
public enum CLIOption {
  XMLDIR,
  NOCLEAN;
  
  public String getName() {
    return this.name().toLowerCase();
  }
}
