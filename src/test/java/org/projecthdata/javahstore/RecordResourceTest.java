/*
 *    Copyright 2009 The MITRE Corporation
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */
package org.projecthdata.javahstore;

import java.io.IOException;
import java.util.Iterator;
import javax.xml.xpath.XPathExpressionException;
import org.w3c.dom.Document;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.test.framework.JerseyTest;
import java.io.ByteArrayInputStream;
import javax.ws.rs.core.MediaType;
import javax.xml.XMLConstants;
import javax.xml.namespace.NamespaceContext;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathFactory;
import org.junit.Test;
import static org.junit.Assert.*;
import org.xml.sax.SAXException;

/**
 * Record level tests
 * @author marc
 */
public class RecordResourceTest extends JerseyTest {

  public RecordResourceTest() throws Exception {
    super("org.projecthdata.javahstore.resources;org.projecthdata.javahstore.representations");
  }

  /**
   * Tests for the root section feed
   */
  @Test
  public void testRootFeed() throws ParserConfigurationException, SAXException, IOException, XPathExpressionException {
    WebResource webResource = resource();
    ClientResponse response = webResource.path("/12345").get(ClientResponse.class);
    String entity = checkResponse(response, 200, MediaType.APPLICATION_ATOM_XML_TYPE);
    System.out.println(entity);
    DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
    dbf.setNamespaceAware(true);
    DocumentBuilder db = dbf.newDocumentBuilder();
    Document doc = db.parse(new ByteArrayInputStream(entity.getBytes()));
    XPathFactory xpf = XPathFactory.newInstance();
    XPath xp = xpf.newXPath();
    xp.setNamespaceContext(new DummyNamespaceContext());
    String title = xp.evaluate("/atom:feed/atom:title", doc);
    assertEquals("FooExt", title);
    String sections = xp.evaluate("count(/atom:feed/atom:entry[atom:category/@term='http://example.com/hdata/ext1'])", doc);
    assertEquals("1", sections);
    String documents = xp.evaluate("count(/atom:feed/atom:entry[not(atom:category/@term='http://example.com/hdata/ext1')])", doc);
    assertEquals("1", documents);
    String author = xp.evaluate("/atom:feed/atom:entry[not(atom:category/@term='http://example.com/hdata/ext1')][1]/atom:content/hdm:DocumentMetaData/hdm:PedigreeInfo/hdm:Author", doc);
    assertEquals(DummySectionDocumentImpl.TEST_AUTHOR, author);
  }

  /**
   * Tests for the root.xml.
   */
  @Test
  public void testRootXML() throws ParserConfigurationException, SAXException, IOException, XPathExpressionException {
    WebResource webResource = resource();
    ClientResponse response = webResource.path("/12345/root.xml").get(ClientResponse.class);
    String entity = checkResponse(response, 200, MediaType.APPLICATION_XML_TYPE);
    //System.out.println(entity);
    DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
    dbf.setNamespaceAware(true);
    DocumentBuilder db = dbf.newDocumentBuilder();
    Document doc = db.parse(new ByteArrayInputStream(entity.getBytes()));
    XPathFactory xpf = XPathFactory.newInstance();
    XPath xp = xpf.newXPath();
    xp.setNamespaceContext(new DummyNamespaceContext());
    String version = xp.evaluate("/hd:root/hd:version", doc);
    assertEquals("1", version);
    String sections = xp.evaluate("count(/hd:root/hd:sections/hd:section)", doc);
    assertEquals("1", sections);
    String extensions = xp.evaluate("count(/hd:root/hd:extensions/hd:extension)", doc);
    assertEquals("2", extensions);
  }

  private String checkResponse(ClientResponse response, int expectedStatus,
          MediaType expectedMediaType) {
    assertEquals(expectedStatus, response.getStatus());
    if (expectedMediaType!=null)
      assertEquals(expectedMediaType, response.getType());
    return response.getEntity(String.class);
  }

  private static class DummyNamespaceContext implements NamespaceContext {

    @Override
    public String getNamespaceURI(String prefix) {
      if (prefix.equals("hd") || prefix.equals(XMLConstants.DEFAULT_NS_PREFIX))
        return "http://projecthdata.org/hdata/schemas/2009/06/core";
      else if (prefix.equals("hdm"))
        return "http://projecthdata.org/hdata/schemas/2009/11/metadata";
      else if (prefix.equals("atom"))
        return "http://www.w3.org/2005/Atom";
      else if (prefix.equals(XMLConstants.XML_NS_PREFIX))
        return XMLConstants.XML_NS_URI;
      else if (prefix.equals(XMLConstants.XMLNS_ATTRIBUTE))
        return XMLConstants.XMLNS_ATTRIBUTE_NS_URI;
      else
        return XMLConstants.NULL_NS_URI;
    }

    @Override
    public String getPrefix(String namespaceURI) {
      throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public Iterator getPrefixes(String namespaceURI) {
      throw new UnsupportedOperationException("Not supported yet.");
    }

  }

}
