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

 File f = new File( basedir, 'target/site/configuration.html' );
assert f.exists();
content = f.text;

// parameter merged from pluginManagement
assert content.contains( 'pluginManagement = pluginManagement' );
// parameter from build.plugin is not merged
assert content.contains( 'buildPlugin = default' );
// parameter from build.plugin is not merged but pluginManagement
assert content.contains( 'buildAndManagement = pluginManagement' );
// parameter from reporting.plugin wins over pluginManagement
assert content.contains( 'reportingPlugin = reporting.plugin' );
// parameter from reporting.plugin.reportSet wins over reporting.plugin and pluginManagement
assert content.contains( 'reportingPluginReportSet = reporting.plugin.reportSet' );

return true;