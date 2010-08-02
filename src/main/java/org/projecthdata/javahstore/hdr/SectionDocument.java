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

import java.io.IOException;
import java.io.InputStream;
import java.util.Date;

/**
 * Represents a section document.
 * @author marc
 */
public interface SectionDocument {

  /**
   * Get the media type of the document
   * @return
   */
  String getMediaType();

  /**
   * Update the content of the document
   * @param mediaType the type of the document, e.g. text/plain or application/xml
   * @param content the stream from which the new content will be read
   * @throws IOException if an error occurs while reading or saving the
   * new content
   */
  void update(String mediaType, InputStream content) throws IOException;

  /**
   * Get the content of the document
   */
  InputStream getContent();

  /**
   * Get the path of the document
   */
  String getPath();

  /**
   * Get the last update time
   */
  Date getLastUpdated();

  /**
   * Get the document metadata
   */
  DocumentMetadata getMetadata();

  /**
   * Set the document metadata
   */
  void setMetadata(DocumentMetadata metadata);
}
