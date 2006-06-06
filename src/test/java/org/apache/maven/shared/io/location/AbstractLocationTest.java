package org.apache.maven.shared.io.location;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import org.codehaus.plexus.util.IOUtil;

import junit.framework.TestCase;

public class AbstractLocationTest
    extends TestCase
{

    public AbstractLocationTest()
    {
        super();
    }

    protected void writeToFile( File file, String testStr )
        throws IOException
    {
        FileWriter fw = null;
        try
        {
            fw = new FileWriter( file );
            fw.write( testStr );
        }
        finally
        {
            IOUtil.close( fw );
        }        
    }

}