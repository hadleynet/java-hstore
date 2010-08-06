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
import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.ServiceLoader;
import javax.ws.rs.Consumes;
import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Request;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;
import org.projecthdata.javahstore.hdr.Extension;
import org.projecthdata.javahstore.hdr.HDR;
import org.projecthdata.javahstore.hdr.HDRProvider;
import org.projecthdata.javahstore.hdr.RootDocument;
import org.projecthdata.javahstore.hdr.Section;

/**
 * JAX-RS resource class for the root of a hData record.
 * @author marc
 */
@Path("{recordId}")
public class RecordResource {

  private static final ServiceLoader<HDRProvider> hdrProviderFinder
     = ServiceLoader.load(HDRProvider.class);

  private static HDRProvider hdrProvider;
  private HDR hdr;

  public RecordResource(@PathParam("recordId") String recordId) {
    this.hdr = getHDRProvider().getHDR(recordId);
    if (hdr==null)
      throw new NotFoundException("Record "+recordId+" not found");
  }

  @Context
  private UriInfo uriInfo;

  @Context
  private Request request;

  @GET
  @Produces(MediaType.APPLICATION_ATOM_XML)
  public Section getRootSections() {
    return hdr.getRootDocument().getRootSection();
  }

  @POST
  @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
  public Response createSection(
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
      hdr.getRootDocument().getRootSection().createChildSection(e, path, name);
    } catch (IllegalArgumentException ex) {
      throw new BadRequestException(ex.getMessage());
    }
    URI childSectionURI = uriInfo.getAbsolutePathBuilder().path(path).build();
    return Response.created(childSectionURI).build();
  }
  
  @GET @Path("root.xml")
  @Produces(MediaType.APPLICATION_XML)
  public RootDocument getRoot() {
    return hdr.getRootDocument();
  }

  @Path("{segment}")
  public Object findChild(@PathParam("segment") String segment) {
    Section childSection = hdr.getRootDocument().getRootSection().getChildSection(segment);
    if (childSection != null)
      return new SectionResource(hdr, childSection, hdr.getRootDocument().getRootSection(), uriInfo, request);
    throw new NotFoundException("Section "+segment+" not found");
  }

  /**
   * Lazy initializer/getter for HDRProvider
   * @return the HDRProvider implementation
   * @throws WebApplicationException if no provider can be found or if there is
   * more than one provider registered.
   */
  private synchronized static HDRProvider getHDRProvider() {
    if (hdrProvider != null)
      return hdrProvider;
    final List<HDRProvider> hdrProviders =
          new ArrayList<HDRProvider>();
    for (HDRProvider provider: hdrProviderFinder) {
      hdrProviders.add(provider);
    }
    if (hdrProviders.isEmpty()) {
      throw new WebApplicationException(
              Response.serverError()
                .type("text/plain")
                .entity("No HDRProvider implementation found")
                .build());
    } else if (hdrProviders.size() > 1) {
      throw new WebApplicationException(
              Response.serverError()
                .type("text/plain")
                .entity("Mulitple HDRProvider implementations found, only one supported")
                .build());
    }
    hdrProvider = hdrProviders.get(0);
    return hdrProvider;
  }

}
