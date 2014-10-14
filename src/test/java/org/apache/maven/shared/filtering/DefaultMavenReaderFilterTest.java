package org.apache.maven.shared.filtering;

import org.apache.maven.shared.utils.io.IOUtil;
import org.codehaus.plexus.PlexusTestCase;

import java.io.Reader;
import java.io.StringReader;
import java.util.Properties;

/**
 * @author Kristian Rosenvold
 *
 */
public class DefaultMavenReaderFilterTest
    extends PlexusTestCase
{
    public void testJustDoSomeFiltering() throws Exception
    {
        assertNotNull(DefaultMavenReaderFilter.class);
        MavenReaderFilter readerFilter = (MavenReaderFilter) lookup( MavenReaderFilter.class.getName(), "default" );

        StringReader src = new StringReader( "toto@titi.com ${foo}" );
        MavenReaderFilterRequest req = new MavenReaderFilterRequest();
        Properties additionalProperties = new Properties();
        additionalProperties.setProperty( "foo", "bar" );
        req.setFrom( src );
        req.setFiltering( true );
        req.setAdditionalProperties( additionalProperties );

        final Reader filter = readerFilter.filter( req );

        assertEquals( "toto@titi.com bar", IOUtil.toString( filter ) );
    }
}
