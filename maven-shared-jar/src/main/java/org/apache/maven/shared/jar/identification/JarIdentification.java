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

import java.util.ArrayList;
import java.util.List;

/**
 * Gathered Maven information about the JAR file. Stores both assumed/validated values and potential values.
 *
 * @see org.apache.maven.shared.jar.identification.JarIdentificationAnalysis#analyze(org.apache.maven.shared.jar.JarAnalyzer)
 */
public class JarIdentification
{
    /**
     * The group ID derived or guessed from the list of potentials of the JAR.
     */
    private String groupId;

    /**
     * The artifact ID derived or guessed from the list of potentials of the JAR.
     */
    private String artifactId;

    /**
     * The version derived or guessed from the list of potentials of the JAR.
     */
    private String version;

    /**
     * The project name derived or guessed from the list of potentials of the JAR.
     */
    private String name;

    /**
     * The vendor (organization name) derived or guessed from the list of potentials of the JAR.
     */
    private String vendor;

    /**
     * The list of possible group IDs discovered.
     */
    private List<String> potentialGroupIds = new ArrayList<String>();

    /**
     * The list of possible artifact IDs discovered.
     */
    private List<String> potentialArtifactIds = new ArrayList<String>();

    /**
     * The list of possible versions discovered.
     */
    private List<String> potentialVersions = new ArrayList<String>();

    /**
     * The list of possible artifact names discovered.
     */
    private List<String> potentialNames = new ArrayList<String>();

    /**
     * The list of possible vendors discovered.
     */
    private List<String> potentialVendors = new ArrayList<String>();

    /**
     * Add a validated group ID.
     *
     * @param groupId the group ID discovered
     */
    public void addAndSetGroupId( String groupId )
    {
        if ( groupId != null )
        {
            this.groupId = groupId;
        }

        addGroupId( groupId );
    }

    /**
     * Add a potential group ID.
     *
     * @param groupId the group ID discovered
     */
    public void addGroupId( String groupId )
    {
        addUnique( potentialGroupIds, groupId );
    }

    /**
     * Add a validated artifact ID.
     *
     * @param artifactId the artifact ID discovered
     */
    public void addAndSetArtifactId( String artifactId )
    {
        if ( artifactId != null )
        {
            this.artifactId = artifactId;
        }

        addArtifactId( artifactId );
    }

    /**
     * Add a potential artifact ID.
     *
     * @param artifactId the artifact ID discovered
     */
    public void addArtifactId( String artifactId )
    {
        addUnique( potentialArtifactIds, artifactId );
    }

    /**
     * Add a validated version.
     *
     * @param version the version discovered
     */
    public void addAndSetVersion( String version )
    {
        if ( version != null )
        {
            this.version = version;
        }

        addVersion( version );
    }

    /**
     * Add a potential version.
     *
     * @param version the version discovered
     */
    public void addVersion( String version )
    {
        addUnique( potentialVersions, version );
    }

    /**
     * Add a validated vendor name.
     *
     * @param name the vendor name discovered
     */
    public void addAndSetVendor( String name )
    {
        if ( name != null )
        {
            vendor = name;
        }

        addVendor( name );
    }

    /**
     * Add a potential vendor name.
     *
     * @param name the vendor name discovered
     */
    public void addVendor( String name )
    {
        addUnique( potentialVendors, name );
    }

    /**
     * Add a validated artifact name.
     *
     * @param name the artifact name discovered
     */
    public void addAndSetName( String name )
    {
        if ( name != null )
        {
            this.name = name;
        }

        addName( name );
    }

    /**
     * Add a potential artifact name.
     *
     * @param name the artifact name discovered
     */
    public void addName( String name )
    {
        addUnique( potentialNames, name );
    }

    private static void addUnique( List<String> list, String value )
    {
        if ( value != null )
        {
            if ( !list.contains( value ) )
            {
                list.add( value );
            }
        }
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

    public List<String> getPotentialVersions()
    {
        return potentialVersions;
    }

    public List<String> getPotentialNames()
    {
        return potentialNames;
    }

    public List<String> getPotentialGroupIds()
    {
        return potentialGroupIds;
    }

    public List<String> getPotentialArtifactIds()
    {
        return potentialArtifactIds;
    }

    public List<String> getPotentialVendors()
    {
        return potentialVendors;
    }
}
