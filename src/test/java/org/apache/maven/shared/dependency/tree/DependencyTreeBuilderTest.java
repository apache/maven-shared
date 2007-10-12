package org.apache.maven.shared.dependency.tree;

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

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import junit.framework.AssertionFailedError;

import org.apache.maven.artifact.Artifact;
import org.apache.maven.artifact.DefaultArtifact;
import org.apache.maven.artifact.factory.ArtifactFactory;
import org.apache.maven.artifact.handler.DefaultArtifactHandler;
import org.apache.maven.artifact.repository.ArtifactRepository;
import org.apache.maven.artifact.repository.DefaultArtifactRepository;
import org.apache.maven.artifact.repository.layout.DefaultRepositoryLayout;
import org.apache.maven.artifact.resolver.ArtifactCollector;
import org.apache.maven.artifact.resolver.ArtifactResolutionResult;
import org.apache.maven.artifact.resolver.ResolutionNode;
import org.apache.maven.artifact.resolver.filter.ArtifactFilter;
import org.apache.maven.artifact.versioning.VersionRange;
import org.apache.maven.project.MavenProject;
import org.codehaus.plexus.PlexusTestCase;

/**
 * Tests <code>DependencyTreeBuilder</code>.
 * 
 * @author <a href="mailto:markhobson@gmail.com">Mark Hobson</a>
 * @version $Id$
 * @see DependencyTreeBuilder
 */
public class DependencyTreeBuilderTest extends PlexusTestCase
{
    // fields -----------------------------------------------------------------

    private DefaultDependencyTreeBuilder builder;

    private ArtifactRepository artifactRepository;

    private ArtifactFactory artifactFactory;

    private ArtifactMetadataSourceStub artifactMetadataSource;

    private ArtifactCollector artifactCollector;

    // TestCase methods -------------------------------------------------------

    /*
     * @see org.codehaus.plexus.PlexusTestCase#setUp()
     */
    protected void setUp() throws Exception
    {
        super.setUp();

        builder = (DefaultDependencyTreeBuilder) lookup( DependencyTreeBuilder.ROLE );

        String repositoryURL = getTestFile( "target/local-repo" ).toURI().toString();
        artifactRepository = new DefaultArtifactRepository( "local", repositoryURL, new DefaultRepositoryLayout() );

        artifactFactory = (ArtifactFactory) lookup( ArtifactFactory.ROLE );
        artifactMetadataSource = new ArtifactMetadataSourceStub();
        artifactCollector = (ArtifactCollector) lookup( ArtifactCollector.class.getName() );
    }

    /*
     * @see org.codehaus.plexus.PlexusTestCase#tearDown()
     */
    protected void tearDown() throws Exception
    {
        super.tearDown();

        builder = null;
    }

    // tests ------------------------------------------------------------------

    /**
     * Tests building a tree for a project with one dependency:
     * 
     * <pre>
     * g:p:t:1
     * \- g:a:t:1
     * </pre>
     * 
     * @throws DependencyTreeBuilderException 
     */
    public void testProjectWithDependency() throws DependencyTreeBuilderException
    {
        Artifact projectArtifact = createArtifact( "g:p:t:1" );
        Artifact childArtifact = createArtifact( "g:a:t:1" );

        MavenProject project = createProject( projectArtifact, new Artifact[] { childArtifact } );

        DependencyNode expectedRootNode = createNode( "g:p:t:1" );
        expectedRootNode.addChild( createNode( "g:a:t:1" ) );

        assertDependencyTree( expectedRootNode, project );
    }

    /**
     * Tests building a tree for a project with one transitive dependency:
     * 
     * <pre>
     * g:p:t:1
     * \- g:a:t:1
     *    \- g:b:t:1
     * </pre>
     *
     * @throws DependencyTreeBuilderException
     */
    public void testProjectWithTransitiveDependency() throws DependencyTreeBuilderException
    {
        Artifact projectArtifact = createArtifact( "g:p:t:1" );
        Artifact childArtifact = createArtifact( "g:a:t:1" );
        Artifact transitiveArtifact = createArtifact( "g:b:t:1" );
        addArtifactMetadata( childArtifact, transitiveArtifact );

        MavenProject project = createProject( projectArtifact, new Artifact[] { childArtifact } );

        DependencyNode expectedRootNode = createNode( "g:p:t:1" );
        DependencyNode childArtifactNode = createNode( "g:a:t:1" );
        expectedRootNode.addChild( childArtifactNode );
        childArtifactNode.addChild( createNode( "g:b:t:1" ) );

        assertDependencyTree( expectedRootNode, project );
    }

    /**
     * Tests building a tree for a project with a duplicate transitive dependency:
     * 
     * <pre>
     * g:p:t:1
     * +- g:a:t:1
     * |  \- g:c:t:1
     * \- g:b:t:1
     *    \- (g:c:t:1 - omitted for duplicate)
     * </pre>
     *
     * @throws DependencyTreeBuilderException
     */
    public void testProjectWithDuplicateDependency() throws DependencyTreeBuilderException
    {
        Artifact projectArtifact = createArtifact( "g:p:t:1" );
        Artifact child1Artifact = createArtifact( "g:a:t:1" );
        Artifact transitiveArtifact = createArtifact( "g:c:t:1" );
        Artifact child2Artifact = createArtifact( "g:b:t:1" );
        Artifact duplicateTransitiveArtifact = createArtifact( "g:c:t:1" );
        addArtifactMetadata( child1Artifact, transitiveArtifact );
        addArtifactMetadata( child2Artifact, duplicateTransitiveArtifact );

        MavenProject project = createProject( projectArtifact, new Artifact[] { child1Artifact, child2Artifact } );

        DependencyNode expectedRootNode = createNode( "g:p:t:1" );
        DependencyNode child1ArtifactNode = createNode( "g:a:t:1" );
        expectedRootNode.addChild( child1ArtifactNode );
        DependencyNode transitiveArtifactNode = createNode( "g:c:t:1" );
        child1ArtifactNode.addChild( transitiveArtifactNode );
        DependencyNode child2ArtifactNode = createNode( "g:b:t:1" );
        expectedRootNode.addChild( child2ArtifactNode );
        child2ArtifactNode.addChild( createNode( "g:c:t:1", DependencyNode.OMITTED_FOR_DUPLICATE, transitiveArtifactNode.getArtifact() ) );

        assertDependencyTree( expectedRootNode, project );
    }

    /**
     * Tests building a tree for a project with a dependency that has conflicting versions, where the nearest is
     * encountered first:
     * 
     * <pre>
     * g:p:t:1
     * +- g:a:t:1
     * \- g:b:t:1
     *    \- (g:a:t:2 - omitted for conflict with 1)
     * </pre>
     *
     * @throws DependencyTreeBuilderException
     */
    public void testProjectWithConflictDependencyVersionFirstWins() throws DependencyTreeBuilderException
    {
        Artifact projectArtifact = createArtifact( "g:p:t:1" );
        Artifact nearestArtifact = createArtifact( "g:a:t:1" );
        Artifact childArtifact = createArtifact( "g:b:t:1" );
        Artifact farthestArtifact = createArtifact( "g:a:t:2" );
        addArtifactMetadata( childArtifact, farthestArtifact );

        MavenProject project = createProject( projectArtifact, new Artifact[] { nearestArtifact, childArtifact } );

        DependencyNode expectedRootNode = createNode( "g:p:t:1" );
        DependencyNode nearestArtifactNode = createNode( "g:a:t:1" );
        expectedRootNode.addChild( nearestArtifactNode );
        DependencyNode childArtifactNode = createNode( "g:b:t:1" );
        expectedRootNode.addChild( childArtifactNode );
        childArtifactNode.addChild( createNode( "g:a:t:2", DependencyNode.OMITTED_FOR_CONFLICT, nearestArtifactNode.getArtifact() ) );

        assertDependencyTree( expectedRootNode, project );
    }

    /**
     * Tests building a tree for a project with a dependency that has conflicting versions, where the nearest is
     * encountered last:
     * 
     * <pre>
     * g:p:t:1
     * +- g:a:t:1
     * |  \- (g:b:t:2 - omitted for conflict with 1)
     * \- g:b:t:1
     * </pre>
     *
     * @throws DependencyTreeBuilderException
     */
    public void testProjectWithConflictDependencyVersionLastWins() throws DependencyTreeBuilderException
    {
        Artifact projectArtifact = createArtifact( "g:p:t:1" );
        Artifact childArtifact = createArtifact( "g:a:t:1" );
        Artifact farthestArtifact = createArtifact( "g:b:t:2" );
        Artifact nearestArtifact = createArtifact( "g:b:t:1" );
        addArtifactMetadata( childArtifact, farthestArtifact );

        MavenProject project = createProject( projectArtifact, new Artifact[] { childArtifact, nearestArtifact } );

        DependencyNode expectedRootNode = createNode( "g:p:t:1" );
        DependencyNode childArtifactNode = createNode( "g:a:t:1" );
        expectedRootNode.addChild( childArtifactNode );
        DependencyNode farthestArtifactNode = createNode( "g:b:t:1" );
        expectedRootNode.addChild( farthestArtifactNode );
        childArtifactNode.addChild( createNode( "g:b:t:2", DependencyNode.OMITTED_FOR_CONFLICT, farthestArtifactNode.getArtifact() ) );

        assertDependencyTree( expectedRootNode, project );
    }

    /**
     * Tests building a tree for a project with a dependency that has conflicting scopes, where the nearest is not
     * broadened since it is defined in the top-level POM:
     * 
     * <pre>
     * g:p:t:1
     * +- g:b:t:1:test (scope not updated to compile)
     * \- g:a:t:1
     *    \- (g:b:t:1:compile - omitted for duplicate)
     * </pre>
     *
     * @throws DependencyTreeBuilderException
     */
    public void testProjectWithConflictDependencyScopeCurrentPom() throws DependencyTreeBuilderException
    {
        Artifact projectArtifact = createArtifact( "g:p:t:1" );
        Artifact nearestArtifact = createArtifact( "g:b:t:1:test" );
        Artifact childArtifact = createArtifact( "g:a:t:1" );
        Artifact farthestArtifact = createArtifact( "g:b:t:1:compile" );
        addArtifactMetadata( childArtifact, farthestArtifact );

        MavenProject project = createProject( projectArtifact, new Artifact[] { nearestArtifact, childArtifact } );

        DependencyNode expectedRootNode = createNode( "g:p:t:1" );
        DependencyNode nearestArtifactNode = createNode( "g:b:t:1:test" );
        nearestArtifactNode.setFailedUpdateScope( "compile" );
        expectedRootNode.addChild( nearestArtifactNode );
        DependencyNode childArtifactNode = createNode( "g:a:t:1" );
        expectedRootNode.addChild( childArtifactNode );
        childArtifactNode.addChild( createNode( "g:b:t:1:compile", DependencyNode.OMITTED_FOR_DUPLICATE, nearestArtifactNode.getArtifact() ) );
        
        assertDependencyTree( expectedRootNode, project );
    }

    // TODO: fix when discussion resolved: http://www.mail-archive.com/dev@maven.apache.org/msg68011.html
    /*
    public void testProjectWithConflictDependencyScope() throws DependencyTreeBuilderException
    {
        Artifact projectArtifact = createArtifact( "g:p:t:1" );
        Artifact childArtifact = createArtifact( "g:a:t:1" );
        Artifact nearestArtifact = createArtifact( "g:c:t:1:test" );
        Artifact grandchildArtifact = createArtifact( "g:b:t:1" );
        Artifact farthestArtifact = createArtifact( "g:c:t:1:compile" );
        addArtifactMetadata( childArtifact, new Artifact[] { nearestArtifact, grandchildArtifact } );
        addArtifactMetadata( grandchildArtifact, farthestArtifact );

        MavenProject project = createProject( projectArtifact, new Artifact[] { childArtifact } );

        // TODO: i would have expected this..
//        DependencyNode expectedRootNode = createNode( "g:p:t:1" );
//        DependencyNode childArtifactNode = createNode( "g:a:t:1" );
//        expectedRootNode.addChild( childArtifactNode );
//        DependencyNode nearestArtifactNode = createNode( "g:c:t:1:compile" );
//        nearestArtifactNode.setOriginalScope( "test" );
//        childArtifactNode.addChild( nearestArtifactNode );
//        DependencyNode grandchildArtifactNode = createNode( "g:b:t:1" );
//        childArtifactNode.addChild( grandchildArtifactNode );
//        grandchildArtifactNode.addChild( createNode( "g:c:t:1:compile", DependencyNode.OMITTED_FOR_DUPLICATE, nearestArtifactNode.getArtifact() ) );

        DependencyNode expectedRootNode = createNode( "g:p:t:1" );
        DependencyNode childArtifactNode = createNode( "g:a:t:1" );
        expectedRootNode.addChild( childArtifactNode );
        DependencyNode farthestArtifactNode = createNode( "g:c:t:1:compile" );
        DependencyNode nearestArtifactNode = createNode( "g:c:t:1:compile", DependencyNode.OMITTED_FOR_DUPLICATE, farthestArtifactNode.getArtifact() );
        nearestArtifactNode.setOriginalScope( "test" );
        childArtifactNode.addChild( nearestArtifactNode );
        DependencyNode grandchildArtifactNode = createNode( "g:b:t:1" );
        childArtifactNode.addChild( grandchildArtifactNode );
        grandchildArtifactNode.addChild( farthestArtifactNode );
        
        assertDependencyTree( expectedRootNode, project );
    }
    */

    // TODO: fix when discussion resolved: http://www.mail-archive.com/dev@maven.apache.org/msg68011.html
    /*
    public void testProjectWithConflictDependencyScopeReversedOrder() throws DependencyTreeBuilderException
    {
        Artifact projectArtifact = createArtifact( "g:p:t:1" );
        Artifact childArtifact = createArtifact( "g:a:t:1" );
        Artifact nearestArtifact = createArtifact( "g:c:t:1:test" );
        Artifact grandchildArtifact = createArtifact( "g:b:t:1" );
        Artifact farthestArtifact = createArtifact( "g:c:t:1:compile" );
        addArtifactMetadata( childArtifact, new Artifact[] { grandchildArtifact, nearestArtifact } );
        addArtifactMetadata( grandchildArtifact, farthestArtifact );

        MavenProject project = createProject( projectArtifact, new Artifact[] { childArtifact } );

        // TODO: add expected results as per above test method
        DependencyNode expectedRootNode = createNode( "g:p:t:1" );
        DependencyNode childArtifactNode = createNode( "g:a:t:1" );
        expectedRootNode.addChild( childArtifactNode );
        DependencyNode grandchildArtifactNode = createNode( "g:b:t:1" );
        childArtifactNode.addChild( grandchildArtifactNode );
        DependencyNode farthestArtifactNode = createNode( "g:c:t:1:compile" );
        grandchildArtifactNode.addChild( farthestArtifactNode );
        DependencyNode nearestArtifactNode = createNode( "g:c:t:1:compile", DependencyNode.OMITTED_FOR_DUPLICATE, farthestArtifactNode.getArtifact() );
        nearestArtifactNode.setOriginalScope( "test" );
        childArtifactNode.addChild( nearestArtifactNode );
        
        assertDependencyTree( expectedRootNode, project );
    }
    */

    /**
     * Tests building a tree for a project with one transitive dependency whose version is fixed in dependency
     * management:
     * 
     * <pre>
     * g:p:t:1
     * \- g:a:t:1
     *    \- g:b:t:2 (version managed from 1)
     * </pre>
     * 
     * @throws DependencyTreeBuilderException
     */
    public void testProjectWithManagedTransitiveDependencyVersion() throws DependencyTreeBuilderException
    {
        Artifact projectArtifact = createArtifact( "g:p:t:1" );
        Artifact childArtifact = createArtifact( "g:a:t:1" );
        Artifact transitiveArtifact = createArtifact( "g:b:t:1" );
        Artifact managedTransitiveArtifact = createArtifact( "g:b:t:2" );
        addArtifactMetadata( childArtifact, transitiveArtifact );

        MavenProject project = createProject( projectArtifact, new Artifact[] { childArtifact } );
        setManagedVersionMap( project, Collections.singleton( managedTransitiveArtifact ) );

        DependencyNode expectedRootNode = createNode( "g:p:t:1" );
        DependencyNode childArtifactNode = createNode( "g:a:t:1" );
        expectedRootNode.addChild( childArtifactNode );
        DependencyNode managedTransitiveArtifactNode = createNode( "g:b:t:2" );
        managedTransitiveArtifactNode.setPremanagedVersion( "1" );
        childArtifactNode.addChild( managedTransitiveArtifactNode );

        assertDependencyTree( expectedRootNode, project );
    }

    /**
     * Tests building a tree for a project with one transitive dependency whose scope is fixed in dependency management:
     * 
     * <pre>
     * g:p:t:1
     * \- g:a:t:1
     *    \- g:b:t:1:test (scope managed from compile)
     * </pre>
     *
     * @throws DependencyTreeBuilderException
     */
    public void testProjectWithManagedTransitiveDependencyScope() throws DependencyTreeBuilderException
    {
        Artifact projectArtifact = createArtifact( "g:p:t:1" );
        Artifact childArtifact = createArtifact( "g:a:t:1" );
        Artifact transitiveArtifact = createArtifact( "g:b:t:1:compile" );
        Artifact managedTransitiveArtifact = createArtifact( "g:b:t:1:test" );
        addArtifactMetadata( childArtifact, transitiveArtifact );

        MavenProject project = createProject( projectArtifact, new Artifact[] { childArtifact } );
        setManagedVersionMap( project, Collections.singleton( managedTransitiveArtifact ) );

        DependencyNode expectedRootNode = createNode( "g:p:t:1" );
        DependencyNode childArtifactNode = createNode( "g:a:t:1" );
        expectedRootNode.addChild( childArtifactNode );
        DependencyNode managedTransitiveArtifactNode = createNode( "g:b:t:1:test" );
        managedTransitiveArtifactNode.setPremanagedScope( "compile" );
        childArtifactNode.addChild( managedTransitiveArtifactNode );

        assertDependencyTree( expectedRootNode, project );
    }

    /**
     * Tests building a tree for a project with one transitive dependency whose version and scope are fixed in
     * dependency management:
     * 
     * <pre>
     * g:p:t:1
     * \- g:a:t:1
     *    \- g:b:t:2:test (version managed from 1; scope managed from compile)
     * </pre>
     * 
     * @throws DependencyTreeBuilderException
     */
    public void testProjectWithManagedTransitiveDependencyVersionAndScope() throws DependencyTreeBuilderException
    {
        Artifact projectArtifact = createArtifact( "g:p:t:1" );
        Artifact childArtifact = createArtifact( "g:a:t:1" );
        Artifact transitiveArtifact = createArtifact( "g:b:t:1:compile" );
        Artifact managedTransitiveArtifact = createArtifact( "g:b:t:2:test" );
        addArtifactMetadata( childArtifact, transitiveArtifact );

        MavenProject project = createProject( projectArtifact, new Artifact[] { childArtifact } );
        setManagedVersionMap( project, Collections.singleton( managedTransitiveArtifact ) );

        DependencyNode expectedRootNode = createNode( "g:p:t:1" );
        DependencyNode childArtifactNode = createNode( "g:a:t:1" );
        expectedRootNode.addChild( childArtifactNode );
        DependencyNode managedTransitiveArtifactNode = createNode( "g:b:t:2:test" );
        managedTransitiveArtifactNode.setPremanagedVersion( "1" );
        managedTransitiveArtifactNode.setPremanagedScope( "compile" );
        childArtifactNode.addChild( managedTransitiveArtifactNode );

        assertDependencyTree( expectedRootNode, project );
    }
    
    /**
     * Tests building a tree for a project with a dependency that has conflicting versions and the version is also fixed
     * in dependency management:
     * 
     * <pre>
     * g:p:t:1
     * +- g:a:t:1
     * \- g:b:t:1
     *    \- (g:a:t:3 - version managed from 2; omitted for conflict with 1)
     * </pre>
     * 
     * @throws DependencyTreeBuilderException
     */
    public void testProjectWithManagedTransitiveDependencyVersionAndConflictDependencyVersion() throws DependencyTreeBuilderException
    {
        Artifact projectArtifact = createArtifact( "g:p:t:1" );
        Artifact nearestArtifact = createArtifact( "g:a:t:1" );
        Artifact childArtifact = createArtifact( "g:b:t:1" );
        Artifact farthestArtifact = createArtifact( "g:a:t:2" );
        Artifact managedTransitiveArtifact = createArtifact( "g:a:t:3" );
        addArtifactMetadata( childArtifact, farthestArtifact );

        MavenProject project = createProject( projectArtifact, new Artifact[] { nearestArtifact, childArtifact } );
        setManagedVersionMap( project, Collections.singleton( managedTransitiveArtifact ) );

        DependencyNode expectedRootNode = createNode( "g:p:t:1" );
        DependencyNode nearestArtifactNode = createNode( "g:a:t:1" );
        expectedRootNode.addChild( nearestArtifactNode );
        DependencyNode childArtifactNode = createNode( "g:b:t:1" );
        expectedRootNode.addChild( childArtifactNode );
        DependencyNode managedTransitiveArtifactNode = createNode( "g:a:t:3", DependencyNode.OMITTED_FOR_CONFLICT, nearestArtifactNode.getArtifact() );
        managedTransitiveArtifactNode.setPremanagedVersion( "2" );
        childArtifactNode.addChild( managedTransitiveArtifactNode );

        assertDependencyTree( expectedRootNode, project );
    }

    // TODO: reinstate when MNG-3236 fixed
    /*
    public void testProjectWithFilter() throws DependencyTreeBuilderException, ArtifactResolutionException
    {
        Artifact projectArtifact = createArtifact( "g:p:t:1" );
        Artifact child1Artifact = createArtifact( "g:a:t:1" );
        Artifact child2Artifact = createArtifact( "g:b:t:1:test" );

        MavenProject project = createProject( projectArtifact, new Artifact[] { child1Artifact, child2Artifact } );

        DependencyNode expectedRootNode = createNode( "g:p:t:1" );
        expectedRootNode.addChild( createNode( "g:a:t:1" ) );

        ArtifactFilter artifactFilter = new ScopeArtifactFilter( Artifact.SCOPE_COMPILE );
        
        assertDependencyTree( expectedRootNode, project, artifactFilter );
    }
    */

    // private methods --------------------------------------------------------
    
    private DependencyNode createNode( String id )
    {
        return createNode( id, DependencyNode.INCLUDED, null );
    }
    
    private DependencyNode createNode( String id, int state, Artifact relatedArtifact )
    {
        return new DependencyNode( createArtifact( id ), state, relatedArtifact );
    }
    
    private Artifact createArtifact( String id )
    {
        String[] tokens = id.split( ":" );

        String groupId = get( tokens, 0 );
        String artifactId = get( tokens, 1 );
        String type = get( tokens, 2, "jar" );
        String version = get( tokens, 3 );
        String scope = get( tokens, 4 );
        
        VersionRange versionRange = VersionRange.createFromVersion( version );

        return new DefaultArtifact( groupId, artifactId, versionRange, scope, type, null, new DefaultArtifactHandler() );
    }
    
    private MavenProject createProject( Artifact projectArtifact, Artifact[] dependencyArtifacts )
    {
        MavenProject project = new MavenProject();
        project.setArtifact( projectArtifact );
        // LinkedHashSet since order is significant when omitting conflicts
        project.setDependencyArtifacts( new LinkedHashSet( Arrays.asList( dependencyArtifacts ) ) );
        project.setManagedVersionMap( new HashMap() );
        project.setRemoteArtifactRepositories( Collections.EMPTY_LIST );
        return project;
    }

    private void addArtifactMetadata( Artifact artifact, Artifact dependencyArtifact )
    {
        addArtifactMetadata( artifact, new Artifact[] { dependencyArtifact } );
    }
    
    private void addArtifactMetadata( Artifact artifact, Artifact[] dependencyArtifacts )
    {
        addArtifactMetadata( artifact, new LinkedHashSet( Arrays.asList( dependencyArtifacts ) ) );
    }
    
    private void addArtifactMetadata( Artifact artifact, Set dependencyArtifacts )
    {
        artifactMetadataSource.addArtifactMetadata( artifact, dependencyArtifacts );
    }
    
    private void setManagedVersionMap( MavenProject project, Set managedArtifacts )
    {
        Map managedVersionMap = new HashMap();
        
        for ( Iterator iterator = managedArtifacts.iterator(); iterator.hasNext(); )
        {
            Artifact artifact = (Artifact) iterator.next();
            String managementKey = getManagementKey( artifact );
            
            managedVersionMap.put( managementKey, artifact );
        }

        project.setManagedVersionMap( managedVersionMap );
    }
    
    private String getManagementKey( Artifact artifact )
    {
        return artifact.getGroupId() + ":" + artifact.getArtifactId() + ":" + artifact.getType() + (artifact.getClassifier() != null ? ":" + artifact.getClassifier() : "");
    }
    
    private void assertDependencyTree( DependencyNode expectedRootNode, MavenProject project ) throws DependencyTreeBuilderException
    {
        assertDependencyTree( expectedRootNode, project, null );
    }
    
    private void assertDependencyTree( DependencyNode expectedRootNode, MavenProject project, ArtifactFilter artifactFilter ) throws DependencyTreeBuilderException
    {
        // assert built dependency tree is as expected
        
        DependencyNode actualRootNode =
            builder.buildDependencyTree( project, artifactRepository, artifactFactory, artifactMetadataSource,
                                         artifactFilter, artifactCollector );
        
        assertEquals( "Dependency tree", expectedRootNode, actualRootNode );
        
        // assert resolution tree is as expected
        
        ArtifactResolutionResult result = builder.getArtifactResolutionResult();
        
        assertTreeEquals( expectedRootNode, project, result );
    }
    
    private void assertTreeEquals( DependencyNode dependencyNode, MavenProject project, ArtifactResolutionResult resolutionResult )
    {
        List rootChildrenResolutionNodes = ResolutionNodeUtils.getRootChildrenResolutionNodes( project, resolutionResult );
        
        try
        {
            assertEquals( "Root node artifact", dependencyNode.getArtifact(), project.getArtifact() );
        
            assertNodesEquals( dependencyNode.getChildren(), rootChildrenResolutionNodes );
        }
        catch ( AssertionFailedError error )
        {
            StringBuffer buffer = new StringBuffer();
            
            buffer.append( error.getMessage() ).append( "; " );
            buffer.append( "expected dependency tree <" ).append( dependencyNode ).append( "> " );
            buffer.append( "actual resolution tree <" );
            ResolutionNodeUtils.append( buffer, project, resolutionResult );
            buffer.append( ">" );
            
            throw new AssertionFailedError( buffer.toString() );
        }
    }
    
    private void assertNodesEquals( List dependencyNodes, List resolutionNodes )
    {
        assertNodesEquals( dependencyNodes.iterator(), resolutionNodes.iterator() );
    }

    private void assertNodesEquals( Iterator dependencyNodesIterator, Iterator resolutionNodesIterator )
    {
        while ( dependencyNodesIterator.hasNext() && resolutionNodesIterator.hasNext() )
        {
            DependencyNode dependencyNode = (DependencyNode) dependencyNodesIterator.next();
            ResolutionNode resolutionNode = (ResolutionNode) resolutionNodesIterator.next();
            
            assertNodeEquals( dependencyNode, resolutionNode );
        }
        
        if ( dependencyNodesIterator.hasNext() || resolutionNodesIterator.hasNext() )
        {
            fail( "Node list size differs" );
        }
    }

    private void assertNodeEquals( DependencyNode dependencyNode, ResolutionNode resolutionNode )
    {
        assertEquals( "Node state", dependencyNode.getState() == DependencyNode.INCLUDED, resolutionNode.isActive() );
        
        assertEquals( "Node artifact", dependencyNode.getArtifact(), resolutionNode.getArtifact() );
        
        assertNodesEquals( dependencyNode.getChildren().iterator(), resolutionNode.getChildrenIterator() );
    }
    
    private String get( String[] array, int index )
    {
        return get( array, index, null );
    }
    
    private String get( String[] array, int index, String defaultValue )
    {
        return ( index < array.length ) ? array[index] : defaultValue;
    }
}
