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
package org.projecthdata.javahstore.resources;

import com.sun.jersey.api.NotFoundException;
import com.sun.jersey.core.header.ContentDisposition;
import com.sun.jersey.multipart.BodyPart;
import com.sun.jersey.multipart.MultiPart;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Request;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;
import javax.xml.parsers.ParserConfigurationException;
import org.projecthdata.javahstore.hdr.DocumentMetadata;
import org.projecthdata.javahstore.hdr.Extension;
import org.projecthdata.javahstore.hdr.HDR;
import org.projecthdata.javahstore.hdr.Section;
import org.projecthdata.javahstore.hdr.SectionDocument;
import org.xml.sax.SAXException;

/**
 * JAX-RS resource for child sections and subsections
 * @author marc
 */
public class SectionResource {

  private Section parent;
  private Section section;
  private UriInfo uriInfo;
  private HDR hdr;
  private Request request;

  public SectionResource(HDR hdr, Section section, Section parent, UriInfo uriInfo, Request request) {
    this.hdr = hdr;
    this.section = section;
    this.parent = parent;
    this.uriInfo = uriInfo;
    this.request = request;
  }

  @GET @Produces(MediaType.APPLICATION_ATOM_XML)
  public Section getSection() {
    return section;
  }

  @POST
  @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
  public Response createSubsection(
          @FormParam("extensionId") String extensionId,
          @FormParam("path") String path,
          @FormParam("name") String name) {
    if (extensionId==null)
      throw new BadRequestException("An extension ID is required");
    else if(path == null)
      throw new BadRequestException("A path is required");
    else if(name == null)
      throw new BadRequestException("A name is required");

    try {
      Extension e = hdr.getRootDocument().getExtension(extensionId);
      section.createChildSection(e, path, name);
    } catch (IllegalArgumentException ex) {
      throw new BadRequestException(ex.getMessage());
    }
    URI childSectionURI = uriInfo.getAbsolutePathBuilder().path(path).build();
    return Response.created(childSectionURI).build();
  }

  @POST
  @Consumes(MediaType.MULTIPART_FORM_DATA)
  public Response addDocumentWithMetadata(MultiPart multipart) throws IOException, ParserConfigurationException, SAXException {
    DocumentMetadata metadata = null;
    InputStream content = null;
    String mediaType = null;
    for (BodyPart part: multipart.getBodyParts()) {
      ContentDisposition cd = part.getContentDisposition();
      if (cd.getParameters().get("name").equals("content")) {
        mediaType = part.getMediaType().toString();
        content = part.getEntityAs(InputStream.class);
      } else if (cd.getParameters().get("name").equals("metadata")) {
        metadata = new DocumentMetadata(part.getEntityAs(String.class));
      }
    }
    try {
      String path = section.createDocument(mediaType, content, metadata).getPath();
      URI childDocumentURI = uriInfo.getAbsolutePathBuilder().path(path).build();
      return Response.created(childDocumentURI).build();
    } catch (IllegalArgumentException ex) {
      throw new BadRequestException(ex.getMessage());
    }
  }

  @POST
  public Response addDocumentWithoutMetadata(@Context HttpHeaders headers, InputStream content) throws IOException {
    MediaType incoming = headers.getMediaType();
    try {
      String path = section.createDocument(incoming.toString(), content).getPath();
      URI childDocumentURI = uriInfo.getAbsolutePathBuilder().path(path).build();
      return Response.created(childDocumentURI).build();
    } catch (IllegalArgumentException ex) {
      throw new BadRequestException(ex.getMessage());
    }
  }

  @DELETE
  public Response deleteSection() {
    parent.deleteChildSection(section);
    return Response.ok().build();
  }

  @Path("{segment}")
  public Object findChild(@PathParam("segment") String segment) {
    Section childSection = section.getChildSection(segment);
    if (childSection != null)
      return new SectionResource(hdr, childSection, section, uriInfo, request);
    SectionDocument document = section.getChildDocument(segment);
    if (document != null)
      return new DocumentResource(document, section, request);
    throw new NotFoundException("Section or document "+segment+" not found");
  }

}
