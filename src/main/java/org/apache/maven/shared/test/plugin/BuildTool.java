package org.apache.maven.shared.test.plugin;

import org.apache.maven.shared.invoker.DefaultInvocationRequest;
import org.apache.maven.shared.invoker.DefaultInvoker;
import org.apache.maven.shared.invoker.InvocationOutputHandler;
import org.apache.maven.shared.invoker.InvocationRequest;
import org.apache.maven.shared.invoker.InvocationResult;
import org.apache.maven.shared.invoker.Invoker;
import org.apache.maven.shared.invoker.MavenInvocationException;
import org.codehaus.plexus.personality.plexus.lifecycle.phase.Disposable;
import org.codehaus.plexus.personality.plexus.lifecycle.phase.Initializable;
import org.codehaus.plexus.personality.plexus.lifecycle.phase.InitializationException;
import org.codehaus.plexus.util.IOUtil;
import org.codehaus.plexus.util.cli.CommandLineUtils;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;
import java.util.Properties;

/**
 * Test-tool used to execute Maven builds in order to test plugin functionality.
 * 
 * @plexus.component role="org.apache.maven.shared.test.plugin.BuildTool" role-hint="default"
 * @author jdcasey
 *
 */
public class BuildTool
    implements Initializable, Disposable
{
    public static final String ROLE = BuildTool.class.getName();
    
    private Invoker mavenInvoker;

    /**
     * Build a standard InvocationRequest using the specified test-build POM, command-line properties,
     * goals, and output logfile. Then, execute Maven using this standard request. Return the result
     * of the invocation.
     * 
     * @param pom The test-build POM
     * @param properties command-line properties to fine-tune the test build, or test parameter 
     *   extraction from CLI properties 
     * @param goals The list of goals and/or lifecycle phases to execute during this build
     * @param buildLogFile The logfile used to capture build output
     * @return The result of the Maven invocation, including exit value and any execution exceptions
     *   resulting from the Maven invocation.
     */
    public InvocationResult executeMaven( File pom, Properties properties, List goals, File buildLogFile )
        throws TestToolsException
    {
        InvocationRequest request = createBasicInvocationRequest( pom, properties, goals, buildLogFile );
        
        return executeMaven( request );
    }
    
    /**
     * Execute a test build using a customized InvocationRequest. Normally, this request would be 
     * created using the <code>createBasicInvocationRequest</code> method in this class.
     * 
     * @param request The customized InvocationRequest containing the configuration used to execute
     *   the current test build
     * @return The result of the Maven invocation, containing exit value, along with any execution
     *   exceptions resulting from the [attempted] Maven invocation.
     */
    public InvocationResult executeMaven( InvocationRequest request )
    throws TestToolsException
    {
        try
        {
            return mavenInvoker.execute( request );
        }
        catch ( MavenInvocationException e )
        {
            throw new TestToolsException( "Error executing maven.", e );
        }
        finally
        {
            closeHandlers( request );
        }
    }

    /**
     * Detect the location of the local Maven installation, and start up the MavenInvoker using that
     * path. Detection uses the system property <code>maven.home</code>, and falls back to the shell
     * environment variable <code>M2_HOME</code>.
     * 
     * @throws IOException in case the shell environment variables cannot be read
     */
    private void startInvoker()
        throws IOException
    {
        if ( mavenInvoker == null )
        {
            mavenInvoker = new DefaultInvoker();
            
            if ( System.getProperty( "maven.home" ) == null )
            {
                Properties envars = CommandLineUtils.getSystemEnvVars();
                
                String mavenHome = envars.getProperty( "M2_HOME" );
                
                if ( mavenHome != null )
                {
                    mavenInvoker.setMavenHome( new File( mavenHome ) );
                }
            }
        }
    }

    /**
     * If we're logging output to a logfile using standard output handlers, make sure these are
     * closed.
     * 
     * @param request
     */
    private void closeHandlers( InvocationRequest request )
    {
        InvocationOutputHandler outHandler = request.getOutputHandler( null );

        if ( outHandler != null && ( outHandler instanceof LoggerHandler ) )
        {
            ( (LoggerHandler) outHandler ).close();
        }

        InvocationOutputHandler errHandler = request.getErrorHandler( null );

        if ( errHandler != null && ( outHandler == null || errHandler != outHandler )
            && ( errHandler instanceof LoggerHandler ) )
        {
            ( (LoggerHandler) errHandler ).close();
        }
    }

    /**
     * Construct a standardized InvocationRequest given the test-build POM, a set of CLI properties,
     * a list of goals to execute, and the location of a log file to which build output should be
     * directed. The resulting InvocationRequest can then be customized by the test class before
     * being used to execute a test build. Both standard-out and standard-error will be directed
     * to the specified log file.
     * 
     * @param pom The POM for the test build
     * @param properties The command-line properties for use in this test build
     * @param goals The goals and/or lifecycle phases to execute during the test build
     * @param buildLogFile Location to which build output should be logged
     * @return The standardized InvocationRequest for the test build, ready for any necessary 
     *   customizations.
     */
    public InvocationRequest createBasicInvocationRequest( File pom, Properties properties, List goals,
                                                            File buildLogFile )
    {
        InvocationRequest request = new DefaultInvocationRequest();

        request.setPomFile( pom );

        request.setGoals( goals );

        request.setProperties( properties );

        LoggerHandler handler = new LoggerHandler( buildLogFile );

        request.setOutputHandler( handler );
        request.setErrorHandler( handler );

        return request;
    }

    private static final class LoggerHandler
        implements InvocationOutputHandler
    {
        private static final String LS = System.getProperty( "line.separator" );

        private final File output;

        private FileWriter writer;

        LoggerHandler( File logFile )
        {
            output = logFile;
        }

        public void consumeLine( String line )
        {
            if ( writer == null )
            {
                try
                {
                    writer = new FileWriter( output );
                }
                catch ( IOException e )
                {
                    throw new IllegalStateException( "Failed to open build log: " + output + "\n\nError: "
                        + e.getMessage() );
                }
            }

            try
            {
                writer.write( line + LS );
                writer.flush();
            }
            catch ( IOException e )
            {
                throw new IllegalStateException( "Failed to write to build log: " + output + " output:\n\n\'" + line
                    + "\'\n\nError: " + e.getMessage() );
            }
        }

        void close()
        {
            IOUtil.close( writer );
        }

    }

    /**
     * Initialize this tool once it's been instantiated and composed, in order to start up the
     * MavenInvoker instance.
     */
    public void initialize()
        throws InitializationException
    {
        try
        {
            startInvoker();
        }
        catch ( IOException e )
        {
            throw new InitializationException( "Error detecting maven home.", e );
        }
        
    }

    /**
     * Not currently used; when this API switches to use the Maven Embedder, it will be used to 
     * shutdown the embedder and its associated container, to free up JVM memory.
     */
    public void dispose()
    {
        // TODO: When we switch to the embedder, use this to deallocate the MavenEmbedder, along 
        // with the PlexusContainer and ClassRealm that it wraps.
    }
}
