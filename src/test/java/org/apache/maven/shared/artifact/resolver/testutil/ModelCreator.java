package org.apache.maven.shared.artifact.resolver.testutil;

import org.apache.maven.model.Dependency;
import org.apache.maven.model.DependencyManagement;
import org.apache.maven.model.Model;

public class ModelCreator
{
    
    private Model model;
    
    public ModelCreator()
    {
        model = new Model();
        model.setModelVersion( "4.0.0" );
    }
    
    public ModelCreator withCoordinate( String groupId, String artifactId, String version )
    {
        model.setGroupId( groupId );
        model.setArtifactId( artifactId );
        model.setVersion( version );
        return this;
    }
    
    public ModelCreator withDependency( String groupId, String artifactId, String version )
    {
        Dependency dep = new Dependency();
        dep.setGroupId( groupId );
        dep.setArtifactId( artifactId );
        dep.setVersion( version );
        
        model.addDependency( dep );
        return this;
    }
    
    public Model getModel()
    {
        return model;
    }

    public ModelCreator withDefaultCoordinate()
    {
        return withCoordinate( "group.id", "artifact-id", "1" );
    }

    public ModelCreator withManagedDependency( String groupId, String artifactId, String version )
    {
        DependencyManagement dm = model.getDependencyManagement();
        if ( dm == null )
        {
            dm = new DependencyManagement();
            model.setDependencyManagement( dm );
        }
        
        Dependency dep = new Dependency();
        dep.setGroupId( groupId );
        dep.setArtifactId( artifactId );
        dep.setVersion( version );
        
        dm.addDependency( dep );
        
        return this;
    }
}
