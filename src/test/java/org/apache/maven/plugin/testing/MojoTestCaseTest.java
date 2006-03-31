package org.apache.maven.plugin.testing;

import org.codehaus.plexus.configuration.PlexusConfiguration;
import org.codehaus.plexus.util.xml.Xpp3Dom;
import org.codehaus.plexus.util.xml.Xpp3DomBuilder;

import java.io.StringReader;
import java.util.Map;

/**
 * @author Jason van Zyl
 * @version $Revision:$
 */
public class MojoTestCaseTest
    extends AbstractMojoTestCase
{
    private String pom;

    private Xpp3Dom pomDom;

    private PlexusConfiguration pluginConfiguration;

    protected void setUp()
        throws Exception
    {
        super.setUp();

        pom =
            "<project>" +
                "<build>" +
                "<plugins>" +
                "<plugin>" +
                "<artifactId>maven-simple-plugin</artifactId>" +
                "<configuration>" +
                "<keyOne>valueOne</keyOne>" +
                "<keyTwo>valueTwo</keyTwo>" +
                "</configuration>" +
                "</plugin>" +
                "</plugins>" +
                "</build>" +
                "</project>";

        pomDom = Xpp3DomBuilder.build( new StringReader( pom ) );

        pluginConfiguration = extractPluginConfiguration( "maven-simple-plugin", pomDom );
    }

    public void testPluginConfigurationExtraction()
        throws Exception
    {
        assertEquals( "valueOne", pluginConfiguration.getChild( "keyOne" ).getValue() );

        assertEquals( "valueTwo", pluginConfiguration.getChild( "keyTwo" ).getValue() );
    }

    public void testMojoConfiguration()
        throws Exception
    {
        SimpleMojo mojo = new SimpleMojo();

        mojo = (SimpleMojo) configureMojo( mojo, pluginConfiguration );

        assertEquals( "valueOne", mojo.getKeyOne() );

        assertEquals( "valueTwo", mojo.getKeyTwo() );
    }

    public void testVariableAccessWithoutGetter()
        throws Exception
    {
        SimpleMojo mojo = new SimpleMojo();

        mojo = (SimpleMojo) configureMojo( mojo, pluginConfiguration );

        assertEquals( "valueOne", (String)getVariableValueFromObject( mojo, "keyOne" ) );

        assertEquals( "valueTwo", (String)getVariableValueFromObject( mojo, "keyTwo" ) );
    }


     public void testVariableAccessWithoutGetter2()
        throws Exception
    {
        SimpleMojo mojo = new SimpleMojo();

        mojo = (SimpleMojo) configureMojo( mojo, pluginConfiguration );

        Map map = getVariablesAndValuesFromObject( mojo );

        assertEquals( "valueOne", (String)map.get( "keyOne" ) );

        assertEquals( "valueTwo", (String)map.get( "keyTwo" ) );
    }


    public void testSettingMojoVariables()
        throws Exception
    {
        SimpleMojo mojo = new SimpleMojo();

        mojo = (SimpleMojo) configureMojo( mojo, pluginConfiguration );

        setVariableValueToObject( mojo, "keyOne", "myValueOne" );

        assertEquals( "myValueOne", (String)getVariableValueFromObject( mojo, "keyOne" ) );

    }

}
