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
package org.projecthdata.javahstore.hdr;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.Iterator;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.XMLConstants;
import javax.xml.namespace.NamespaceContext;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.dom.DOMSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import javax.xml.validation.Validator;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathFactory;
import org.w3c.dom.Document;
import org.w3c.dom.ls.LSInput;
import org.w3c.dom.ls.LSResourceResolver;
import org.xml.sax.ErrorHandler;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

/**
 * Helper class for working with document metadata
 * @author marc
 */
public class DocumentMetadata {
  private String xml;
  private Document doc;
  private static XPathFactory xpf = XPathFactory.newInstance();
  private static NamespaceContext nsc = new HDataMetadataContext();
  private static DocumentBuilderFactory dbf;
  private static Schema s;
  private String author;

  /**
   * Initialize from a String that contains the XML serialization of the
   * document metadata.
   * @param xml
   * @throws ParserConfigurationException
   * @throws SAXException
   * @throws IOException
   */
  public DocumentMetadata(String xml) throws ParserConfigurationException,
          SAXException, IOException {
    this.xml = xml;
    doc = getDocumentBuilder().parse(new ByteArrayInputStream(xml.getBytes()));
    SchemaFactory sf = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
    sf.setResourceResolver(new SchemaImportResolver());
    Validator v = null;
    try {
      v = getValidator();
      v.setErrorHandler(new ErrorHandler() {
        @Override
        public void warning(SAXParseException exception) throws SAXException {
          throw exception;
        }

        @Override
        public void error(SAXParseException exception) throws SAXException {
          throw exception;
        }

        @Override
        public void fatalError(SAXParseException exception) throws SAXException {
          throw exception;
        }
      });
    } catch (SAXException ex) {
      Logger.getLogger(DocumentMetadata.class.getName()).log(Level.WARNING,
              "Failed to parse section metadata schema - schema validation disabled", ex);
    }
    try {
      if (v!=null)
        v.validate(new DOMSource(doc));
    } catch (SAXException ex) {
      throw new SAXException(ex.getMessage());
    }
  }

  private static synchronized Validator getValidator() throws SAXException {
    if (s==null) {
      SchemaFactory sf = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
      sf.setResourceResolver(new SchemaImportResolver());
      s = sf.newSchema(DocumentMetadata.class.getResource(ResourceResolver.SCHEMA_PATH+"section_metadata.xsd"));
    }
    return s.newValidator();
  }

  /**
   * Get the XML form of the metadata
   * @return
   */
  public String getXml() {
    return xml;
  }

  /**
   * Extract the author from the metadata
   * @return
   */
  public String getAuthor() {
    if (author != null)
      return author;
    try {
      XPath xp = xpf.newXPath();
      xp.setNamespaceContext(nsc);
      author = xp.evaluate("hd:DocumentMetaData/hd:PedigreeInfo/hd:Author", doc);
      return author;
    } catch (Exception ex) {
      Logger.getLogger(DocumentMetadata.class.getName()).log(Level.WARNING,
              "Error extracting author from metadata", ex);
    }
    return null;
  }

  private static DocumentBuilder getDocumentBuilder() throws ParserConfigurationException {
    if (dbf==null) {
      dbf = DocumentBuilderFactory.newInstance();
      dbf.setNamespaceAware(true);
    }
    return dbf.newDocumentBuilder();
  }

  private static class HDataMetadataContext implements NamespaceContext {

    @Override
    public String getNamespaceURI(String prefix) {
      if (prefix.equals("hd") || prefix.equals(XMLConstants.DEFAULT_NS_PREFIX))
        return "http://projecthdata.org/hdata/schemas/2009/11/metadata";
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

  /**
   * A LSResourceResolver that redirects the XML DSIG import to a local file.
   * This could probably be reworked to use a catalog resolver instead with an
   * additional dependency on Xerces.
   */
  private static class SchemaImportResolver implements LSResourceResolver {

    @Override
    public LSInput resolveResource(String type, String namespaceURI, String publicId, String systemId, final String baseURI) {
      if (namespaceURI!=null && namespaceURI.equals("http://www.w3.org/2000/09/xmldsig#")) {
        return new ResourceResolver(
                "http://www.w3.org/TR/2008/REC-xmldsig-core-20080610/xmldsig-core-schema.xsd",
                "xmldsig-core-schema.xsd");
      } else if (systemId!=null && systemId.equals("http://www.w3.org/2001/XMLSchema.dtd")) {
        return new ResourceResolver(
                "http://www.w3.org/2001/XMLSchema.dtd",
                "XMLSchema.dtd");
      } else if (systemId!=null && systemId.equals("datatypes.dtd")) {
        return new ResourceResolver(
                "datatypes.dtd",
                "datatypes.dtd");
      } else {
        return null;
      }
    }

  }
}
