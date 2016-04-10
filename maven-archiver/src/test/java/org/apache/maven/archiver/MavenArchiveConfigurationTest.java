package org.apache.maven.archiver;

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

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.entry;

import java.util.Map;

import org.junit.Before;
import org.junit.Test;

/**
 * @author Karl Heinz Marbaise <a href="mailto:khmarbaise@apache.org">khmarbaise@apache.org</a>.
 */
public class MavenArchiveConfigurationTest
{

    private ManifestConfiguration manifestConfiguration;

    private MavenArchiveConfiguration archive;

    @Before
    public void before()
    {
        this.manifestConfiguration = new ManifestConfiguration();
        archive = new MavenArchiveConfiguration();
        archive.setManifest( manifestConfiguration );
        archive.setForced( false );
        archive.setCompress( false );
        archive.setIndex( false );
    }

    @Test
    public void addingSingleEntryShouldBeReturned()
    {
        archive.addManifestEntry( "key1", "value1" );
        Map<String, String> manifestEntries = archive.getManifestEntries();
        assertThat( manifestEntries ).containsExactly( entry( "key1", "value1" ) );
    }

    @Test
    public void addingTwoEntriesShouldBeReturnedInInsertOrder()
    {
        archive.addManifestEntry( "key1", "value1" );
        archive.addManifestEntry( "key2", "value2" );
        Map<String, String> manifestEntries = archive.getManifestEntries();
        assertThat( manifestEntries ).containsExactly( entry( "key1", "value1" ), entry( "key2", "value2" ) );
    }

    @Test
    public void addingThreeEntriesShouldBeReturnedInInsertOrder()
    {
        archive.addManifestEntry( "key1", "value1" );
        archive.addManifestEntry( "key2", "value2" );
        archive.addManifestEntry( "key3", "value3" );
        Map<String, String> manifestEntries = archive.getManifestEntries();
        assertThat( manifestEntries ).containsExactly( entry( "key1", "value1" ), entry( "key2", "value2" ),
                                                       entry( "key3", "value3" ) );
    }
}
