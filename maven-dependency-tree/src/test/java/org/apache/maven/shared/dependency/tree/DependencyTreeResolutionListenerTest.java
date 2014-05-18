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

import org.apache.maven.artifact.Artifact;
import org.apache.maven.artifact.DefaultArtifact;
import org.apache.maven.artifact.handler.DefaultArtifactHandler;
import org.apache.maven.artifact.versioning.VersionRange;
import org.apache.maven.plugin.testing.SilentLog;

/**
 * Tests <code>DependencyTreeResolutionListener</code>.
 * 
 * @author Edwin Punzalan
 * @author <a href="mailto:markhobson@gmail.com">Mark Hobson</a>
 * @version $Id$
 * @see DependencyTreeResolutionListener
 */
public class DependencyTreeResolutionListenerTest
    extends AbstractDependencyNodeTest
{
    // fields -----------------------------------------------------------------

    private DependencyTreeResolutionListener listener;

    // TestCase methods -------------------------------------------------------

    /**
     * {@inheritDoc}
     */
    protected void setUp()
        throws Exception
    {
        super.setUp();

        listener = new DependencyTreeResolutionListener( new SilentLog() );
    }

    // tests ------------------------------------------------------------------

    public void testSimpleDependencyTree()
    {
        Artifact projectArtifact = createArtifact( "test-project", "project-artifact", "1.0" );
        listener.includeArtifact( projectArtifact );

        listener.startProcessChildren( projectArtifact );

        Artifact depArtifact01 = createArtifact( "test-dep", "dependency-one", "1.0" );
        listener.includeArtifact( depArtifact01 );

        Artifact depArtifact02 = createArtifact( "test-dep", "dependency-two", "1.0" );
        listener.includeArtifact( depArtifact02 );

        Artifact depArtifact03 = createArtifact( "test-dep", "dependency-three", "1.0" );
        listener.includeArtifact( depArtifact03 );

        listener.endProcessChildren( projectArtifact );

        DependencyNode projectArtifactNode = new DependencyNode( projectArtifact );
        projectArtifactNode.addChild( new DependencyNode( depArtifact01 ) );
        projectArtifactNode.addChild( new DependencyNode( depArtifact02 ) );
        projectArtifactNode.addChild( new DependencyNode( depArtifact03 ) );

        assertEquals( projectArtifactNode, listener.getRootNode() );
    }

    public void testSimpleDepTreeWithTransitiveDeps()
    {
        Artifact projectArtifact = createArtifact( "test-project", "project-artifact", "1.0" );
        listener.includeArtifact( projectArtifact );

        listener.startProcessChildren( projectArtifact );

        Artifact depArtifact1 = createArtifact( "test-dep", "dependency-one", "1.0" );
        listener.includeArtifact( depArtifact1 );

        listener.startProcessChildren( depArtifact1 );

        Artifact depArtifact01 = createArtifact( "test-dep", "dependency-zero-one", "1.0" );
        listener.includeArtifact( depArtifact01 );

        Artifact depArtifact02 = createArtifact( "test-dep", "dependency-zero-two", "1.0" );
        listener.includeArtifact( depArtifact02 );

        listener.endProcessChildren( depArtifact1 );

        Artifact depArtifact2 = createArtifact( "test-dep", "dependency-two", "1.0" );
        listener.includeArtifact( depArtifact2 );

        Artifact depArtifact3 = createArtifact( "test-dep", "dependency-three", "1.0" );
        listener.includeArtifact( depArtifact3 );

        listener.endProcessChildren( projectArtifact );

        DependencyNode projectArtifactNode = new DependencyNode( projectArtifact );
        DependencyNode depArtifact1Node = new DependencyNode( depArtifact1 );
        projectArtifactNode.addChild( depArtifact1Node );
        depArtifact1Node.addChild( new DependencyNode( depArtifact01 ) );
        depArtifact1Node.addChild( new DependencyNode( depArtifact02 ) );
        projectArtifactNode.addChild( new DependencyNode( depArtifact2 ) );
        projectArtifactNode.addChild( new DependencyNode( depArtifact3 ) );

        assertEquals( projectArtifactNode, listener.getRootNode() );
    }

    public void testComplexDependencyTree()
    {
        Artifact projectArtifact = createArtifact( "test-project", "project-artifact", "1.0" );
        listener.includeArtifact( projectArtifact );

        listener.startProcessChildren( projectArtifact );

        Artifact depArtifact1 = createArtifact( "test-dep", "dependency-one", "jar", "1.0", Artifact.SCOPE_COMPILE );
        listener.includeArtifact( depArtifact1 );

        listener.startProcessChildren( depArtifact1 );

        Artifact depArtifact11 = createArtifact( "test-dep", "dependency-zero-one", "1.0" );
        listener.includeArtifact( depArtifact11 );

        Artifact depArtifact12 = createArtifact( "test-dep", "dependency-zero-two", "1.0" );
        listener.includeArtifact( depArtifact12 );

        listener.startProcessChildren( depArtifact12 );

        Artifact depArtifact121 = createArtifact( "test-dep", "dep-zero-two-1", "1.0" );
        listener.includeArtifact( depArtifact121 );

        listener.endProcessChildren( depArtifact12 );

        listener.endProcessChildren( depArtifact1 );

        Artifact depArtifact2 = createArtifact( "test-dep", "dependency-two", "jar", "1.0", Artifact.SCOPE_TEST );
        listener.includeArtifact( depArtifact2 );

        listener.startProcessChildren( depArtifact2 );

        Artifact depArtifact21 = createArtifact( "test-dep", "dep-zero-two-1", "1.0" );
        listener.omitForNearer( depArtifact121, depArtifact21 );
        listener.includeArtifact( depArtifact21 );

        listener.endProcessChildren( depArtifact2 );

        Artifact depArtifact3 = createArtifact( "test-dep", "dependency-three", "jar", "1.0", Artifact.SCOPE_COMPILE );
        listener.includeArtifact( depArtifact3 );

        listener.endProcessChildren( projectArtifact );

        DependencyNode projectArtifactNode = new DependencyNode( projectArtifact );
        DependencyNode depArtifact1Node = new DependencyNode( depArtifact1 );
        projectArtifactNode.addChild( depArtifact1Node );
        depArtifact1Node.addChild( new DependencyNode( depArtifact11 ) );
        DependencyNode depArtifact12Node = new DependencyNode( depArtifact12 );
        depArtifact1Node.addChild( depArtifact12Node );
        depArtifact12Node.addChild( new DependencyNode( depArtifact121, DependencyNode.OMITTED_FOR_DUPLICATE,
                                                        depArtifact21 ) );
        DependencyNode depArtifact2Node = new DependencyNode( depArtifact2 );
        projectArtifactNode.addChild( depArtifact2Node );
        depArtifact2Node.addChild( new DependencyNode( depArtifact21 ) );
        projectArtifactNode.addChild( new DependencyNode( depArtifact3 ) );

        assertEquals( projectArtifactNode, listener.getRootNode() );
    }

    public void testIncludeArtifactDuplicate()
    {
        Artifact projectArtifact = createArtifact( "test-project", "project-artifact", "1.0" );
        listener.includeArtifact( projectArtifact );

        listener.startProcessChildren( projectArtifact );

        Artifact depArtifact1 = createArtifact( "test-dep", "dependency", "1.0" );
        listener.includeArtifact( depArtifact1 );

        Artifact depArtifact2 = createArtifact( "test-dep", "dependency", "1.0" );
        listener.omitForNearer( depArtifact1, depArtifact2 );
        listener.includeArtifact( depArtifact2 );

        listener.endProcessChildren( projectArtifact );

        DependencyNode projectArtifactNode = new DependencyNode( projectArtifact );
        projectArtifactNode.addChild( new DependencyNode( depArtifact1, DependencyNode.OMITTED_FOR_DUPLICATE,
                                                          depArtifact2 ) );
        projectArtifactNode.addChild( new DependencyNode( depArtifact2 ) );

        assertEquals( projectArtifactNode, listener.getRootNode() );
    }

    public void testIncludeArtifactDuplicateWithChildren()
    {
        Artifact projectArtifact = createArtifact( "test-project", "project-artifact", "1.0" );
        listener.includeArtifact( projectArtifact );

        listener.startProcessChildren( projectArtifact );

        Artifact depArtifact1 = createArtifact( "test-dep", "dependency", "1.0" );
        listener.includeArtifact( depArtifact1 );

        listener.startProcessChildren( depArtifact1 );

        Artifact depArtifact11 = createArtifact( "test-dep", "child", "1.0" );
        listener.includeArtifact( depArtifact11 );

        listener.endProcessChildren( depArtifact1 );

        Artifact depArtifact2 = createArtifact( "test-dep", "dependency", "1.0" );
        listener.omitForNearer( depArtifact1, depArtifact2 );
        listener.includeArtifact( depArtifact2 );

        listener.startProcessChildren( depArtifact2 );

        Artifact depArtifact21 = createArtifact( "test-dep", "child", "1.0" );
        listener.includeArtifact( depArtifact21 );

        listener.endProcessChildren( depArtifact2 );

        listener.endProcessChildren( projectArtifact );

        DependencyNode projectArtifactNode = new DependencyNode( projectArtifact );
        DependencyNode depArtifact1Node =
            new DependencyNode( depArtifact1, DependencyNode.OMITTED_FOR_DUPLICATE, depArtifact2 );
        projectArtifactNode.addChild( depArtifact1Node );
        DependencyNode depArtifact2Node = new DependencyNode( depArtifact2 );
        projectArtifactNode.addChild( depArtifact2Node );
        depArtifact2Node.addChild( new DependencyNode( depArtifact21 ) );

        assertEquals( projectArtifactNode, listener.getRootNode() );
    }

    public void testOmitForConflictKept()
    {
        Artifact projectArtifact = createArtifact( "test-project", "project-artifact", "1.0" );
        listener.includeArtifact( projectArtifact );

        listener.startProcessChildren( projectArtifact );

        Artifact depArtifact1 = createArtifact( "test-dep", "dependency", "1.0" );
        listener.includeArtifact( depArtifact1 );

        Artifact depArtifact2 = createArtifact( "test-dep", "dependency", "2.0" );
        listener.omitForNearer( depArtifact1, depArtifact2 );
        listener.includeArtifact( depArtifact2 );

        listener.endProcessChildren( projectArtifact );

        DependencyNode projectArtifactNode = new DependencyNode( projectArtifact );
        projectArtifactNode.addChild( new DependencyNode( depArtifact1, DependencyNode.OMITTED_FOR_CONFLICT,
                                                          depArtifact2 ) );
        projectArtifactNode.addChild( new DependencyNode( depArtifact2 ) );

        assertEquals( projectArtifactNode, listener.getRootNode() );
    }

    public void testOmitForConflictKeptWithChildren()
    {
        Artifact projectArtifact = createArtifact( "test-project", "project-artifact", "1.0" );
        listener.includeArtifact( projectArtifact );

        listener.startProcessChildren( projectArtifact );

        Artifact depArtifact1 = createArtifact( "test-dep", "dependency", "1.0" );
        listener.includeArtifact( depArtifact1 );

        listener.startProcessChildren( depArtifact1 );

        Artifact depArtifact11 = createArtifact( "test-dep", "child", "1.0" );
        listener.includeArtifact( depArtifact11 );

        listener.endProcessChildren( depArtifact1 );

        Artifact depArtifact2 = createArtifact( "test-dep", "dependency", "2.0" );
        listener.omitForNearer( depArtifact1, depArtifact2 );
        listener.includeArtifact( depArtifact2 );

        listener.startProcessChildren( depArtifact2 );

        Artifact depArtifact21 = createArtifact( "test-dep", "child", "2.0" );
        listener.includeArtifact( depArtifact21 );

        listener.endProcessChildren( depArtifact2 );

        listener.endProcessChildren( projectArtifact );

        DependencyNode projectArtifactNode = new DependencyNode( projectArtifact );
        projectArtifactNode.addChild( new DependencyNode( depArtifact1, DependencyNode.OMITTED_FOR_CONFLICT,
                                                          depArtifact2 ) );
        DependencyNode depArtifact2Node = new DependencyNode( depArtifact2 );
        projectArtifactNode.addChild( depArtifact2Node );
        depArtifact2Node.addChild( new DependencyNode( depArtifact21 ) );

        assertEquals( projectArtifactNode, listener.getRootNode() );
    }

    public void testOmitForConflictOmitted()
    {
        Artifact projectArtifact = createArtifact( "test-project", "project-artifact", "1.0" );
        listener.includeArtifact( projectArtifact );

        listener.startProcessChildren( projectArtifact );

        Artifact depArtifact2 = createArtifact( "test-dep", "dependency", "2.0" );
        listener.includeArtifact( depArtifact2 );

        Artifact depArtifact1 = createArtifact( "test-dep", "dependency", "1.0" );
        listener.omitForNearer( depArtifact1, depArtifact2 );

        listener.endProcessChildren( projectArtifact );

        DependencyNode projectArtifactNode = new DependencyNode( projectArtifact );
        projectArtifactNode.addChild( new DependencyNode( depArtifact2 ) );
        projectArtifactNode.addChild( new DependencyNode( depArtifact1, DependencyNode.OMITTED_FOR_CONFLICT,
                                                          depArtifact2 ) );

        assertEquals( projectArtifactNode, listener.getRootNode() );
    }

    public void testOmitForConflictOmittedWithChildren()
    {
        Artifact projectArtifact = createArtifact( "test-project", "project-artifact", "1.0" );
        listener.includeArtifact( projectArtifact );

        listener.startProcessChildren( projectArtifact );

        Artifact depArtifact2 = createArtifact( "test-dep", "dependency", "2.0" );
        listener.includeArtifact( depArtifact2 );

        listener.startProcessChildren( depArtifact2 );

        Artifact depArtifact21 = createArtifact( "test-dep", "child", "2.0" );
        listener.includeArtifact( depArtifact21 );

        listener.endProcessChildren( depArtifact2 );

        Artifact depArtifact1 = createArtifact( "test-dep", "dependency", "1.0" );
        listener.omitForNearer( depArtifact1, depArtifact2 );

        listener.startProcessChildren( depArtifact1 );

        Artifact depArtifact11 = createArtifact( "test-dep", "child", "1.0" );
        listener.includeArtifact( depArtifact11 );

        listener.endProcessChildren( depArtifact1 );

        listener.endProcessChildren( projectArtifact );

        DependencyNode projectArtifactNode = new DependencyNode( projectArtifact );
        DependencyNode depArtifact2Node = new DependencyNode( depArtifact2 );
        projectArtifactNode.addChild( depArtifact2Node );
        depArtifact2Node.addChild( new DependencyNode( depArtifact21 ) );
        projectArtifactNode.addChild( new DependencyNode( depArtifact1, DependencyNode.OMITTED_FOR_CONFLICT,
                                                          depArtifact2 ) );

        assertEquals( projectArtifactNode, listener.getRootNode() );
    }

    public void testOmitForCycle()
    {
        Artifact projectArtifact = createArtifact( "test-project", "project-artifact", "1.0" );
        listener.includeArtifact( projectArtifact );

        listener.startProcessChildren( projectArtifact );

        listener.omitForCycle( projectArtifact );

        listener.endProcessChildren( projectArtifact );

        DependencyNode projectArtifactNode = new DependencyNode( projectArtifact );
        projectArtifactNode.addChild( new DependencyNode( projectArtifact, DependencyNode.OMITTED_FOR_CYCLE ) );

        assertEquals( projectArtifactNode, listener.getRootNode() );
    }

    public void testAddNode()
    {
        Artifact a1 = createArtifact( "test-project", "project-artifact", "1.0" );
        listener.addNode( a1 );
        listener.startProcessChildren( a1 );
        Artifact a2 = createArtifact( "test-project", "project-artifact", "1.1" );
        listener.addNode( a2 );
        assertEquals( 1, listener.getRootNode().getChildren().size() );
    }

    // protected methods ------------------------------------------------------

    /**
     * {@inheritDoc}
     */
    protected Artifact createArtifact( String groupId, String artifactId, String type, String version, String scope )
    {
        // TODO: use super.createArtifact when possible

        VersionRange versionRange = VersionRange.createFromVersion( version );

        return new DefaultArtifact( groupId, artifactId, versionRange, scope, "jar", null,
                                    new DefaultArtifactHandler(), false );
    }
}
