package org.apache.maven.shared.dependencies.resolve.internal;

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

import java.util.List;

import org.apache.maven.shared.dependencies.resolve.DependencyResolverException;
import org.apache.maven.shared.dependencies.resolve.DependencyResult;
import org.sonatype.aether.collection.DependencyCollectionException;
import org.sonatype.aether.resolution.ArtifactResolutionException;

/**
 * 
 * @author Robert Scholte
 *
 */
class Maven30DependencyResolverException extends DependencyResolverException
{
    private DependencyCollectionException dce;
    
    private ArtifactResolutionException are;

    protected Maven30DependencyResolverException( DependencyCollectionException e )
    {
        super( e );
        this.dce = e;
    }
    
    public Maven30DependencyResolverException( ArtifactResolutionException e )
    {
        super( e );
        this.are = e;
    }
    
    @Override
    public DependencyResult getResult()
    {
        return new DependencyResult()
        {
            @Override
            public List<Exception> getCollectorExceptions()
            {
                return dce.getResult().getExceptions();
            }
        };
    }
}
