package org.danizen.solrconfig;

import org.apache.commons.cli.Options;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.ParseException;

import java.nio.file.Paths;
import java.nio.file.Files;
import java.nio.file.Path;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter;

public class CLI {
  
  private Config config = new Config();
  private Options options = createOptions(); 
  
  public static void main(String[] args) {
    CLI cli = new CLI();   
    cli.config.loadDefaults();
    
    CommandLine cmd = null;
    try {
      cmd = cli.parseOptions(args);
    } catch (ParseException e) {
      System.err.println(e.getMessage());
      System.err.println();
      cli.printHelp();
      System.exit(1);
    }

    if (!cli.validateOptions(cmd)) {
      cli.printHelp();
      System.exit(2);
    }   
    if (!cli.executeTest()) {
      System.exit(3);      
    }
  }
  
  private boolean executeTest() {
    // TODO Auto-generated method stub
    System.out.println("Test configuration in "+config.path+" using "+config.method);
    if (config.method == Config.Method.CLOUD) {
      System.out.println("SolrCloud zookeeper hosts: \""+config.zkhost+"\"");
      if (config.zkroot != null) {
        System.out.println("SolrCloud zookeeper chroot: \""+config.zkroot+"\"");
      }      
    }
    System.err.println("Actual configtest not yet implemented");
    return false;
  }

  private boolean validateOptions(CommandLine cmd) {

    if (cmd.hasOption("help")) {
      this.printHelp();
      System.exit(0);
      // NEVER REACHED
      return false;
    }
    
    if (cmd.hasOption("config")) {
      String configpath = cmd.getOptionValue("config");
      Path path = config.path = Paths.get(configpath);
      
      if (!Files.exists(path) || !Files.isDirectory(path)) {
        System.err.println("invalid option value: config");
        System.err.println(path+" is not an existing directory");
        System.err.println();
        return false;
      }
    }
    
    if (cmd.hasOption("use")) {
      try {
        config.method = Config.Method.valueOf(cmd.getOptionValue("use").toUpperCase());
      } catch (IllegalArgumentException e) {
        System.err.println("unsupported value for option: use must be set to cloud or embedded");
        System.err.println();
        return false;
      }
    }
    
    if (cmd.hasOption("zkhost")) {
      config.zkhost = cmd.getOptionValue("zkhost");
    }
    
    if (cmd.hasOption("zkroot")) {
      config.zkroot = cmd.getOptionValue("zkroot");     
    }
    
    if (config.method == Config.Method.CLOUD && config.zkhost == null) {
      System.err.println("When using cloud verification, zkhost is required");
      System.err.println();
      return false;
    }
    
    return true;
  }

  private CommandLine parseOptions(String[] args) throws ParseException {
    CommandLineParser parser = new DefaultParser();
    return parser.parse(options, args);
  }

  private void printHelp() {
    HelpFormatter helper = new HelpFormatter();
    helper.printHelp("solr-config-test", options);   
  }

  private static Options createOptions() {
    Options options = new Options();
    Option confpath = Option.builder()
        .longOpt("config")
        .hasArg()
        .argName("PATH")
        .desc("Path to configuration directory [required]")
        .build();
    options.addOption(confpath);
    
    Option cloud = Option.builder()
        .longOpt("use")
        .hasArg()
        .argName("cloud|embedded")
        .desc("how to test [default EmbeddedSolrServer]")
        .build();
    options.addOption(cloud);
       
    Option zkhost = Option.builder()
        .longOpt("zkhost")
        .hasArg()
        .argName("IPADDR:PORT...")
        .desc("Zookeeper host:port list [no default]")
        .build();
    options.addOption(zkhost);
    
    Option chroot = Option.builder()
        .longOpt("zkroot")
        .hasArg()
        .argName("PATH")
        .desc("Zookeeper chroot [no default]")
        .build();
    options.addOption(chroot);
    
    Option help = Option.builder()
        .longOpt("help")
        .desc("Display this message")
        .build();
    options.addOption(help);
    return options;
  }

}
