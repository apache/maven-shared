package org.apache.maven.shared.dependency;

/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
import static org.junit.Assert.assertEquals;

import org.apache.maven.shared.dependencies.DefaultDependableCoordinate;
import org.junit.Test;

public class DefaultDependencyCoordinateTest
{

    @Test
    public void testToStringWithoutType()
    {
        DefaultDependableCoordinate coordinate = new DefaultDependableCoordinate();
        coordinate.setGroupId( "GROUPID" );
        coordinate.setArtifactId( "ARTIFACTID" );
        coordinate.setVersion( "VERSION" );
        assertEquals( "GROUPID:ARTIFACTID:jar:VERSION", coordinate.toString() );
    }

    @Test
    public void testToStringWithClassifier()
    {
        DefaultDependableCoordinate coordinate = new DefaultDependableCoordinate();
        coordinate.setGroupId( "GROUPID" );
        coordinate.setArtifactId( "ARTIFACTID" );
        coordinate.setVersion( "VERSION" );
        coordinate.setClassifier( "CLASSIFIER" );
        coordinate.setType( "TYPE" );
        assertEquals( "GROUPID:ARTIFACTID:TYPE:CLASSIFIER:VERSION", coordinate.toString() );
    }

}
