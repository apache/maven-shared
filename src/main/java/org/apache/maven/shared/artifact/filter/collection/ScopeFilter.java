package org.apache.maven.shared.artifact.filter.collection;

/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.    
 */

import java.util.HashSet;
import java.util.Set;

import org.apache.maven.artifact.Artifact;
import org.apache.maven.artifact.resolver.filter.ArtifactFilter;
import org.apache.maven.artifact.resolver.filter.ScopeArtifactFilter;
import org.apache.maven.shared.utils.StringUtils;

/**
 * @author <a href="mailto:brianf@apache.org">Brian Fox</a>
 * @version $Id$
 */
public class ScopeFilter
    extends AbstractArtifactsFilter
{

    private String includeScope;

    private String excludeScope;

    public ScopeFilter( String includeScope, String excludeScope )
    {
        this.includeScope = includeScope;
        this.excludeScope = excludeScope;
    }

    /**
     * This function determines if filtering needs to be performed. Excludes are
     * ignored if Includes are used.
     * 
     * @param dependencies
     *            the set of dependencies to filter.
     * 
     * @return a Set of filtered dependencies.
     * @throws ArtifactFilterException
     */
    public Set<Artifact> filter( Set<Artifact> artifacts )
        throws ArtifactFilterException
    {
        Set<Artifact> results = artifacts;

        if ( StringUtils.isNotEmpty( includeScope ) )
        {
            if ( !Artifact.SCOPE_COMPILE.equals( includeScope ) && !Artifact.SCOPE_TEST.equals( includeScope )
                && !Artifact.SCOPE_PROVIDED.equals( includeScope ) && !Artifact.SCOPE_RUNTIME.equals( includeScope )
                && !Artifact.SCOPE_SYSTEM.equals( includeScope ) )
            {
                throw new ArtifactFilterException( "Invalid Scope in includeScope: " + includeScope );
            }

            results = new HashSet<Artifact>();

            if ( Artifact.SCOPE_PROVIDED.equals( includeScope ) || Artifact.SCOPE_SYSTEM.equals( includeScope ) )
            {
                results = includeSingleScope( artifacts, includeScope );
            }
            else
            {
                ArtifactFilter saf = new ScopeArtifactFilter( includeScope );

                for ( Artifact artifact : artifacts )
                {
                    if ( saf.include( artifact ) )
                    {
                        results.add( artifact );
                    }
                }
            }
        }
        else if ( StringUtils.isNotEmpty( excludeScope ) )
        {
            if ( !Artifact.SCOPE_COMPILE.equals( excludeScope ) && !Artifact.SCOPE_TEST.equals( excludeScope )
                && !Artifact.SCOPE_PROVIDED.equals( excludeScope ) && !Artifact.SCOPE_RUNTIME.equals( excludeScope )
                && !Artifact.SCOPE_SYSTEM.equals( excludeScope ) )
            {
                throw new ArtifactFilterException( "Invalid Scope in excludeScope: " + excludeScope );
            }
            results = new HashSet<Artifact>();
            // plexus ScopeArtifactFilter doesn't handle the provided scope so
            // we
            // need special handling for it.
            if ( Artifact.SCOPE_TEST.equals( excludeScope ) )
            {
                throw new ArtifactFilterException( " Can't exclude Test scope, this will exclude everything." );
            }
            else if ( !Artifact.SCOPE_PROVIDED.equals( excludeScope ) && !Artifact.SCOPE_SYSTEM.equals( excludeScope ) )
            {
                ArtifactFilter saf = new ScopeArtifactFilter( excludeScope );

                for ( Artifact artifact : artifacts )
                {
                    if ( !saf.include( artifact ) )
                    {
                        results.add( artifact );
                    }
                }
            }
            else
            {
                results = excludeSingleScope( artifacts, excludeScope );
            }
        }

        return results;
    }

    private Set<Artifact> includeSingleScope( Set<Artifact> artifacts, String scope )
    {
        Set<Artifact> results = new HashSet<Artifact>();
        for ( Artifact artifact : artifacts )
        {
            if ( scope.equals( artifact.getScope() ) )
            {
                results.add( artifact );
            }
        }
        return results;
    }

    private Set<Artifact> excludeSingleScope( Set<Artifact> artifacts, String scope )
    {
        Set<Artifact> results = new HashSet<Artifact>();
        for ( Artifact artifact : artifacts )
        {
            if ( !scope.equals( artifact.getScope() ) )
            {
                results.add( artifact );
            }
        }
        return results;
    }

    /**
     * @return Returns the includeScope.
     */
    public String getIncludeScope()
    {
        return this.includeScope;
    }

    /**
     * @param includeScope
     *            The includeScope to set.
     */
    public void setIncludeScope( String scope )
    {
        this.includeScope = scope;
    }

    /**
     * @return Returns the excludeScope.
     */
    public String getExcludeScope()
    {
        return this.excludeScope;
    }

    /**
     * @param excludeScope
     *            The excludeScope to set.
     */
    public void setExcludeScope( String excludeScope )
    {
        this.excludeScope = excludeScope;
    }

}
