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
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.internet.ContentDisposition;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMultipart;
import javax.mail.internet.ParseException;
import javax.ws.rs.core.MediaType;
import org.projecthdata.javahstore.hdr.DocumentMetadataTest;

/**
 * Utility class for producing multipart messages for hData document+metadata
 * updates
 */
public class MultipartCreator {

  public static Multipart create(String content, MediaType contentType, String metadata) throws ParseException, MessagingException {
    MimeMultipart pkg = new MimeMultipart();
    ContentDisposition cd = new ContentDisposition("attachment");
    cd.setParameter("filename", "content");
    MimeBodyPart contentPart = new MimeBodyPart();
    contentPart.setContent(content, contentType.toString());
    contentPart.setHeader("Content-Type", contentType.toString());
    contentPart.setDisposition(cd.toString());
    cd = new ContentDisposition("attachment");
    cd.setParameter("filename", "metadata");
    MimeBodyPart metadataPart = new MimeBodyPart();
    metadataPart.setContent(metadata, MediaType.APPLICATION_XML);
    metadataPart.setHeader("Content-Type", MediaType.APPLICATION_XML);
    metadataPart.setDisposition(cd.toString());
    pkg.addBodyPart(contentPart);
    pkg.addBodyPart(metadataPart);
    return pkg;
  }

  public static void main(String args[]) throws MessagingException, IOException {
    Multipart pkg = create("Hello World", MediaType.TEXT_PLAIN_TYPE, DocumentMetadataTest.TEST_METADATA);
    System.out.print(pkg.getContentType()+"\n\n");
    pkg.writeTo(System.out);
  }

}
