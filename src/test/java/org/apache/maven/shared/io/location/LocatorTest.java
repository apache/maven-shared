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
import java.util.Collections;
import java.util.List;

import org.apache.maven.shared.io.MockManager;
import org.apache.maven.shared.io.logging.DefaultMessageHolder;
import org.apache.maven.shared.io.logging.MessageHolder;
import org.easymock.MockControl;

import junit.framework.TestCase;

public class LocatorTest
    extends TestCase
{

    public void testShouldConstructWithNoParams()
    {
        new Locator();
    }

    public void testShouldConstructWithStrategyStackAndMessageHolder()
    {
        new Locator( Collections.EMPTY_LIST, new DefaultMessageHolder() );
    }

    public void testShouldAllowModificationOfStrategiesAfterConstructionWithUnmodifiableStack()
    {
        Locator locator = new Locator( Collections.unmodifiableList( Collections.EMPTY_LIST ),
                                       new DefaultMessageHolder() );

        locator.addStrategy( new FileLocatorStrategy() );

        assertEquals( 1, locator.getStrategies().size() );
    }

    public void testShouldRetrieveNonNullMessageHolderWhenConstructedWithoutParams()
    {
        assertNotNull( new Locator().getMessageHolder() );
    }

    public void testSetStrategiesShouldClearAnyPreExistingStrategiesOut()
    {
        MockManager mgr = new MockManager();

        MockControl originalStrategyControl = MockControl.createControl( LocatorStrategy.class );

        mgr.add( originalStrategyControl );

        LocatorStrategy originalStrategy = (LocatorStrategy) originalStrategyControl.getMock();

        MockControl replacementStrategyControl = MockControl.createControl( LocatorStrategy.class );

        mgr.add( replacementStrategyControl );

        LocatorStrategy replacementStrategy = (LocatorStrategy) replacementStrategyControl.getMock();

        mgr.replayAll();

        Locator locator = new Locator();
        locator.addStrategy( originalStrategy );

        locator.setStrategies( Collections.singletonList( replacementStrategy ) );

        List strategies = locator.getStrategies();

        assertFalse( strategies.contains( originalStrategy ) );
        assertTrue( strategies.contains( replacementStrategy ) );

        mgr.verifyAll();
    }

    public void testShouldRemovePreviouslyAddedStrategy()
    {
        MockManager mgr = new MockManager();

        MockControl originalStrategyControl = MockControl.createControl( LocatorStrategy.class );

        mgr.add( originalStrategyControl );

        LocatorStrategy originalStrategy = (LocatorStrategy) originalStrategyControl.getMock();

        mgr.replayAll();

        Locator locator = new Locator();
        locator.addStrategy( originalStrategy );

        List strategies = locator.getStrategies();

        assertTrue( strategies.contains( originalStrategy ) );

        locator.removeStrategy( originalStrategy );

        strategies = locator.getStrategies();

        assertFalse( strategies.contains( originalStrategy ) );

        mgr.verifyAll();
    }

    public void testResolutionFallsThroughStrategyStackAndReturnsNullIfNotResolved()
    {
        List strategies = new ArrayList();

        strategies.add( new LoggingLocatorStrategy() );
        strategies.add( new LoggingLocatorStrategy() );
        strategies.add( new LoggingLocatorStrategy() );

        MessageHolder mh = new DefaultMessageHolder();

        Locator locator = new Locator( strategies, mh );

        Location location = locator.resolve( "some-specification" );

        assertNull( location );

        assertEquals( 3, mh.size() );
    }

    public static final class LoggingLocatorStrategy implements LocatorStrategy
    {

        static int instanceCounter = 0;

        int counter = instanceCounter++;

        public Location resolve( String locationSpecification, MessageHolder messageHolder )
        {
            messageHolder.addMessage( "resolve hit on strategy-" + (counter) );
            return null;
        }

    }

}
