package org.apache.maven.shared.dependency.analyzer.asm;

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

import java.io.IOException;
import java.net.URL;
import java.util.Set;

import org.apache.maven.shared.dependency.analyzer.ClassFileVisitorUtils;
import org.apache.maven.shared.dependency.analyzer.DependencyAnalyzerWithUsages;
import org.apache.maven.shared.dependency.analyzer.DependencyUsage;
import org.codehaus.plexus.component.annotations.Component;

/**
 * ASMDependencyAnalyzerWithUsages
 *
 * @author <a href="mailto:hijon89@gmail.com">Jonathan Haber</a>
 * @version $Id$
 */
@Component( role = DependencyAnalyzerWithUsages.class )
public class ASMDependencyAnalyzerWithUsages
    implements DependencyAnalyzerWithUsages
{
  // DependencyAnalyzerWithUsages methods ---------------------------------------------

  /*
   * @see org.apache.maven.shared.dependency.analyzer.DependencyAnalyzerWithUsages#analyze(java.net.URL)
   */
  public Set<DependencyUsage> analyze( URL url )
      throws IOException
  {
    DependencyClassFileVisitor visitor = new DependencyClassFileVisitor();

    ClassFileVisitorUtils.accept( url, visitor );

    return visitor.getDependencyUsages();
  }
}
