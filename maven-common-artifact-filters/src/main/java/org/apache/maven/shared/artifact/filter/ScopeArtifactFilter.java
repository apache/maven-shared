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
 * {@link ArtifactFilter} implementation that selects artifacts based on their
 * scopes.
 * <br/>
 * <b>NOTE:</b> None of the fine-grained scopes imply other scopes when enabled;
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

    public String toString()
    {
        return "Scope filter [null-scope=" + includeNullScope + ", compile=" + includeCompileScope + ", runtime="
            + includeRuntimeScope + ", test=" + includeTestScope + ", provided=" + includeProvidedScope + ", system="
            + includeSystemScope + "]";
    }

    public void reportFilteredArtifacts( Logger logger )
    {
        if ( !filteredArtifactIds.isEmpty() && logger.isDebugEnabled() )
        {
            StringBuffer buffer = new StringBuffer( "The following artifacts were removed by this filter: " );

            for ( String artifactId : filteredArtifactIds )
            {
                buffer.append( '\n' ).append( artifactId );
            }

            logger.debug( buffer.toString() );
        }
    }

    public void reportMissedCriteria( Logger logger )
    {
        if ( logger.isDebugEnabled() )
        {
            StringBuffer buffer = new StringBuffer();

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
    
    public boolean isIncludeCompileScope()
    {
        return includeCompileScope;
    }

    public ScopeArtifactFilter setIncludeCompileScope( boolean includeCompileScope )
    {
        this.includeCompileScope = includeCompileScope;
        
        return this;
    }

    public boolean isIncludeRuntimeScope()
    {
        return includeRuntimeScope;
    }

    public ScopeArtifactFilter setIncludeRuntimeScope( boolean includeRuntimeScope )
    {
        this.includeRuntimeScope = includeRuntimeScope;
        
        return this;
    }

    public boolean isIncludeTestScope()
    {
        return includeTestScope;
    }

    public ScopeArtifactFilter setIncludeTestScope( boolean includeTestScope )
    {
        this.includeTestScope = includeTestScope;
        
        return this;
    }

    public boolean isIncludeProvidedScope()
    {
        return includeProvidedScope;
    }

    public ScopeArtifactFilter setIncludeProvidedScope( boolean includeProvidedScope )
    {
        this.includeProvidedScope = includeProvidedScope;
        
        return this;
    }

    public boolean isIncludeSystemScope()
    {
        return includeSystemScope;
    }

    public ScopeArtifactFilter setIncludeSystemScope( boolean includeSystemScope )
    {
        this.includeSystemScope = includeSystemScope;
        
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
     */
    public ScopeArtifactFilter setIncludeNullScope( boolean enable )
    {
        includeNullScope = enable;
        
        return this;
    }
    
    /**
     * Reset hit counts and tracking of filtered artifacts, BUT NOT ENABLED SCOPES.
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
