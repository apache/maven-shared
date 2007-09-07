package org.apache.maven.it;

/**
 * @author Jason van Zyl
 * @version $Id$
 */
public class VerificationException
    extends Exception
{
    public VerificationException()
    {
    }

    public VerificationException( String message )
    {
        super( message );
    }

    public VerificationException( Throwable cause )
    {
        super( cause );
    }

    public VerificationException( String message, Throwable cause )
    {
        super( message, cause );
    }
}
