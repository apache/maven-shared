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

/**
 * The Locator.
 *
 */
public final class Locator
{

    private List<LocatorStrategy> strategies;
    private final MessageHolder messageHolder;

    /**
     * @param strategies List of strategies.
     * @param messageHolder {@link MessageHolder}
     */
    public Locator( List<LocatorStrategy> strategies, MessageHolder messageHolder )
    {
        this.messageHolder = messageHolder;
        this.strategies = new ArrayList<LocatorStrategy>( strategies );
    }

    /**
     * Create instance.
     */
    public Locator()
    {
        this.messageHolder = new DefaultMessageHolder();
        this.strategies = new ArrayList<LocatorStrategy>();
    }

    /**
     * @return {@link MessageHolder}
     */
    public MessageHolder getMessageHolder()
    {
        return messageHolder;
    }

    /**
     * @param strategy The strategy to be added.
     */
    public void addStrategy( LocatorStrategy strategy )
    {
        this.strategies.add( strategy );
    }

    /**
     * @param strategy the strategy to remove.
     */
    public void removeStrategy( LocatorStrategy strategy )
    {
        this.strategies.remove( strategy );
    }

    /**
     * @param strategies the strategies to be set.
     */
    public void setStrategies( List<LocatorStrategy> strategies )
    {
        this.strategies.clear();
        this.strategies.addAll( strategies );
    }

    /**
     * @return list of strategies.
     */
    public List<LocatorStrategy> getStrategies()
    {
        return strategies;
    }

    /**
     * @param locationSpecification location spec.
     * @return {@link Location}
     */
    public Location resolve( String locationSpecification )
    {
        Location location = null;

        for ( Iterator<LocatorStrategy> it = strategies.iterator(); location == null && it.hasNext(); )
        {
            LocatorStrategy strategy = (LocatorStrategy) it.next();

            location = strategy.resolve( locationSpecification, messageHolder );
        }

        return location;
    }

}
