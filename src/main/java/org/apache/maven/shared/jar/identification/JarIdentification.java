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

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * JarAnalyzer Taxon, the set of Maven dependency information both found and potential.
 */
public class JarIdentification
{
    private boolean wellKnown = false;

    private String groupId;

    private String artifactId;

    private String version;

    private String name;

    private String vendor;

    private List potentials;

    public JarIdentification()
    {
        potentials = new ArrayList();
    }

    public String getArtifactId()
    {
        return artifactId;
    }

    public void setArtifactId( String artifactId )
    {
        this.artifactId = artifactId;
    }

    public String getGroupId()
    {
        return groupId;
    }

    public void setGroupId( String groupId )
    {
        this.groupId = groupId;
    }

    public String getName()
    {
        return name;
    }

    public void setName( String name )
    {
        this.name = name;
    }

    public List getPotentials()
    {
        return potentials;
    }

    public void setPotentials( List potentials )
    {
        this.potentials = potentials;
    }

    public String getVendor()
    {
        return vendor;
    }

    public void setVendor( String vendor )
    {
        this.vendor = vendor;
    }

    public String getVersion()
    {
        return version;
    }

    public void setVersion( String version )
    {
        this.version = version;
    }

    public boolean isWellKnown()
    {
        return wellKnown;
    }

    public void setWellKnown( boolean wellKnown )
    {
        this.wellKnown = wellKnown;
    }

    public List getGroupIds()
    {
        List ret = new ArrayList();
        Iterator it = potentials.iterator();
        while ( it.hasNext() )
        {
            AbstractJarIdentificationExposer exposer = (AbstractJarIdentificationExposer) it.next();
            if ( exposer.getGroupIds() != null )
            {
                ret.addAll( exposer.getGroupIds() );
            }
        }
        return ret;
    }

    public List getArtifactIds()
    {
        List ret = new ArrayList();
        Iterator it = potentials.iterator();
        while ( it.hasNext() )
        {
            AbstractJarIdentificationExposer exposer = (AbstractJarIdentificationExposer) it.next();
            if ( exposer.getArtifactIds() != null )
            {
                ret.addAll( exposer.getArtifactIds() );
            }
        }
        return ret;
    }

    public List getVersions()
    {
        List ret = new ArrayList();
        Iterator it = potentials.iterator();
        while ( it.hasNext() )
        {
            AbstractJarIdentificationExposer exposer = (AbstractJarIdentificationExposer) it.next();
            if ( exposer.getVersions() != null )
            {
                ret.addAll( exposer.getVersions() );
            }
        }
        return ret;
    }

    public List getNames()
    {
        List ret = new ArrayList();
        Iterator it = potentials.iterator();
        while ( it.hasNext() )
        {
            AbstractJarIdentificationExposer exposer = (AbstractJarIdentificationExposer) it.next();
            if ( exposer.getNames() != null )
            {
                ret.addAll( exposer.getNames() );
            }
        }
        return ret;
    }

    public List getVendors()
    {
        List ret = new ArrayList();
        Iterator it = potentials.iterator();
        while ( it.hasNext() )
        {
            AbstractJarIdentificationExposer exposer = (AbstractJarIdentificationExposer) it.next();
            if ( exposer.getVendors() != null )
            {
                ret.addAll( exposer.getVendors() );
            }
        }
        return ret;
    }
}
