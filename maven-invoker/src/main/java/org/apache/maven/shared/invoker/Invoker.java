package org.apache.maven.shared.invoker;

import java.io.File;
import java.io.InputStream;

public interface Invoker
{

    static final String userHome = System.getProperty( "user.home" );

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