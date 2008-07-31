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

import java.util.Properties;

import org.codehaus.plexus.interpolation.ValueSource;

/**
 * @author <a href="mailto:olamy@apache.org">olamy</a>
 * @version $Id$
 */
public class PropertiesEscapingBackSlashValueSource
    implements ValueSource
{

    private boolean escapedBackslashesInFilePath;

    private Properties properties;

    /**
     * 
     */
    public PropertiesEscapingBackSlashValueSource( boolean escapedBackslashesInFilePath, Properties properties )
    {
        this.escapedBackslashesInFilePath = escapedBackslashesInFilePath;
        this.properties = properties == null ? new Properties() : properties;
    }

    /** 
     * @see org.codehaus.plexus.interpolation.ValueSource#getValue(java.lang.String)
     */
    public Object getValue( String expression )
    {
        String value = properties.getProperty( expression );
        return escapedBackslashesInFilePath ? FilteringUtils.escapeWindowsPath( value ) : value;
    }

}
