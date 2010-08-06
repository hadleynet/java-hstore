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

import java.io.IOException;
import java.io.InputStream;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Request;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.ResponseBuilder;
import org.projecthdata.javahstore.hdr.DocumentMetadata;
import org.projecthdata.javahstore.hdr.Section;
import org.projecthdata.javahstore.hdr.SectionDocument;

/**
 * JAX-RS resource for section documents
 * @author marc
 */
public class DocumentResource {

  private SectionDocument document;
  private Section section;
  private Request request;

  public DocumentResource(SectionDocument document, Section section, Request request) {
    this.document = document;
    this.section = section;
    this.request = request;
  }

  @GET
  public Response getDocument() {
    ResponseBuilder rb = request.evaluatePreconditions(document.getLastUpdated());
    if (rb!=null)
      return rb.build(); // preconditions not met
    return Response.ok(document.getContent(), document.getMediaType())
            .lastModified(document.getLastUpdated()).build();
  }

  @PUT
  public Response updateDocument(@Context HttpHeaders headers, InputStream content) throws IOException {
    ResponseBuilder rb = request.evaluatePreconditions(document.getLastUpdated());
    if (rb!=null)
      return rb.build(); // preconditions not met
    MediaType incoming = headers.getMediaType();
    MediaType existing = MediaType.valueOf(document.getMediaType());
    if (!incoming.isCompatible(existing)) {
      return Response.status(Response.Status.BAD_REQUEST)
              .type("text/plain")
              .entity("Incompatible media types, original: "+existing.toString()+", new: "+incoming.toString())
              .build();
    }
    document.update(incoming.toString(), content);
    return Response.ok().build();
  }

  @POST
  @Consumes(MediaType.APPLICATION_XML)
  public Response updateMetadata(@Context HttpHeaders headers, String data) {
    ResponseBuilder rb = request.evaluatePreconditions(document.getLastUpdated());
    if (rb!=null)
      return rb.build(); // preconditions not met
    DocumentMetadata metadata;
    try {
      metadata = new DocumentMetadata(data);
      document.setMetadata(metadata);
      return Response.ok().build();
    } catch (Exception ex) {
      throw new BadRequestException(ex.getMessage());
    }
  }

  @DELETE
  public Response deleteDocument() {
    ResponseBuilder rb = request.evaluatePreconditions(document.getLastUpdated());
    if (rb!=null)
      return rb.build(); // preconditions not met
    section.deleteDocument(document);
    return Response.ok().build();
  }

}
