package org.apache.maven.shared.invoker;

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

import java.io.File;
import java.io.InputStream;

public interface Invoker
{
    
    String ROLE = Invoker.class.getName();

    String userHome = System.getProperty( "user.home" );

    InvocationResult execute( InvocationRequest request )
        throws MavenInvocationException;
    
    File getLocalRepositoryDirectory();
    
    File getWorkingDirectory();
    
    InvokerLogger getLogger();

    File getMavenHome();
    
    Invoker setMavenHome( File mavenHome );
    
    Invoker setLocalRepositoryDirectory( File localRepositoryDirectory );

    Invoker setLogger( InvokerLogger logger );

    Invoker setWorkingDirectory( File workingDirectory );
    
    Invoker setInputStream( InputStream inputStream );
    
    Invoker setOutputHandler( InvocationOutputHandler outputHandler );
    
    Invoker setErrorHandler( InvocationOutputHandler errorHandler );

}