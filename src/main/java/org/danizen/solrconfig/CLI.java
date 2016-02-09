package org.danizen.solrconfig;

import org.apache.commons.cli.Options;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.ParseException;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter;

import static org.danizen.solrconfig.SolrConfigOption.*;

import java.nio.file.Path;

import static org.danizen.solrconfig.CLIOption.*;


public class CLI {
  
  // namespace for commands, enum is too much
  private interface Command {
    static final String TEST = "test";
    static final String DEPLOY = "deploy";
    static final String HELP = "help";
  }
   
  private String command = null;
  private Options options = null;
  private TestController controller;
  
  public CLI() throws Exception {
    controller = new TestController();
  }
  
  public static void main(String[] args) throws Exception {
    CLI cli = new CLI();
    if (!cli.processOptions(args)) {
      System.exit(1);
    }
    if (cli.command.equals(Command.HELP)) {
      // printed during processOptions
      System.exit(0);
    }
    if (!cli.execute()) {
      System.exit(2);
    }
    System.exit(0);
  }
  
  public boolean processOptions(String[] args) throws Exception {    
    if (args.length == 0) {
      System.err.println("Command name required.");
      return false;
    }
    command = args[0];
    if (command.equals(Command.HELP)) {
      if (args.length == 2) {
        // command help
        String cmdhelp = args[1];
        if (cmdhelp.equals(Command.TEST)) {
          options = createTestOptions();
          printHelp();
          return true;
        } else if (cmdhelp.equals(Command.DEPLOY)) {
          options = createDeployOptions();
          printHelp();
          return true;
        } else {
          System.err.println("No such command: "+cmdhelp);
          return false;
        }
      } else {
        // general help
        printGeneralHelp();
        return true;
      }
    } else if (command.equals(Command.TEST)) {
      options = createTestOptions();
    } else if (command.equals(Command.DEPLOY)) {
      options = createDeployOptions();
    } else {
      System.err.println("No such command: "+command);
      return false;
    }
      
    if (!validateOptions(args)) {
      printHelp();
      return false;
    }
    return true;
  }
  
  private boolean execute() throws Exception {
    controller.execute();
    return true;
  }
  
  public void setPropertyOption(CommandLine cmd, SolrConfigOption opt) {
    if (cmd.hasOption(opt.getName())) {
      System.setProperty(opt.getPropertyName(), cmd.getOptionValue(opt.getName()));
    }
  }
  
  public Path getXmlDir() {
    return controller.getOutputDir();
  }

  public boolean validateOptions(String[] args) {    
    CommandLine cmd = null;
    try {
      CommandLineParser parser = new DefaultParser();
      cmd = parser.parse(options, args);
    } catch (ParseException e) {
      System.err.println(e.getMessage());
      System.err.println();
      return false;
    }

    if (command.equals(Command.DEPLOY)) {
      System.setProperty(RELOAD.getPropertyName(), "true");
      System.setProperty(CLEANUP.getPropertyName(), "false");
      
      // configuration name is required for deploy
      if (!cmd.hasOption(CONFNAME.getName())) {
        System.err.println("error: --confname required for deploy");
        return false;
      }

      // collecction name is required for deploy
      if (!cmd.hasOption(COLLECTION.getName())) {
        System.err.println("error: --collection required for deploy");
        return false;
      }      
    } else if (cmd.equals(Command.TEST)) {
      // option to avoid cleanup
      if (cmd.hasOption(NOCLEAN.getName())) {
        System.setProperty(CLEANUP.getPropertyName(), "false");
      }
    }
      
    setPropertyOption(cmd, CONFDIR);
    setPropertyOption(cmd, CONFNAME);
    setPropertyOption(cmd, COLLECTION);
    setPropertyOption(cmd, SOLRURL);
    setPropertyOption(cmd, ZKHOST);
    setPropertyOption(cmd, ZKROOT);

    if (cmd.hasOption(XMLDIR.getName())) {
      controller.setXmlEnabled(true);
      controller.setOutputDir(cmd.getOptionValue(XMLDIR.getName()));
    }    
    
    return true;
  }

  private static void printGeneralHelp() {
    String[] generalHelp = {
        "Usage: solr-config-test <command> <options>",
        "Commands:",
        "    test [options]",
        "    deploy",
        "    help",
        "",
        "For help on a particular command:",
        "    solr-config-test help <command>",
        "",
        "$HOME/.solrconfigtest properties:",
        "    solrurl=<value> - see --solrurl above",
        "    zkhost=<value> - see --zkhost above",
        "    zkroot=<value> - see --zkroot above",
        "",
        "To test config set in current directory against localhost:",
        "    solr-config-test test",
        "",
        "To test config set in solrconf sub-directory against SolrCloud running on 10.1.0.4:",
        "    solr-config-test test --confdir solrconf --zkhost 10.1.0.4:2181",
        "",
        "To deploy config set and reload collection against a particular URL:",
        "    solr-config-test deploy --zkhost 10.1.0.4:2181 \\",
        "                            --solrurl https://solr.example.org/solr/ \\",
        "                            --configname collection \\",
        "                            --collection collection \\",
        ""
    };
    for (String line : generalHelp) {
      System.out.println(line);
    }
  }
  
  private void printHelp() {
    HelpFormatter helper = new HelpFormatter();
    helper.printHelp("solr-config-test "+command, options);
  }

  private static Options createTestOptions() {
    Options options = new Options();
    Option confdir = Option.builder()
        .longOpt(CONFDIR.getName())
        .hasArg()
        .argName("PATH")
        .desc("Path to local directory [default is .]")
        .build();
    options.addOption(confdir);

    // confname is optional
    Option confname = Option.builder()
        .longOpt(CONFNAME.getName())
        .hasArg()
        .argName("NAME")
        .desc("name of config uploaded [default random]")
        .build();
    options.addOption(confname);

    // collection is optional
    Option collection = Option.builder()
        .longOpt(COLLECTION.getName())
        .hasArg()
        .argName("NAME")
        .desc("name of collection [default random]")
        .build();
    options.addOption(collection);
    
    Option solrurl = Option.builder()
        .longOpt(SOLRURL.getName())
        .hasArg()
        .argName("URL")
        .desc("Use specific Solr URL [default ZooKeeper]")
        .build();
    options.addOption(solrurl);

    Option zkhost = Option.builder()
        .longOpt(ZKHOST.getName())
        .hasArg()
        .argName("IPADDR:PORT...")
        .desc("Zookeeper host:port list [no default]")
        .build();
    options.addOption(zkhost);
    
    Option chroot = Option.builder()
        .longOpt(ZKROOT.getName())
        .hasArg()
        .argName("PATH")
        .desc("Zookeeper chroot [default empty]")
        .build();
    options.addOption(chroot);
    
    // values should be truthy
    Option noclean = Option.builder()
        .longOpt(NOCLEAN.getName())
        .desc("Do not clean-up SolrCloud")
        .build();
      options.addOption(noclean);
      
    Option outdir = Option.builder()
        .longOpt(XMLDIR.getName())
        .desc("Store results to directory")
        .hasArg()
        .argName("PATH")
        .build();
    options.addOption(outdir);
    
    return options;
  }

  private static Options createDeployOptions() {
    Options options = new Options();
    Option confdir = Option.builder()
        .longOpt(CONFDIR.getName())
        .hasArg()
        .argName("PATH")
        .desc("Path to local directory [default is .]")
        .build();
    options.addOption(confdir);

    // --confname is required
    Option confname = Option.builder()
        .longOpt(CONFNAME.getName())
        .hasArg()
        .argName("NAME")
        .required()
        .desc("name of config uploaded")
        .build();
    options.addOption(confname);

    // --collection is required
    Option collection = Option.builder()
        .longOpt(COLLECTION.getName())
        .hasArg()
        .argName("NAME")
        .desc("name of collection")
        .required()
        .build();
    options.addOption(collection);
    
    Option solrurl = Option.builder()
        .longOpt(SOLRURL.getName())
        .hasArg()
        .argName("URL")
        .desc("Use specific Solr URL [default ZooKeeper]")
        .build();
    options.addOption(solrurl);

    Option zkhost = Option.builder()
        .longOpt(ZKHOST.getName())
        .hasArg()
        .argName("IPADDR:PORT...")
        .desc("Zookeeper host:port list [no default]")
        .build();
    options.addOption(zkhost);
    
    Option chroot = Option.builder()
        .longOpt(ZKROOT.getName())
        .hasArg()
        .argName("PATH")
        .desc("Zookeeper chroot [default empty]")
        .build();
    options.addOption(chroot);
    
    Option outdir = Option.builder()
        .longOpt(XMLDIR.getName())
        .desc("Store results to directory")
        .hasArg()
        .argName("PATH")
        .build();
    options.addOption(outdir);
    
    return options;
  }

}
