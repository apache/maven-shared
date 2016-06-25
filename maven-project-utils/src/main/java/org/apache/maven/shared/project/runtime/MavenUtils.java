package org.apache.maven.shared.project.runtime;

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

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import org.apache.maven.artifact.versioning.DefaultArtifactVersion;
import org.apache.maven.project.MavenProject;
import org.codehaus.plexus.util.IOUtil;

/**
 * 
 */
public final class MavenUtils
{
    
    private MavenUtils()
    {
    }

    public static String getMavenVersion()
    {
        // This relies on the fact that MavenProject is the in core classloader
        // and that the core classloader is for the maven-core artifact
        // and that should have a pom.properties file
        // if this ever changes, we will have to revisit this code.
        final Properties properties = new Properties();
        final InputStream in =
            MavenProject.class.getClassLoader().getResourceAsStream( "META-INF/maven/org.apache.maven/maven-core/"
                                                                         + "pom.properties" );
        try
        {
            properties.load( in );
        }
        catch ( IOException ioe )
        {
            return "";
        }
        finally
        {
            IOUtil.close( in );
        }

        return properties.getProperty( "version" ).trim();
    }

    /**
     * Compares the runtime Maven version to the version parameter.
     * Returns a positive value if the runtime Maven version is bigger than the version parameter. 
     * Returns a negative value if the runtime Maven version is less than the version parameter. 
     * Returns 0 if they are the same.
     * 
     * @param version the version to compare
     * @return 
     */
    public static int compareToVersion( String version )
    {
        return new DefaultArtifactVersion( getMavenVersion() ).compareTo( new DefaultArtifactVersion( version ) );
    }
}
