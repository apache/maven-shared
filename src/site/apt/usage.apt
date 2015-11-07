 ------
 Usage
 ------
 Olivier Lamy
 ------
 2011-01-05
 ------

 ~~ Licensed to the Apache Software Foundation (ASF) under one
 ~~ or more contributor license agreements.  See the NOTICE file
 ~~ distributed with this work for additional information
 ~~ regarding copyright ownership.  The ASF licenses this file
 ~~ to you under the Apache License, Version 2.0 (the
 ~~ "License"); you may not use this file except in compliance
 ~~ with the License.  You may obtain a copy of the License at
 ~~
 ~~   http://www.apache.org/licenses/LICENSE-2.0
 ~~
 ~~ Unless required by applicable law or agreed to in writing,
 ~~ software distributed under the License is distributed on an
 ~~ "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 ~~ KIND, either express or implied.  See the License for the
 ~~ specific language governing permissions and limitations
 ~~ under the License.

Usage

* Filter a <<<List>>> of <<<org.apache.maven.model.Resource>>>

  Lookup the component in your Mojo:

+-----+
    @Required
    private MavenResourcesFiltering mavenResourcesFiltering;
+-----+

  Apply filtering on your <<<List>>> of resources, see {{{./index.html}Introduction}} for the default <<<FilterWrappers>>>
  that are used.

+-----+
        MavenResourcesExecution mavenResourcesExecution =
            new MavenResourcesExecution ( resources, outputDirectory, mavenProject,
                                          encoding, fileFilters,
                                          nonFilteredFileExtensions, mavenSession );

        mavenResourcesFiltering.filterResources( mavenResourcesExecution );
+-----+

* Add a new filtering token

  You must use the other methods from the <<<MavenResourcesFiltering>>> component and construct your own <<<List>>> of
  <<<FilterWrappers>>>. The following example adds interpolation for the token @ @ using values coming from reflection
  with the Maven Project.

+-----+
        // Create your FilterWrapper
        FileUtils.FilterWrapper filterWrapper = new FileUtils.FilterWrapper()
        {
            public Reader getReader( Reader reader )
            {
                Interpolator propertiesInterpolator =
                    new RegexBasedInterpolator(  "\\@", "(.+?)\\@" );
                ValueSource valueSource = new MavenProjectValueSource( mavenProject,
                                                                       true );
                propertiesInterpolator.addValueSource( valueSource );
                return new InterpolatorFilterReader( reader,
                                                     propertiesInterpolator,
                                                     "@", "@" );
            }
        };

        // Add the new filterWrapper to your MavenResourcesExecution instance
        mavenResourcesExecution.addFilterWrapper( filterWrapper );
+-----+

  There is a helper method to simplify this. Here's how you would use it to do what we did above:

+-----+
        mavenResourcesExecution.addFilerWrapper( new MavenProjectValueSource( mavenProject,
                                                                              true ),
                                                 "\\@", "(.+?)\\@", "@", "@" );
+-----+

  <<Note:>> If <<<mavenResourcesExecution.useDefaultFilterWrappers>>> is set to <<<true>>>,
  the default <<<FilterWrapper>>>s will be added first.

  Now it's time to filter the resources:

+-----+
        // Apply filtering on your resources
        mavenResourcesFiltering.filterResources( mavenResourcesExecution );
+-----+

  <<Note:>> Maven Filtering uses the
  {{{http://codehaus-plexus.github.io/plexus-interpolation/}plexus-interpolation component}}.

