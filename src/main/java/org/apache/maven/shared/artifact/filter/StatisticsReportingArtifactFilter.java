package org.apache.maven.shared.artifact.filter;

import org.codehaus.plexus.logging.Logger;

public interface StatisticsReportingArtifactFilter
{
    
    void reportMissedCriteria( Logger logger );
    
    void reportFilteredArtifacts( Logger logger );
    
    boolean hasMissedCriteria();

}
