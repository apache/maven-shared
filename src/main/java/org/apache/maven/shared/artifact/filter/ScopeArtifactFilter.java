package org.apache.maven.shared.artifact.filter;

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

import org.apache.maven.artifact.Artifact;
import org.apache.maven.artifact.DefaultArtifact;
import org.apache.maven.artifact.resolver.filter.ArtifactFilter;
import org.codehaus.plexus.logging.Logger;

/**
 * <p>
 * {@link ArtifactFilter} implementation that selects artifacts based on their scopes.
 * </p>
 * <strong>NOTE:</strong> None of the fine-grained scopes imply other scopes when enabled;
 * when fine-grained scope control is used, each scope must be enabled separately,
 * UNLESS the corresponding XXXWithImplications() method is used to enable that
 * scope.
 */
public class ScopeArtifactFilter
    implements ArtifactFilter, StatisticsReportingArtifactFilter
{
    private boolean includeCompileScope;

    private boolean includeRuntimeScope;

    private boolean includeTestScope;

    private boolean includeProvidedScope;

    private boolean includeSystemScope;
    
    private boolean includeNullScope = true;
    
    private boolean nullScopeHit = false;

    private boolean compileScopeHit = false;

    private boolean runtimeScopeHit = false;

    private boolean testScopeHit = false;

    private boolean providedScopeHit = false;

    private boolean systemScopeHit = false;

    private List<String> filteredArtifactIds = new ArrayList<String>();
    
    /**
     * Constructor that is meant to be used with fine-grained manipulation to 
     * enable/disable specific scopes using the associated mutator methods.
     */
    public ScopeArtifactFilter()
    {
        // don't enable anything by default.
        this( null );
    }

    /**
     * Constructor that uses the implied nature of Maven scopes to determine which
     * artifacts to include. For instance, 'test' scope implies compile, provided, and runtime,
     * while 'runtime' scope implies only compile.
     * 
     * @param scope the scope
     */
    public ScopeArtifactFilter( String scope )
    {
        if ( DefaultArtifact.SCOPE_COMPILE.equals( scope ) )
        {
            setIncludeCompileScopeWithImplications( true );
        }
        else if ( DefaultArtifact.SCOPE_RUNTIME.equals( scope ) )
        {
            setIncludeRuntimeScopeWithImplications( true );
        }
        else if ( DefaultArtifact.SCOPE_TEST.equals( scope ) )
        {
            setIncludeTestScopeWithImplications( true );
        }
        else if ( DefaultArtifact.SCOPE_PROVIDED.equals( scope ) )
        {
            setIncludeProvidedScope( true );
        }
        else if ( DefaultArtifact.SCOPE_SYSTEM.equals( scope ) )
        {
            setIncludeSystemScope( true );
        }
    }

    /** {@inheritDoc} */
    public boolean include( Artifact artifact )
    {
        boolean result = true;
        
        if ( artifact.getScope() == null )
        {
            nullScopeHit = true;
            result = includeNullScope;
        }
        else if ( Artifact.SCOPE_COMPILE.equals( artifact.getScope() ) )
        {
            compileScopeHit = true;
            result = includeCompileScope;
        }
        else if ( Artifact.SCOPE_RUNTIME.equals( artifact.getScope() ) )
        {
            runtimeScopeHit = true;
            result = includeRuntimeScope;
        }
        else if ( Artifact.SCOPE_TEST.equals( artifact.getScope() ) )
        {
            testScopeHit = true;
            result = includeTestScope;
        }
        else if ( Artifact.SCOPE_PROVIDED.equals( artifact.getScope() ) )
        {
            providedScopeHit = true;
            result = includeProvidedScope;
        }
        else if ( Artifact.SCOPE_SYSTEM.equals( artifact.getScope() ) )
        {
            systemScopeHit = true;
            result = includeSystemScope;
        }

        if ( !result )
        {
            // We have to be very careful with artifacts that have ranges, 
            // because DefaultArtifact.getId() as of <= 2.1.0-M1 will throw a NPE 
            // if a range is specified.
            String id;
            if ( artifact.getVersionRange() != null )
            {
                id = artifact.getDependencyConflictId() + ":" + artifact.getVersionRange();
            }
            else
            {
                id = artifact.getId();
            }
            
            filteredArtifactIds.add( id );
        }

        return result;
    }

    /**
     * @return Information converted to a string.
     */
    public String toString()
    {
        return "Scope filter [null-scope=" + includeNullScope + ", compile=" + includeCompileScope + ", runtime="
            + includeRuntimeScope + ", test=" + includeTestScope + ", provided=" + includeProvidedScope + ", system="
            + includeSystemScope + "]";
    }

    /** {@inheritDoc} */
    public void reportFilteredArtifacts( Logger logger )
    {
        if ( !filteredArtifactIds.isEmpty() && logger.isDebugEnabled() )
        {
            StringBuilder buffer = new StringBuilder( "The following artifacts were removed by this filter: " );

            for ( String artifactId : filteredArtifactIds )
            {
                buffer.append( '\n' ).append( artifactId );
            }

            logger.debug( buffer.toString() );
        }
    }

    /** {@inheritDoc} */
    public void reportMissedCriteria( Logger logger )
    {
        if ( logger.isDebugEnabled() )
        {
            StringBuilder buffer = new StringBuilder();

            boolean report = false;
            if ( !nullScopeHit )
            {
                buffer.append( "\no [Null Scope]" );
                report = true;
            }
            if ( !compileScopeHit )
            {
                buffer.append( "\no Compile" );
                report = true;
            }
            if ( !runtimeScopeHit )
            {
                buffer.append( "\no Runtime" );
                report = true;
            }
            if ( !testScopeHit )
            {
                buffer.append( "\no Test" );
                report = true;
            }
            if ( !providedScopeHit )
            {
                buffer.append( "\no Provided" );
                report = true;
            }
            if ( !systemScopeHit )
            {
                buffer.append( "\no System" );
                report = true;
            }

            if ( report )
            {
                logger.debug( "The following scope filters were not used: " + buffer.toString() );
            }
        }
    }

    /** {@inheritDoc} */
    public boolean hasMissedCriteria()
    {
        boolean report = false;

        if ( !nullScopeHit )
        {
            report = true;
        }
        if ( !compileScopeHit )
        {
            report = true;
        }
        if ( !runtimeScopeHit )
        {
            report = true;
        }
        if ( !testScopeHit )
        {
            report = true;
        }
        if ( !providedScopeHit )
        {
            report = true;
        }
        if ( !systemScopeHit )
        {
            report = true;
        }

        return report;
    }
    
    /**
     * @return {@link #includeCompileScope}
     */
    public boolean isIncludeCompileScope()
    {
        return includeCompileScope;
    }

    /**
     * @param pIncludeCompileScope true/false.
     * @return {@link ScopeArtifactFilter}
     */
    public ScopeArtifactFilter setIncludeCompileScope( boolean pIncludeCompileScope )
    {
        this.includeCompileScope = pIncludeCompileScope;
        
        return this;
    }

    /**
     * @return {@link #includeRuntimeScope}
     */
    public boolean isIncludeRuntimeScope()
    {
        return includeRuntimeScope;
    }

    /**
     * @param pIncludeRuntimeScope true/false
     * @return {@link ScopeArtifactFilter}
     */
    public ScopeArtifactFilter setIncludeRuntimeScope( boolean pIncludeRuntimeScope )
    {
        this.includeRuntimeScope = pIncludeRuntimeScope;
        
        return this;
    }

    /**
     * @return {@link #includeTestScope}
     */
    public boolean isIncludeTestScope()
    {
        return includeTestScope;
    }

    /**
     * @param pIncludeTestScope {@link #includeTestScope}
     * @return {@link ScopeArtifactFilter}
     */
    public ScopeArtifactFilter setIncludeTestScope( boolean pIncludeTestScope )
    {
        this.includeTestScope = pIncludeTestScope;
        
        return this;
    }

    /**
     * @return {@link #includeProvidedScope}
     */
    public boolean isIncludeProvidedScope()
    {
        return includeProvidedScope;
    }

    /**
     * @param pIncludeProvidedScope yes/no.
     * @return {@link #ScopeArtifactFilter()}
     */
    public ScopeArtifactFilter setIncludeProvidedScope( boolean pIncludeProvidedScope )
    {
        this.includeProvidedScope = pIncludeProvidedScope;
        
        return this;
    }

    /**
     * @return {@link #includeSystemScope}
     */
    public boolean isIncludeSystemScope()
    {
        return includeSystemScope;
    }

    /** {@inheritDoc} */
    public ScopeArtifactFilter setIncludeSystemScope( boolean pIncludeSystemScope )
    {
        this.includeSystemScope = pIncludeSystemScope;
        
        return this;
    }
    
    /**
     * Manages the following scopes:
     * 
     * <ul>
     *   <li>system</li>
     *   <li>provided</li>
     *   <li>compile</li>
     * </ul>
     *
     * @param enabled whether specified scopes should be included
     * @return this instance 
     */
    public ScopeArtifactFilter setIncludeCompileScopeWithImplications( boolean enabled )
    {
        includeSystemScope = enabled;
        includeProvidedScope = enabled;
        includeCompileScope = enabled;
        
        return this;
    }
    
    /**
     * Manages the following scopes:
     * 
     * <ul>
     *   <li>compile</li>
     *   <li>runtime</li>
     * </ul>
     *
     * @param enabled whether specified scopes should be included
     * @return this instance 
     */
    public ScopeArtifactFilter setIncludeRuntimeScopeWithImplications( boolean enabled )
    {
        includeCompileScope = enabled;
        includeRuntimeScope = enabled;
        
        return this;
    }

    /**
     * Manages the following scopes:
     * 
     * <ul>
     *   <li>system</li>
     *   <li>provided</li>
     *   <li>compile</li>
     *   <li>runtime</li>
     *   <li>test</li>
     * </ul>
     *
     * @param enabled whether specified scopes should be included
     * @return this instance 
     */
    public ScopeArtifactFilter setIncludeTestScopeWithImplications( boolean enabled )
    {
        includeSystemScope = enabled;
        includeProvidedScope = enabled;
        includeCompileScope = enabled;
        includeRuntimeScope = enabled;
        includeTestScope = enabled;
        
        return this;
    }
    
    /**
     * Determine whether artifacts that have a null scope are included or excluded.
     *
     * @param enable whether null-scope should be included
     * @return this instance 
     */
    public ScopeArtifactFilter setIncludeNullScope( boolean enable )
    {
        includeNullScope = enable;
        
        return this;
    }
    
    /**
     * Reset hit counts and tracking of filtered artifacts, BUT NOT ENABLED SCOPES.
     *
     * @return this instance 
     */
    public ScopeArtifactFilter reset()
    {
        compileScopeHit = false;
        runtimeScopeHit = false;
        testScopeHit = false;
        providedScopeHit = false;
        systemScopeHit = false;
        filteredArtifactIds.clear();
        
        return this;
    }
}
