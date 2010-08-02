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
import java.util.Collection;

/**
 * Represents a section within a hData record
 * @author marc
 */
public interface Section {

  /**
   * Get the path of this section.
   * @return
   */
  String getPath();

  /**
   * Get the extension for this section
   * @return
   */
  Extension getExtension();

  /**
   * Get the human-readable name of the section
   * @return
   */
  String getName();

  /**
   * Get a list of child sections of the current section.
   * @return a list of child sections of the current section.
   */
  Collection<Section> getChildSections();

  /**
   * Get a child section by path
   * @param path
   * @return the matching child section or null if not found
   */
  Section getChildSection(String path);

  /**
   * Get a list of child documents of the current section.
   * @return a list of child documents of the current section.
   */
  Collection<SectionDocument> getChildDocuments();

  /**
   * Get a child document by path
   * @param path
   * @return the matching child document or null if not found
   */
  SectionDocument getChildDocument(String path);

  /**
   * Create a new child section with the specified extension, path and name
   * @param extension
   * @param path
   * @param name
   * @throws IllegalArgumentException if the extension ID is not supported or if
   * there is already a child section with the same path
   */
  public Section createChildSection(Extension extension, String path, String name);

  /**
   * Create a new document with the specified media type and content
   * @param mediaType the type of section documents, e.g. text/plain or application/xml
   * @param content
   * @return the path of the new document
   * @throws IllegalArgumentException if the media type is not allowed for this
   * section.
   * @throws UnsupportedOperationException if this section does not allow child
   * document, e.g. if it is the root section.
   * @throws IOException if an error occurs while reading the content or storing it
   */
  public SectionDocument createDocument(String mediaType, InputStream content) throws IOException;

  /**
   * Create a new document with the specified media type and content
   * @param mediaType the type of section documents, e.g. text/plain or application/xml
   * @param content
   * @param metadata
   * @return the path of the new document
   * @throws IllegalArgumentException if the media type is not allowed for this
   * section.
   * @throws UnsupportedOperationException if this section does not allow child
   * document, e.g. if it is the root section.
   * @throws IOException if an error occurs while reading the content or storing it
   */
  public SectionDocument createDocument(String mediaType, InputStream content, DocumentMetadata metadata) throws IOException;

  /**
   * Delete a document from this section.
   * @param document
   */
  public void deleteDocument(SectionDocument document);

  /**
   * Delete a child section from this section
   * @param childSection
   */
  public void deleteChildSection(Section childSection);
}
