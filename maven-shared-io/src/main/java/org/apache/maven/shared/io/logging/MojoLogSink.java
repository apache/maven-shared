package org.apache.maven.shared.io.logging;

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

import org.apache.maven.plugin.logging.Log;


/**
 * The Mojo Log Sink.
 *
 */
public class MojoLogSink
    implements MessageSink
{

    private final Log logger;

    /**
     * @param logger {@link Log}
     */
    public MojoLogSink( Log logger )
    {
        this.logger = logger;
    }

    /** {@inheritDoc} */
    public void debug( String message )
    {
        logger.debug( message );
    }

    /** {@inheritDoc} */
    public void error( String message )
    {
        logger.error( message );
    }

    /** {@inheritDoc} */
    public void info( String message )
    {
        logger.info( message );
    }

    /** {@inheritDoc} */
    public void severe( String message )
    {
        logger.error( message );
    }

    /** {@inheritDoc} */
    public void warning( String message )
    {
        logger.warn( message );
    }

}
