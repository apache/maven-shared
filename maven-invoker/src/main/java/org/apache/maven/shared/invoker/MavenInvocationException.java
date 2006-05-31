package org.apache.maven.shared.invoker;

public class MavenInvocationException
    extends Exception
{

    private static final long serialVersionUID = 1L;

    public MavenInvocationException( String message, Throwable cause )
    {
        super( message, cause );
    }

    public MavenInvocationException( String message )
    {
        super( message );
    }

}
