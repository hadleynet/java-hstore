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

import java.io.IOException;
import java.io.OutputStream;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import java.net.URI;
import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.UriInfo;
import javax.ws.rs.ext.MessageBodyWriter;
import javax.ws.rs.ext.Provider;
import org.apache.abdera.Abdera;
import org.apache.abdera.model.Category;
import org.apache.abdera.model.Entry;
import org.apache.abdera.model.Feed;
import org.apache.abdera.model.Link;
import org.projecthdata.javahstore.hdr.Section;
import org.projecthdata.javahstore.hdr.SectionDocument;

/**
 * JAX-RS message body writer that renders an Atom feed of a section's
 * contents.
 * @author marc
 */
@Produces(MediaType.APPLICATION_ATOM_XML)
@Provider
public class SectionFeedWriter implements MessageBodyWriter<Section> {

  private static Abdera abdera = null;
  @Context UriInfo uriInfo;

  public static synchronized Abdera getAbdera() {
    if (abdera == null)
      abdera = new Abdera();
    return abdera;
  }

  @Override
  public boolean isWriteable(Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType) {
    return Section.class.isAssignableFrom(type);
  }

  @Override
  public long getSize(Section t, Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType) {
    return -1;
  }

  @Override
  public void writeTo(Section t, Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType, MultivaluedMap<String, Object> httpHeaders, OutputStream entityStream) throws IOException, WebApplicationException {
    Feed feed = getAbdera().newFeed();
    feed.setId(uriInfo.getRequestUri().toString());
    feed.setTitle(t.getName());
    feed.addLink(uriInfo.getRequestUri().toString(), "self");
    for (Section section: t.getChildSections()) {
      Entry entry = feed.addEntry();
      URI sectionUri = uriInfo.getAbsolutePathBuilder().path(section.getPath()).build();
      Link sectionLink = entry.addLink(sectionUri.toString());
      sectionLink.setMimeType(MediaType.APPLICATION_ATOM_XML);
      sectionLink.setRel("alternate");
      entry.setId(sectionUri.toString());
      entry.setTitle(section.getName());
      if (section.getExtension() != null) {
        Category category = entry.addCategory(section.getExtension().getId());
        category.setScheme(Constants.HDATA_XML_NS);
      }
    }
    for (SectionDocument document: t.getChildDocuments()) {
      Entry entry = feed.addEntry();
      URI sectionUri = uriInfo.getAbsolutePathBuilder().path(document.getPath()).build();
      Link sectionLink = entry.addLink(sectionUri.toString());
      sectionLink.setMimeType(document.getMediaType());
      sectionLink.setRel("alternate");
      entry.setEdited(document.getLastUpdated());
      entry.setUpdated(document.getLastUpdated());
      entry.setId(sectionUri.toString());
      entry.setTitle(document.getPath());
      if (document.getMetadata() != null) {
        entry.addAuthor(document.getMetadata().getAuthor());
        entry.setContent(document.getMetadata().getXml(), MediaType.APPLICATION_XML);
      }
    }
    feed.writeTo(entityStream);
  }

}
