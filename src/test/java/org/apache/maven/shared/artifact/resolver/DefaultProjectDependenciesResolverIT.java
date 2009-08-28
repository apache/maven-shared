package org.apache.maven.shared.artifact.resolver;

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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static junit.framework.Assert.fail;

import org.apache.maven.ProjectDependenciesResolver;
import org.apache.maven.artifact.Artifact;
import org.apache.maven.artifact.factory.ArtifactFactory;
import org.apache.maven.artifact.metadata.ArtifactMetadataSource;
import org.apache.maven.artifact.repository.ArtifactRepository;
import org.apache.maven.artifact.repository.DefaultArtifactRepository;
import org.apache.maven.artifact.repository.layout.ArtifactRepositoryLayout;
import org.apache.maven.artifact.resolver.ArtifactNotFoundException;
import org.apache.maven.artifact.resolver.ArtifactResolutionException;
import org.apache.maven.artifact.resolver.ArtifactResolver;
import org.apache.maven.execution.MavenSession;
import org.apache.maven.model.Model;
import org.apache.maven.model.io.xpp3.MavenXpp3Writer;
import org.apache.maven.project.DefaultProjectBuilderConfiguration;
import org.apache.maven.project.MavenProject;
import org.apache.maven.project.MavenProjectBuilder;
import org.apache.maven.project.ProjectBuilderConfiguration;
import org.apache.maven.project.ProjectBuildingException;
import org.apache.maven.settings.Settings;
import org.apache.maven.shared.artifact.resolver.testutil.ModelCreator;
import org.codehaus.plexus.DefaultPlexusContainer;
import org.codehaus.plexus.PlexusContainer;
import org.codehaus.plexus.PlexusContainerException;
import org.codehaus.plexus.component.repository.exception.ComponentLifecycleException;
import org.codehaus.plexus.component.repository.exception.ComponentLookupException;
import org.codehaus.plexus.util.FileUtils;
import org.codehaus.plexus.util.IOUtil;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Set;


public class DefaultProjectDependenciesResolverIT
{
    
    private static PlexusContainer container;
    
    private static ArtifactResolver artifactResolver;
    
    private static ArtifactMetadataSource metadataSource;
    
    private static ArtifactFactory artifactFactory;
    
    private static MavenProjectBuilder projectBuilder;
    
    private static DefaultProjectDependenciesResolver resolver;
    
    private static MavenSession session;

    private static File localRepoDir;
    
    private static File pomsDir;

    private static ProjectBuilderConfiguration pbConfig;
    
    @Test
    public void resolveSingleDependency_SingleProject_CompileScope()
        throws ArtifactResolutionException, ArtifactNotFoundException, IOException, ProjectBuildingException
    {
        String gid = "org.codehaus.plexus";
        String aid = "plexus-utils";
        String version = "1.5.15";
        
        Model model = new ModelCreator().withDefaultCoordinate().withDependency( gid, aid, version ).getModel();
        
        MavenProject project = writeAndBuild( model, "pom.1dep-1project-compile-scope.xml" );
        
        Collection<String> scopes = Collections.singleton( Artifact.SCOPE_COMPILE );
        
        Set<Artifact> result = resolver.resolve( project, scopes, session );
        
        assertSingleArtifact( result, gid, aid, version );
    }
    
    @Test
    public void resolveUsingDependencyManagementForTransitives_SingleProject_CompileScope()
        throws ArtifactResolutionException, ArtifactNotFoundException, IOException, ProjectBuildingException
    {
        String gid = "org.apache.maven";
        String aid = "maven-project";
        String version = "2.2.1";
        
        String maid = "maven-model";
        String mversion = "2.1.0";
        
        Model model = new ModelCreator().withDefaultCoordinate()
                                        .withDependency( gid, aid, version )
                                        .withManagedDependency( gid, maid, mversion )
                                        .getModel();
        
        MavenProject project = writeAndBuild( model, "pom.managed-transitive-dep-1project-compile-scope.xml" );
        
        Collection<String> scopes = Collections.singleton( Artifact.SCOPE_COMPILE );
        
        Set<Artifact> result = resolver.resolve( project, scopes, session );
        
        assertNotNull( result );
        
//        System.out.println( "Got: " + result.size() + " results:" );
//        for ( Artifact artifact : result )
//        {
//            System.out.println( "\n- " + artifact.getId() );
//        }
        
        assertArtifactPresent( result, gid, aid, version );
        assertArtifactPresent( result, gid, maid, mversion );
    }
    
    @Test
    public void resolveUsingDependencyManagementIntermittently_TwoProjects_CompileScope_FirstWins()
        throws ArtifactResolutionException, ArtifactNotFoundException, IOException, ProjectBuildingException
    {
        String gid = "org.apache.maven";
        String aid = "maven-project";
        String version = "2.2.1";
        
        String maid = "maven-model";
        String mversion = "2.1.0";
        
        Model model1 = new ModelCreator().withDefaultCoordinate()
                                        .withDependency( gid, aid, version )
                                        .withManagedDependency( gid, maid, mversion )
                                        .getModel();
        
        MavenProject project1 = writeAndBuild( model1, "pom.managed-transitive-dep-2proj-compile-scope-A.xml" );
        
        Model model2 = new ModelCreator().withDefaultCoordinate().withDependency( gid, aid, version ).getModel();
        MavenProject project2 = writeAndBuild( model2, "pom.managed-transitive-dep-2proj-compile-scope-B.xml" );
        
        Set<MavenProject> projects = new LinkedHashSet<MavenProject>();
        projects.add( project1 );
        projects.add( project2 );

        Collection<String> scopes = Collections.singleton( Artifact.SCOPE_COMPILE );
        
        Set<Artifact> result = resolver.resolve( projects, scopes, session );
        
        assertNotNull( result );
        
//        System.out.println( "Got: " + result.size() + " results:" );
//        for ( Artifact artifact : result )
//        {
//            System.out.println( "\n- " + artifact.getId() );
//        }
        
        assertArtifactPresent( result, gid, aid, version );
        assertArtifactPresent( result, gid, maid, mversion );
    }
    
    private void assertArtifactPresent( Set<Artifact> result, String gid, String aid, String version )
    {
        for ( Artifact artifact : result )
        {
            if ( gid.equals( artifact.getGroupId() ) && aid.equals( artifact.getArtifactId() )
                && version.equals( artifact.getVersion() ) )
            {
                return;
            }
        }
        
        fail( "Did not find required artifact: " + gid + ":" + aid + ":" + version + " in resolution result." );
    }

    @Test
    public void resolveSingleDependency_TwoProjects_CompileScope()
        throws ArtifactResolutionException, ArtifactNotFoundException, IOException, ProjectBuildingException
    {
        String gid = "org.codehaus.plexus";
        String aid = "plexus-utils";
        String version = "1.5.15";
        
        Model model1 = new ModelCreator().withDefaultCoordinate().withDependency( gid, aid, version ).getModel();
        MavenProject project1 = writeAndBuild( model1, "pom.1dep-2proj-compile-scope-A.xml" );
        
        Model model2 = new ModelCreator().withDefaultCoordinate().withDependency( gid, aid, version ).getModel();
        MavenProject project2 = writeAndBuild( model2, "pom.1dep-2proj-compile-scope-B.xml" );
        
        Set<MavenProject> projects = new LinkedHashSet<MavenProject>();
        projects.add( project1 );
        projects.add( project2 );
        
        Collection<String> scopes = Collections.singleton( Artifact.SCOPE_COMPILE );
        
        Set<Artifact> result = resolver.resolve( projects, scopes, session );
        
        assertSingleArtifact( result, gid, aid, version );
    }
    
    @Test
    public void resolveSingleDependency_TwoVersions_TwoProjects_CompileScope_FirstWins()
        throws ArtifactResolutionException, ArtifactNotFoundException, IOException, ProjectBuildingException
    {
        String gid = "org.codehaus.plexus";
        String aid = "plexus-utils";
        String version = "1.5.15";
        
        Model model1 = new ModelCreator().withDefaultCoordinate().withDependency( gid, aid, version ).getModel();
        MavenProject project1 = writeAndBuild( model1, "pom.1dep-2proj-compile-scope-A.xml" );
        
        String version2 = "1.5.14";
        
        Model model2 = new ModelCreator().withDefaultCoordinate().withDependency( gid, aid, version2 ).getModel();
        MavenProject project2 = writeAndBuild( model2, "pom.1dep-2proj-compile-scope-B.xml" );
        
        Set<MavenProject> projects = new LinkedHashSet<MavenProject>();
        projects.add( project1 );
        projects.add( project2 );
        
        Collection<String> scopes = Collections.singleton( Artifact.SCOPE_COMPILE );
        
        Set<Artifact> result = resolver.resolve( projects, scopes, session );
        
        assertSingleArtifact( result, gid, aid, version );
    }
    
    private void assertSingleArtifact( Set<Artifact> result, String gid, String aid, String version )
    {
        assertNotNull( result );
        assertEquals( 1, result.size() );
        
        Artifact a = result.iterator().next();
        assertEquals( gid, a.getGroupId() );
        assertEquals( aid, a.getArtifactId() );
        assertEquals( version, a.getVersion() );
        
        assertTrue( a.isResolved() );
        assertTrue( a.getFile().exists() );
    }

    private MavenProject writeAndBuild( Model model, String filename )
        throws IOException, ProjectBuildingException
    {
        File pomFile = new File( pomsDir, filename );
        
        FileWriter writer = null;
        try
        {
            writer = new FileWriter( pomFile );
            new MavenXpp3Writer().write( writer, model );
        }
        finally
        {
            IOUtil.close( writer );
        }
        
        return projectBuilder.build( pomFile, pbConfig );
    }

    @BeforeClass
    public static void bootstrap()
        throws PlexusContainerException, ComponentLookupException, IOException
    {
        container = new DefaultPlexusContainer();
        container.initialize();
        container.start();
        
        artifactResolver = (ArtifactResolver) container.lookup( ArtifactResolver.class.getName() );
        metadataSource = (ArtifactMetadataSource) container.lookup( ArtifactMetadataSource.class.getName(), "maven" );
        artifactFactory = (ArtifactFactory) container.lookup( ArtifactFactory.class.getName() );
        projectBuilder = (MavenProjectBuilder) container.lookup( MavenProjectBuilder.class.getName() );
        
        resolver = (DefaultProjectDependenciesResolver) container.lookup( ProjectDependenciesResolver.class.getName(), "default" );
        
        localRepoDir = newTempDir( "local-repo" );
        pomsDir = newTempDir( "poms" );
        
        ArtifactRepositoryLayout layout = (ArtifactRepositoryLayout) container.lookup( ArtifactRepositoryLayout.class.getName(), "default" );
        
        ArtifactRepository localRepo = new DefaultArtifactRepository( "local", localRepoDir.getAbsolutePath(), layout );
        
        session = new MavenSession( container, new Settings(), localRepo, null, null, null, null, null, null );
        
        pbConfig = new DefaultProjectBuilderConfiguration().setLocalRepository( localRepo );
    }
    
    private static File newTempDir( String basename )
        throws IOException
    {
        File dir = File.createTempFile( basename + ".", ".dir" );
        dir.delete();
        dir.mkdirs();
        
        return dir;
    }

    @AfterClass
    public static void shutdown()
    {
        try
        {
            container.release( resolver );
            container.release( artifactResolver );
            container.release( metadataSource );
            container.release( artifactFactory );
            container.release( projectBuilder );
            
            container.dispose();
        }
        catch ( ComponentLifecycleException e )
        {
        }
        
        try
        {
            FileUtils.forceDelete( localRepoDir );
            FileUtils.forceDelete( pomsDir );
        }
        catch ( IOException e )
        {
        }
    }

}
