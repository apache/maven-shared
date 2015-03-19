package org.apache.maven.shared.invoker;

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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.net.MalformedURLException;

import org.junit.Test;

public class SystemOutLoggerTest
{

    private static final Throwable EXCEPTION =
        new MalformedURLException( "This is meant to happen. It's part of the test." );

    private static final String MESSAGE = "This is a test message.";

    @Test
    public void testDebugWithMessageOnly()
    {
        logTestStart();
        new SystemOutLogger().debug( MESSAGE );
    }

    @Test
    public void testDebugWithMessageAndError()
    {
        logTestStart();
        new SystemOutLogger().debug( MESSAGE, EXCEPTION );
    }

    @Test
    public void testDebugWithNullMessageAndNoError()
    {
        logTestStart();
        new SystemOutLogger().debug( null );
    }

    @Test
    public void testDebugWithNullMessageError()
    {
        logTestStart();
        new SystemOutLogger().debug( null, EXCEPTION );
    }

    @Test
    public void testDebugWithMessageNullError()
    {
        logTestStart();
        new SystemOutLogger().debug( MESSAGE, null );
    }

    @Test
    public void testInfoWithMessageOnly()
    {
        logTestStart();
        new SystemOutLogger().info( MESSAGE );
    }

    @Test
    public void testInfoWithMessageAndError()
    {
        logTestStart();
        new SystemOutLogger().info( MESSAGE, EXCEPTION );
    }

    @Test
    public void testInfoWithNullMessageAndNoError()
    {
        logTestStart();
        new SystemOutLogger().info( null );
    }

    @Test
    public void testInfoWithNullMessageError()
    {
        logTestStart();
        new SystemOutLogger().info( null, EXCEPTION );
    }

    @Test
    public void testInfoWithMessageNullError()
    {
        logTestStart();
        new SystemOutLogger().info( MESSAGE, null );
    }

    @Test
    public void testWarnWithMessageOnly()
    {
        logTestStart();
        new SystemOutLogger().warn( MESSAGE );
    }

    @Test
    public void testWarnWithMessageAndError()
    {
        logTestStart();
        new SystemOutLogger().warn( MESSAGE, EXCEPTION );
    }

    @Test
    public void testWarnWithNullMessageAndNoError()
    {
        logTestStart();
        new SystemOutLogger().warn( null );
    }

    @Test
    public void testWarnWithNullMessageError()
    {
        logTestStart();
        new SystemOutLogger().warn( null, EXCEPTION );
    }

    @Test
    public void testWarnWithMessageNullError()
    {
        logTestStart();
        new SystemOutLogger().warn( MESSAGE, null );
    }

    @Test
    public void testErrorWithMessageOnly()
    {
        logTestStart();
        new SystemOutLogger().error( MESSAGE );
    }

    @Test
    public void testErrorWithMessageAndError()
    {
        logTestStart();
        new SystemOutLogger().error( MESSAGE, EXCEPTION );
    }

    @Test
    public void testErrorWithNullMessageAndNoError()
    {
        logTestStart();
        new SystemOutLogger().error( null );
    }

    @Test
    public void testErrorWithNullMessageError()
    {
        logTestStart();
        new SystemOutLogger().error( null, EXCEPTION );
    }

    @Test
    public void testErrorWithMessageNullError()
    {
        logTestStart();
        new SystemOutLogger().error( MESSAGE, null );
    }

    @Test
    public void testFatalErrorWithMessageOnly()
    {
        logTestStart();
        new SystemOutLogger().fatalError( MESSAGE );
    }

    @Test
    public void testFatalErrorWithMessageAndError()
    {
        logTestStart();
        new SystemOutLogger().fatalError( MESSAGE, EXCEPTION );
    }

    @Test
    public void testFatalErrorWithNullMessageAndNoError()
    {
        logTestStart();
        new SystemOutLogger().fatalError( null );
    }

    @Test
    public void testFatalErrorWithNullMessageError()
    {
        logTestStart();
        new SystemOutLogger().fatalError( null, EXCEPTION );
    }

    @Test
    public void testFatalErrorWithMessageNullError()
    {
        logTestStart();
        new SystemOutLogger().fatalError( MESSAGE, null );
    }

    @Test
    public void testDefaultThresholdInfo()
    {
        assertEquals( InvokerLogger.INFO, new SystemOutLogger().getThreshold() );
    }

    @Test
    public void testThresholdDebug()
    {
        InvokerLogger logger = new SystemOutLogger();
        logger.setThreshold( InvokerLogger.DEBUG );
        assertTrue( logger.isDebugEnabled() );
        assertTrue( logger.isInfoEnabled() );
        assertTrue( logger.isWarnEnabled() );
        assertTrue( logger.isErrorEnabled() );
        assertTrue( logger.isFatalErrorEnabled() );
    }

    @Test
    public void testThresholdInfo()
    {
        InvokerLogger logger = new SystemOutLogger();
        logger.setThreshold( InvokerLogger.INFO );
        assertFalse( logger.isDebugEnabled() );
        assertTrue( logger.isInfoEnabled() );
        assertTrue( logger.isWarnEnabled() );
        assertTrue( logger.isErrorEnabled() );
        assertTrue( logger.isFatalErrorEnabled() );
    }

    @Test
    public void testThresholdWarn()
    {
        InvokerLogger logger = new SystemOutLogger();
        logger.setThreshold( InvokerLogger.WARN );
        assertFalse( logger.isDebugEnabled() );
        assertFalse( logger.isInfoEnabled() );
        assertTrue( logger.isWarnEnabled() );
        assertTrue( logger.isErrorEnabled() );
        assertTrue( logger.isFatalErrorEnabled() );
    }

    @Test
    public void testThresholdError()
    {
        InvokerLogger logger = new SystemOutLogger();
        logger.setThreshold( InvokerLogger.ERROR );
        assertFalse( logger.isDebugEnabled() );
        assertFalse( logger.isInfoEnabled() );
        assertFalse( logger.isWarnEnabled() );
        assertTrue( logger.isErrorEnabled() );
        assertTrue( logger.isFatalErrorEnabled() );
    }

    @Test
    public void testThresholdFatal()
    {
        InvokerLogger logger = new SystemOutLogger();
        logger.setThreshold( InvokerLogger.FATAL );
        assertFalse( logger.isDebugEnabled() );
        assertFalse( logger.isInfoEnabled() );
        assertFalse( logger.isWarnEnabled() );
        assertFalse( logger.isErrorEnabled() );
        assertTrue( logger.isFatalErrorEnabled() );
    }

    // this is just a debugging helper for separating unit test output...
    private void logTestStart()
    {
        NullPointerException npe = new NullPointerException();
        StackTraceElement element = npe.getStackTrace()[1];

        System.out.println( "Starting: " + element.getMethodName() );
    }

}
