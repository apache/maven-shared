package org.apache.maven.it;

import java.util.List;
import java.util.Map;
import java.util.Properties;

public interface InvocationRequest
{
    InvocationRequest setGoals( String goal );
    InvocationRequest setBasedir( String basedir );
    InvocationRequest addCliOption( String option );
    InvocationRequest setCliOptions( List cliOptions );
    InvocationRequest addSystemProperty( String key, String value );
    InvocationRequest setEnvars( Map envars );
    InvocationRequest addEnvar( String key, String value );
    InvocationRequest setAutoclean( boolean autoclean );
    
    boolean getAutoclean();
    String getGoals();
    String getBasedir();
    List getCliOptions();
    Properties getSystemProperties();
    Map getEnvars();    
}
