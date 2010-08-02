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

import java.util.Collection;
import java.util.Date;

/**
 * Representation of a root document
 * @author marc
 */
public interface RootDocument {

  /**
   * Get the record identifier
   * @return
   */
  String getId();

  /**
   * Get the creation date/time of the record
   * @return
   */
  Date getCreated();

  /**
   * Get the last modified data/time of the record
   * @return
   */
  Date getLastModified();

  /**
   * Get the list of supported extensions
   * @return
   */
  Collection<Extension> getExtensions();

  /**
   * Get an extension by ID
   * throws IllegalArgumentException if no such extension is known
   */
  Extension getExtension(String id);

  /**
   * Get the root section. The root section cannot contain documents so the
   * returned section will return an empty list of child documents and will
   * not allow creation of child documents.
   * @return a list of child sections of the current section.
   */
  Section getRootSection();

}
