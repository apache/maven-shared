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

import java.io.IOException;
import java.io.Reader;

/**
 * A reader that imposes a limit to the number of bytes that can be read from an underlying reader, simulating eof when
 * this limit is reached. This stream can typically be used to constrain a client with regard to a readAheadLimit of an
 * underlying stream, to avoid overrunning this limit and hence lose the opportunity do to reset.
 */
public class BoundedReader
    extends Reader
{

    private final Reader target;

    int pos = 0;

    int readAheadLimit;

    /**
     * @param target {@link Reader}
     * @param readAheadLimit read ahead limit.
     * @throws IOException in case of a failure.
     */
    public BoundedReader( Reader target, int readAheadLimit )
        throws IOException
    {
        this.target = target;
        target.mark( readAheadLimit );
        this.readAheadLimit = readAheadLimit;
    }

    @Override
    public void close()
        throws IOException
    {
        target.close();
    }

    @Override
    public void reset()
        throws IOException
    {
        pos = 0;
        target.reset();
    }

    @Override
    public void mark( int theReadAheadLimit )
        throws IOException
    {
        this.readAheadLimit = theReadAheadLimit;
        target.mark( theReadAheadLimit );
    }

    @Override
    public int read()
        throws IOException
    {
        if ( pos >= readAheadLimit )
        {
            return -1;
        }
        pos++;
        return target.read();
    }

    @Override
    public int read( char[] cbuf, int off, int len )
        throws IOException
    {
        int c;
        for ( int i = 0; i < len; i++ )
        {
            c = read();
            if ( c == -1 )
            {
                return i;
            }
            cbuf[off + i] = (char) c;
        }
        return len;
    }
}
