package org.apache.maven.shared.scriptinterpreter;
/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

import junit.framework.TestCase;
import org.apache.maven.plugin.logging.SystemStreamLog;
import org.apache.maven.shared.utils.io.FileUtils;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Olivier Lamy
 */
public class ScriptRunnerTest
    extends TestCase
{
    public void testBeanshell()
        throws Exception
    {
        File logFile = new File( "target/build.log" );
        if ( logFile.exists() )
        {
            logFile.delete();
        }
        SystemStreamLog systemStreamLog = new SystemStreamLog();

        ScriptRunner scriptRunner = new ScriptRunner( systemStreamLog );
        scriptRunner.setGlobalVariable( "globalVar", "Yeah baby it's rocks" );
        scriptRunner.run( "test", new File( "src/test/resources/bsh-test" ), "verify", buildContext(),
                          new FileLogger( logFile ), "foo", true );

        String logContent = FileUtils.fileRead( logFile );
        assertTrue( logContent.contains( new File( "src/test/resources/bsh-test/verify.bsh" ).getPath() ) );
        assertTrue( logContent.contains( "foo=bar" ) );
        assertTrue( logContent.contains( "globalVar=Yeah baby it's rocks"));

    }

    public void testBeanshellWithFile()
        throws Exception
    {
        File logFile = new File( "target/build.log" );
        if ( logFile.exists() )
        {
            logFile.delete();
        }
        SystemStreamLog systemStreamLog = new SystemStreamLog();

        ScriptRunner scriptRunner = new ScriptRunner( systemStreamLog );
        scriptRunner.setGlobalVariable( "globalVar", "Yeah baby it's rocks" );
        scriptRunner.run( "test", new File( "src/test/resources/bsh-test/verify.bsh" ), buildContext(),
                          new FileLogger( logFile ), "foo", true );

        String logContent = FileUtils.fileRead( logFile );
        assertTrue( logContent.contains( new File( "src/test/resources/bsh-test/verify.bsh" ).getPath() ) );
        assertTrue( logContent.contains( "foo=bar" ) );


    }

    public void testGroovy()
        throws Exception
    {
        File logFile = new File( "target/build.log" );
        if ( logFile.exists() )
        {
            logFile.delete();
        }
        SystemStreamLog systemStreamLog = new SystemStreamLog();

        ScriptRunner scriptRunner = new ScriptRunner( systemStreamLog );
        scriptRunner.setGlobalVariable( "globalVar", "Yeah baby it's rocks" );
        scriptRunner.run( "test", new File( "src/test/resources/groovy-test" ), "verify", buildContext(),
                          new FileLogger( logFile ), "foo", true );

        String logContent = FileUtils.fileRead( logFile );
        assertTrue( logContent.contains( new File( "src/test/resources/groovy-test/verify.groovy" ).getPath() ) );
        assertTrue( logContent.contains( "foo=bar" ) );
        assertTrue( logContent.contains( "globalVar=Yeah baby it's rocks"));

    }

    public void testGroovyWithFile()
        throws Exception
    {
        File logFile = new File( "target/build.log" );
        if ( logFile.exists() )
        {
            logFile.delete();
        }
        SystemStreamLog systemStreamLog = new SystemStreamLog();

        ScriptRunner scriptRunner = new ScriptRunner( systemStreamLog );
        scriptRunner.run( "test", new File( "src/test/resources/groovy-test/verify.groovy" ), buildContext(),
                          new FileLogger( logFile ), "foo", true );

        String logContent = FileUtils.fileRead( logFile );
        assertTrue( logContent.contains( new File( "src/test/resources/groovy-test/verify.groovy" ).getPath() ) );
        assertTrue( logContent.contains( "foo=bar" ) );


    }


    private Map<String, ? extends Object> buildContext()
    {
        Map<String, Object> context = new HashMap<String, Object>();
        context.put( "foo", "bar" );
        return context;
    }

}
