package org.apache.maven.shared.test.plugin;

public class TestToolsException
    extends Exception
{

    private static final long serialVersionUID = 1L;

    public TestToolsException( String message, Throwable cause )
    {
        super( message, cause );
    }

    public TestToolsException( String message )
    {
        super( message );
    }

}
