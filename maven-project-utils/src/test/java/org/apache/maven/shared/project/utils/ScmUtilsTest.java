package org.apache.maven.shared.project.utils;

import static org.junit.Assert.*;

import org.apache.maven.model.Model;
import org.apache.maven.model.Scm;
import org.apache.maven.project.MavenProject;
import org.junit.Test;

public class ScmUtilsTest
{

    @Test
    public void resolveScmConnection()
    {
        MavenProject project = new MavenProject();
        assertEquals( "",  ScmUtils.resolveScmConnection( project ) );
    }

    @Test
    public void resolveScmDeveloperConnection()
    {
        MavenProject project = new MavenProject();
        assertEquals( "",  ScmUtils.resolveScmDeveloperConnection( project ) );
    }

    @Test
    public void getScmConnectionByModel()
    {
        Model model = new Model();
        assertNull( ScmUtils.getScmConnection( model ) );
        model.setScm( new Scm() );
        assertNull( ScmUtils.getScmConnection( model ) );
        String connection = "scmConnection";
        model.getScm().setConnection( connection );
        assertEquals( connection, ScmUtils.getScmConnection( model ) );
    }

    @Test
    public void getScmDeveloperConnectionByModel()
    {
        Model model = new Model();
        assertNull( ScmUtils.getScmDeveloperConnection( model ) );
        model.setScm( new Scm() );
        assertNull( ScmUtils.getScmDeveloperConnection( model ) );
        String connection = "scmConnection";
        model.getScm().setDeveloperConnection( connection );
        assertEquals( connection, ScmUtils.getScmDeveloperConnection( model ) );
    }

    @Test
    public void getScmConnectionByMavenProject()
    {
        MavenProject project = new MavenProject();
        assertNull( ScmUtils.getScmConnection( project ) );
        project.setScm( new Scm() );
        assertNull( ScmUtils.getScmConnection( project ) );
        String connection = "scmConnection";
        project.getScm().setConnection( connection );
        assertEquals( connection, ScmUtils.getScmConnection( project ) );
    }

    @Test
    public void getScmDeveloperConnectionByMavenProject()
    {
        MavenProject project = new MavenProject();
        assertNull( ScmUtils.getScmDeveloperConnection( project ) );
        project.setScm( new Scm() );
        assertNull( ScmUtils.getScmDeveloperConnection( project ) );
        String connection = "scmConnection";
        project.getScm().setDeveloperConnection( connection );
        assertEquals( connection, ScmUtils.getScmDeveloperConnection( project ) );
    }

}
