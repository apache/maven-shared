package org.apache.maven.shared.filtering;

import org.apache.maven.execution.MavenSession;
import org.apache.maven.project.MavenProject;

import java.util.List;
import java.util.Properties;

/**
 * @since 1.0-beta-3
 */
public class AbstractMavenFilteringRequest
{

    private MavenProject mavenProject;

    private List filters;

    private boolean escapeWindowsPaths = true;

    private String encoding;

    private MavenSession mavenSession;

    /**
     * String which will escape interpolation mechanism : foo \${foo.bar} -> foo ${foo.bar}
     * @since 1.0-beta-2
     */
    private String escapeString;
    
    /**
     * @since 1.0-beta-3
     */
    private Properties additionalProperties;
    
    /**
     * @since 1.0-beta-3
     */
    private boolean injectProjectBuildFilters = false;
    
    protected AbstractMavenFilteringRequest()
    {
    }

    protected AbstractMavenFilteringRequest( MavenProject mavenProject, List filters,
                                          String encoding, MavenSession mavenSession )
    {
        this.mavenProject = mavenProject;
        this.filters = filters;
        this.encoding = encoding;
        this.mavenSession = mavenSession;
    }

    public MavenProject getMavenProject()
    {
        return mavenProject;
    }

    public void setMavenProject( MavenProject mavenProject )
    {
        this.mavenProject = mavenProject;
    }

    public List getFilters()
    {
        return filters;
    }

    public void setFilters( List filters )
    {
        this.filters = filters;
    }

    public List getFileFilters()
    {
        return getFilters();
    }

    public void setFileFilters( List filters )
    {
        setFilters( filters );
    }

    /**
     * @since 1.0-beta-3
     */
    public boolean isEscapeWindowsPaths()
    {
        return escapeWindowsPaths;
    }

    /**
     * @since 1.0-beta-3
     */
    public void setEscapeWindowsPaths( boolean escapedBackslashesInFilePath )
    {
        this.escapeWindowsPaths = escapedBackslashesInFilePath;
    }
    
    public boolean isEscapedBackslashesInFilePath()
    {
        return isEscapeWindowsPaths();
    }
    
    public void setEscapedBackslashesInFilePath( boolean escape )
    {
        setEscapeWindowsPaths( escape );
    }

    public String getEncoding()
    {
        return encoding;
    }

    public void setEncoding( String encoding )
    {
        this.encoding = encoding;
    }

    public MavenSession getMavenSession()
    {
        return mavenSession;
    }

    public void setMavenSession( MavenSession mavenSession )
    {
        this.mavenSession = mavenSession;
    }

    /**
     * @since 1.0-beta-3
     */
    public Properties getAdditionalProperties()
    {
        return additionalProperties;
    }

    /**
     * @since 1.0-beta-3
     */
    public void setAdditionalProperties( Properties additionalProperties )
    {
        this.additionalProperties = additionalProperties;
    }

    /**
     * @since 1.0-beta-3
     */
    public boolean isInjectProjectBuildFilters()
    {
        return injectProjectBuildFilters;
    }

    /**
     * @since 1.0-beta-3
     */
    public void setInjectProjectBuildFilters( boolean injectProjectBuildFilters )
    {
        this.injectProjectBuildFilters = injectProjectBuildFilters;
    }

    /**
     * @return
     * @since 1.0-beta-2
     */
    public String getEscapeString()
    {
        return escapeString;
    }

    /**
     * @param escapeString
     * @since 1.0-beta-2
     */
    public void setEscapeString( String escapeString )
    {
        this.escapeString = escapeString;
    }
    
    

}
