package org.apache.maven.plugin.testing;

import org.apache.maven.plugin.MojoExecutionException;
import org.codehaus.plexus.configuration.PlexusConfiguration;
import org.codehaus.plexus.util.xml.Xpp3Dom;
import org.codehaus.plexus.util.xml.Xpp3DomBuilder;

import java.io.StringReader;

/**
 * @author Edwin Punzalan
 * @version $Id$
 */
public class ExpressionEvaluatorTest
    extends AbstractMojoTestCase
{
    private Xpp3Dom pomDom;

    private PlexusConfiguration pluginConfiguration;

    protected void setUp()
        throws Exception
    {
        super.setUp();

        StringBuffer pom = new StringBuffer();

        pom.append( "<project>" ).append( "\n" );
        pom.append( "  <build>" ).append( "\n" );
        pom.append( "    <plugins>" ).append( "\n" );
        pom.append( "      <plugin>" ).append( "\n" );
        pom.append( "        <artifactId>maven-test-mojo</artifactId>" ).append( "\n" );
        pom.append( "        <configuration>" ).append( "\n" );
        pom.append( "          <basedir>${basedir}</basedir>" ).append( "\n" );
        pom.append( "          <workdir>${basedir}/workDirectory</workdir>" ).append( "\n" );
        pom.append( "          <localRepository>${localRepository}</localRepository>" ).append( "\n" );
        pom.append( "        </configuration>" ).append( "\n" );
        pom.append( "      </plugin>" ).append( "\n" );
        pom.append( "    </plugins>" ).append( "\n" );
        pom.append( "  </build>" ).append( "\n" );
        pom.append( "</project>" ).append( "\n" );

        pomDom = Xpp3DomBuilder.build( new StringReader( pom.toString() ) );

        pluginConfiguration = extractPluginConfiguration( "maven-test-mojo", pomDom );
    }

    public void testInjection()
        throws Exception
    {
        ExpressionEvaluatorMojo mojo = new ExpressionEvaluatorMojo();

        mojo = (ExpressionEvaluatorMojo) configureMojo( mojo, pluginConfiguration );

        try
        {
            mojo.execute();
        }
        catch ( MojoExecutionException e )
        {
            fail( e.getMessage() );
        }
    }
}
