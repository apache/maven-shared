package org.apache.maven.shared.project.utils;

import static org.junit.Assert.*;

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

}
