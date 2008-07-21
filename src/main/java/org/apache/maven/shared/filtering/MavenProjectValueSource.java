package org.apache.maven.shared.filtering;

/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

import java.io.File;

import org.apache.maven.project.MavenProject;
import org.codehaus.plexus.interpolation.ValueSource;
import org.codehaus.plexus.util.StringUtils;
import org.codehaus.plexus.util.introspection.ReflectionValueExtractor;


/**
 * @author Andreas Hoheneder (ahoh_at_inode.at)
 * @author <a href="mailto:olamy@apache.org">olamy</a>
 * @version $Id$
 */
public class MavenProjectValueSource
    implements ValueSource
{

    private MavenProject project;

    private boolean escapedBackslashesInFilePath;

    public MavenProjectValueSource( MavenProject mavenProject  ) 
    {
       this( mavenProject, false );
    }    
    
    public MavenProjectValueSource( MavenProject mavenProject, boolean escapedBackslashesInFilePath ) 
    {
       super();

       project = mavenProject;

       this.escapedBackslashesInFilePath = escapedBackslashesInFilePath;
    }
    

    public Object getValue( String expression )
    {
        if ( expression == null || StringUtils.isEmpty( expression.toString() ) )
        {
            return null;
        }
        
        Object value = null;
        try 
        {
            value = ReflectionValueExtractor.evaluate( "" + expression, project );

            if ( escapedBackslashesInFilePath && value != null
                && "java.lang.String".equals( value.getClass().getName() ) )
            {
                String val = (String) value;
                value = FilteringUtils.escapeWindowsPath( val );
            }
            else if ( escapedBackslashesInFilePath && value != null
                && File.class.getName().equals( value.getClass().getName() ) )
            {
                String val = ( (File) value ).getPath();
                value = FilteringUtils.escapeWindowsPath( val );
            }
            
        }
        catch ( Exception e ) 
        {
            //TODO: remove the try-catch block when ReflectionValueExtractor.evaluate() throws no more exceptions
        } 
        return value;
    }    
}
