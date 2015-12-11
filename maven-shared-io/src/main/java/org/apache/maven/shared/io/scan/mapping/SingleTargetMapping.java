package org.apache.maven.shared.io.scan.mapping;

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

import org.apache.maven.shared.io.scan.InclusionScanException;

import java.util.Set;
import java.util.Collections;
import java.io.File;

/**
 * Maps a set of input files to a single output file.
 *
 * @author <a href="mailto:trygvis@inamo.no">Trygve Laugst&oslash;l</a>
 * @version $Id$
 */
public class SingleTargetMapping
    implements SourceMapping
{
    private String sourceSuffix;

    private String outputFile;

    /**
     * @param sourceSuffix source suffix.
     * @param outputFile output file.
     */
    public SingleTargetMapping( String sourceSuffix, String outputFile )
    {
        this.sourceSuffix = sourceSuffix;

        this.outputFile = outputFile;
    }

    /** {@inheritDoc} */
    public Set<File> getTargetFiles( File targetDir, String source )
        throws InclusionScanException
    {
        if ( !source.endsWith( sourceSuffix ) )
        {
            return Collections.<File>emptySet();
        }

        return Collections.singleton( new File( targetDir, outputFile ) );
    }
}
