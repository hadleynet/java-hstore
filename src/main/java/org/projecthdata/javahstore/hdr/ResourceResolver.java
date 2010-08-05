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

import java.io.InputStream;
import java.io.Reader;
import org.w3c.dom.ls.LSInput;

/**
 * Implementation of LSInput for locally bundled files
 * @author mhadley
 */
class ResourceResolver implements LSInput {
  
  String systemId, path;

  final static String SCHEMA_PATH = "/org/projecthdata/schemas/";

  ResourceResolver(String systemId, String path) {
    this.systemId = systemId;
    this.path = SCHEMA_PATH+path;
  }

  @Override
  public Reader getCharacterStream() {
    return null;
  }

  @Override
  public void setCharacterStream(Reader characterStream) {
  }

  @Override
  public InputStream getByteStream() {
    return DocumentMetadata.class.getResourceAsStream(path);
  }

  @Override
  public void setByteStream(InputStream byteStream) {
  }

  @Override
  public String getStringData() {
    return null;
  }

  @Override
  public void setStringData(String stringData) {
  }

  @Override
  public String getSystemId() {
    return systemId;
  }

  @Override
  public void setSystemId(String systemId) {
  }

  @Override
  public String getPublicId() {
    return null;
  }

  @Override
  public void setPublicId(String publicId) {
  }

  @Override
  public String getBaseURI() {
    return null;
  }

  @Override
  public void setBaseURI(String baseURI) {
  }

  @Override
  public String getEncoding() {
    return "utf-8";
  }

  @Override
  public void setEncoding(String encoding) {
  }

  @Override
  public boolean getCertifiedText() {
    return true;
  }

  @Override
  public void setCertifiedText(boolean certifiedText) {
  }
}
