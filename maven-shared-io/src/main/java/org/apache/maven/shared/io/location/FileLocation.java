package org.apache.maven.shared.io.location;

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

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;


public class FileLocation
    implements Location
{

    private File file;
    private FileChannel channel;
    private final String specification;
    private FileInputStream stream;

    public FileLocation( File file, String specification )
    {
        this.file = file;
        this.specification = specification;
    }

    protected FileLocation( String specification )
    {
        this.specification = specification;
    }

    public void close()
    {
        if ( ( channel != null ) && channel.isOpen() )
        {
            try
            {
                channel.close();
            }
            catch ( IOException e )
            {
                //swallow it.
            }
        }

        if ( stream != null )
        {
            try
            {
                stream.close();
            }
            catch( IOException e )
            {
                // swallow it.
            }
        }
    }

    public File getFile()
        throws IOException
    {
        initFile();

        return unsafeGetFile();
    }

    protected File unsafeGetFile()
    {
        return file;
    }

    protected void initFile()
        throws IOException
    {
        // TODO: Log this in the debug log-level...
        if ( file == null )
        {
            file = new File( specification );
        }
    }

    protected void setFile( File file )
    {
        if ( channel != null )
        {
            throw new IllegalStateException( "Location is already open; cannot setFile(..)." );
        }

        this.file = file;
    }

    public String getSpecification()
    {
        return specification;
    }

    public void open()
        throws IOException
    {
        if ( stream == null )
        {
            initFile();

            stream = new FileInputStream( file );
            channel = stream.getChannel();
        }
    }

    public int read( ByteBuffer buffer )
        throws IOException
    {
        open();
        return channel.read( buffer );
    }

    public int read( byte[] buffer )
        throws IOException
    {
        open();
        return channel.read( ByteBuffer.wrap( buffer ) );
    }

    public InputStream getInputStream()
        throws IOException
    {
        open();
        return stream;
    }

}
