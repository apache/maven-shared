package org.apache.maven.shared.invoker;

import org.codehaus.plexus.util.cli.CommandLineException;

public final class DefaultInvocationResult implements InvocationResult
{
    
    private CommandLineException executionException;
    
    private int exitCode = Integer.MIN_VALUE;

    DefaultInvocationResult()
    {
    }
    
    public int getExitCode()
    {
        return exitCode;
    }
    
    public CommandLineException getExecutionException()
    {
        return executionException;
    }
    
    void setExitCode( int exitCode )
    {
        this.exitCode = exitCode;
    }
    
    void setExecutionException( CommandLineException executionException )
    {
        this.executionException = executionException;
    }

}
