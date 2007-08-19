package org.apache.maven.model.converter;

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
import org.apache.maven.model.Plugin;

/**
 * @author Dennis Lundberg
 * @version $Id$
 */
public class PluginComparatorTest
    extends TestCase
{
    Object object1;
    Object object2;
    Object object3;
    Object object4;
    Plugin plugin1;
    Plugin plugin2;
    Plugin plugin3;
    Plugin plugin4;
    Plugin plugin5;
    Plugin plugin6;
    Plugin plugin7;
    Plugin plugin8;
    Plugin plugin9;
    Plugin plugin10;
    PluginComparator comparator;

    protected void setUp()
        throws Exception
    {
        super.setUp();
        object3 = new Object();
        object4 = new Object();
        plugin1 = new Plugin();
        plugin1.setGroupId( null );
        plugin1.setArtifactId( null );
        plugin2 = new Plugin();
        plugin2.setGroupId( null );
        plugin2.setArtifactId( null );
        plugin3 = new Plugin();
        plugin3.setGroupId( "a" );
        plugin3.setArtifactId( "d" );
        plugin4 = new Plugin();
        plugin4.setGroupId( "a" );
        plugin4.setArtifactId( "d" );
        plugin5 = new Plugin();
        plugin5.setGroupId( "a" );
        plugin5.setArtifactId( "c" );
        plugin6 = new Plugin();
        plugin6.setGroupId( "a" );
        plugin6.setArtifactId( "e" );
        plugin7 = new Plugin();
        plugin7.setGroupId( "b" );
        plugin7.setArtifactId( "b" );
        plugin8 = new Plugin();
        plugin8.setGroupId( "b" );
        plugin8.setArtifactId( "e" );
        plugin9 = new Plugin();
        plugin9.setGroupId( null );
        plugin9.setArtifactId( "e" );
        plugin10 = new Plugin();
        plugin10.setGroupId( "a" );
        plugin10.setArtifactId( null );
        comparator = new PluginComparator();
    }

    public void testClass()
    {
        assertEquals( "Test class", 0, comparator.compare( object3, object4 ) );
        assertEquals( "Test class", 1, comparator.compare( plugin1, object4 ) );
        assertEquals( "Test class", -1, comparator.compare( object3, plugin2 ) );
    }

    public void testNullObjects()
    {
        assertEquals( "Test null objects", 0, comparator.compare( object1, object2 ) );
        assertEquals( "Test null objects", -1, comparator.compare( object1, object3 ) );
        assertEquals( "Test null objects", 1, comparator.compare( object3, object2 ) );
    }

    public void testNullValues()
    {
        assertEquals( "Test null values", -1, comparator.compare( plugin1, plugin3 ) );
        assertEquals( "Test null values", 1, comparator.compare( plugin3, plugin1 ) );
        assertEquals( "Test null values", -1, comparator.compare( plugin1, plugin9 ) );
        assertEquals( "Test null values", 1, comparator.compare( plugin9, plugin1 ) );
        assertEquals( "Test null values", -1, comparator.compare( plugin1, plugin10 ) );
        assertEquals( "Test null values", 1, comparator.compare( plugin10, plugin1 ) );
        assertEquals( "Test null values", -1, comparator.compare( plugin9, plugin10 ) );
        assertEquals( "Test null values", 1, comparator.compare( plugin10, plugin9 ) );
    }

    public void testValues()
    {
        assertEquals( "Test values", 0, comparator.compare( plugin3, plugin4 ) );
        assertEquals( "Test values", 1, comparator.compare( plugin3, plugin5 ) );
        assertEquals( "Test values", -1, comparator.compare( plugin5, plugin3 ) );
        assertEquals( "Test values", -1, comparator.compare( plugin3, plugin6 ) );
        assertEquals( "Test values", 1, comparator.compare( plugin6, plugin3 ) );
        assertEquals( "Test values", -1, comparator.compare( plugin3, plugin7 ) );
        assertEquals( "Test values", 1, comparator.compare( plugin7, plugin3 ) );
    }
}
