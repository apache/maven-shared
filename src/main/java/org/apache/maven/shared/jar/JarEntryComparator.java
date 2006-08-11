/**
 * 
 */
package org.apache.maven.shared.jar;

import java.util.Comparator;
import java.util.jar.JarEntry;

class JarEntryComparator
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