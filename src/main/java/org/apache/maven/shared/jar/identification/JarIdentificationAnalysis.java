package org.apache.maven.shared.jar.identification;

/*
 * Copyright 2001-2006 The Apache Software Foundation.
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

import org.apache.maven.shared.jar.JarAnalyzer;
import org.codehaus.plexus.util.StringUtils;

import java.util.Collection;
import java.util.Iterator;
import java.util.List;

/**
 * JarAnalyzer Taxon Analyzer
 *
 * @plexus.component role="org.apache.maven.shared.jar.identification.JarIdentificationAnalysis"
 */
public class JarIdentificationAnalysis
{
    /**
     * @plexus.requirement role="org.apache.maven.shared.jar.identification.JarIdentificationExposer"
     */
    private List exposers;

    public void analyze( JarAnalyzer jar )
    {
        JarIdentification taxon = new JarIdentification();

        for ( Iterator i = exposers.iterator(); i.hasNext(); )
        {
            JarIdentificationExposer exposer = (JarIdentificationExposer) i.next();
            exposer.initialize();
            exposer.setJar( jar );
            addExposer( taxon, exposer );
        }

        normalize( taxon );

        jar.setIdentification( taxon );
    }

    private void addExposer( JarIdentification taxon, JarIdentificationExposer exposer )
    {
        taxon.getPotentials().add( exposer );
        exposer.expose();

        if ( exposer.isAuthoritative() )
        {
            if ( StringUtils.isEmpty( taxon.getGroupId() ) && isNotEmpty( exposer.getGroupIds() ) )
            {
                taxon.setGroupId( (String) exposer.getGroupIds().get( 0 ) );
            }

            if ( StringUtils.isEmpty( taxon.getArtifactId() ) && isNotEmpty( exposer.getArtifactIds() ) )
            {
                taxon.setArtifactId( (String) exposer.getArtifactIds().get( 0 ) );
            }

            if ( StringUtils.isEmpty( taxon.getVersion() ) && isNotEmpty( exposer.getVersions() ) )
            {
                taxon.setVersion( (String) exposer.getVersions().get( 0 ) );
            }

            if ( StringUtils.isEmpty( taxon.getName() ) && isNotEmpty( exposer.getNames() ) )
            {
                taxon.setName( (String) exposer.getNames().get( 0 ) );
            }

            if ( StringUtils.isEmpty( taxon.getVendor() ) && isNotEmpty( exposer.getVendors() ) )
            {
                taxon.setVendor( (String) exposer.getVendors().get( 0 ) );
            }
        }
    }

    private void normalize( JarIdentification taxon )
    {
        if ( StringUtils.isEmpty( taxon.getGroupId() ) )
        {
            taxon.setGroupId( pickSmallest( taxon.getGroupIds() ) );
        }

        if ( StringUtils.isEmpty( taxon.getArtifactId() ) )
        {
            taxon.setArtifactId( pickLargest( taxon.getArtifactIds() ) );
        }

        if ( StringUtils.isEmpty( taxon.getVersion() ) )
        {
            taxon.setVersion( pickSmallest( taxon.getVersions() ) );
        }

        if ( StringUtils.isEmpty( taxon.getName() ) )
        {
            taxon.setName( pickLargest( taxon.getNames() ) );
        }

        if ( StringUtils.isEmpty( taxon.getVendor() ) )
        {
            taxon.setVendor( pickLargest( taxon.getVendors() ) );
        }
    }

    private String pickSmallest( List list )
    {
        if ( isEmpty( list ) )
        {
            return null;
        }

        int size = Integer.MAX_VALUE;
        String smallest = null;
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

        return smallest;
    }

    private String pickLargest( List list )
    {
        if ( isEmpty( list ) )
        {
            return null;
        }

        int size = Integer.MIN_VALUE;
        String largest = null;
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

        return largest;
    }

    private boolean isEmpty( Collection coll )
    {
        return ( ( coll == null ) || coll.isEmpty() );
    }

    private boolean isNotEmpty( Collection coll )
    {
        return ( ( coll != null ) && !coll.isEmpty() );
    }
}
