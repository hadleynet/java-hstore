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

import org.junit.Ignore;
import org.projecthdata.javahstore.hdr.HDR;
import org.projecthdata.javahstore.hdr.HDRProvider;
import org.projecthdata.javahstore.hdr.RootDocument;

/**
 * Dummy HDRProviderImpl for testing
 * @author marc
 */
public class DummyHDRProviderImpl implements HDRProvider {

  public static class TestHDRImpl implements HDR {

    private String id;

    public TestHDRImpl(String id) {
      this.id = id;
    }

    @Override
    public String getId() {
      return id;
    }

    @Override
    public RootDocument getRootDocument() {
      return new DummyRootDocumentImpl();
    }

  }

  @Override
  public HDR getHDR(String recordId) {
    return new TestHDRImpl(recordId);
  }

}
