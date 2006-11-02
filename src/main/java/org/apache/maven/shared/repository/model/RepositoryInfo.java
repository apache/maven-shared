package org.apache.maven.shared.repository.model;

import java.util.List;

public interface RepositoryInfo
{
    
    List getGroupVersionAlignments();
    
    boolean isIncludeMetadata();
    
    String getScope();

    List getIncludes();

    List getExcludes();

}
