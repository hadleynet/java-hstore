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
import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.MessageBodyWriter;
import javax.ws.rs.ext.Provider;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import org.projecthdata.javahstore.hdr.RootDocument;

/**
 * JAX-RS message body writer for root.xml
 *
 * @author marc
 */
@Produces(MediaType.APPLICATION_XML)
@Provider
public class RootXMLWriter implements MessageBodyWriter<RootDocument> {

  private static JAXBContext jbc;
  
  private static synchronized JAXBContext getJAXBContext() throws JAXBException {
    if (jbc==null) {
      jbc = JAXBContext.newInstance(
          RootXMLRepresentation.class);
    }
    return jbc;
  }

  @Override
  public boolean isWriteable(Class<?> type, Type genericType,
          Annotation[] annotations, MediaType mediaType) {
    return RootDocument.class.isAssignableFrom(type);
  }

  @Override
  public long getSize(RootDocument t, Class<?> type, Type genericType,
          Annotation[] annotations, MediaType mediaType) {
    return -1;
  }

  @Override
  public void writeTo(RootDocument t, Class<?> type, Type genericType,
          Annotation[] annotations, MediaType mediaType,
          MultivaluedMap<String, Object> httpHeaders,
          OutputStream entityStream) throws IOException, WebApplicationException {
    RootXMLRepresentation representation = new RootXMLRepresentation(t);
    try {
      getJAXBContext().createMarshaller().marshal(representation, entityStream);
    } catch (JAXBException ex) {
      ex.printStackTrace();
      throw new WebApplicationException(Response.serverError()
              .type("text/plain").entity(ex.getMessage()).build());
    }
  }

}
