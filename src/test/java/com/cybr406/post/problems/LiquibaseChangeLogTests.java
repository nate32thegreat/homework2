package com.cybr406.post.problems;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.env.Environment;
import org.springframework.core.io.ClassPathResource;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXParseException;

import javax.xml.XMLConstants;
import javax.xml.namespace.NamespaceContext;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathFactory;
import java.util.Iterator;

import static org.junit.Assert.*;

@RunWith(SpringRunner.class)
@ActiveProfiles("setup")
@SpringBootTest(classes = LiquibaseSetupApplication.class)
public class LiquibaseChangeLogTests {
  
  @Autowired
  Environment env;

  private Document loadChangeLog() throws Exception {
    String location = env.getProperty("spring.liquibase.change-log");
    assertNotNull("Change log location must be set.", location);
    assertEquals("classpath:db/changelog/db.changelog-master.xml", location);

    ClassPathResource resource = new ClassPathResource("db/changelog/db.changelog-master.xml");
    assertTrue("Add db.changelog-master.xml to your project.", resource.exists());

    DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
    factory.setNamespaceAware(true);
    DocumentBuilder builder = factory.newDocumentBuilder();
    
    try {
      return builder.parse(resource.getInputStream());  
    } catch (SAXParseException e) {
      throw new Exception("Your db.changelog-master.xml is not valid xml. " +
          "See the xml sample in the dbdemo project, or at https://www.liquibase.org/ " +
          "for a starting point.", e);
    }
  }
  
  private XPath documentXpath(Document document) {
    XPath xpath = XPathFactory.newInstance().newXPath();
    xpath.setNamespaceContext(new NamespaceContext() {
      public String getNamespaceURI(String prefix) {
        if (prefix == null) throw new NullPointerException("Null prefix");
        else if ("db".equals(prefix)) return "http://www.liquibase.org/xml/ns/dbchangelog";
        else if ("xml".equals(prefix)) return XMLConstants.XML_NS_URI;
        return XMLConstants.NULL_NS_URI;
      }

      // This method isn't necessary for XPath processing.
      public String getPrefix(String uri) {
        throw new UnsupportedOperationException();
      }

      // This method isn't necessary for XPath processing either.
      public Iterator getPrefixes(String uri) {
        throw new UnsupportedOperationException();
      }
    });
    return xpath;
  }
  
  @Test
  public void testDatabaseChangeLogRootElementExists() throws Exception {
    Document doc = loadChangeLog();
    NodeList elements = doc.getElementsByTagName("databaseChangeLog");
    assertNotNull("A root databaseChangeLog element must exist.", elements.item(0));

    Element root = (Element) elements.item(0);
    assertEquals(
        "databaseChangeLog must include an xmlns attribute. " +
            "See the xml sample in the dbdemo project, or at https://www.liquibase.org/ " +
            "for a starting point.",
        "http://www.liquibase.org/xml/ns/dbchangelog",
        root.getAttribute("xmlns"));
  }
  
  @Test
  public void testInitialChangeSetExists() throws Exception {
    Document document = loadChangeLog();
    XPath xpath = documentXpath(document);

    NodeList elements = (NodeList) xpath.evaluate(
        "/db:databaseChangeLog/db:changeSet",
        document.getDocumentElement(),
        XPathConstants.NODESET);
    
    assertTrue("A changeSet element must exist.", elements.getLength() > 0);
    
    Element changeSet = (Element) elements.item(0);
    assertEquals(
        "The first changeSet should have an id attribute set to 'initial-setup'.",
        "initial-setup",
        changeSet.getAttribute("id"));
    assertNotNull("The first changeSet should have an author attribute.", changeSet.getAttribute("author"));
  }
  
  @Test
  public void testInitialTableCreation() throws Exception {
    Document document = loadChangeLog();
    XPath xpath = documentXpath(document);
    
    NodeList nodes = (NodeList) xpath.evaluate(
        "/db:databaseChangeLog/db:changeSet[@id='initial-setup']",
        document.getDocumentElement(),
        XPathConstants.NODESET);
    
    assertEquals("A changeSet with id 'initial-setup' must exist.", 1, nodes.getLength());
    
    Element changeSet = (Element) nodes.item(0); 
    NodeList elements = changeSet.getElementsByTagName("createTable");
    assertEquals("A single createTable element should exist in the initial changeSet", 
        1,
        elements.getLength());
    
    Element createTable = (Element) elements.item(0);
    assertEquals("post", createTable.getAttribute("tableName"));
  }
  
  @Test
  public void testIdColumn() throws Exception {
    Document document = loadChangeLog();
    XPath xpath = documentXpath(document);

    NodeList nodes = (NodeList) xpath.evaluate(
        "/db:databaseChangeLog/db:changeSet[@id='initial-setup']/db:createTable[@tableName='post']/db:column[@name='id']",
        document.getDocumentElement(),
        XPathConstants.NODESET);

    assertEquals("A column named 'id' must exist.", 1, nodes.getLength());
    
    Element column = (Element) nodes.item(0);
    assertEquals("BIGINT", column.getAttribute("type"));
    assertEquals("true", column.getAttribute("autoIncrement"));
    
    nodes = column.getElementsByTagName("constraints");
    assertEquals("The id column must have a constraints element.", 1, nodes.getLength());
    
    Element constraint = (Element) nodes.item(0);
    assertEquals("true", constraint.getAttribute("primaryKey"));
  }

  @Test
  public void testAuthorColumn() throws Exception {
    Document document = loadChangeLog();
    XPath xpath = documentXpath(document);

    NodeList nodes = (NodeList) xpath.evaluate(
        "/db:databaseChangeLog/db:changeSet[@id='initial-setup']/db:createTable[@tableName='post']/db:column[@name='author']",
        document.getDocumentElement(),
        XPathConstants.NODESET);

    assertEquals("A column named 'author' must exist.", 1, nodes.getLength());

    Element column = (Element) nodes.item(0);
    assertEquals("VARCHAR(255)", column.getAttribute("type"));

    nodes = column.getElementsByTagName("constraints");
    assertEquals("The 'author' column must have a constraints element.", 1, nodes.getLength());

    Element constraint = (Element) nodes.item(0);
    assertEquals("false", constraint.getAttribute("nullable"));
  }

  @Test
  public void testContentColumn() throws Exception {
    Document document = loadChangeLog();
    XPath xpath = documentXpath(document);

    NodeList nodes = (NodeList) xpath.evaluate(
        "/db:databaseChangeLog/db:changeSet[@id='initial-setup']/db:createTable[@tableName='post']/db:column[@name='content']",
        document.getDocumentElement(),
        XPathConstants.NODESET);

    assertEquals("A column named 'content' must exist.", 1, nodes.getLength());

    Element column = (Element) nodes.item(0);
    assertEquals("CLOB", column.getAttribute("type"));

    nodes = column.getElementsByTagName("constraints");
    assertEquals("The 'content' column must have a constraints element.", 1, nodes.getLength());

    Element constraint = (Element) nodes.item(0);
    assertEquals("false", constraint.getAttribute("nullable"));
  }
  
}
