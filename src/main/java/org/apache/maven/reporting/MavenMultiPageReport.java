package org.apache.maven.reporting;

/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

import org.apache.maven.doxia.sink.Sink;
import org.apache.maven.doxia.sink.SinkFactory;

import java.util.Locale;

/**
 * Interface created separately for backwards compatibility. This method
 * would ideally have been added in the {@link MavenReport} interface, and the other 'generate'
 * method dropped. But that would have rendered all reporting mojo's uncompilable and binary incompatible.
 *
 * @author <a href="mailto:kenney@apache.org">Kenney Westerhof</a>
 * @see MavenReport#generate(org.codehaus.doxia.sink.Sink, Locale)
 * @since 3.0 (copied in maven-site-plugin 2.0-beta-6)
 */
public interface MavenMultiPageReport
    extends MavenReport
{
    void generate( Sink sink, SinkFactory sinkFactory, Locale locale )
        throws MavenReportException;
}
