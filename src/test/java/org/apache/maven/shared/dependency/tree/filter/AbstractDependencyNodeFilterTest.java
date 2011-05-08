package org.apache.maven.shared.dependency.tree.filter;

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

import org.apache.maven.shared.dependency.tree.DependencyNode;
import org.jmock.Mock;
import org.jmock.MockObjectTestCase;

/**
 * Provides utility methods for testing dependency node filters.
 * 
 * @author <a href="mailto:markhobson@gmail.com">Mark Hobson</a>
 * @version $Id$
 */
public abstract class AbstractDependencyNodeFilterTest
    extends MockObjectTestCase
{
    // protected methods ---------------------------------------------------------

    protected DependencyNodeFilter createDependencyNodeFilter( DependencyNode node, boolean accept )
    {
        Mock mock = mock( DependencyNodeFilter.class );

        mock.stubs().method( "accept" ).with( same( node ) ).will( returnValue( accept ) );

        return (DependencyNodeFilter) mock.proxy();
    }
}
