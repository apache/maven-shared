package org.apache.maven.it;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

public class DefaultInvocationRequest
    implements InvocationRequest
{
    private List cliOptions = new ArrayList();
    private Map envars = new HashMap();
    private Properties systemProperties = new Properties();
    private String basedir;
    private String goals;
    private boolean autoclean;
        
    public InvocationRequest setCliOptions( List options )
    {
        this.cliOptions = options;
        
        return this;
    }
    
    public InvocationRequest addCliOption( String option )
    {
        if ( cliOptions == null )
        {
            cliOptions = new ArrayList();
        }
        
        cliOptions.add( option );
        
        return this;
    }

    public List getCliOptions()
    {
        return cliOptions;
    }
    
    public InvocationRequest addEnvar( String key, String value )
    {
       if ( envars == null )
       {
           envars = new HashMap();
       }
       
       envars.put(  key, value );   
       
       return this;
    }

    public Map getEnvars()
    {
        return envars;
    }
    
    public InvocationRequest addSystemProperty( String key, String value )
    {
        if ( systemProperties == null )
        {
            systemProperties = new Properties();
        }
        
        systemProperties.setProperty( key, value );
        
        return this;
    }

    public Properties getSystemProperties()
    {
        return systemProperties;
    }
    
    public InvocationRequest setBasedir( String basedir )
    {
        this.basedir = basedir;
        
        return this;
    }

    public String getBasedir()
    {
        return basedir;
    }
    
    public InvocationRequest setGoals( String goals )
    {
        this.goals = goals;
        
        return this;
    }
    
    public String getGoals()
    {
        return goals;
    }

    public InvocationRequest setEnvars( Map envars )
    {
        // TODO Auto-generated method stub
        this.envars = envars;
        return this;
    }

    public boolean getAutoclean()
    {
        return autoclean;
    }

    public InvocationRequest setAutoclean( boolean autoclean )
    {
        this.autoclean = autoclean;
        
        return this;
    }
}
