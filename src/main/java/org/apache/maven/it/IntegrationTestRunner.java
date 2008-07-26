package org.apache.maven.it;

/*
 * Licensed to the Apache Software Foundation (ASF) under one or more contributor license
 * agreements. See the NOTICE file distributed with this work for additional information regarding
 * copyright ownership. The ASF licenses this file to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance with the License. You may obtain a
 * copy of the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 */

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.StringTokenizer;
import java.util.regex.Pattern;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import junit.framework.Assert;

import org.codehaus.plexus.util.FileUtils;
import org.codehaus.plexus.util.cli.CommandLineUtils;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;
import org.xml.sax.helpers.DefaultHandler;

/**
 * @author Jason van Zyl
 * @todo pass in what's necessary to invoker
 * @todo separate out verifier
 * @todo separate out tools
 * @todo create embedder invoker
 * @todo find better way to pass in maven version
 */
public class IntegrationTestRunner
{
    private static final String LOG_FILENAME = "log.txt";
    public String localRepo;
    private final String basedir;
    private static String localRepoLayout = "default";

    public IntegrationTestRunner( String basedir )
        throws IntegrationTestException
    {
        this.basedir = basedir;
        findLocalRepo( null );
        findDefaultMavenHome();
    }

    // Execution

    Invoker invoker = new DefaultInvoker();

    public void executeGoal( String goal )
        throws IntegrationTestException
    {
        InvocationRequest r = new DefaultInvocationRequest().setGoals( goal ).setBasedir( basedir );
        invoker.invoke( r );
    }

    public void executeGoal( String goal, List cliOptions )
        throws IntegrationTestException
    {
        InvocationRequest r = new DefaultInvocationRequest().setGoals( goal ).setBasedir( basedir ).setCliOptions( cliOptions );
        invoker.invoke( r );
    }

    public void executeGoal( String goal, Map envars )
        throws IntegrationTestException
    {
        InvocationRequest r = new DefaultInvocationRequest().setGoals( goal ).setBasedir( basedir ).setEnvars( envars );
        invoker.invoke( r );
    }

    public void invoke( InvocationRequest request )
        throws IntegrationTestException
    {
        invoker.invoke( request );
    }

    public String getMavenVersion()
        throws IntegrationTestException
    {
        return invoker.getMavenVersion();
    }

    public String getExecutable()
    {
        return invoker.getExecutable();
    }

    //

    private void findDefaultMavenHome()
        throws IntegrationTestException
    {
        try
        {
            Properties envVars = CommandLineUtils.getSystemEnvVars();
            envVars.getProperty( "M2_HOME" );
        }
        catch ( IOException e )
        {
            throw new IntegrationTestException( "Cannot read system environment variables.", e );
        }
    }

    public void resetStreams()
    {
    }

    public void verifyErrorFreeLog()
        throws IntegrationTestException
    {
        List lines;
        lines = loadFile( getBasedir(), LOG_FILENAME, false );

        for ( Iterator i = lines.iterator(); i.hasNext(); )
        {
            String line = (String) i.next();

            // A hack to keep stupid velocity resource loader errors from triggering failure
            if ( line.indexOf( "[ERROR]" ) >= 0 && line.indexOf( "VM_global_library.vm" ) == -1 )
            {
                throw new IntegrationTestException( "Error in execution: " + line );
            }
        }
    }

    /**
     * Throws an exception if the text is not present in the log.
     * 
     * @param text
     * @throws IntegrationTestException
     */
    public void verifyTextInLog( String text )
        throws IntegrationTestException
    {
        List lines;
        lines = loadFile( getBasedir(), LOG_FILENAME, false );

        boolean result = false;
        for ( Iterator i = lines.iterator(); i.hasNext(); )
        {
            String line = (String) i.next();
            if ( line.indexOf( text ) >= 0 )
            {
                result = true;
                break;
            }
        }
        if ( !result )
        {
            throw new IntegrationTestException( "Text not found in log: " + text );
        }
    }

    public List loadFile( String basedir, String filename, boolean hasCommand )
        throws IntegrationTestException
    {
        return loadFile( new File( basedir, filename ), hasCommand );
    }

    public List loadFile( File file, boolean hasCommand )
        throws IntegrationTestException
    {
        List lines = new ArrayList();

        if ( file.exists() )
        {
            try
            {
                BufferedReader reader = new BufferedReader( new FileReader( file ) );

                String line = reader.readLine();

                while ( line != null )
                {
                    line = line.trim();

                    if ( !line.startsWith( "#" ) && line.length() != 0 )
                    {
                        lines.addAll( replaceArtifacts( line, hasCommand ) );
                    }
                    line = reader.readLine();
                }

                reader.close();
            }
            catch ( FileNotFoundException e )
            {
                throw new IntegrationTestException( e );
            }
            catch ( IOException e )
            {
                throw new IntegrationTestException( e );
            }
        }

        return lines;
    }

    private List replaceArtifacts( String line, boolean hasCommand )
    {
        String MARKER = "${artifact:";
        int index = line.indexOf( MARKER );
        if ( index >= 0 )
        {
            String newLine = line.substring( 0, index );
            index = line.indexOf( "}", index );
            if ( index < 0 )
            {
                throw new IllegalArgumentException( "line does not contain ending artifact marker: '" + line + "'" );
            }
            String artifact = line.substring( newLine.length() + MARKER.length(), index );

            newLine += getArtifactPath( artifact );
            newLine += line.substring( index + 1 );

            List l = new ArrayList();
            l.add( newLine );

            int endIndex = newLine.lastIndexOf( '/' );

            String command = null;
            String filespec;
            if ( hasCommand )
            {
                int startIndex = newLine.indexOf( ' ' );

                command = newLine.substring( 0, startIndex );

                filespec = newLine.substring( startIndex + 1, endIndex );
            }
            else
            {
                filespec = newLine;
            }

            File dir = new File( filespec );
            addMetadataToList( dir, hasCommand, l, command );
            addMetadataToList( dir.getParentFile(), hasCommand, l, command );

            return l;
        }
        else
        {
            return Collections.singletonList( line );
        }
    }

    private static void addMetadataToList( File dir, boolean hasCommand, List l, String command )
    {
        if ( dir.exists() && dir.isDirectory() )
        {
            String[] files = dir.list( new FilenameFilter()
            {
                public boolean accept( File dir, String name )
                {
                    return name.startsWith( "maven-metadata" ) && name.endsWith( ".xml" );

                }
            } );

            for ( int i = 0; i < files.length; i++ )
            {
                if ( hasCommand )
                {
                    l.add( command + " " + new File( dir, files[i] ).getPath() );
                }
                else
                {
                    l.add( new File( dir, files[i] ).getPath() );
                }
            }
        }
    }

    private String getArtifactPath( String artifact )
    {
        StringTokenizer tok = new StringTokenizer( artifact, ":" );
        if ( tok.countTokens() != 4 )
        {
            throw new IllegalArgumentException( "Artifact must have 4 tokens: '" + artifact + "'" );
        }

        String[] a = new String[4];
        for ( int i = 0; i < 4; i++ )
        {
            a[i] = tok.nextToken();
        }

        String org = a[0];
        String name = a[1];
        String version = a[2];
        String ext = a[3];
        return getArtifactPath( org, name, version, ext );
    }

    public String getArtifactPath( String org, String name, String version, String ext )
    {
        if ( "maven-plugin".equals( ext ) )
        {
            ext = "jar";
        }
        String classifier = null;
        if ( "coreit-artifact".equals( ext ) )
        {
            ext = "jar";
            classifier = "it";
        }
        if ( "test-jar".equals( ext ) )
        {
            ext = "jar";
            classifier = "tests";
        }

        String repositoryPath;
        if ( "legacy".equals( localRepoLayout ) )
        {
            repositoryPath = org + "/" + ext + "s/" + name + "-" + version + "." + ext;
        }
        else if ( "default".equals( localRepoLayout ) )
        {
            repositoryPath = org.replace( '.', '/' );
            repositoryPath = repositoryPath + "/" + name + "/" + version;
            repositoryPath = repositoryPath + "/" + name + "-" + version;
            if ( classifier != null )
            {
                repositoryPath = repositoryPath + "-" + classifier;
            }
            repositoryPath = repositoryPath + "." + ext;
        }
        else
        {
            throw new IllegalStateException( "Unknown layout: " + localRepoLayout );
        }

        return localRepo + "/" + repositoryPath;
    }

    public List getArtifactFileNameList( String org, String name, String version, String ext )
    {
        List files = new ArrayList();
        String artifactPath = getArtifactPath( org, name, version, ext );
        File dir = new File( artifactPath );
        files.add( artifactPath );
        addMetadataToList( dir, false, files, null );
        addMetadataToList( dir.getParentFile(), false, files, null );
        return files;
    }

    private static String retrieveLocalRepo( String settingsXmlPath )
        throws IntegrationTestException
    {
        UserModelReader userModelReader = new UserModelReader();

        String userHome = System.getProperty( "user.home" );

        File userXml;

        String repo = null;

        if ( settingsXmlPath != null )
        {
            userXml = new File( settingsXmlPath );
        }
        else
        {
            userXml = new File( userHome, ".m2/settings.xml" );
        }

        if ( userXml.exists() )
        {
            userModelReader.parse( userXml );

            String localRepository = userModelReader.getLocalRepository();
            if ( localRepository != null )
            {
                repo = new File( localRepository ).getAbsolutePath();
            }
        }

        return repo;
    }

    public void deleteArtifact( String org, String name, String version, String ext )
        throws IOException
    {
        List files = getArtifactFileNameList( org, name, version, ext );
        for ( Iterator i = files.iterator(); i.hasNext(); )
        {
            String fileName = (String) i.next();
            FileUtils.forceDelete( new File( fileName ) );
        }
    }

    public void assertFilePresent( String file )
    {
        try
        {
            verifyExpectedResult( file, true );
        }
        catch ( IntegrationTestException e )
        {
            Assert.fail( e.getMessage() );
        }
    }

    /**
     * Check that given file's content matches an regular expression. Note this method also checks
     * that the file exists and is readable.
     * 
     * @param file the file to check.
     * @param regex a regular expression.
     * @see Pattern
     */
    public void assertFileMatches( String file, String regex )
    {
        assertFilePresent( file );
        try
        {
            String content = FileUtils.fileRead( file );
            if ( !Pattern.matches( regex, content ) )
            {
                Assert.fail( "Content of " + file + " does not match " + regex );
            }
        }
        catch ( IOException e )
        {
            Assert.fail( e.getMessage() );
        }
    }

    public void assertFileNotPresent( String file )
    {
        try
        {
            verifyExpectedResult( file, false );
        }
        catch ( IntegrationTestException e )
        {
            Assert.fail( e.getMessage() );
        }
    }

    private void verifyArtifactPresence( boolean wanted, String org, String name, String version, String ext )
    {
        List files = getArtifactFileNameList( org, name, version, ext );
        for ( Iterator i = files.iterator(); i.hasNext(); )
        {
            String fileName = (String) i.next();
            try
            {
                verifyExpectedResult( fileName, wanted );
            }
            catch ( IntegrationTestException e )
            {
                Assert.fail( e.getMessage() );
            }
        }
    }

    public void assertArtifactPresent( String org, String name, String version, String ext )
    {
        verifyArtifactPresence( true, org, name, version, ext );
    }

    public void assertArtifactNotPresent( String org, String name, String version, String ext )
    {
        verifyArtifactPresence( false, org, name, version, ext );
    }

    private void verifyExpectedResult( String line, boolean wanted )
        throws IntegrationTestException
    {
        if ( line.indexOf( "!/" ) > 0 )
        {
            String urlString = "jar:file:" + getBasedir() + "/" + line;

            InputStream is = null;
            try
            {
                URL url = new URL( urlString );

                is = url.openStream();

                if ( is == null )
                {
                    if ( wanted )
                    {
                        throw new IntegrationTestException( "Expected JAR resource was not found: " + line );
                    }
                }
                else
                {
                    if ( !wanted )
                    {
                        throw new IntegrationTestException( "Unwanted JAR resource was found: " + line );
                    }
                }
            }
            catch ( MalformedURLException e )
            {
                throw new IntegrationTestException( "Error looking for JAR resource", e );
            }
            catch ( IOException e )
            {
                throw new IntegrationTestException( "Error looking for JAR resource", e );
            }
            finally
            {
                if ( is != null )
                {
                    try
                    {
                        is.close();
                    }
                    catch ( IOException e )
                    {
                        System.err.println( "WARN: error closing stream: " + e );
                    }
                }
            }
        }
        else
        {
            File expectedFile = new File( line );

            if ( !expectedFile.isAbsolute() && !line.startsWith( "/" ) )
            {
                expectedFile = new File( getBasedir(), line );
            }

            if ( line.indexOf( '*' ) > -1 )
            {
                File parent = expectedFile.getParentFile();

                if ( !parent.exists() )
                {
                    if ( wanted )
                    {
                        throw new IntegrationTestException( "Expected file pattern was not found: " + expectedFile.getPath() );
                    }
                }
                else
                {
                    String shortNamePattern = expectedFile.getName().replaceAll( "\\*", ".*" );

                    String[] candidates = parent.list();

                    boolean found = false;

                    if ( candidates != null )
                    {
                        for ( int i = 0; i < candidates.length; i++ )
                        {
                            if ( candidates[i].matches( shortNamePattern ) )
                            {
                                found = true;
                                break;
                            }
                        }
                    }

                    if ( !found && wanted )
                    {
                        throw new IntegrationTestException( "Expected file pattern was not found: " + expectedFile.getPath() );
                    }
                    else if ( found && !wanted )
                    {
                        throw new IntegrationTestException( "Unwanted file pattern was found: " + expectedFile.getPath() );
                    }
                }
            }
            else
            {
                if ( !expectedFile.exists() )
                {
                    if ( wanted )
                    {
                        throw new IntegrationTestException( "Expected file was not found: " + expectedFile.getPath() );
                    }
                }
                else
                {
                    if ( !wanted )
                    {
                        throw new IntegrationTestException( "Unwanted file was found: " + expectedFile.getPath() );
                    }
                }
            }
        }
    }

    private void findLocalRepo( String settingsFile )
        throws IntegrationTestException
    {
        if ( localRepo == null )
        {
            localRepo = System.getProperty( "maven.repo.local" );
        }

        if ( localRepo == null )
        {
            localRepo = retrieveLocalRepo( settingsFile );
        }

        if ( localRepo == null )
        {
            localRepo = System.getProperty( "user.home" ) + "/.m2/repository";
        }

        File repoDir = new File( localRepo );

        if ( !repoDir.exists() )
        {
            repoDir.mkdirs();
        }
    }

    public void assertArtifactContents( String org, String artifact, String version, String type, String contents )
        throws IOException
    {
        String fileName = getArtifactPath( org, artifact, version, type );
        Assert.assertEquals( contents, FileUtils.fileRead( fileName ) );
    }

    static class UserModelReader
        extends DefaultHandler
    {
        private String localRepository;

        private StringBuffer currentBody = new StringBuffer();

        public void parse( File file )
            throws IntegrationTestException
        {
            try
            {
                SAXParserFactory saxFactory = SAXParserFactory.newInstance();

                SAXParser parser = saxFactory.newSAXParser();

                InputSource is = new InputSource( new FileInputStream( file ) );

                parser.parse( is, this );
            }
            catch ( FileNotFoundException e )
            {
                throw new IntegrationTestException( e );
            }
            catch ( IOException e )
            {
                throw new IntegrationTestException( e );
            }
            catch ( ParserConfigurationException e )
            {
                throw new IntegrationTestException( e );
            }
            catch ( SAXException e )
            {
                throw new IntegrationTestException( e );
            }
        }

        public void warning( SAXParseException spe )
        {
            printParseError( "Warning", spe );
        }

        public void error( SAXParseException spe )
        {
            printParseError( "Error", spe );
        }

        public void fatalError( SAXParseException spe )
        {
            printParseError( "Fatal Error", spe );
        }

        private final void printParseError( String type, SAXParseException spe )
        {
            System.err.println( type + " [line " + spe.getLineNumber() + ", row " + spe.getColumnNumber() + "]: " + spe.getMessage() );
        }

        public String getLocalRepository()
        {
            return localRepository;
        }

        public void characters( char[] ch, int start, int length )
            throws SAXException
        {
            currentBody.append( ch, start, length );
        }

        public void endElement( String uri, String localName, String rawName )
            throws SAXException
        {
            if ( "localRepository".equals( rawName ) )
            {
                if ( notEmpty( currentBody.toString() ) )
                {
                    localRepository = currentBody.toString().trim();
                }
                else
                {
                    throw new SAXException( "Invalid mavenProfile entry. Missing one or more " + "fields: {localRepository}." );
                }
            }

            currentBody = new StringBuffer();
        }

        private boolean notEmpty( String test )
        {
            return test != null && test.trim().length() > 0;
        }

        public void reset()
        {
            currentBody = null;
            localRepository = null;
        }
    }

    public String getBasedir()
    {
        return basedir;
    }
}
