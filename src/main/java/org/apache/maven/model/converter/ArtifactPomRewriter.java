package org.apache.maven.model.converter;

/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

import java.io.Reader;
import java.io.Writer;
import java.util.List;

/**
 * @author jdcasey
 */
public interface ArtifactPomRewriter
{
    String ROLE = ArtifactPomRewriter.class.getName();

    String V3_POM = "v3";

    String V4_POM = "v4";

    void rewrite( Reader from, Writer to, boolean reportOnly, String groupId, String artifactId, String version,
                  String packaging )
        throws Exception;

    List getWarnings();
}
