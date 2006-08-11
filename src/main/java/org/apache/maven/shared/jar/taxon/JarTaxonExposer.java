package org.apache.maven.shared.jar.taxon;

/*
 * Copyright 2001-2006 The Apache Software Foundation.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import org.apache.maven.shared.jar.Jar;

import java.util.List;

/**
 * @author <a href="mailto:evenisse@apache.org">Emmanuel Venisse</a>
 * @version $Id$
 */
public interface JarTaxonExposer
{
    static final String ROLE = JarTaxonExposer.class.getName();

    void setJar( Jar jar );

    void expose();

    boolean isAuthoritative();

    String getExposerName();

    List getGroupIds();

    List getArtifactIds();

    List getVersions();

    List getNames();

    List getVendors();
}
