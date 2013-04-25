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
File buildLog = new File( basedir, 'build.log' )
assert buildLog.text.contains( '[INFO] Resolved distributionManagement site URL for org.apache.maven.shared.project.utils.it.aggregator-flat:aggregator:pom:0.0.1-SNAPSHOT: http://localhost/aggregator')
// aggregator is not the parent of this project, so should use its own site URL
assert buildLog.text.contains( '[INFO] Resolved distributionManagement site URL for org.apache.maven.shared.project.utils.it.aggregator-flat:project:pom:0.0.1-SNAPSHOT: null' )

assert buildLog.text.contains( '[INFO] Resolved scm connection for org.apache.maven.shared.project.utils.it.aggregator-flat:aggregator:pom:0.0.1-SNAPSHOT: scm:local:/project' );
assert buildLog.text.contains( '[INFO] Resolved scm developer connection for org.apache.maven.shared.project.utils.it.aggregator-flat:aggregator:pom:0.0.1-SNAPSHOT: scm:local:/sproject' );

def LS = System.getProperty( "line.separator" )
assert buildLog.text.contains( "[INFO] Resolved scm connection for org.apache.maven.shared.project.utils.it.aggregator-flat:project:pom:0.0.1-SNAPSHOT: ${LS}" );
assert buildLog.text.contains( "[INFO] Resolved scm developer connection for org.apache.maven.shared.project.utils.it.aggregator-flat:project:pom:0.0.1-SNAPSHOT: ${LS}" );
