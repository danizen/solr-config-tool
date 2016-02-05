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
    System.exit(0);
  }
  
  private boolean executeTest() {
    JUnitCore core = new JUnitCore();
    core.addListener(new ConsoleOutput());
    
    // TODO:
    //  - Can use org.apache.solr.util.SimplePostTool to make it work,
    //  - Benefit is that in guesses the document type...
    //  - Constraints us to use SolrCloud rather than EmbeddedSolrServer
    //  - Adds easier support for multiple file tests 
    //  - How to add indexing pipeline and stuff is less clear - asked online
    //  - Maybe sub-class SimplePostTool and add that in a protected method
    //   
    // TODO:
    //  - Allow user to add tests classes through some sort of test discovery
    //  - Again try to make the test discovery rely on JunitCore. 
    //
    Result result = core.run(
        ConfigDirExists.class,
        SchemaExists.class,
        SolrConfigExists.class,
        XmlFilesAreValid.class,
        // Tests tests assume SolrCloud
        CanUpConfig.class,
        CanCreateCollection.class,
        CanReloadCollection.class
    );
    if (config.getCleanUp()) {
      CleanUpTask.removeCollection();
      CleanUpTask.removeConfigSet();
    }
    return result.wasSuccessful();
  }

  public boolean validateOptions(CommandLine cmd) {


    if (cmd.hasOption("help")) {
      this.printHelp();
      System.exit(0);
      // NEVER REACHED
      return false;
    }

    if (cmd.hasOption("confdir")) {
      config.setPath(cmd.getOptionValue("confdir"));
    }
    
    if (cmd.hasOption("confname")) {
      config.setConfigName(cmd.getOptionValue("confname"));
    }
    
    if (cmd.hasOption("collection")) {
      config.setCollectionName(cmd.getOptionValue("collection"));
    }
    
    if (cmd.hasOption("solrurl")) {      
      config.setSolrURL(cmd.getOptionValue("solrurl"));
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
    
    if (cmd.hasOption("noclean")) {
      config.setCleanUp(false);
    }
    
    if (cmd.hasOption("reload")) {
      if (!cmd.hasOption("collection")) {
        System.err.println("When only reloading the collection, --collection is required");
        System.err.println();
        return false;
      }
      config.setReloadCollection(true);
    }
    
    if (config.getTestMethod() == TestMethod.CLOUD && config.getZkHost() == null) {
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
        .longOpt("confdir")
        .hasArg()
        .argName("PATH")
        .desc("Path to local directory [default is .]")
        .build();
    options.addOption(confdir);

    Option confname = Option.builder()
        .longOpt("confname")
        .hasArg()
        .argName("NAME")
        .desc("name of config uploaded [default random]")
        .build();
    options.addOption(confname);

    Option collection = Option.builder()
        .longOpt("collection")
        .hasArg()
        .argName("NAME")
        .desc("name of collection [default random]")
        .build();
    options.addOption(collection);
    
    Option solrurl = Option.builder()
        .longOpt("solrurl")
        .hasArg()
        .argName("URL")
        .desc("Use specific Solr URL [default ZooKeeper]")
        .build();
    options.addOption(solrurl);

    // EmbeddedSolrServer not supported 
    //Option cloud = Option.builder()
    //    .longOpt("use")
    //    .hasArg()
    //    .argName("cloud|embedded")
    //    .desc("how to test [default EmbeddedSolrServer]")
    //    .build();
    //options.addOption(cloud);
    
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
        .desc("Zookeeper chroot [default empty]")
        .build();
    options.addOption(chroot);
    
    Option xmlout = Option.builder()
        .longOpt("xmlout")
        .hasArg()
        .argName("PATH")
        .desc("Generate JUnit style output XML to PATH")
        .build();
    options.addOption(xmlout);
    
    Option reload = Option.builder()
        .longOpt("reload")
        .desc("Reload named collection, e.g. deploy")
        .build();
    options.addOption(reload);
    
    Option noclean = Option.builder()
    	.longOpt("noclean")
    	.desc("Do not clean-up SolrCloud")
    	.build();
    options.addOption(noclean);
    
    Option help = Option.builder()
        .longOpt("help")
        .desc("Display this message")
        .build();
    options.addOption(help);
    return options;
  }

}
