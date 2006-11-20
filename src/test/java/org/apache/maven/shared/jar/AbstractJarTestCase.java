package org.apache.maven.shared.jar;

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

import junit.framework.AssertionFailedError;
import org.codehaus.plexus.PlexusTestCase;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Abstract JarAnalyzer TestCase
 */
public class AbstractJarTestCase
    extends PlexusTestCase
{
    protected File basedir;

    protected File testdir;

    public AbstractJarTestCase()
    {
        super();
        String userdir = System.getProperty( "user.dir" );
        this.basedir = new File( System.getProperty( "basedir", userdir ) );
        this.testdir = new File( basedir, "src/test" );
    }

    public File getSampleJarsDirectory()
    {
        return new File( testdir, "jars" );
    }

    public File getTestLocalRepoDirectory()
    {
        return new File( testdir, "localrepo" );
    }
    
    public JarAnalyzerFactory getJarAnalyzerFactory() throws Exception
    {
        return (JarAnalyzerFactory) lookup( JarAnalyzerFactory.ROLE, "default" );
    }

    public void assertContains( String msg, Object expected, Collection coll )
    {
        if ( !coll.contains( expected ) )
        {
            throw new AssertionFailedError( msg + " collection did not have: " + expected.toString() );
        }
    }

    public void assertNotContainsRegex( String msg, String regex, Collection coll )
    {
        List failures = new ArrayList();
        Pattern pat = Pattern.compile( regex );
        Matcher mat;
        Iterator it = coll.iterator();
        while ( it.hasNext() )
        {
            String value = (String) it.next();
            mat = pat.matcher( value );
            if ( mat.find() )
            {
                failures.add( value );
            }
        }

        if ( !failures.isEmpty() )
        {
            StringBuffer sb = new StringBuffer();
            sb.append( msg ).append( " collection has illegal regex \"" ).append( regex ).append( "\"" );
            it = failures.iterator();
            while ( it.hasNext() )
            {
                sb.append( "\n   - \"" ).append( it.next() ).append( "\"" );
            }
            throw new AssertionFailedError( sb.toString() );
        }
    }
}
