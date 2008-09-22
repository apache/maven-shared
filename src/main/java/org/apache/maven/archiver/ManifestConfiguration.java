package org.apache.maven.archiver;

import org.apache.maven.artifact.Artifact;
import org.apache.maven.artifact.handler.ArtifactHandler;

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

/**
 * Capture common manifest configuration.
 *
 * @author <a href="mailto:brett@apache.org">Brett Porter</a>
 * @version $Id$
 * @todo is this general enough to be in Plexus Archiver?
 */
public class ManifestConfiguration
{
    public static final String CLASSPATH_LAYOUT_TYPE_SIMPLE = "simple";

    public static final String CLASSPATH_LAYOUT_TYPE_REPOSITORY = "repository";

    public static final String CLASSPATH_LAYOUT_TYPE_CUSTOM = "custom";

    private String mainClass;

    private String packageName;

    private boolean addClasspath;

    private boolean addExtensions;

    /**
     * This gets prefixed to all classpath entries.
     */
    private String classpathPrefix = "";

    /**
     * Add default implementation entries if this is an extension specification.
     *
     * @since 2.1
     */
    private boolean addDefaultSpecificationEntries;

    /**
     * Add default implementation entries if this is an extension.
     *
     * @since 2.1
     */
    private boolean addDefaultImplementationEntries;
    
    /**
     * The generated Class-Path entry will contains paths that follow the
     * Maven 2 repository layout:
     * $groupId[0]/../${groupId[n]/$artifactId/$version/{fileName}
     * @since 2.3
     * @deprecated Use {@link ManifestConfiguration#classpathLayoutType} instead.
     */
    private boolean classpathMavenRepositoryLayout = false;
    
    private String classpathLayoutType = CLASSPATH_LAYOUT_TYPE_SIMPLE;
    
    private String customClasspathLayout;

    public String getMainClass()
    {
        return mainClass;
    }

    public String getPackageName()
    {
        return packageName;
    }

    public boolean isAddClasspath()
    {
        return addClasspath;
    }

    public boolean isAddDefaultImplementationEntries()
    {
        return addDefaultImplementationEntries;
    }

    public boolean isAddDefaultSpecificationEntries()
    {
        return addDefaultSpecificationEntries;
    }

    public boolean isAddExtensions()
    {
        return addExtensions;
    }

    /**
     * @deprecated Use {@link ManifestConfiguration#getClasspathLayoutType()}, and compare to
     * CLASSPATH_LAYOUT_TYPE_SIMPLE or CLASSPATH_LAYOUT_TYPE_REPOSITORY, also declared in {@link ManifestConfiguration}.
     */
    public boolean isClasspathMavenRepositoryLayout()
    {
        return classpathMavenRepositoryLayout;
    }

    public void setAddClasspath( boolean addClasspath )
    {
        this.addClasspath = addClasspath;
    }

    public void setAddDefaultImplementationEntries( boolean addDefaultImplementationEntries )
    {
        this.addDefaultImplementationEntries = addDefaultImplementationEntries;
    }

    public void setAddDefaultSpecificationEntries( boolean addDefaultSpecificationEntries )
    {
        this.addDefaultSpecificationEntries = addDefaultSpecificationEntries;
    }

    public void setAddExtensions( boolean addExtensions )
    {
        this.addExtensions = addExtensions;
    }

    /**
     * @deprecated Use {@link ManifestConfiguration#setClasspathLayoutType(String)}, and use
     * CLASSPATH_LAYOUT_TYPE_SIMPLE, CLASSPATH_LAYOUT_TYPE_CUSTOM, or CLASSPATH_LAYOUT_TYPE_REPOSITORY, 
     * also declared in {@link ManifestConfiguration}.
     */
    public void setClasspathMavenRepositoryLayout( boolean classpathMavenRepositoryLayout )
    {
        this.classpathMavenRepositoryLayout = classpathMavenRepositoryLayout;
    }

    public void setClasspathPrefix( String classpathPrefix )
    {
        this.classpathPrefix = classpathPrefix;
    }

    public void setMainClass( String mainClass )
    {
        this.mainClass = mainClass;
    }

    public void setPackageName( String packageName )
    {
        this.packageName = packageName;
    }

    public String getClasspathPrefix()
    {
        String cpp = classpathPrefix.replaceAll( "\\\\", "/" );

        if ( cpp.length() != 0 && !cpp.endsWith( "/" ) )
        {
            cpp += "/";
        }

        return cpp;
    }

    /**
     * Return the type of layout to use when formatting classpath entries.
     * Default is taken from the constant CLASSPATH_LAYOUT_TYPE_SIMPLE, declared 
     * in this class, which has a value of 'simple'. Other values are: 'repository'
     * (CLASSPATH_LAYOUT_TYPE_REPOSITORY, or the same as a maven classpath layout),
     * and 'custom' (CLASSPATH_LAYOUT_TYPE_CUSTOM).
     * <br/>
     * <b>NOTE:</b> If you specify a type of 'custom' you MUST set {@link ManifestConfiguration#setCustomClasspathLayout(String)}.
     */
    public String getClasspathLayoutType()
    {
        return CLASSPATH_LAYOUT_TYPE_SIMPLE.equals( classpathLayoutType ) && classpathMavenRepositoryLayout ? CLASSPATH_LAYOUT_TYPE_REPOSITORY
                        : classpathLayoutType;
    }

    /**
     * Set the type of layout to use when formatting classpath entries.
     * Should be one of: 'simple' (CLASSPATH_LAYOUT_TYPE_SIMPLE), 'repository'
     * (CLASSPATH_LAYOUT_TYPE_REPOSITORY, or the same as a maven classpath layout),
     * and 'custom' (CLASSPATH_LAYOUT_TYPE_CUSTOM). The constant names noted here
     * are defined in the {@link ManifestConfiguration} class.
     * <br/>
     * <b>NOTE:</b> If you specify a type of 'custom' you MUST set {@link ManifestConfiguration#setCustomClasspathLayout(String)}.
     */
    public void setClasspathLayoutType( String classpathLayoutType )
    {
        this.classpathLayoutType = classpathLayoutType;
    }

    /**
     * Retrieve the layout expression for use when the layout type set in {@link ManifestConfiguration#setClasspathLayoutType(String)}
     * has the value 'custom'. <b>The default value is null.</b>
     * Expressions will be evaluated against the following ordered list of classpath-related objects:
     * <ol>
     *   <li>The current {@link Artifact} instance, if one exists.</li>
     *   <li>The current {@link ArtifactHandler} instance from the artifact above.</li>
     * </ol>
     * <br/>
     * <b>NOTE:</b> If you specify a layout type of 'custom' you MUST set this layout expression.
     */
    public String getCustomClasspathLayout()
    {
        return customClasspathLayout;
    }

    /**
     * Set the layout expression for use when the layout type set in {@link ManifestConfiguration#setClasspathLayoutType(String)}
     * has the value 'custom'. Expressions will be evaluated against the following ordered list of classpath-related objects:
     * <ol>
     *   <li>The current {@link Artifact} instance, if one exists.</li>
     *   <li>The current {@link ArtifactHandler} instance from the artifact above.</li>
     * </ol>
     * <br/>
     * <b>NOTE:</b> If you specify a layout type of 'custom' you MUST set this layout expression.
     */
    public void setCustomClasspathLayout( String customClasspathLayout )
    {
        this.customClasspathLayout = customClasspathLayout;
    }
}
