package org.apache.maven.archiver;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.Before;
import org.junit.Test;

public class ManifestConfigurationTest
{

    private ManifestConfiguration manifestConfiguration;

    @Before
    public void before()
    {
        this.manifestConfiguration = new ManifestConfiguration();
    }

    @Test
    public void XXX()
    {
        assertThat( manifestConfiguration.getClasspathLayoutType() ).isEqualTo( ManifestConfiguration.CLASSPATH_LAYOUT_TYPE_SIMPLE );
    }

    @Test
    public void getClasspathPrefixShouldReturnPrefixWithSlashesInsteadOfBackSlashes()
    {
        manifestConfiguration.setClasspathPrefix( "\\lib\\const\\" );
        assertThat( manifestConfiguration.getClasspathPrefix() ).isEqualTo( "/lib/const/" );
    }

    @Test
    public void getClasspathPrefixShouldReturnPrefixWithTraingSlash()
    {
        manifestConfiguration.setClasspathPrefix( "const" );
        assertThat( manifestConfiguration.getClasspathPrefix() ).isEqualTo( "const/" );
    }

    @Test
    public void getClasspathPrefixShouldReturnTheTrailingSlash()
    {
        manifestConfiguration.setClasspathPrefix( "const/" );
        assertThat( manifestConfiguration.getClasspathPrefix() ).isEqualTo( "const/" );
    }
}
