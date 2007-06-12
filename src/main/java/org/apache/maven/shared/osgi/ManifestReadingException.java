package org.apache.maven.shared.osgi;

/**
 * Encapsulates an IOException to make it runtime
 * 
 * @author <a href="mailto:carlos@apache.org">Carlos Sanchez</a>
 * @version $Id$
 */
public class ManifestReadingException
    extends RuntimeException
{

    public ManifestReadingException()
    {
        super();
    }

    public ManifestReadingException( String message, Throwable cause )
    {
        super( message, cause );
    }

    public ManifestReadingException( String message )
    {
        super( message );
    }

    public ManifestReadingException( Throwable cause )
    {
        super( cause );
    }
}
