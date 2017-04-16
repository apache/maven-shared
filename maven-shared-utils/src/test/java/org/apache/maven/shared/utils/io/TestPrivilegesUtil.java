package org.apache.maven.shared.utils.io;

import static org.junit.Assert.fail;

import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;
import java.util.prefs.Preferences;

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

public class TestPrivilegesUtil
{

    public static void assertElevatedPrivileges()
    {
        Preferences prefs = Preferences.systemRoot();
        PrintStream systemErr = System.err;
        synchronized ( systemErr )
        { // better synchronize to avoid problems with
          // other threads that access System.err
            System.setErr( new PrintStream( new OutputStream()
            {
                @Override
                public void write( int b )
                    throws IOException
                {
                    // empty
                }
            } ) );
            try
            {
                long currentTimeMillis = System.currentTimeMillis();
                String key = "MVNTEST" + currentTimeMillis;

                prefs.put( key, "bar" ); // SecurityException on Windows
                prefs.remove( key );
                prefs.flush(); // BackingStoreException on Linux
            }
            catch ( Exception e )
            {
                fail( "This test needs access to privileged operations to work!! Run command line as Administrator!!" );
            }
            finally
            {
                System.setErr( systemErr );
            }
        }
    }

}
