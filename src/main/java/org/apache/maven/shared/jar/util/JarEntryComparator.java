package org.apache.maven.shared.jar.util;

/*
 * Copyright 2001-2006 The Apache Software Foundation.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import java.util.Comparator;
import java.util.jar.JarEntry;

/**
 * JarEntryComparator 
 *
 * @author <a href="mailto:joakim@erdfelt.com">Joakim Erdfelt</a>
 * @version $Id$
 */
public class JarEntryComparator
    implements Comparator
{

    public int compare( Object o1, Object o2 )
    {
        if ( !( o1 instanceof JarEntry ) )
        {
            return 0;
        }
        
        if ( !( o2 instanceof JarEntry ) )
        {
            return 0;
        }
        
        JarEntry j1 = (JarEntry) o1;
        JarEntry j2 = (JarEntry) o2;

        return j1.getName().compareTo( j2.getName() );
    }

}