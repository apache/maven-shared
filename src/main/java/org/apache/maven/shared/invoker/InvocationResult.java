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

import org.codehaus.plexus.util.cli.CommandLineException;

/**
 * Describes the result of a Maven invocation.
 * 
 * @author jdcasey
 * @version $Id$
 */
public interface InvocationResult
{

    /**
     * Gets the exception that possibly occurred during the execution of the command line.
     * 
     * @return The exception that prevented to invoke Maven or <code>null</code> if the command line was successfully
     *         processed by the operating system.
     */
    CommandLineException getExecutionException();

    /**
     * Gets the exit code from the Maven invocation. A non-zero value indicates a build failure. <strong>Note:</strong>
     * This value is undefined if {@link #getExecutionException()} reports an exception.
     * 
     * @return The exit code from the Maven invocation.
     */
    int getExitCode();

}
