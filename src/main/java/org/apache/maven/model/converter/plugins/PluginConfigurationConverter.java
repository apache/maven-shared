package org.apache.maven.model.converter.plugins;

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

import org.apache.maven.model.Model;
import org.apache.maven.model.converter.ConverterListener;
import org.apache.maven.model.converter.ProjectConverterException;

import java.util.List;
import java.util.Properties;

/**
 * A plugin configuration converter reads properties from a v3 pom or project.properties and add them to the v4 pom.
 *
 * @author Fabrizio Giustina
 * @version $Id$
 */
public interface PluginConfigurationConverter
{
    void convertConfiguration( Model v4Model, org.apache.maven.model.v3_0_0.Model v3Model,
                               Properties projectProperties )
        throws ProjectConverterException;

    /**
     * Add a listeners list for all messages sended by the relocator.
     *
     * @param listeners The listeners list that will receive messages
     */
    void addListeners( List listeners );

    /**
     * Add a listener for all messages sended by the relocator.
     *
     * @param listener The listener that will receive messages
     */
    void addListener( ConverterListener listener );
}
