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
package org.projecthdata.javahstore;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import org.projecthdata.javahstore.hdr.DocumentMetadata;
import org.projecthdata.javahstore.hdr.Extension;
import org.projecthdata.javahstore.hdr.Section;
import org.projecthdata.javahstore.hdr.SectionDocument;

public class DummySectionImpl implements Section {

  String path;
  String name;
  Extension extension;
  List<Section> childSections;
  List<SectionDocument> childDocuments;

  public DummySectionImpl(Extension extension) {
    this.extension = extension;
    path = "foo";
    name = "FooExt";
    childSections = new ArrayList<Section>();
    childDocuments = new ArrayList<SectionDocument>();
  }

  @Override
  public String getPath() {
    return path;
  }

  @Override
  public Extension getExtension() {
    return extension;
  }

  @Override
  public String getName() {
    return name;
  }

  @Override
  public Collection<Section> getChildSections() {
    return childSections;
  }

  @Override
  public Section getChildSection(String path) {
    throw new UnsupportedOperationException("Not supported yet.");
  }

  @Override
  public Collection<SectionDocument> getChildDocuments() {
    return childDocuments;
  }

  @Override
  public SectionDocument getChildDocument(String path) {
    throw new UnsupportedOperationException("Not supported yet.");
  }

  @Override
  public Section createChildSection(Extension extensionId, String path, String name) {
    throw new UnsupportedOperationException("Not supported yet.");
  }

  @Override
  public SectionDocument createDocument(String mediaType, InputStream content) {
    throw new UnsupportedOperationException("Not supported yet.");
  }

  @Override
  public SectionDocument createDocument(String mediaType, InputStream content, DocumentMetadata metadata) {
    throw new UnsupportedOperationException("Not supported yet.");
  }

  @Override
  public void deleteDocument(SectionDocument document) {
    throw new UnsupportedOperationException("Not supported yet.");
  }

  @Override
  public void deleteChildSection(Section childSection) {
    throw new UnsupportedOperationException("Not supported yet.");
  }
}
