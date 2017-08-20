package org.apache.maven.plugin.artifact.installer;

import static org.junit.Assert.assertTrue;

import java.io.File;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import io.takari.maven.testing.TestResources;
import io.takari.maven.testing.executor.MavenExecutionResult;
import io.takari.maven.testing.executor.MavenRuntime;
import io.takari.maven.testing.executor.MavenRuntime.MavenRuntimeBuilder;
import io.takari.maven.testing.executor.MavenVersions;
import io.takari.maven.testing.executor.junit.MavenJUnitTestRunner;

/**
 * This will check if the ArtifactInstaller works for all Maven versions 3.0.5, 3.1.1, 3.2.5, 3.3.1, 3.3.9, 3.5.0. This
 * is done by using the test plugin <code>maven-component-plugin</code> which uses the ArtifactInstaller as component.
 * By using this way we get a real runtime environment which supports all Maven versions.
 * 
 * @author Karl Heinz Marbaise
 */
@RunWith( MavenJUnitTestRunner.class )
@MavenVersions( { "3.0.5", "3.1.1", "3.2.5", "3.3.1", "3.3.9", "3.5.0" } )
public class ArtifactInstallerTest
{

    @Rule
    public final TestResources resources = new TestResources();

    public final MavenRuntime mavenRuntime;

    public ArtifactInstallerTest( MavenRuntimeBuilder builder )
        throws Exception
    {
        this.mavenRuntime = builder.build();
    }

    @Test
    public void buildExample()
        throws Exception
    {
        File basedir = resources.getBasedir( "example" );
        //@formatter:off
        MavenExecutionResult result =
            mavenRuntime
                .forProject( basedir )
                .withCliOption( "-DmvnVersion=" + mavenRuntime.getMavenVersion() ) // Might be superfluous
                .withCliOption( "-B" )
                .withCliOption( "-V" )
                .execute( "clean", "verify" );
        //@formatter:on

        result.assertErrorFreeLog();
        // Check that the current plugins has been called at least once.
        result.assertLogText( "[INFO] --- maven-artifact-installer-plugin:1.0.0:artifact-installer (id-artifact-installer) @ maven-artifact-installer-plugin-it ---" );

        String mvnVersion = mavenRuntime.getMavenVersion();
        // The "." will be replaced by "/" in the running of the artifact-installer-plugin so I need to do the same here.
        // Maybe there is a more elegant way to do that?
        mvnVersion = mvnVersion.replaceAll( "\\.", "/" );

        String mavenRepoLocal = System.getProperty( "maven.repo.local" );
        File localRepo = new File( mavenRepoLocal );

        System.out.println( "localRepo='" + localRepo.getAbsolutePath() + "'" );
        System.out.println( "mvnVersion='" + mvnVersion + "'" );

        // The real checking of what should had happen..
        assertTrue( new File( localRepo,
                              "GROUPID-" + mvnVersion + "/ARTIFACTID/VERSION/ARTIFACTID-VERSION.EXTENSION" ).exists() );
        assertTrue( new File( localRepo, "GROUPID-" + mvnVersion
            + "/ARTIFACTID/VERSION/ARTIFACTID-VERSION-CLASSIFIER.EXTENSION" ).exists() );
        assertTrue( new File( localRepo, "GROUPID-" + mvnVersion + "/ARTIFACTID/maven-metadata-local.xml" ).exists() ); // ??

    }
}