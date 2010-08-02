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
package org.projecthdata.javahstore.representations;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.ws.rs.core.UriBuilder;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlValue;
import org.projecthdata.javahstore.hdr.RootDocument;
import org.projecthdata.javahstore.hdr.Extension;
import org.projecthdata.javahstore.hdr.Section;

/**
 * JAXB classes for root.xml
 * @author marc
 */
@XmlRootElement(name="root")
public class RootXMLRepresentation {

  @XmlElement(name="id") String id;
  @XmlElement(name="version") final static String version = "1";
  @XmlElement(name="created") Date created;
  @XmlElement(name="lastModified") Date lastModified;
  @XmlElement(name="extension") @XmlElementWrapper(name="extensions")
  List<ExtensionRepresentation> extensions;
  @XmlElement(name="section") @XmlElementWrapper(name="sections")
  List<SectionRepresentation> sections;

  public RootXMLRepresentation() {} // keep JAXB happy

  public RootXMLRepresentation(RootDocument doc) {
    this.id = doc.getId();
    this.created = doc.getCreated();
    this.lastModified = doc.getLastModified();
    this.extensions = new ArrayList<ExtensionRepresentation>();
    for (Extension e: doc.getExtensions()) {
      this.extensions.add(new ExtensionRepresentation(e));
    }
    this.sections = new ArrayList<SectionRepresentation>();
    List<String> pathSegments = new ArrayList<String>();
    addChildSections(doc.getRootSection(), pathSegments);
  }

  /**
   * Recursive descent of all sections
   * @param parent
   */
  private void addChildSections(Section parent, List<String> pathSegments) {
    pathSegments.add(parent.getPath());
    for (Section s: parent.getChildSections()) {
      this.sections.add(new SectionRepresentation(s, pathSegments));
      addChildSections(s, pathSegments);
    }
    pathSegments.remove(pathSegments.size()-1);
  }

  private static class SectionRepresentation {

    @XmlAttribute(name="path") String path;
    @XmlAttribute(name="extensionId") String extensionId;
    @XmlAttribute(name="name") String name;

    public SectionRepresentation() {} // keep JAXB happy

    public SectionRepresentation(Section section, List<String> pathSegments) {
      UriBuilder uriPath = UriBuilder.fromPath("");
      for (String segment: pathSegments) {
        uriPath = uriPath.path(segment);
      }
      uriPath.path(section.getPath());
      path = uriPath.build().getPath();
      extensionId=section.getExtension().getId();
      name=section.getName();
    }

  }

  private static class ExtensionRepresentation {

    @XmlValue String id;
    @XmlAttribute(name="contentType") String contentType;

    public ExtensionRepresentation() {} // keep JAXB happy

    public ExtensionRepresentation(Extension extension) {
      id = extension.getId();
      contentType = extension.getContentType();
    }

  }

}
