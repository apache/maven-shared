package org.apache.maven.shared.io;

/*
 * Copyright 2004-2005 The Apache Software Foundation.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import org.codehaus.plexus.util.FileUtils;
import org.codehaus.plexus.util.StringUtils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URL;

/**
 * Performs Locator services for the <code>*Location</code> parameters in the 
 * Reports.
 *
 * @author <a href="mailto:joakim@erdfelt.com">Joakim Erdfelt</a>
 */
public class Locator
{
    /**
     * Create a Locator object.
     * 
     * @param logger the logger object to log with.
     */
    public Locator()
    {
    }

    /**
     * <p>
     * Attempts to resolve a location parameter into a real file.
     * </p>
     * 
     * <p>
     * Checks a location string to for a resource, URL, or File that matches.
     * If a resource or URL is found, then a local file is created with that
     * locations contents.
     * </p>
     * 
     * @param location the location string to match against.
     * @param localfile the local file to use in case of resource or URL.
     * @return the File of the resolved location.
     * @throws IOException if file is unable to be found or copied into <code>localfile</code> destination.
     */
    public File resolveLocation( String location, String localfile )
        throws IOException
    {
        if ( StringUtils.isEmpty( location ) )
        {
            return null;
        }

        File retFile = new File( localfile );

        // Attempt a URL
        if ( location.indexOf( "://" ) > 1 )
        {
            // Found a URL
            URL url = new URL( location );
            FileUtils.copyURLToFile( url, retFile );
        }
        else
        {
            // Attempt a File.
            File fileLocation = new File( location );
            if ( fileLocation.exists() )
            {
                // Found a File.
                FileUtils.copyFile( fileLocation, retFile );
            }
            else
            {
                // Attempt a Resource.
                URL url = this.getClass().getClassLoader().getResource( location );
                if ( url != null )
                {
                    // Found a Resource.
                    FileUtils.copyURLToFile( url, retFile );
                }
                else
                {
                    throw new IOException( "Unable to find location '" + location + "' as URL, File or Resource." );
                }
            }
        }

        if ( !retFile.exists() )
        {
            throw new FileNotFoundException( "Destination file does not exist." );
        }

        if ( retFile.length() <= 0 )
        {
            throw new IOException( "Destination file has no content." );
        }

        return retFile;
    }
}
