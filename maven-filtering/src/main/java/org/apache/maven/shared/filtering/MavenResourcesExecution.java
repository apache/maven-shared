package org.apache.maven.shared.filtering;

/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

import java.io.File;
import java.io.Reader;
import java.util.ArrayList;
import java.util.List;

import org.apache.maven.execution.MavenSession;
import org.apache.maven.model.Resource;
import org.apache.maven.project.MavenProject;
import org.apache.maven.shared.utils.io.FileUtils;
import org.apache.maven.shared.utils.io.FileUtils.FilterWrapper;
import org.codehaus.plexus.interpolation.Interpolator;
import org.codehaus.plexus.interpolation.RegexBasedInterpolator;
import org.codehaus.plexus.interpolation.StringSearchInterpolator;
import org.codehaus.plexus.interpolation.ValueSource;

/**
 * A bean to configure a resources filtering execution.
 *
 * @author Olivier Lamy
 */
public class MavenResourcesExecution
    extends AbstractMavenFilteringRequest
{

    private List<Resource> resources;

    private File outputDirectory;

    private List<String> nonFilteredFileExtensions;

    private List<FileUtils.FilterWrapper> filterWrappers;

    private File resourcesBaseDirectory;

    private boolean useDefaultFilterWrappers = false;

    private boolean filterFilenames = false;

    private String encoding;

    /**
     * Overwrite existing files even if the destination files are newer. <code>false</code> by default.
     *
     * @since 1.0-beta-2
     */
    private boolean overwrite = false;

    /**
     * Copy any empty directories included in the Resources.
     *
     * @since 1.0-beta-2
     */
    private boolean includeEmptyDirs = false;

    /**
     * Do not stop trying to filter tokens when reaching EOL.
     *
     * @since 1.0
     */
    private boolean supportMultiLineFiltering;

    /**
     * Do nothing.
     */
    public MavenResourcesExecution()
    {
        // no op
    }

    /**
     * As we use a Maven project <code>useDefaultFilterWrappers</code> will be set to <code>true</code>.
     *
     * @param resources The list of resources.
     * @param outputDirectory The output directory.
     * @param mavenProject The maven project.
     * @param encoding The given encoding.
     * @param fileFilters The file filters.
     * @param nonFilteredFileExtensions The extensions which should not being filtered.
     * @param mavenSession The maven session.
     */
    public MavenResourcesExecution( List<Resource> resources, File outputDirectory, MavenProject mavenProject,
                                    String encoding, List<String> fileFilters, List<String> nonFilteredFileExtensions,
                                    MavenSession mavenSession )
    {
        super( mavenProject, fileFilters, mavenSession );
        this.encoding = encoding;
        this.resources = resources;
        this.outputDirectory = outputDirectory;
        this.nonFilteredFileExtensions = nonFilteredFileExtensions;
        this.useDefaultFilterWrappers = true;
        this.resourcesBaseDirectory = mavenProject.getBasedir();
    }

    /**
     * @param resources The list of resources.
     * @param outputDirectory The output directory.
     * @param encoding The given encoding.
     * @param filterWrappers The list of filter wrappers.
     * @param resourcesBaseDirectory The resources base directory.
     * @param nonFilteredFileExtensions The list of extensions which should not being filtered.
     */
    public MavenResourcesExecution( List<Resource> resources, File outputDirectory, String encoding,
                                    List<FileUtils.FilterWrapper> filterWrappers, File resourcesBaseDirectory,
                                    List<String> nonFilteredFileExtensions )
    {
        this();
        this.resources = resources;
        this.outputDirectory = outputDirectory;
        this.filterWrappers = filterWrappers;
        this.nonFilteredFileExtensions = nonFilteredFileExtensions;
        this.resourcesBaseDirectory = resourcesBaseDirectory;
        this.useDefaultFilterWrappers = false;
        setEncoding( encoding );
    }

    /**
     * Return the encoding.
     * 
     * @return Current encoding.
     */
    public String getEncoding()
    {
        return encoding;
    }

    /**
     * Set the value for encoding.
     * 
     * @param encoding Give the new value for encoding.
     */
    public void setEncoding( String encoding )
    {
        this.encoding = encoding;
    }

    /**
     * @return List of {@link org.apache.maven.model.Resource}
     */
    public List<Resource> getResources()
    {
        return resources;
    }

    /**
     * @param resources List of {@link org.apache.maven.model.Resource}
     */
    public void setResources( List<Resource> resources )
    {
        this.resources = resources;
    }

    /**
     * @return The output directory.
     */
    public File getOutputDirectory()
    {
        return outputDirectory;
    }

    /**
     * @param outputDirectory The output directory.
     */
    public void setOutputDirectory( File outputDirectory )
    {
        this.outputDirectory = outputDirectory;
    }

    /**
     * @return List of {@link String} file extensions to not filter
     */
    public List<String> getNonFilteredFileExtensions()
    {
        return nonFilteredFileExtensions;
    }

    /**
     * @param nonFilteredFileExtensions List of {@link String} file extensions to not filter
     */
    public void setNonFilteredFileExtensions( List<String> nonFilteredFileExtensions )
    {
        this.nonFilteredFileExtensions = nonFilteredFileExtensions;
    }

    /**
     * @return List of {@link FileUtils.FilterWrapper}
     */
    public List<FileUtils.FilterWrapper> getFilterWrappers()
    {
        return filterWrappers;
    }

    /**
     * @param filterWrappers List of {@link FileUtils.FilterWrapper}
     */
    public void setFilterWrappers( List<FileUtils.FilterWrapper> filterWrappers )
    {
        this.filterWrappers = filterWrappers;
    }

    /**
     * @param filterWrapper The filter wrapper which should be added.
     */
    public void addFilterWrapper( FilterWrapper filterWrapper )
    {
        if ( this.filterWrappers == null )
        {
            this.filterWrappers = new ArrayList<FilterWrapper>();
        }
        this.filterWrappers.add( filterWrapper );
    }

    /**
     * Helper to add {@link FileUtils.FilterWrapper}, will {@link RegexBasedInterpolator} with default regex Exp ${ }
     * and InterpolatorFilterReaderLineEnding with defaultTokens ${ }.
     *
     * @param valueSource The value Source.
     * @deprecated This doesn't support escaping use
     *             {@link #addFilerWrapperWithEscaping(ValueSource, String, String, String, boolean)}
     */
    public void addFilerWrapper( final ValueSource valueSource )
    {
        addFilterWrapper( new FileUtils.FilterWrapper()
        {
            public Reader getReader( Reader reader )
            {
                Interpolator propertiesInterpolator = new RegexBasedInterpolator();
                propertiesInterpolator.addValueSource( valueSource );
                return new InterpolatorFilterReaderLineEnding( reader, propertiesInterpolator,
                                                               InterpolatorFilterReaderLineEnding.DEFAULT_BEGIN_TOKEN,
                                                               InterpolatorFilterReaderLineEnding.DEFAULT_END_TOKEN,
                                                               false );
            }
        } );
    }

    /**
     * @param valueSource The valueSource.
     * @param startRegExp The start regular expression.
     * @param endRegExp The end regular expression.
     * @param startToken The start token.
     * @param endToken The end token.
     * @deprecated This doesn't support escaping use
     *             {@link #addFilerWrapperWithEscaping(ValueSource, String, String, String, boolean)}
     */
    public void addFilerWrapper( final ValueSource valueSource, final String startRegExp, final String endRegExp,
                                 final String startToken, final String endToken )
    {
        addFilterWrapper( new FileUtils.FilterWrapper()
        {
            public Reader getReader( Reader reader )
            {
                Interpolator propertiesInterpolator = new RegexBasedInterpolator( startRegExp, endRegExp );
                propertiesInterpolator.addValueSource( valueSource );
                return new InterpolatorFilterReaderLineEnding( reader, propertiesInterpolator, startToken, endToken,
                                                               false );
            }
        } );
    }

    /**
     * @param valueSource {@link ValueSource}
     * @param startExp start token like <code>${</code>
     * @param endExp endToken <code>}</code>
     * @param escapeString The escape string.
     * @since 1.0-beta-2
     * @deprecated This doesn't support escaping use
     *             {@link #addFilerWrapperWithEscaping(ValueSource, String, String, String, boolean)}
     */
    public void addFilerWrapperWithEscaping( final ValueSource valueSource, final String startExp, final String endExp,
                                             final String escapeString )
    {
        addFilterWrapper( new FileUtils.FilterWrapper()
        {
            public Reader getReader( Reader reader )
            {
                StringSearchInterpolator propertiesInterpolator = new StringSearchInterpolator( startExp, endExp );
                propertiesInterpolator.addValueSource( valueSource );
                propertiesInterpolator.setEscapeString( escapeString );
                InterpolatorFilterReaderLineEnding interpolatorFilterReader =
                    new InterpolatorFilterReaderLineEnding( reader, propertiesInterpolator, startExp, endExp, false );
                interpolatorFilterReader.setInterpolateWithPrefixPattern( false );
                return interpolatorFilterReader;
            }
        } );
    }

    /**
     * @param valueSource {@link ValueSource}
     * @param startExp start token like <code>${</code>
     * @param endExp endToken <code>}</code>
     * @param escapeString The escape string.
     * @param multiLineFiltering do we support or use filtering on multi lines with start and endtoken on multi lines
     * @since 1.0
     */
    public void addFilerWrapperWithEscaping( final ValueSource valueSource, final String startExp, final String endExp,
                                             final String escapeString, final boolean multiLineFiltering )
    {
        addFilterWrapper( new FileUtils.FilterWrapper()
        {
            public Reader getReader( Reader reader )
            {
                StringSearchInterpolator propertiesInterpolator = new StringSearchInterpolator( startExp, endExp );
                propertiesInterpolator.addValueSource( valueSource );
                propertiesInterpolator.setEscapeString( escapeString );
                InterpolatorFilterReaderLineEnding interpolatorFilterReader =
                    new InterpolatorFilterReaderLineEnding( reader, propertiesInterpolator, startExp, endExp,
                                                            multiLineFiltering );
                interpolatorFilterReader.setInterpolateWithPrefixPattern( false );
                return interpolatorFilterReader;
            }
        } );
    }

    /**
     * @return The resource base directory.
     */
    public File getResourcesBaseDirectory()
    {
        return resourcesBaseDirectory;
    }

    /**
     * @param resourcesBaseDirectory Set the resource base directory.
     */
    public void setResourcesBaseDirectory( File resourcesBaseDirectory )
    {
        this.resourcesBaseDirectory = resourcesBaseDirectory;
    }

    /**
     * @return use default filter wrapper
     */
    public boolean isUseDefaultFilterWrappers()
    {
        return useDefaultFilterWrappers;
    }

    /**
     * @param useDefaultFilterWrappers {@link #useDefaultFilterWrappers}
     */
    public void setUseDefaultFilterWrappers( boolean useDefaultFilterWrappers )
    {
        this.useDefaultFilterWrappers = useDefaultFilterWrappers;
    }

    /**
     * Overwrite existing files even if the destination files are newer.
     *
     * @return {@link #overwrite}
     * @since 1.0-beta-2
     */
    public boolean isOverwrite()
    {
        return overwrite;
    }

    /**
     * Overwrite existing files even if the destination files are newer.
     *
     * @param overwrite overwrite true or false.
     * @since 1.0-beta-2
     */
    public void setOverwrite( boolean overwrite )
    {
        this.overwrite = overwrite;
    }

    /**
     * Copy any empty directories included in the Resources.
     *
     * @return {@link #includeEmptyDirs}
     * @since 1.0-beta-2
     */
    public boolean isIncludeEmptyDirs()
    {
        return includeEmptyDirs;
    }

    /**
     * Copy any empty directories included in the Resources.
     *
     * @param includeEmptyDirs {@code true} to include empty directories, otherwise {@code false}.
     * @since 1.0-beta-2
     */
    public void setIncludeEmptyDirs( boolean includeEmptyDirs )
    {
        this.includeEmptyDirs = includeEmptyDirs;
    }

    /**
     * @return {@code true} if filenames are filtered, otherwise {@code false}
     * @since 1.2
     */
    public boolean isFilterFilenames()
    {
        return filterFilenames;
    }

    /**
     * @param filterFilenames {@code true} if filenames should be filtered, otherwise {@code false}
     * @since 1.2
     */
    public void setFilterFilenames( boolean filterFilenames )
    {
        this.filterFilenames = filterFilenames;
    }

    /**
     * @return {@link MavenResourcesExecution}
     */
    public MavenResourcesExecution copyOf()
    {
        MavenResourcesExecution mre = new MavenResourcesExecution();
        mre.setAdditionalProperties( mre.getAdditionalProperties() );
        mre.setEncoding( mre.getEncoding() );
        mre.setEscapedBackslashesInFilePath( mre.isEscapedBackslashesInFilePath() );
        mre.setEscapeString( mre.getEscapeString() );
        mre.setFileFilters( copyList( mre.getFileFilters() ) );
        mre.setFilterWrappers( copyList( mre.getFilterWrappers() ) );
        mre.setIncludeEmptyDirs( mre.isIncludeEmptyDirs() );
        mre.setInjectProjectBuildFilters( mre.isInjectProjectBuildFilters() );
        mre.setMavenProject( mre.getMavenProject() );
        mre.setMavenSession( mre.getMavenSession() );
        mre.setNonFilteredFileExtensions( copyList( mre.getNonFilteredFileExtensions() ) );
        mre.setOutputDirectory( mre.getOutputDirectory() );
        mre.setOverwrite( mre.isOverwrite() );
        mre.setProjectStartExpressions( copyList( mre.getProjectStartExpressions() ) );
        mre.setResources( copyList( mre.getResources() ) );
        mre.setResourcesBaseDirectory( mre.getResourcesBaseDirectory() );
        mre.setUseDefaultFilterWrappers( mre.isUseDefaultFilterWrappers() );
        mre.setSupportMultiLineFiltering( mre.isSupportMultiLineFiltering() );
        return mre;
    }

    private <T> List<T> copyList( List<T> lst )
    {
        if ( lst == null )
        {
            return null;
        }
        else if ( lst.isEmpty() )
        {
            return new ArrayList<T>();
        }
        else
        {
            return new ArrayList<T>( lst );
        }
    }

    /** {@inheritDoc} */
    public boolean isSupportMultiLineFiltering()
    {
        return supportMultiLineFiltering;
    }

    /** {@inheritDoc} */
    public void setSupportMultiLineFiltering( boolean supportMultiLineFiltering )
    {
        this.supportMultiLineFiltering = supportMultiLineFiltering;
    }
}
