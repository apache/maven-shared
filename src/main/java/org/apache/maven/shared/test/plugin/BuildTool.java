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
 * @plexus.component role="org.apache.maven.shared.test.plugin.BuildTool" role-hint="default"
 * @author jdcasey
 *
 */
public class BuildTool
    implements Initializable, Disposable
{
    public static final String ROLE = BuildTool.class.getName();
    
    private Invoker mavenInvoker;

    protected InvocationResult executeMaven( File pom, Properties properties, List goals, File buildLogFile )
        throws TestToolsException
    {
        InvocationRequest request = createBasicInvocationRequest( pom, properties, goals, buildLogFile );
        
        return executeMaven( request );
    }
    
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

    public void dispose()
    {
        // TODO: When we switch to the embedder, use this to deallocate the MavenEmbedder, along 
        // with the PlexusContainer and ClassRealm that it wraps.
    }
}
