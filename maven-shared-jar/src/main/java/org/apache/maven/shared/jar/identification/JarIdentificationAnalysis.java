package org.apache.maven.shared.jar.identification;

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

import org.apache.maven.shared.jar.JarAnalyzer;
import org.apache.maven.shared.utils.StringUtils;

import java.util.Collections;
import java.util.Iterator;
import java.util.List;

/**
 * Analyze the JAR file to identify Maven artifact metadata. This class is thread safe and immutable as long as all
 * provided exposers are, as it retains no state.
 * <p/>
 * If using Plexus, the class will use all available exposers in the container.
 * <p/>
 * If not using Plexus, the exposers must be set using {@link #setExposers(java.util.List)} before calling
 * {@link #analyze(org.apache.maven.shared.jar.JarAnalyzer)}
 * <p/>
 * Note that you must first create an instance of {@link org.apache.maven.shared.jar.JarAnalyzer} - see its Javadoc for
 * a typical use.
 *
 * @plexus.component role="org.apache.maven.shared.jar.identification.JarIdentificationAnalysis" role-hint="default"
 */
public class JarIdentificationAnalysis
{
    /**
     * The Maven information exposers to use during identification.
     *
     * @plexus.requirement role="org.apache.maven.shared.jar.identification.JarIdentificationExposer"
     */
    private List exposers;

    /**
     * Analyze a JAR and find any associated Maven metadata. Note that if the provided JAR analyzer has previously
     * analyzed the JAR, the cached results will be returned. You must obtain a new JAR analyzer to the re-read the
     * contents of the file.
     *
     * @param jarAnalyzer the JAR to analyze. This must not yet have been closed.
     * @return the Maven metadata discovered
     */
    public JarIdentification analyze( JarAnalyzer jarAnalyzer )
    {
        JarIdentification taxon = jarAnalyzer.getJarData().getJarIdentification();
        if ( taxon != null )
        {
            return taxon;
        }

        taxon = new JarIdentification();

        for ( Iterator i = exposers.iterator(); i.hasNext(); )
        {
            JarIdentificationExposer exposer = (JarIdentificationExposer) i.next();
            exposer.expose( taxon, jarAnalyzer );
        }

        normalize( taxon );

        jarAnalyzer.getJarData().setJarIdentification( taxon );

        return taxon;
    }

    private void normalize( JarIdentification taxon )
    {
        if ( StringUtils.isEmpty( taxon.getGroupId() ) )
        {
            taxon.setGroupId( pickSmallest( taxon.getPotentialGroupIds() ) );
        }

        if ( StringUtils.isEmpty( taxon.getArtifactId() ) )
        {
            taxon.setArtifactId( pickLargest( taxon.getPotentialArtifactIds() ) );
        }

        if ( StringUtils.isEmpty( taxon.getVersion() ) )
        {
            taxon.setVersion( pickSmallest( taxon.getPotentialVersions() ) );
        }

        if ( StringUtils.isEmpty( taxon.getName() ) )
        {
            taxon.setName( pickLargest( taxon.getPotentialNames() ) );
        }

        if ( StringUtils.isEmpty( taxon.getVendor() ) )
        {
            taxon.setVendor( pickLargest( taxon.getPotentialVendors() ) );
        }
    }

    private String pickSmallest( List list )
    {
        String smallest = null;

        if ( !list.isEmpty() )
        {
            int size = Integer.MAX_VALUE;
            Iterator it = list.iterator();
            while ( it.hasNext() )
            {
                String val = (String) it.next();

                if ( StringUtils.isNotEmpty( val ) )
                {
                    if ( val.length() < size )
                    {
                        smallest = val;
                        size = val.length();
                    }
                }
            }
        }

        return smallest;
    }

    private String pickLargest( List list )
    {
        String largest = null;
        if ( !list.isEmpty() )
        {
            int size = Integer.MIN_VALUE;
            Iterator it = list.iterator();
            while ( it.hasNext() )
            {
                String val = (String) it.next();
                if ( StringUtils.isNotEmpty( val ) )
                {
                    if ( val.length() > size )
                    {
                        largest = val;
                        size = val.length();
                    }
                }
            }
        }
        return largest;
    }

    public void setExposers( List exposers )
    {
        this.exposers = Collections.unmodifiableList( exposers );
    }
}
