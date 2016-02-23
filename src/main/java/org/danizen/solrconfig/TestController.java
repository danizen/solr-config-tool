package org.danizen.solrconfig;

import java.nio.file.Paths;
import java.nio.file.Files;
import java.nio.file.Path;

import org.apache.tools.ant.Project;
import org.apache.tools.ant.taskdefs.optional.junit.FormatterElement;
import org.apache.tools.ant.taskdefs.optional.junit.JUnitTask;
import org.apache.tools.ant.taskdefs.optional.junit.JUnitTask.ForkMode;
import org.apache.tools.ant.taskdefs.optional.junit.JUnitTest;
import org.danizen.solrconfig.tests.CanCreateCollection;
import org.danizen.solrconfig.tests.CanReloadCollection;
import org.danizen.solrconfig.tests.CanUpConfig;
import org.danizen.solrconfig.tests.CleanUp;
import org.danizen.solrconfig.tests.ConfigDirExists;
import org.danizen.solrconfig.tests.SchemaExists;
import org.danizen.solrconfig.tests.SolrConfigExists;
import org.danizen.solrconfig.tests.XmlFilesAreValid;

public class TestController {
  
  // the fundamental test runner and formatter
  private JUnitTask task;
  private Path outputDir;
  private boolean xmlEnabled = false;
  
  // tests to run
  public static final Class<?>[] testclasses = new Class[] {
      // These basic tests run locally
      ConfigDirExists.class,
      SchemaExists.class,
      SolrConfigExists.class,
      XmlFilesAreValid.class,
      // This test interacts with SolrCloud
      CanUpConfig.class,
  };
  
  public TestController() throws Exception {
    task = new JUnitTask();
    task.setProject(new Project());
    task.setFork(false);
    task.setShowOutput(true);
    task.setOutputToFormatters(true);
  }
  
  public void addTestCase(Class<?> clz) {
    JUnitTest test = new JUnitTest(clz.getName());
    if (outputDir != null)
      test.setTodir(outputDir.toFile());
    task.addTest(test);    
  }
    
  public void execute() throws Exception {

    // configure XML output
    if (isXmlEnabled()) {      
      FormatterElement xml = new FormatterElement();
      xml.setClassname(FormatterElement.XML_FORMATTER_CLASS_NAME);
      task.addFormatter(xml);
    }

    // Add tests to task
    SolrConfig config = SolrConfig.getInstance();
    
    for (int i = 0; i < testclasses.length; i++) {
      addTestCase(testclasses[i]);
    }
       
    if (config.getReloadCollection())
      addTestCase(CanReloadCollection.class);
    else
      addTestCase(CanCreateCollection.class);     
    if (config.isCleanupEnabled())
      addTestCase(CleanUp.class);

    // Create output directory if needed
    if (outputDir != null && Files.notExists(outputDir)) {
      Files.createDirectory(outputDir);
    }

    task.execute();
  }
  
  public void setOutputDir(String outputDir) {
    setOutputDir(Paths.get(outputDir));   
  }
  
  public void setOutputDir(Path outputDir) {
    this.outputDir = outputDir;
  }
  
  public Path getOutputDir() {
    return this.outputDir;
  }

  public boolean isXmlEnabled() {
    return xmlEnabled;
  }

  public void setXmlEnabled(boolean xmlEnabled) {
    this.xmlEnabled = xmlEnabled;
  }
}
