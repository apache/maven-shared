package org.apache.maven.shared.io.download;

public class DownloadFailedException
    extends Exception
{
    
    private static final long serialVersionUID = 1L;
    
    private String url;

    public DownloadFailedException( String url, String message, Throwable cause )
    {
        super( message, cause );
        this.url = url;
    }

    public DownloadFailedException( String url, String message )
    {
        super( message );
        this.url = url;
    }
    
    public String getUrl()
    {
        return url;
    }

}
