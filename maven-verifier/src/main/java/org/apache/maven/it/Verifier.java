package org.apache.maven.it;

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

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.io.Writer;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.StringTokenizer;
import java.util.regex.Pattern;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import junit.framework.Assert;

import org.apache.maven.shared.utils.StringUtils;
import org.apache.maven.shared.utils.cli.CommandLineException;
import org.apache.maven.shared.utils.cli.CommandLineUtils;
import org.apache.maven.shared.utils.cli.Commandline;
import org.apache.maven.shared.utils.cli.StreamConsumer;
import org.apache.maven.shared.utils.cli.WriterStreamConsumer;
import org.apache.maven.shared.utils.io.FileUtils;
import org.apache.maven.shared.utils.io.IOUtil;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;
import org.xml.sax.helpers.DefaultHandler;

/**
 * @author Jason van Zyl
 * @author <a href="mailto:brett@apache.org">Brett Porter</a>
 * @version $Id$
 * @noinspection UseOfSystemOutOrSystemErr, RefusedBequest, UnusedDeclaration
 */
public class Verifier
{
    private static final String LOG_FILENAME = "log.txt";

    private static final String[] DEFAULT_CLI_OPTIONS = {"-e", "--batch-mode"};

    private String localRepo;

    private final String basedir;

    private final ByteArrayOutputStream outStream = new ByteArrayOutputStream();

    private final ByteArrayOutputStream errStream = new ByteArrayOutputStream();

    private final String[] defaultCliOptions;

    private PrintStream originalOut;

    private PrintStream originalErr;

    private List<String> cliOptions = new ArrayList<String>();

    private Properties systemProperties = new Properties();

    private Map<String, String> environmentVariables = new HashMap<String, String>();

    private Properties verifierProperties = new Properties();

    private boolean autoclean = true;

    private String localRepoLayout = "default";

    private boolean debug;

    private Boolean forkJvm;

    private String logFileName = LOG_FILENAME;

    private String defaultMavenHome;

    private String defaultClassworldConf;

    private String defaultClasspath;

    // will launch mvn with --debug 
    private boolean mavenDebug = false;

    private String forkMode;

    private boolean debugJvm = false;

    private static MavenLauncher embeddedLauncher;

    public Verifier( String basedir )
        throws VerificationException
    {
        this( basedir, null );
    }

    public Verifier( String basedir, boolean debug )
        throws VerificationException
    {
        this( basedir, null, debug );
    }

    public Verifier( String basedir, String settingsFile )
        throws VerificationException
    {
        this( basedir, settingsFile, false );
    }

    public Verifier( String basedir, String settingsFile, boolean debug )
        throws VerificationException
    {
        this( basedir, settingsFile, debug, DEFAULT_CLI_OPTIONS );
    }

    public Verifier( String basedir, String settingsFile, boolean debug, String[] defaultCliOptions )
        throws VerificationException
    {
        this( basedir, settingsFile, debug, null, defaultCliOptions );
    }

    public Verifier( String basedir, String settingsFile, boolean debug, boolean forkJvm )
        throws VerificationException
    {
        this( basedir, settingsFile, debug, forkJvm, DEFAULT_CLI_OPTIONS );
    }

    public Verifier( String basedir, String settingsFile, boolean debug, boolean forkJvm, String[] defaultCliOptions )
        throws VerificationException
    {
        this( basedir, settingsFile, debug, (Boolean) forkJvm, defaultCliOptions );
    }

    private Verifier( String basedir, String settingsFile, boolean debug, Boolean forkJvm, String[] defaultCliOptions )
        throws VerificationException
    {
        this.basedir = basedir;

        this.forkJvm = forkJvm;
        this.forkMode = System.getProperty( "verifier.forkMode" );

        if ( !debug )
        {
            originalOut = System.out;

            originalErr = System.err;
        }

        setDebug( debug );

        findLocalRepo( settingsFile );
        findDefaultMavenHome();

        if ( StringUtils.isEmpty( defaultMavenHome ) && StringUtils.isEmpty( forkMode ) )
        {
            forkMode = "auto";
        }

        this.defaultCliOptions = defaultCliOptions == null ? new String[0] : defaultCliOptions.clone();
    }

    private void findDefaultMavenHome()
        throws VerificationException
    {
        defaultClasspath = System.getProperty( "maven.bootclasspath" );
        defaultClassworldConf = System.getProperty( "classworlds.conf" );
        defaultMavenHome = System.getProperty( "maven.home" );

        if ( defaultMavenHome == null )
        {
            Properties envVars = CommandLineUtils.getSystemEnvVars();
            defaultMavenHome = envVars.getProperty( "M2_HOME" );
        }

        if ( defaultMavenHome == null )
        {
            File f = new File( System.getProperty( "user.home" ), "m2" );
            if ( new File( f, "bin/mvn" ).isFile() )
            {
                defaultMavenHome = f.getAbsolutePath();
            }
        }
    }

    public void setLocalRepo( String localRepo )
    {
        this.localRepo = localRepo;
    }

    public void resetStreams()
    {
        if ( !debug )
        {
            System.setOut( originalOut );

            System.setErr( originalErr );
        }
    }

    public void displayStreamBuffers()
    {
        String out = outStream.toString();

        if ( out != null && out.trim().length() > 0 )
        {
            System.out.println( "----- Standard Out -----" );

            System.out.println( out );
        }

        String err = errStream.toString();

        if ( err != null && err.trim().length() > 0 )
        {
            System.err.println( "----- Standard Error -----" );

            System.err.println( err );
        }
    }

    // ----------------------------------------------------------------------
    //
    // ----------------------------------------------------------------------

    public void verify( boolean chokeOnErrorOutput )
        throws VerificationException
    {
        List<String> lines = loadFile( getBasedir(), "expected-results.txt", false );

        for ( String line : lines )
        {
            verifyExpectedResult( line );
        }

        if ( chokeOnErrorOutput )
        {
            verifyErrorFreeLog();
        }
    }

    public void verifyErrorFreeLog()
        throws VerificationException
    {
        List<String> lines = loadFile( getBasedir(), getLogFileName(), false );

        for ( String line : lines )
        {
            // A hack to keep stupid velocity resource loader errors from triggering failure
            if ( stripAnsi( line ).contains( "[ERROR]" ) && !isVelocityError( line ) )
            {
                throw new VerificationException( "Error in execution: " + line );
            }
        }
    }

    /**
     * Checks whether the specified line is just an error message from Velocity. Especially old versions of Doxia employ
     * a very noisy Velocity instance.
     *
     * @param line The log line to check, must not be <code>null</code>.
     * @return <code>true</code> if the line appears to be a Velocity error, <code>false</code> otherwise.
     */
    private static boolean isVelocityError( String line )
    {
        return line.contains( "VM_global_library.vm" ) || line.contains( "VM #" ) && line.contains( "macro" );
    }

    /**
     * Throws an exception if the text is not present in the log.
     *
     * @param text the text to assert present
     * @throws VerificationException
     */
    public void verifyTextInLog( String text )
        throws VerificationException
    {
        List<String> lines = loadFile( getBasedir(), getLogFileName(), false );

        boolean result = false;
        for ( String line : lines )
        {
            if ( stripAnsi( line ).contains( text ) )
            {
                result = true;
                break;
            }
        }
        if ( !result )
        {
            throw new VerificationException( "Text not found in log: " + text );
        }
    }

    public static String stripAnsi( String msg )
    {
        return msg.replaceAll( "\u001B\\[[;\\d]*[ -/]*[@-~]", "" );
    }

    public Properties loadProperties( String filename )
        throws VerificationException
    {
        Properties properties = new Properties();

        try
        {
            File propertiesFile = new File( getBasedir(), filename );
            if ( propertiesFile.exists() )
            {
                FileInputStream fis = new FileInputStream( propertiesFile );
                try
                {
                    properties.load( fis );
                }
                finally
                {
                    fis.close();
                }
            }
        }
        catch ( FileNotFoundException e )
        {
            throw new VerificationException( "Error reading properties file", e );
        }
        catch ( IOException e )
        {
            throw new VerificationException( "Error reading properties file", e );
        }

        return properties;
    }

    /**
     * Loads the (non-empty) lines of the specified text file.
     *
     * @param filename The path to the text file to load, relative to the base directory, must not be <code>null</code>.
     * @param encoding The character encoding of the file, may be <code>null</code> or empty to use the platform default
     *                 encoding.
     * @return The list of (non-empty) lines from the text file, can be empty but never <code>null</code>.
     * @throws IOException If the file could not be loaded.
     * @since 1.2
     */
    public List<String> loadLines( String filename, String encoding )
        throws IOException
    {
        List<String> lines = new ArrayList<String>();

        File file = new File( getBasedir(), filename );

        BufferedReader reader = null;
        try
        {
            if ( StringUtils.isNotEmpty( encoding ) )
            {
                reader = new BufferedReader( new InputStreamReader( new FileInputStream( file ), encoding ) );
            }
            else
            {
                reader = new BufferedReader( new FileReader( file ) );
            }

            String line;
            while ( ( line = reader.readLine() ) != null )
            {
                if ( line.length() > 0 )
                {
                    lines.add( line );
                }
            }
        }
        finally
        {
            IOUtil.close( reader );
        }

        return lines;
    }

    public List<String> loadFile( String basedir, String filename, boolean hasCommand )
        throws VerificationException
    {
        return loadFile( new File( basedir, filename ), hasCommand );
    }

    public List<String> loadFile( File file, boolean hasCommand )
        throws VerificationException
    {
        List<String> lines = new ArrayList<String>();

        BufferedReader reader = null;

        if ( file.exists() )
        {
            try
            {
                reader = new BufferedReader( new FileReader( file ) );

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
                throw new VerificationException( e );
            }
            catch ( IOException e )
            {
                throw new VerificationException( e );
            }
            finally
            {
                IOUtil.close( reader );
            }
        }

        return lines;
    }

    private static final String MARKER = "${artifact:";

    private List<String> replaceArtifacts( String line, boolean hasCommand )
    {
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

            List<String> l = new ArrayList<String>();
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

    private static void addMetadataToList( File dir, boolean hasCommand, List<String> l, String command )
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

            for ( String file : files )
            {
                if ( hasCommand )
                {
                    l.add( command + " " + new File( dir, file ).getPath() );
                }
                else
                {
                    l.add( new File( dir, file ).getPath() );
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
        return getArtifactPath( org, name, version, ext, null );
    }

    /**
     * Returns the absolute path to the artifact denoted by groupId, artifactId, version, extension and classifier.
     *
     * @param gid        The groupId, must not be null.
     * @param aid        The artifactId, must not be null.
     * @param version    The version, must not be null.
     * @param ext        The extension, must not be null.
     * @param classifier The classifier, may be null to be omitted.
     * @return the absolute path to the artifact denoted by groupId, artifactId, version, extension and classifier,
     *         never null.
     */
    public String getArtifactPath( String gid, String aid, String version, String ext, String classifier )
    {
        if ( classifier != null && classifier.length() == 0 )
        {
            classifier = null;
        }
        if ( "maven-plugin".equals( ext ) )
        {
            ext = "jar";
        }
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
            repositoryPath = gid + "/" + ext + "s/" + aid + "-" + version + "." + ext;
        }
        else if ( "default".equals( localRepoLayout ) )
        {
            repositoryPath = gid.replace( '.', '/' );
            repositoryPath = repositoryPath + "/" + aid + "/" + version;
            repositoryPath = repositoryPath + "/" + aid + "-" + version;
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

    public List<String> getArtifactFileNameList( String org, String name, String version, String ext )
    {
        List<String> files = new ArrayList<String>();
        String artifactPath = getArtifactPath( org, name, version, ext );
        File dir = new File( artifactPath );
        files.add( artifactPath );
        addMetadataToList( dir, false, files, null );
        addMetadataToList( dir.getParentFile(), false, files, null );
        return files;
    }

    /**
     * Gets the path to the local artifact metadata. Note that the method does not check whether the returned path
     * actually points to existing metadata.
     *
     * @param gid     The group id, must not be <code>null</code>.
     * @param aid     The artifact id, must not be <code>null</code>.
     * @param version The artifact version, may be <code>null</code>.
     * @return The (absolute) path to the local artifact metadata, never <code>null</code>.
     */
    public String getArtifactMetadataPath( String gid, String aid, String version )
    {
        return getArtifactMetadataPath( gid, aid, version, "maven-metadata-local.xml" );
    }

    /**
     * Gets the path to a file in the local artifact directory. Note that the method does not check whether the returned
     * path actually points to an existing file.
     *
     * @param gid      The group id, must not be <code>null</code>.
     * @param aid      The artifact id, may be <code>null</code>.
     * @param version  The artifact version, may be <code>null</code>.
     * @param filename The filename to use, must not be <code>null</code>.
     * @return The (absolute) path to the local artifact metadata, never <code>null</code>.
     */
    public String getArtifactMetadataPath( String gid, String aid, String version, String filename )
    {
        StringBuilder buffer = new StringBuilder( 256 );

        buffer.append( localRepo );
        buffer.append( '/' );

        if ( "default".equals( localRepoLayout ) )
        {
            buffer.append( gid.replace( '.', '/' ) );
            buffer.append( '/' );

            if ( aid != null )
            {
                buffer.append( aid );
                buffer.append( '/' );

                if ( version != null )
                {
                    buffer.append( version );
                    buffer.append( '/' );
                }
            }

            buffer.append( filename );
        }
        else
        {
            throw new IllegalStateException( "Unsupported repository layout: " + localRepoLayout );
        }

        return buffer.toString();
    }

    /**
     * Gets the path to the local artifact metadata. Note that the method does not check whether the returned path
     * actually points to existing metadata.
     *
     * @param gid The group id, must not be <code>null</code>.
     * @param aid The artifact id, must not be <code>null</code>.
     * @return The (absolute) path to the local artifact metadata, never <code>null</code>.
     */
    public String getArtifactMetadataPath( String gid, String aid )
    {
        return getArtifactMetadataPath( gid, aid, null );
    }

    public void executeHook( String filename )
        throws VerificationException
    {
        try
        {
            File f = new File( getBasedir(), filename );

            if ( !f.exists() )
            {
                return;
            }

            List<String> lines = loadFile( f, true );

            for ( String line1 : lines )
            {
                String line = resolveCommandLineArg( line1 );

                executeCommand( line );
            }
        }
        catch ( VerificationException e )
        {
            throw e;
        }
        catch ( Exception e )
        {
            throw new VerificationException( e );
        }
    }

    private void executeCommand( String line )
        throws VerificationException
    {
        int index = line.indexOf( " " );

        String cmd;

        String args = null;

        if ( index >= 0 )
        {
            cmd = line.substring( 0, index );

            args = line.substring( index + 1 );
        }
        else
        {
            cmd = line;
        }

        if ( "rm".equals( cmd ) )
        {
            System.out.println( "Removing file: " + args );

            File f = new File( args );

            if ( f.exists() && !f.delete() )
            {
                throw new VerificationException( "Error removing file - delete failed" );
            }
        }
        else if ( "rmdir".equals( cmd ) )
        {
            System.out.println( "Removing directory: " + args );

            try
            {
                File f = new File( args );

                FileUtils.deleteDirectory( f );
            }
            catch ( IOException e )
            {
                throw new VerificationException( "Error removing directory - delete failed" );
            }
        }
        else if ( "svn".equals( cmd ) )
        {
            launchSubversion( line, getBasedir() );
        }
        else
        {
            throw new VerificationException( "unknown command: " + cmd );
        }
    }

    public static void launchSubversion( String line, String basedir )
        throws VerificationException
    {
        try
        {
            Commandline cli = new Commandline( line );

            cli.setWorkingDirectory( basedir );

            Writer logWriter = new FileWriter( new File( basedir, LOG_FILENAME ) );

            StreamConsumer out = new WriterStreamConsumer( logWriter );

            StreamConsumer err = new WriterStreamConsumer( logWriter );

            System.out.println( "Command: " + CommandLineUtils.toString( cli.getCommandline() ) );

            int ret = CommandLineUtils.executeCommandLine( cli, out, err );

            logWriter.close();

            if ( ret > 0 )
            {
                System.err.println( "Exit code: " + ret );

                throw new VerificationException();
            }
        }
        catch ( CommandLineException e )
        {
            throw new VerificationException( e );
        }
        catch ( IOException e )
        {
            throw new VerificationException( e );
        }
    }

    private static String retrieveLocalRepo( String settingsXmlPath )
        throws VerificationException
    {
        UserModelReader userModelReader = new UserModelReader();

        String userHome = System.getProperty( "user.home" );

        File userXml;

        String repo = null;

        if ( settingsXmlPath != null )
        {
            System.out.println( "Using settings from " + settingsXmlPath );
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
        List<String> files = getArtifactFileNameList( org, name, version, ext );
        for ( String fileName : files )
        {
            FileUtils.forceDelete( new File( fileName ) );
        }
    }

    /**
     * Deletes all artifacts in the specified group id from the local repository.
     *
     * @param gid The group id whose artifacts should be deleted, must not be <code>null</code>.
     * @throws IOException If the artifacts could not be deleted.
     * @since 1.2
     */
    public void deleteArtifacts( String gid )
        throws IOException
    {
        String path;
        if ( "default".equals( localRepoLayout ) )
        {
            path = gid.replace( '.', '/' );
        }
        else if ( "legacy".equals( localRepoLayout ) )
        {
            path = gid;
        }
        else
        {
            throw new IllegalStateException( "Unsupported repository layout: " + localRepoLayout );
        }

        FileUtils.deleteDirectory( new File( localRepo, path ) );
    }

    /**
     * Deletes all artifacts in the specified g:a:v from the local repository.
     *
     * @param gid     The group id whose artifacts should be deleted, must not be <code>null</code>.
     * @param aid     The artifact id whose artifacts should be deleted, must not be <code>null</code>.
     * @param version The (base) version whose artifacts should be deleted, must not be <code>null</code>.
     * @throws IOException If the artifacts could not be deleted.
     * @since 1.3
     */
    public void deleteArtifacts( String gid, String aid, String version )
        throws IOException
    {
        String path;
        if ( "default".equals( localRepoLayout ) )
        {
            path = gid.replace( '.', '/' ) + '/' + aid + '/' + version;
        }
        else
        {
            throw new IllegalStateException( "Unsupported repository layout: " + localRepoLayout );
        }

        FileUtils.deleteDirectory( new File( localRepo, path ) );
    }

    /**
     * Deletes the specified directory.
     *
     * @param path The path to the directory to delete, relative to the base directory, must not be <code>null</code>.
     * @throws IOException If the directory could not be deleted.
     * @since 1.2
     */
    public void deleteDirectory( String path )
        throws IOException
    {
        FileUtils.deleteDirectory( new File( getBasedir(), path ) );
    }

    /**
     * Writes a text file with the specified contents. The contents will be encoded using UTF-8.
     *
     * @param path     The path to the file, relative to the base directory, must not be <code>null</code>.
     * @param contents The contents to write, must not be <code>null</code>.
     * @throws IOException If the file could not be written.
     * @since 1.2
     */
    public void writeFile( String path, String contents )
        throws IOException
    {
        FileUtils.fileWrite( new File( getBasedir(), path ).getAbsolutePath(), "UTF-8", contents );
    }

    /**
     * Filters a text file by replacing some user-defined tokens.
     *
     * @param srcPath          The path to the input file, relative to the base directory, must not be
     *                         <code>null</code>.
     * @param dstPath          The path to the output file, relative to the base directory and possibly equal to the
     *                         input file, must not be <code>null</code>.
     * @param fileEncoding     The file encoding to use, may be <code>null</code> or empty to use the platform's default
     *                         encoding.
     * @param filterProperties The mapping from tokens to replacement values, must not be <code>null</code>.
     * @return The path to the filtered output file, never <code>null</code>.
     * @throws IOException If the file could not be filtered.
     * @since 1.2
     */
    public File filterFile( String srcPath, String dstPath, String fileEncoding, Map<String, String> filterProperties )
        throws IOException
    {
        File srcFile = new File( getBasedir(), srcPath );
        String data = FileUtils.fileRead( srcFile, fileEncoding );

        for ( String token : filterProperties.keySet() )
        {
            String value = String.valueOf( filterProperties.get( token ) );
            data = StringUtils.replace( data, token, value );
        }

        File dstFile = new File( getBasedir(), dstPath );
        //noinspection ResultOfMethodCallIgnored
        dstFile.getParentFile().mkdirs();
        FileUtils.fileWrite( dstFile.getPath(), fileEncoding, data );

        return dstFile;
    }

    /**
     * There are 226 references to this method in Maven core ITs. In most (all?) cases it is used together with
     * {@link #newDefaultFilterProperties()}. Need to remove both methods and update all clients eventually/
     * 
     * @deprecated use {@link #filterFile(String, String, String, Map)}
     */
    @SuppressWarnings( { "rawtypes", "unchecked" } )
    public File filterFile( String srcPath, String dstPath, String fileEncoding, Properties filterProperties )
        throws IOException
    {
        return filterFile( srcPath, dstPath, fileEncoding, (Map) filterProperties );
    }

    /**
     * Gets a new copy of the default filter properties. These default filter properties map the tokens "@basedir@" and
     * "@baseurl@" to the test's base directory and its base <code>file:</code> URL, respectively.
     *
     * @return The (modifiable) map with the default filter properties, never <code>null</code>.
     * @since 1.2
     */
    public Properties newDefaultFilterProperties()
    {
        Properties filterProperties = new Properties();

        String basedir = new File( getBasedir() ).getAbsolutePath();
        filterProperties.put( "@basedir@", basedir );

        /*
         * NOTE: Maven fails to properly handle percent-encoded "file:" URLs (WAGON-111) so don't use File.toURI() here
         * and just do it the simple way.
         */
        String baseurl = basedir;
        if ( !baseurl.startsWith( "/" ) )
        {
            baseurl = '/' + baseurl;
        }
        baseurl = "file://" + baseurl.replace( '\\', '/' );
        filterProperties.put( "@baseurl@", baseurl );

        return filterProperties;
    }

    public void assertFilePresent( String file )
    {
        try
        {
            verifyExpectedResult( file, true );
        }
        catch ( VerificationException e )
        {
            Assert.fail( e.getMessage() );
        }
    }

    /**
     * Check that given file's content matches an regular expression. Note this method also checks that the file exists
     * and is readable.
     *
     * @param file  the file to check.
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
        catch ( VerificationException e )
        {
            Assert.fail( e.getMessage() );
        }
    }

    private void verifyArtifactPresence( boolean wanted, String org, String name, String version, String ext )
    {
        List<String> files = getArtifactFileNameList( org, name, version, ext );
        for ( String fileName : files )
        {
            try
            {
                verifyExpectedResult( fileName, wanted );
            }
            catch ( VerificationException e )
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

    private void verifyExpectedResult( String line )
        throws VerificationException
    {
        boolean wanted = true;
        if ( line.startsWith( "!" ) )
        {
            line = line.substring( 1 );
            wanted = false;
        }

        verifyExpectedResult( line, wanted );
    }

    private void verifyExpectedResult( String line, boolean wanted )
        throws VerificationException
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
                        throw new VerificationException( "Expected JAR resource was not found: " + line );
                    }
                }
                else
                {
                    if ( !wanted )
                    {
                        throw new VerificationException( "Unwanted JAR resource was found: " + line );
                    }
                }
            }
            catch ( MalformedURLException e )
            {
                throw new VerificationException( "Error looking for JAR resource", e );
            }
            catch ( IOException e )
            {
                if ( wanted )
                {
                    throw new VerificationException( "Error looking for JAR resource: " + line );
                }
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

            // NOTE: On Windows, a path with a leading (back-)slash is relative to the current drive
            if ( !expectedFile.isAbsolute() && !expectedFile.getPath().startsWith( File.separator ) )
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
                        throw new VerificationException(
                            "Expected file pattern was not found: " + expectedFile.getPath() );
                    }
                }
                else
                {
                    String shortNamePattern = expectedFile.getName().replaceAll( "\\*", ".*" );

                    String[] candidates = parent.list();

                    boolean found = false;

                    if ( candidates != null )
                    {
                        for ( String candidate : candidates )
                        {
                            if ( candidate.matches( shortNamePattern ) )
                            {
                                found = true;
                                break;
                            }
                        }
                    }

                    if ( !found && wanted )
                    {
                        throw new VerificationException(
                            "Expected file pattern was not found: " + expectedFile.getPath() );
                    }
                    else if ( found && !wanted )
                    {
                        throw new VerificationException( "Unwanted file pattern was found: " + expectedFile.getPath() );
                    }
                }
            }
            else
            {
                if ( !expectedFile.exists() )
                {
                    if ( wanted )
                    {
                        throw new VerificationException( "Expected file was not found: " + expectedFile.getPath() );
                    }
                }
                else
                {
                    if ( !wanted )
                    {
                        throw new VerificationException( "Unwanted file was found: " + expectedFile.getPath() );
                    }
                }
            }
        }
    }

    // ----------------------------------------------------------------------
    //
    // ----------------------------------------------------------------------

    public void executeGoal( String goal )
        throws VerificationException
    {
        executeGoal( goal, environmentVariables );
    }

    public void executeGoal( String goal, Map<String, String> envVars )
        throws VerificationException
    {
        executeGoals( Arrays.asList( goal ), envVars );
    }

    public void executeGoals( List<String> goals )
        throws VerificationException
    {
        executeGoals( goals, environmentVariables );
    }

    public String getExecutable()
    {
        // Use a strategy for finding the maven executable, John has a simple method like this
        // but a little strategy + chain of command would be nicer.

        String mavenHome = defaultMavenHome;

        if ( mavenHome != null )
        {
            return mavenHome + "/bin/mvn";
        }
        else
        {
            File f = new File( System.getProperty( "user.home" ), "m2/bin/mvn" );

            if ( f.exists() )
            {
                return f.getAbsolutePath();
            }
            else
            {
                return "mvn";
            }
        }
    }

    public void executeGoals( List<String> goals, Map<String, String> envVars )
        throws VerificationException
    {
        List<String> allGoals = new ArrayList<String>();

        if ( autoclean )
        {
            /*
             * NOTE: Neither test lifecycle binding nor prefix resolution here but call the goal directly.
             */
            allGoals.add( "org.apache.maven.plugins:maven-clean-plugin:clean" );
        }

        allGoals.addAll( goals );

        List<String> args = new ArrayList<String>();

        int ret;

        File logFile = new File( getBasedir(), getLogFileName() );

        for ( Object cliOption : cliOptions )
        {
            String key = String.valueOf( cliOption );

            String resolvedArg = resolveCommandLineArg( key );

            try
            {
                args.addAll( Arrays.asList( CommandLineUtils.translateCommandline( resolvedArg ) ) );
            }
            catch ( Exception e )
            {
                e.printStackTrace();
            }
        }

        Collections.addAll( args, defaultCliOptions );

        if ( this.mavenDebug )
        {
            args.add( "--debug" );
        }

        /*
         * NOTE: Unless explicitly requested by the caller, the forked builds should use the current local
         * repository. Otherwise, the forked builds would in principle leave the sandbox environment which has been
         * setup for the current build. In particular, using "maven.repo.local" will make sure the forked builds use
         * the same local repo as the parent build even if a custom user settings is provided.
         */
        boolean useMavenRepoLocal = Boolean.valueOf( verifierProperties.getProperty( "use.mavenRepoLocal", "true" ) );

        if ( useMavenRepoLocal )
        {
            args.add( "-Dmaven.repo.local=" + localRepo );
        }

        args.addAll( allGoals );

        try
        {
            String[] cliArgs = args.toArray( new String[args.size()] );

            MavenLauncher launcher = getMavenLauncher( envVars );

            ret = launcher.run( cliArgs, systemProperties, getBasedir(), logFile );
        }
        catch ( LauncherException e )
        {
            throw new VerificationException( "Failed to execute Maven: " + e.getMessage(), e );
        }
        catch ( IOException e )
        {
            throw new VerificationException( e );
        }

        if ( ret > 0 )
        {
            System.err.println( "Exit code: " + ret );

            throw new VerificationException(
                "Exit code was non-zero: " + ret + "; command line and log = \n" + new File( defaultMavenHome,
                                                                                             "bin/mvn" ) + " "
                    + StringUtils.join( args.iterator(), " " ) + "\n" + getLogContents( logFile ) );
        }
    }

    private MavenLauncher getMavenLauncher( Map<String, String> envVars )
        throws LauncherException
    {
        boolean fork;
        if ( forkJvm != null )
        {
            fork = forkJvm;
        }
        else if ( ( envVars.isEmpty() && "auto".equalsIgnoreCase( forkMode ) )
            || "embedded".equalsIgnoreCase( forkMode ) )
        {
            fork = false;

            try
            {
                initEmbeddedLauncher();
            }
            catch ( Exception e )
            {
                fork = true;
            }
        }
        else
        {
            fork = true;
        }

        if ( !fork )
        {
            if ( !envVars.isEmpty() )
            {
                throw new LauncherException( "Environment variables are not supported in embedded runtime" );
            }

            initEmbeddedLauncher();

            return embeddedLauncher;
        }
        else
        {
            return new ForkedLauncher( defaultMavenHome, envVars, debugJvm );
        }
    }

    private void initEmbeddedLauncher()
        throws LauncherException
    {
        if ( embeddedLauncher == null )
        {
            if ( StringUtils.isEmpty( defaultMavenHome ) )
            {
                embeddedLauncher = Embedded3xLauncher.createFromClasspath();
            }
            else
            {
                embeddedLauncher =
                    Embedded3xLauncher.createFromMavenHome( defaultMavenHome, defaultClassworldConf, getClasspath() );
            }
        }
    }

    private List<URL> getClasspath()
        throws LauncherException
    {
        if ( defaultClasspath == null )
        {
            return null;
        }
        ArrayList<URL> classpath = new ArrayList<URL>();
        StringTokenizer st = new StringTokenizer( defaultClasspath, File.pathSeparator );
        while ( st.hasMoreTokens() )
        {
            try
            {
                classpath.add( new File( st.nextToken() ).toURI().toURL() );
            }
            catch ( MalformedURLException e )
            {
                throw new LauncherException( "Invalid launcher classpath " + defaultClasspath, e );
            }
        }
        return classpath;
    }

    public String getMavenVersion()
        throws VerificationException
    {
        try
        {
            return getMavenLauncher( Collections.<String, String>emptyMap() ).getMavenVersion();
        }
        catch ( LauncherException e )
        {
            throw new VerificationException( e );
        }
        catch ( IOException e )
        {
            throw new VerificationException( e );
        }
    }

    private static String getLogContents( File logFile )
    {
        try
        {
            return FileUtils.fileRead( logFile );
        }
        catch ( IOException e )
        {
            // ignore
            return "(Error reading log contents: " + e.getMessage() + ")";
        }
    }

    private String resolveCommandLineArg( String key )
    {
        String result = key.replaceAll( "\\$\\{basedir\\}", getBasedir() );
        if ( result.contains( "\\\\" ) )
        {
            result = result.replaceAll( "\\\\", "\\" );
        }
        result = result.replaceAll( "\\/\\/", "\\/" );

        return result;
    }

    private static List<String> discoverIntegrationTests( String directory )
        throws VerificationException
    {
        try
        {
            ArrayList<String> tests = new ArrayList<String>();

            List<File> subTests = FileUtils.getFiles( new File( directory ), "**/goals.txt", null );

            for ( File testCase : subTests )
            {
                tests.add( testCase.getParent() );
            }

            return tests;
        }
        catch ( IOException e )
        {
            throw new VerificationException( directory + " is not a valid test case container", e );
        }
    }

    private void displayLogFile()
    {
        System.out.println( "Log file contents:" );
        BufferedReader reader = null;
        try
        {
            reader = new BufferedReader( new FileReader( new File( getBasedir(), getLogFileName() ) ) );
            String line = reader.readLine();
            while ( line != null )
            {
                System.out.println( line );
                line = reader.readLine();
            }
            reader.close();
        }
        catch ( FileNotFoundException e )
        {
            System.err.println( "Error: " + e );
        }
        catch ( IOException e )
        {
            System.err.println( "Error: " + e );
        }
        finally
        {
            IOUtil.close( reader );
        }
    }

    // ----------------------------------------------------------------------
    //
    // ----------------------------------------------------------------------

    public static void main( String args[] )
        throws VerificationException
    {
        String basedir = System.getProperty( "user.dir" );

        List<String> tests = null;

        List<String> argsList = new ArrayList<String>();

        String settingsFile = null;

        // skip options
        for ( int i = 0; i < args.length; i++ )
        {
            if ( args[i].startsWith( "-D" ) )
            {
                int index = args[i].indexOf( "=" );
                if ( index >= 0 )
                {
                    System.setProperty( args[i].substring( 2, index ), args[i].substring( index + 1 ) );
                }
                else
                {
                    System.setProperty( args[i].substring( 2 ), "true" );
                }
            }
            else if ( "-s".equals( args[i] ) || "--settings".equals( args[i] ) )
            {
                if ( i == args.length - 1 )
                {
                    // should have been detected before
                    throw new IllegalStateException( "missing argument to -s" );
                }
                i += 1;

                settingsFile = args[i];
            }
            else if ( args[i].startsWith( "-" ) )
            {
                System.out.println( "skipping unrecognised argument: " + args[i] );
            }
            else
            {
                argsList.add( args[i] );
            }
        }

        if ( argsList.size() == 0 )
        {
            if ( FileUtils.fileExists( basedir + File.separator + "integration-tests.txt" ) )
            {
                try
                {
                    tests = FileUtils.loadFile( new File( basedir, "integration-tests.txt" ) );
                }
                catch ( IOException e )
                {
                    System.err.println( "Unable to load integration tests file" );

                    System.err.println( e.getMessage() );

                    System.exit( 2 );
                }
            }
            else
            {
                tests = discoverIntegrationTests( "." );
            }
        }
        else
        {
            tests = new ArrayList<String>( argsList.size() );
            NumberFormat fmt = new DecimalFormat( "0000" );
            for ( String test : argsList )
            {
                if ( test.endsWith( "," ) )
                {
                    test = test.substring( 0, test.length() - 1 );
                }

                if ( StringUtils.isNumeric( test ) )
                {

                    test = "it" + fmt.format( Integer.valueOf( test ) );
                    tests.add( test.trim() );
                }
                else if ( "it".startsWith( test ) )
                {
                    test = test.trim();
                    if ( test.length() > 0 )
                    {
                        tests.add( test );
                    }
                }
                else if ( FileUtils.fileExists( test ) && new File( test ).isDirectory() )
                {
                    tests.addAll( discoverIntegrationTests( test ) );
                }
                else
                {
                    System.err.println(
                        "[WARNING] rejecting " + test + " as an invalid test or test source directory" );
                }
            }
        }

        if ( tests.size() == 0 )
        {
            System.out.println( "No tests to run" );
        }

        int exitCode = 0;

        List<String> failed = new ArrayList<String>();
        for ( String test : tests )
        {
            System.out.print( test + "... " );

            String dir = basedir + "/" + test;

            if ( !new File( dir, "goals.txt" ).exists() )
            {
                System.err.println( "Test " + test + " in " + dir + " does not exist" );

                System.exit( 2 );
            }

            Verifier verifier = new Verifier( dir );
            verifier.findLocalRepo( settingsFile );

            System.out.println( "Using default local repository: " + verifier.localRepo );

            try
            {
                runIntegrationTest( verifier );
            }
            catch ( Throwable e )
            {
                verifier.resetStreams();

                System.out.println( "FAILED" );

                verifier.displayStreamBuffers();

                System.out.println( ">>>>>> Error Stacktrace:" );
                e.printStackTrace( System.out );
                System.out.println( "<<<<<< Error Stacktrace" );

                verifier.displayLogFile();

                exitCode = 1;

                failed.add( test );
            }
        }

        System.out.println( tests.size() - failed.size() + "/" + tests.size() + " passed" );
        if ( !failed.isEmpty() )
        {
            System.out.println( "Failed tests: " + failed );
        }

        System.exit( exitCode );
    }

    private void findLocalRepo( String settingsFile )
        throws VerificationException
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
            //noinspection ResultOfMethodCallIgnored
            repoDir.mkdirs();
        }

        // normalize path
        localRepo = repoDir.getAbsolutePath();

        localRepoLayout = System.getProperty( "maven.repo.local.layout", "default" );
    }

    private static void runIntegrationTest( Verifier verifier )
        throws VerificationException
    {
        verifier.executeHook( "prebuild-hook.txt" );

        Properties properties = verifier.loadProperties( "system.properties" );

        Properties controlProperties = verifier.loadProperties( "verifier.properties" );

        boolean chokeOnErrorOutput = Boolean.valueOf( controlProperties.getProperty( "failOnErrorOutput", "true" ) );

        List<String> goals = verifier.loadFile( verifier.getBasedir(), "goals.txt", false );

        List<String> cliOptions = verifier.loadFile( verifier.getBasedir(), "cli-options.txt", false );

        verifier.setCliOptions( cliOptions );

        verifier.setSystemProperties( properties );

        verifier.setVerifierProperties( controlProperties );

        verifier.executeGoals( goals );

        verifier.executeHook( "postbuild-hook.txt" );

        System.out.println( "*** Verifying: fail when [ERROR] detected? " + chokeOnErrorOutput + " ***" );

        verifier.verify( chokeOnErrorOutput );

        verifier.resetStreams();

        System.out.println( "OK" );
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
            throws VerificationException
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
                throw new VerificationException( "file not found path : " + file.getAbsolutePath(), e );
            }
            catch ( IOException e )
            {
                throw new VerificationException( " IOException path : " + file.getAbsolutePath(), e );
            }
            catch ( ParserConfigurationException e )
            {
                throw new VerificationException( e );
            }
            catch ( SAXException e )
            {
                throw new VerificationException( "Parsing exception for file " + file.getAbsolutePath(), e );
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

        private void printParseError( String type, SAXParseException spe )
        {
            System.err.println(
                type + " [line " + spe.getLineNumber() + ", row " + spe.getColumnNumber() + "]: " + spe.getMessage() );
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
                    throw new SAXException(
                        "Invalid mavenProfile entry. Missing one or more " + "fields: {localRepository}." );
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

    public List<String> getCliOptions()
    {
        return cliOptions;
    }

    public void setCliOptions( List<String> cliOptions )
    {
        this.cliOptions = cliOptions;
    }

    public void addCliOption( String option )
    {
        cliOptions.add( option );
    }

    public Properties getSystemProperties()
    {
        return systemProperties;
    }

    public void setSystemProperties( Properties systemProperties )
    {
        this.systemProperties = systemProperties;
    }

    public void setSystemProperty( String key, String value )
    {
        if ( value != null )
        {
            systemProperties.setProperty( key, value );
        }
        else
        {
            systemProperties.remove( key );
        }
    }

    public Map<String, String> getEnvironmentVariables()
    {
        return environmentVariables;
    }

    public void setEnvironmentVariables( Map<String, String> environmentVariables )
    {
        this.environmentVariables = environmentVariables;
    }

    public void setEnvironmentVariable( String key, String value )
    {
        if ( value != null )
        {
            environmentVariables.put( key, value );
        }
        else
        {
            environmentVariables.remove( key );
        }
    }

    public Properties getVerifierProperties()
    {
        return verifierProperties;
    }

    public void setVerifierProperties( Properties verifierProperties )
    {
        this.verifierProperties = verifierProperties;
    }

    public boolean isAutoclean()
    {
        return autoclean;
    }

    public void setAutoclean( boolean autoclean )
    {
        this.autoclean = autoclean;
    }

    public String getBasedir()
    {
        return basedir;
    }

    /**
     * Gets the name of the file used to log build output.
     *
     * @return The name of the log file, relative to the base directory, never <code>null</code>.
     * @since 1.2
     */
    public String getLogFileName()
    {
        return this.logFileName;
    }

    /**
     * Sets the name of the file used to log build output.
     *
     * @param logFileName The name of the log file, relative to the base directory, must not be empty or
     *                    <code>null</code>.
     * @since 1.2
     */
    public void setLogFileName( String logFileName )
    {
        if ( StringUtils.isEmpty( logFileName ) )
        {
            throw new IllegalArgumentException( "log file name unspecified" );
        }
        this.logFileName = logFileName;
    }

    public void setDebug( boolean debug )
    {
        this.debug = debug;

        if ( !debug )
        {
            System.setOut( new PrintStream( outStream ) );

            System.setErr( new PrintStream( errStream ) );
        }
    }

    public boolean isMavenDebug()
    {
        return mavenDebug;
    }

    public void setMavenDebug( boolean mavenDebug )
    {
        this.mavenDebug = mavenDebug;
    }

    public void setForkJvm( boolean forkJvm )
    {
        this.forkJvm = forkJvm;
    }

    public boolean isDebugJvm()
    {
        return debugJvm;
    }

    public void setDebugJvm( boolean debugJvm )
    {
        this.debugJvm = debugJvm;
    }

    public String getLocalRepoLayout()
    {
        return localRepoLayout;
    }

    public void setLocalRepoLayout( String localRepoLayout )
    {
        this.localRepoLayout = localRepoLayout;
    }

    public String getLocalRepository()
    {
        return localRepo;
    }
}
