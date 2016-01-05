package org.danizen.solrconfig.tests;

import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.PathMatcher;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.LinkedList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.junit.Test;
import org.xml.sax.ErrorHandler;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

import static org.junit.Assert.*;

import org.danizen.solrconfig.SolrConfig;


public class XmlFilesAreValid {
  
  private static final Logger logger = LoggerFactory.getLogger(XmlFilesAreValid.class); 
  private SolrConfig config = SolrConfig.getInstance();
  private List<Exception> issues = new LinkedList<Exception>();
  
  @Test
  public void test() throws Exception {   
    Files.walkFileTree(config.getPath(), new SimpleFileVisitor<Path>() {
      @Override
      public FileVisitResult visitFile(Path path, BasicFileAttributes attrs) {        
        String fileName = path.getFileName().toString();
        if (fileName.endsWith(".xml")) {
          try {
            testXmlFile(path);
            logger.info(fileName+" syntax OK");
          } catch (ParserConfigurationException e) {
            issues.add(e);
            logger.error(fileName+": "+e);
          } catch (SAXException e) {
            issues.add(new SAXException(fileName+": "+e));
            logger.error(fileName+": "+e);
          } catch (IOException e) {
            issues.add(new IOException(fileName+": "+e));
            logger.error(fileName+": "+e);
          }
        }
        return FileVisitResult.CONTINUE;
      }
    });
    
    if (!issues.isEmpty()) {
      Exception e = issues.get(0);
      throw e;
    }
  }

  public void testXmlFile(Path path) throws ParserConfigurationException, SAXException, IOException {    
    
    DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
    factory.setValidating(false);
    factory.setNamespaceAware(true);
    DocumentBuilder builder = factory.newDocumentBuilder();
    builder.parse(new InputSource(Files.newInputStream(path)));
  }
}
