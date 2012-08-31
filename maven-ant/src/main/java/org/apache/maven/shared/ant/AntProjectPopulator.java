package org.apache.maven.shared.ant;

import org.apache.tools.ant.Project;
import org.apache.tools.ant.PropertyHelper;
import org.apache.tools.ant.DefaultLogger;
import org.apache.tools.ant.types.Path;
import org.apache.maven.project.MavenProject;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.logging.Log;
import org.apache.maven.shared.ant.components.AntTargetConverter;
import org.apache.maven.artifact.DependencyResolutionRequiredException;
import org.apache.maven.artifact.Artifact;
import org.codehaus.plexus.component.configurator.expression.ExpressionEvaluator;
import org.apache.maven.shared.utils.StringUtils;

import java.util.List;
import java.util.Collection;
import java.util.ArrayList;
import java.util.Iterator;
import java.io.File;

/**
 * @author Jason van Zyl
 */
public class AntProjectPopulator
{
    public void populateAntProjectWithMavenInformation( MavenProject mavenProject,
                                                        Project antProject,
                                                        List pluginArtifacts,
                                                        Log log )
        throws MojoExecutionException
    {
        try
        {
            //TODO refactor - place the manipulation of the expressionEvaluator into a separated class.
            ExpressionEvaluator exprEvaluator =
                (ExpressionEvaluator) antProject.getReference( AntTargetConverter.MAVEN_EXPRESSION_EVALUATOR_ID );

            PropertyHelper propertyHelper = PropertyHelper.getPropertyHelper( antProject );

            propertyHelper.setNext( new AntPropertyHelper( exprEvaluator, mavenProject.getArtifacts(), log ) );

            DefaultLogger antLogger = new DefaultLogger();
            antLogger.setOutputPrintStream( System.out );
            antLogger.setErrorPrintStream( System.err );
            antLogger.setMessageOutputLevel( log.isDebugEnabled() ? Project.MSG_DEBUG : Project.MSG_INFO );

            antProject.addBuildListener( antLogger );
            antProject.setBaseDir( mavenProject.getBasedir() );

            Path p = new Path( antProject );
            p.setPath( StringUtils.join( mavenProject.getCompileClasspathElements().iterator(), File.pathSeparator ) );

            /* maven.dependency.classpath it's deprecated as it's equal to maven.compile.classpath */
            antProject.addReference( "maven.dependency.classpath", p );

            antProject.addReference( "maven.compile.classpath", p );
            p = new Path( antProject );
            p.setPath( StringUtils.join( mavenProject.getRuntimeClasspathElements().iterator(), File.pathSeparator ) );
            antProject.addReference( "maven.runtime.classpath", p );
            p = new Path( antProject );
            p.setPath( StringUtils.join( mavenProject.getTestClasspathElements().iterator(), File.pathSeparator ) );
            antProject.addReference( "maven.test.classpath", p );

            /* set maven.plugin.classpath with plugin dependencies */
            antProject.addReference( "maven.plugin.classpath", getPathFromArtifacts( pluginArtifacts, antProject ) );
        }
        catch ( Exception e )
        {
            throw new MojoExecutionException( "Error executing ant tasks", e );
        }
    }

    public Path getPathFromArtifacts( Collection artifacts,
                                      Project antProject )
        throws DependencyResolutionRequiredException
    {
        List list = new ArrayList( artifacts.size() );

        for ( Iterator i = artifacts.iterator(); i.hasNext(); )
        {
            Artifact a = (Artifact) i.next();
            File file = a.getFile();
            if ( file == null )
            {
                throw new DependencyResolutionRequiredException( a );
            }
            list.add( file.getPath() );
        }
        Path p = new Path( antProject );
        p.setPath( StringUtils.join( list.iterator(), File.pathSeparator ) );
        return p;
    }
}
