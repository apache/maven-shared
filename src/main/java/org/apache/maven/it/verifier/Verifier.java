package org.apache.maven.it.verifier;

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
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.apache.maven.it.VerificationException;

/**
 * Interface for Maven intergration tests verifiers
 * 
 * @author <a href="mailto:carlos@apache.org">Carlos Sanchez</a>
 * @version $Id$
 */
public interface Verifier
{

    void resetStreams();

    void displayStreamBuffers();

    void verify( boolean chokeOnErrorOutput )
        throws VerificationException;

    void verifyErrorFreeLog()
        throws VerificationException;

    Properties loadProperties( String filename )
        throws VerificationException;

    List loadFile( String basedir, String filename, boolean hasCommand )
        throws VerificationException;

    List loadFile( File file, boolean hasCommand )
        throws VerificationException;

    String getArtifactPath( String org, String name, String version, String ext );

    List getArtifactFileNameList( String org, String name, String version, String ext );

    void executeHook( String filename )
        throws VerificationException;

    void deleteArtifact( String org, String name, String version, String ext )
        throws IOException;

    void assertFilePresent( String file );

    void assertFileNotPresent( String file );

    void assertArtifactPresent( String org, String name, String version, String ext );

    void assertArtifactNotPresent( String org, String name, String version, String ext );

    void executeGoal( String goal )
        throws VerificationException;

    void executeGoal( String goal, Map envVars )
        throws VerificationException;

    void executeGoals( List goals )
        throws VerificationException;

    void executeGoals( List goals, Map envVars )
        throws VerificationException;

    void assertArtifactContents( String org, String artifact, String version, String type, String contents )
        throws IOException;

    List getCliOptions();

    void setCliOptions( List cliOptions );

    Properties getSystemProperties();

    void setSystemProperties( Properties systemProperties );

    Properties getVerifierProperties();

    void setVerifierProperties( Properties verifierProperties );

    String getBasedir();

    String getLocalRepo();

    public void displayLogFile();

    public void findLocalRepo( String settingsFile )
        throws VerificationException;
}