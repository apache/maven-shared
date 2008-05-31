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

/**
 * A logger used by {@link Invoker} instances to output diagnostic messages.
 * 
 * @see Invoker#setLogger(InvokerLogger)
 * @author <a href="mailto:jason@maven.org">Jason van Zyl</a>
 * @version $Id$
 */
public interface InvokerLogger
{

    /**
     * The threshold for debug output.
     */
    public static final int DEBUG = 4;

    /**
     * The threshold for info output.
     */
    public static final int INFO = 3;

    /**
     * The threshold for warn output.
     */
    public static final int WARN = 2;

    /**
     * The threshold for error output.
     */
    public static final int ERROR = 1;

    /**
     * The threshold for fatal error output.
     */
    public static final int FATAL = 0;

    /**
     * Logs the specified debug message.
     * 
     * @param message The message to log, may be <code>null</code>.
     */
    void debug( String message );

    /**
     * Logs the specified debug message and the accompanying exception.
     * 
     * @param message The message to log, may be <code>null</code>.
     * @param throwable The exception to log, may be <code>null</code>.
     */
    void debug( String message, Throwable throwable );

    /**
     * Tests whether debug output is enabled for this logger.
     * 
     * @return <code>true</code> if messages with priority "debug" or above are logged, <code>false</code>
     *         otherwise.
     */
    boolean isDebugEnabled();

    /**
     * Logs the specified info message.
     * 
     * @param message The message to log, may be <code>null</code>.
     */
    void info( String message );

    /**
     * Logs the specified info message and the accompanying exception.
     * 
     * @param message The message to log, may be <code>null</code>.
     * @param throwable The exception to log, may be <code>null</code>.
     */
    void info( String message, Throwable throwable );

    /**
     * Tests whether info output is enabled for this logger.
     * 
     * @return <code>true</code> if messages with priority "info" or above are logged, <code>false</code> otherwise.
     */
    boolean isInfoEnabled();

    /**
     * Logs the specified warning message.
     * 
     * @param message The message to log, may be <code>null</code>.
     */
    void warn( String message );

    /**
     * Logs the specified warning message and the accompanying exception.
     * 
     * @param message The message to log, may be <code>null</code>.
     * @param throwable The exception to log, may be <code>null</code>.
     */
    void warn( String message, Throwable throwable );

    /**
     * Tests whether warn output is enabled for this logger.
     * 
     * @return <code>true</code> if messages with priority "warn" or above are logged, <code>false</code> otherwise.
     */
    boolean isWarnEnabled();

    /**
     * Logs the specified error message.
     * 
     * @param message The message to log, may be <code>null</code>.
     */
    void error( String message );

    /**
     * Logs the specified error message and the accompanying exception.
     * 
     * @param message The message to log, may be <code>null</code>.
     * @param throwable The exception to log, may be <code>null</code>.
     */
    void error( String message, Throwable throwable );

    /**
     * Tests whether error output is enabled for this logger.
     * 
     * @return <code>true</code> if messages with priority "error" or above are logged, <code>false</code>
     *         otherwise.
     */
    boolean isErrorEnabled();

    /**
     * Logs the specified fatal error message.
     * 
     * @param message The message to log, may be <code>null</code>.
     */
    void fatalError( String message );

    /**
     * Logs the specified fatal error message and the accompanying exception.
     * 
     * @param message The message to log, may be <code>null</code>.
     * @param throwable The exception to log, may be <code>null</code>.
     */
    void fatalError( String message, Throwable throwable );

    /**
     * Tests whether fatal error output is enabled for this logger.
     * 
     * @return <code>true</code> if messages with priority "fatal" or above are logged, <code>false</code>
     *         otherwise.
     */
    boolean isFatalErrorEnabled();

    /**
     * Sets the logger's threshold.
     * 
     * @param threshold The logger's threshold, must be one of {@link #DEBUG}, {@link #INFO}, {@link #WARN},
     *            {@link #ERROR} and {@link #FATAL}.
     */
    void setThreshold( int threshold );

    /**
     * Gets the logger's threshold.
     * 
     * @return The logger's threshold, one of {@link #DEBUG}, {@link #INFO}, {@link #WARN}, {@link #ERROR} and
     *         {@link #FATAL}.
     */
    int getThreshold();

}
