package org.danizen.solrconfig;

import java.util.LinkedList;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.xml.sax.ErrorHandler;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

import java.nio.file.Path;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.nio.file.FileVisitResult;

public class BaseTester {
  
  private Config config;
  private List<Issue> issues = new LinkedList<Issue>();
  
  public BaseTester(Config config) {
    this.config = config;
  }
  
  public void addIssue(Issue issue) {
    issues.add(issue);
  }
  
  public void addIssue(String description) {
    this.addIssue(new Issue(description));
  }
  
  public void addIssue(String description, String context) {
    this.addIssue(new Issue(description, context));
  }
  
  public void addIssue(String description, String context, Throwable throwable) {
    this.addIssue(new Issue(description, context, throwable));
  }
  
  public void test() {
    testLocal();
  }
  
  public void testLocal() {
    testRequiredFile("solrconfig.xml");
    testRequiredFile("schema.xml");
    
    try {
      Files.walkFileTree(config.path, new SimpleFileVisitor<Path>() {
        @Override
        public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
          if (file.endsWith(".xml"))
            testXmlFile(file);
          return FileVisitResult.CONTINUE;
        }
      });
    } catch (IOException e) {
      addIssue(e.getMessage(), null, e);
      e.printStackTrace();
    }
  }
  
  public void testRequiredFile(String relpath) {
    Path path = config.path.resolve(relpath);
    if (!Files.exists(path)) {
      addIssue(relpath+" is required");
    }
  }
  
  public Document testXmlFile(Path path) {    
    String context = path.relativize(config.path).toString();
    DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
    factory.setValidating(false);
    factory.setNamespaceAware(true);
    try {
      DocumentBuilder builder = factory.newDocumentBuilder();
      builder.setErrorHandler(new SAXErrorHandler(this, context));
      Document document = builder.parse(new InputSource(Files.newInputStream(path)));
      return document;
    } catch (ParserConfigurationException e) {
      addIssue(e.getMessage(), context, e);
    } catch (SAXException e) {
      addIssue(e.getMessage(), context, e);
    } catch (IOException e) {
      addIssue(e.getMessage(), context, e);
    }
    return null;
  }
  
  private class SAXErrorHandler implements ErrorHandler {
    
    BaseTester tester;
    String context;
    
    SAXErrorHandler(BaseTester tester, String context) {
      this.tester = tester;
      this.context = context;
    }
    
    public void warning(SAXParseException e) {
      // DO NOTHING
    }
    
    public void error(SAXParseException e) {
      this.tester.addIssue(e.getMessage(), this.context, e);
    }
    
    public void fatalError(SAXParseException e) {
      this.tester.addIssue(e.getMessage(), this.context, e);     
    }
  }
  
}
