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
  
  private Options options = createOptions();
  private TestController controller;
  
  public CLI() throws Exception {
    controller = new TestController();
  }
  
  public static void main(String[] args) throws Exception {
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
    
    if (!cli.validateOptions(cmd)) {
      cli.printHelp();
      System.exit(2);
    }   
    if (!cli.executeTest()) {
        System.exit(3);      
    }
    System.exit(0);
  }
  
  private boolean executeTest() throws Exception {
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

  public boolean validateOptions(CommandLine cmd) {

    if (cmd.hasOption(HELP.getName())) {
      this.printHelp();
      System.exit(0);
      // NEVER REACHED
      return false;
    }
    
    if (cmd.hasOption(XMLDIR.getName())) {
      controller.setXmlEnabled(true);
      controller.setOutputDir(cmd.getOptionValue(XMLDIR.getName()));
    }
    
    if (cmd.hasOption(NOCLEAN.getName())) {
      System.setProperty(CLEANUP.getPropertyName(), "false");
    }

    setPropertyOption(cmd, CONFDIR);
    setPropertyOption(cmd, CONFNAME);
    setPropertyOption(cmd, COLLECTION);
    setPropertyOption(cmd, SOLRURL);
    setPropertyOption(cmd, ZKHOST);
    setPropertyOption(cmd, ZKROOT);
    setPropertyOption(cmd, RELOAD);
    
    if (cmd.hasOption(RELOAD.getName())) {
      if (!cmd.hasOption(COLLECTION.getName())) {
        System.err.println("When only reloading the collection, --collection is required");
        System.err.println();
        return false;
      }
    }
    
    if (!cmd.hasOption(ZKHOST.getName())) {
      System.err.println("--zkhost is required unless provided by $HOME/.solrconfigtest");
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
    String[] moreHelp = {
        "",
        "$HOME/.solrconfigtest properties:",
        "    solrurl=<value> - see --solrurl above",
        "    zkhost=<value> - see --zkhost above",
        "    zkroot=<value> - see --zkroot above",
        "",
        "To test config set in current directory against localhost:",
        "",
        "    solr-config-test --zkhost 127.0.0.1:9983",
        "",
        "To deploy config set and reload collection:",
        "",
        "    solr-config-test --zkhost zoo.example.org:2181 \\",
        "                     --solrurl http://solr.example.org/solr/ \\",
        "                     --configname collection \\",
        "                     --collection collection \\",
        "                     --noclean",
        ""
    };
    for (String line : moreHelp) {
      System.out.println(line);
    }
  }

  private static Options createOptions() {
    Options options = new Options();
    Option confdir = Option.builder()
        .longOpt(CONFDIR.getName())
        .hasArg()
        .argName("PATH")
        .desc("Path to local directory [default is .]")
        .build();
    options.addOption(confdir);

    Option confname = Option.builder()
        .longOpt(CONFNAME.getName())
        .hasArg()
        .argName("NAME")
        .desc("name of config uploaded [default random]")
        .build();
    options.addOption(confname);

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
    
    Option reload = Option.builder()
        .longOpt(RELOAD.getName())
        .desc("Reload named collection, e.g. deploy")
        .build();
    options.addOption(reload);

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
    
    Option help = Option.builder()
        .longOpt(HELP.getName())
        .desc("Display this message")
        .build();
    options.addOption(help);
    
    return options;
  }

}
