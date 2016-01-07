package org.danizen.solrconfig;

import java.nio.file.Paths;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.ParseException;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter;
import org.junit.runner.JUnitCore;
import org.junit.runner.Result;

import org.danizen.solrconfig.tests.*;


public class CLI {
  
  private SolrConfig config;
  private Options options = createOptions();
  
  public CLI() {
    this.config = SolrConfig.getInstance();
  }
  
  public static void main(String[] args) {
    CLI cli = new CLI();   
    
    CommandLine cmd = null;
    try {
      cmd = cli.parseOptions(args);
    } catch (ParseException e) {
      System.err.println(e.getMessage());
      System.err.println();
      cli.printHelp();
      System.exit(1);
    }
    
    // load defaults - never done in unit tests of option parser
    cli.config.loadDefaults();
    
    if (!cli.validateOptions(cmd)) {
      cli.printHelp();
      System.exit(2);
    }   
    if (!cli.executeTest()) {
      System.exit(3);      
    }
  }
  
  private boolean executeTest() {
    JUnitCore core = new JUnitCore();
    core.addListener(new ConsoleOutput());
    Result result = core.run(
        ConfigDirExists.class,
        SchemaExists.class,
        SolrConfigExists.class,
        XmlFilesAreValid.class,
        // Tests tests assume SolrCloud
        CanUpConfig.class);
    CleanUpTask.runAll();
    return result.wasSuccessful();
  }

  public boolean validateOptions(CommandLine cmd) {


    if (cmd.hasOption("help")) {
      this.printHelp();
      System.exit(0);
      // NEVER REACHED
      return false;
    }

    if (cmd.hasOption("config")) {
      config.setPath(cmd.getOptionValue("config"));
    }
    
    if (cmd.hasOption("use")) {
      try {
        config.setTestMethod(cmd.getOptionValue("use"));
      } catch (IllegalArgumentException e) {
        System.err.println("unsupported value for option: use must be set to cloud or embedded");
        System.err.println();
        return false;
      }
    }
    
    if (cmd.hasOption("xmlout")) {
      config.setXmlOutPath(Paths.get(cmd.getOptionValue("xmlout")));          
    }
    
    if (cmd.hasOption("zkhost")) {
      config.setZkHost(cmd.getOptionValue("zkhost"));
    }
    
    if (cmd.hasOption("zkroot")) {
      config.setZkRoot(cmd.getOptionValue("zkroot"));     
    }
    
    if (config.getTestMethod() == TestMethod.CLOUD && config.getZkHost() == null) {
      System.err.println("When using cloud verification, zkhost is required");
      System.err.println();
      return false;
    }
    
    return true;
  }

  public CommandLine parseOptions(String[] args) throws ParseException {
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
    
    Option xmlout = Option.builder()
        .longOpt("xmlout")
        .hasArg()
        .argName("PATH")
        .desc("Generate JUnit style output XML to PATH")
        .build();
    options.addOption(xmlout);
    
    Option help = Option.builder()
        .longOpt("help")
        .desc("Display this message")
        .build();
    options.addOption(help);
    return options;
  }

}
