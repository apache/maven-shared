package org.apache.maven.shared.io.location;

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

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.maven.shared.io.logging.DefaultMessageHolder;
import org.apache.maven.shared.io.logging.MessageHolder;

public final class Locator
{

    private List strategies;
    private final MessageHolder messageHolder;

    public Locator( List strategies, MessageHolder messageHolder )
    {
        this.messageHolder = messageHolder;
        this.strategies = new ArrayList( strategies );
    }

    public Locator()
    {
        this.messageHolder = new DefaultMessageHolder();
        this.strategies = new ArrayList();
    }

    public MessageHolder getMessageHolder()
    {
        return messageHolder;
    }

    public void addStrategy( LocatorStrategy strategy )
    {
        this.strategies.add( strategy );
    }

    public void removeStrategy( LocatorStrategy strategy )
    {
        this.strategies.remove( strategy );
    }

    public void setStrategies( List strategies )
    {
        this.strategies.clear();
        this.strategies.addAll( strategies );
    }

    public List getStrategies()
    {
        return strategies;
    }

    public Location resolve( String locationSpecification )
    {
        Location location = null;

        for ( Iterator it = strategies.iterator(); location == null && it.hasNext(); )
        {
            LocatorStrategy strategy = (LocatorStrategy) it.next();

            location = strategy.resolve( locationSpecification, messageHolder );
        }

        return location;
    }

}
