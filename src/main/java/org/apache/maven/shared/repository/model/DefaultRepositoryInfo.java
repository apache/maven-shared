package org.apache.maven.shared.repository.model;

import java.util.List;

public class DefaultRepositoryInfo
    implements RepositoryInfo
{

    private boolean includeMetadata;
    private String scope;
    private List includes;
    private List groupVersionAlignments;
    private List excludes;

    public List getExcludes()
    {
        return excludes;
    }

    public void setGroupVersionAlignments( List groupVersionAlignments )
    {
        this.groupVersionAlignments = groupVersionAlignments;
    }

    public void setIncludeMetadata( boolean includeMetadata )
    {
        this.includeMetadata = includeMetadata;
    }

    public void setIncludes( List includes )
    {
        this.includes = includes;
    }

    public void setScope( String scope )
    {
        this.scope = scope;
    }

    public List getGroupVersionAlignments()
    {
        return groupVersionAlignments;
    }

    public List getIncludes()
    {
        return includes;
    }

    public String getScope()
    {
        return scope;
    }

    public boolean isIncludeMetadata()
    {
        return includeMetadata;
    }

    public void setExcludes( List excludes )
    {
        this.excludes = excludes;
    }

}
