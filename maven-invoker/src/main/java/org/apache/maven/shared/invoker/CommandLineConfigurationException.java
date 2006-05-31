package org.apache.maven.shared.invoker;

public class CommandLineConfigurationException
    extends Exception
{

    private static final long serialVersionUID = 1L;

    public CommandLineConfigurationException( String message, Throwable cause )
    {
        super( message, cause );
    }

    public CommandLineConfigurationException( String message )
    {
        super( message );
    }

}
