package org.apache.maven.shared.filtering;

/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

import java.io.Reader;

import org.codehaus.plexus.interpolation.Interpolator;

public class InterpolatorFilterReaderLineEndingTest
    extends AbstractInterpolatorFilterReaderLineEndingTest
{
    @Override
    protected Reader getAaa_AaaReader( Reader in, Interpolator interpolator )
    {
        return new InterpolatorFilterReaderLineEnding( in, interpolator, "aaa", "aaa", true );
    }

    @Override
    protected Reader getAbc_AbcReader( Reader in, Interpolator interpolator )
    {
        return new InterpolatorFilterReaderLineEnding( in, interpolator, "abc", "abc", true );
    }

    @Override
    protected Reader getDollarBracesReader( Reader in, Interpolator interpolator, String escapeString )
    {
        InterpolatorFilterReaderLineEnding reader =
            new InterpolatorFilterReaderLineEnding( in, interpolator, "${", "}", true );
        reader.setEscapeString( escapeString );
        return reader;
    }

    @Override
    protected Reader getAtReader( Reader in, Interpolator interpolator, String escapeString )
    {
        InterpolatorFilterReaderLineEnding reader =
            new InterpolatorFilterReaderLineEnding( in, interpolator, "@", "@", true );
        reader.setEscapeString( escapeString );
        return reader;
    }
}
