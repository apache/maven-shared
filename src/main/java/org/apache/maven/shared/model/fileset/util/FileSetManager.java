package org.apache.maven.shared.model.fileset.util;

/*
 * Copyright 2001-2004 The Apache Software Foundation.
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

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.apache.maven.plugin.logging.Log;
import org.apache.maven.shared.io.logging.DefaultMessageHolder;
import org.apache.maven.shared.io.logging.MessageHolder;
import org.apache.maven.shared.io.logging.MessageLevels;
import org.apache.maven.shared.io.logging.MojoLogSink;
import org.apache.maven.shared.io.logging.PlexusLoggerSink;
import org.apache.maven.shared.model.fileset.FileSet;
import org.codehaus.plexus.logging.Logger;
import org.codehaus.plexus.util.DirectoryScanner;

/**
 * Provides operations for use with FileSet instances, such as retrieving the included/excluded
 * files, deleting all matching entries, etc.
 * 
 * @author jdcasey
 *
 */
public class FileSetManager
{
    
    private static final int DELETE_RETRY_SLEEP_MILLIS = 10;
    private static final String[] EMPTY_STRING_ARRAY = new String[0];
    
    private final boolean verbose;
    private MessageHolder messages;

    /**
     * Create a new manager instance with the supplied log instance and flag for whether to output
     * verbose messages.
     * 
     * @param log The mojo log instance
     * @param verbose Whether to output verbose messages
     */
    public FileSetManager( Log log, boolean verbose )
    {
        if ( verbose )
        {
            this.messages = new DefaultMessageHolder( MessageLevels.LEVEL_DEBUG, MessageLevels.LEVEL_INFO, new MojoLogSink( log ) );
        }
        else
        {
            this.messages = new DefaultMessageHolder( MessageLevels.LEVEL_INFO, MessageLevels.LEVEL_INFO, new MojoLogSink( log ) );
        }
        
        this.verbose = verbose;
    }
    
    /**
     * Create a new manager instance with the supplied log instance. Verbose flag is set to false.
     * 
     * @param log The mojo log instance
     */
    public FileSetManager( Log log )
    {
        this.messages = new DefaultMessageHolder( MessageLevels.LEVEL_INFO, MessageLevels.LEVEL_INFO, new MojoLogSink( log ) );
        this.verbose = false;
    }
    
    /**
     * Create a new manager instance with the supplied log instance and flag for whether to output
     * verbose messages.
     * 
     * @param log The mojo log instance
     * @param verbose Whether to output verbose messages
     */
    public FileSetManager( Logger log, boolean verbose )
    {
        if ( verbose )
        {
            this.messages = new DefaultMessageHolder( MessageLevels.LEVEL_DEBUG, MessageLevels.LEVEL_INFO, new PlexusLoggerSink( log ) );
        }
        else
        {
            this.messages = new DefaultMessageHolder( MessageLevels.LEVEL_INFO, MessageLevels.LEVEL_INFO, new PlexusLoggerSink( log ) );
        }
        
        this.verbose = verbose;
    }
    
    /**
     * Create a new manager instance with the supplied log instance. Verbose flag is set to false.
     * 
     * @param log The mojo log instance
     */
    public FileSetManager( Logger log )
    {
        this.messages = new DefaultMessageHolder( MessageLevels.LEVEL_INFO, MessageLevels.LEVEL_INFO, new PlexusLoggerSink( log ) );
        this.verbose = false;
    }
    
    /**
     * Create a new manager instance with an empty messages. Verbose flag is set to false.
     * 
     * @param log The mojo log instance
     */
    public FileSetManager()
    {
        this.verbose = false;
    }
    
    /**
     * Get all the filenames which have been included by the rules in this fileset.
     * @param fileSet The fileset defining rules for inclusion/exclusion, and base directory.
     * @return the array of matching filenames, relative to the basedir of the file-set.
     */
    public String[] getIncludedFiles( FileSet fileSet )
    {
        DirectoryScanner scanner = scan( fileSet );
        
        if ( scanner != null )
        {
            return scanner.getIncludedFiles();
        }        
        else
        {
            return EMPTY_STRING_ARRAY;
        }
    }

    /**
     * Get all the directory names which have been included by the rules in this fileset.
     * @param fileSet The fileset defining rules for inclusion/exclusion, and base directory.
     * @return the array of matching dirnames, relative to the basedir of the file-set.
     */
    public String[] getIncludedDirectories( FileSet fileSet )
    {
        DirectoryScanner scanner = scan( fileSet );
        
        if ( scanner != null )
        {
            return scanner.getIncludedDirectories();
        }        
        else
        {
            return EMPTY_STRING_ARRAY;
        }
    }

    /**
     * Get all the filenames which have been excluded by the rules in this fileset.
     * @param fileSet The fileset defining rules for inclusion/exclusion, and base directory.
     * @return the array of non-matching filenames, relative to the basedir of the file-set.
     */
    public String[] getExcludedFiles( FileSet fileSet )
    {
        DirectoryScanner scanner = scan( fileSet );
        
        if ( scanner != null )
        {
            return scanner.getExcludedFiles();
        }        
        else
        {
            return EMPTY_STRING_ARRAY;
        }
    }

    /**
     * Get all the directory names which have been excluded by the rules in this fileset.
     * @param fileSet The fileset defining rules for inclusion/exclusion, and base directory.
     * @return the array of non-matching dirnames, relative to the basedir of the file-set.
     */
    public String[] getExcludedDirectories( FileSet fileSet )
    {
        DirectoryScanner scanner = scan( fileSet );
        
        if ( scanner != null )
        {
            return scanner.getExcludedDirectories();
        }        
        else
        {
            return EMPTY_STRING_ARRAY;
        }
    }
    
    /**
     * Delete the matching files and directories for the given file-set definition.
     * @param fileSet The file-set matching rules, along with search base directory
     * @throws IOException If a matching file cannot be deleted
     */
    public void delete( FileSet fileSet ) throws IOException
    {
        Set deletablePaths = findDeletablePaths( fileSet );
        
        if ( messages != null && messages.isDebugEnabled() )
        {
            messages.addDebugMessage( "Found deletable paths: " + String.valueOf( deletablePaths ).replace( ',', '\n' ) );
        }        
        
        for ( Iterator it = deletablePaths.iterator(); it.hasNext(); )
        {
            String path = (String) it.next();
            
            File file = new File( fileSet.getDirectory(), path );
            
            if ( file.exists() )
            {
                if ( file.isDirectory() && ( fileSet.isFollowSymlinks() || !isSymlink( file ) ) )
                {
                    if ( verbose && messages != null )
                    {
                        messages.addInfoMessage( "Deleting directory: " + file );
                    }
                    
                    removeDir( file, fileSet.isFollowSymlinks() );
                }
                else
                {
                    if ( verbose && messages != null )
                    {
                        messages.addInfoMessage( "Deleting file: " + file );
                    }
                    
                    if ( !delete( file ) )
                    {
                        throw new IOException( "Failed to delete file: " + file + ". Reason is unknown." );
                    }
                }
            }            
        }
    }
    
    private boolean isSymlink( File file ) throws IOException
    {
        File parent = file.getParentFile();
        File canonicalFile = file.getCanonicalFile();
        
        if ( messages != null && messages.isDebugEnabled() )
        {
            messages.addDebugMessage( "Checking for symlink:\nParent file's canonical path: " + parent.getCanonicalPath()
                + "\nMy canonical path: " + canonicalFile.getPath() );
        }        
        return parent != null && ( !canonicalFile.getName().equals( file.getName() ) || !canonicalFile.getPath().startsWith( parent.getCanonicalPath() ) );
    }

    private Set findDeletablePaths( FileSet fileSet )
    {
        Set includes = findDeletableDirectories( fileSet );
        includes.addAll( findDeletableFiles( fileSet, includes ) );
        
        return includes;
    }

    private Set findDeletableDirectories( FileSet fileSet )
    {
        if ( verbose && messages != null )
        {
            messages.addInfoMessage( "Scanning for deletable directories." );
        }
        
        DirectoryScanner scanner = scan( fileSet );
        
        if ( scanner == null )
        {
            return Collections.EMPTY_SET;
        }
        
        String[] includedDirs = scanner.getIncludedDirectories();
        String[] excludedDirs = scanner.getExcludedDirectories();
        
        Set includes = new HashSet( Arrays.asList( includedDirs ) );
        List excludes = new ArrayList( Arrays.asList( excludedDirs ) );
        List linksForDeletion = new ArrayList();
        
        if ( !fileSet.isFollowSymlinks() )
        {
            if ( verbose && messages != null )
            {
                messages.addInfoMessage( "Adding symbolic link dirs which were previously excluded to the list being deleted." );
            }
            
            // we need to see which entries were excluded because they're symlinks...
            scanner.setFollowSymlinks( true );
            scanner.scan();
            
            if ( messages != null && messages.isDebugEnabled() )
            {
                messages.addDebugMessage( "Originally marked for delete: " + includes );
                messages.addDebugMessage( "Marked for preserve (with followSymlinks == false): " + excludes );
            }
            
            List notSymlinks = Arrays.asList( scanner.getIncludedDirectories() );
            
            linksForDeletion.addAll( excludes );
            linksForDeletion.retainAll( notSymlinks );
            
            if ( messages != null && messages.isDebugEnabled() )
            {
                messages.addDebugMessage( "Symlinks marked for deletion (originally mismarked): " + linksForDeletion );
            }
            
            excludes.removeAll( notSymlinks );
        }
        
        for ( int i = 0; i < excludedDirs.length; i++ )
        {
            String path = excludedDirs[i];
            
            File excluded = new File( path );
            
            String parentPath = excluded.getParent();
            
            while( parentPath != null )
            {
                if ( messages != null && messages.isDebugEnabled() )
                {
                    messages.addDebugMessage( "Verifying path: " + parentPath + " is not present; contains file which is excluded." );
                }
                
                boolean removed = includes.remove( parentPath );
                
                if ( removed && messages != null && messages.isDebugEnabled() )
                {
                    messages.addDebugMessage( "Path: " + parentPath + " was removed from delete list." );
                }
                
                parentPath = new File( parentPath ).getParent();
            }
        }
        
        includes.addAll( linksForDeletion );
        
        return includes;
    }

    private Set findDeletableFiles( FileSet fileSet, Set deletableDirectories )
    {
        if ( verbose && messages != null )
        {
            messages.addInfoMessage( "Re-scanning for deletable files." );
        }
        
        DirectoryScanner scanner = scan( fileSet );
        
        if ( scanner == null )
        {
            return deletableDirectories;
        }
        
        String[] includedFiles = scanner.getIncludedFiles();
        String[] excludedFiles = scanner.getExcludedFiles();
        
        Set includes = deletableDirectories;
        includes.addAll( Arrays.asList( includedFiles ) );
        List excludes = new ArrayList( Arrays.asList( excludedFiles ) );
        List linksForDeletion = new ArrayList();
        
        if ( !fileSet.isFollowSymlinks() )
        {
            if ( verbose && messages != null )
            {
                messages.addInfoMessage( "Adding symbolic link files which were previously excluded to the list being deleted." );
            }
            
            // we need to see which entries were excluded because they're symlinks...
            scanner.setFollowSymlinks( true );
            scanner.scan();
            
            if ( messages != null && messages.isDebugEnabled() )
            {
                messages.addDebugMessage( "Originally marked for delete: " + includes );
                messages.addDebugMessage( "Marked for preserve (with followSymlinks == false): " + excludes );
            }
            
            List notSymlinks = Arrays.asList( scanner.getExcludedFiles() );
            
            linksForDeletion.addAll( excludes );
            linksForDeletion.retainAll( notSymlinks );
            
            if ( messages != null && messages.isDebugEnabled() )
            {
                messages.addDebugMessage( "Symlinks marked for deletion (originally mismarked): " + linksForDeletion );
            }
            
            excludes.removeAll( notSymlinks );
        }
        
        for ( int i = 0; i < excludedFiles.length; i++ )
        {
            String path = excludedFiles[i];
            
            File excluded = new File( path );
            
            String parentPath = excluded.getParent();
            
            while( parentPath != null )
            {
                if ( messages != null && messages.isDebugEnabled() )
                {
                    messages.addDebugMessage( "Verifying path: " + parentPath + " is not present; contains file which is excluded." );
                }
                
                boolean removed = includes.remove( parentPath );
                
                if ( removed && messages != null && messages.isDebugEnabled() )
                {
                    messages.addDebugMessage( "Path: " + parentPath + " was removed from delete list." );
                }
                
                parentPath = new File( parentPath ).getParent();
            }
        }
        
        includes.addAll( linksForDeletion );
        
//        for ( Iterator it = includes.iterator(); it.hasNext(); )
//        {
//            String path = (String) it.next();
//            
//            if ( includes.contains( new File( path ).getParent() ) )
//            {
//                it.remove();
//            }
//        }
        
        return includes;
    }

    /**
     * Delete a directory
     *
     * @param dir the directory to delete
     * @param followSymlinks whether to follow symbolic links, or simply delete the link
     */
    private void removeDir( File dir, boolean followSymlinks )
        throws IOException
    {
        String[] list = dir.list();
        if ( list == null )
        {
            list = new String[0];
        }
        for ( int i = 0; i < list.length; i++ )
        {
            String s = list[i];
            File f = new File( dir, s );
            if ( f.isDirectory() && ( followSymlinks || !isSymlink( f ) ) )
            {
                removeDir( f, followSymlinks );
            }
            else
            {
                if ( !delete( f ) )
                {
                    String message = "Unable to delete file " + f.getAbsolutePath();
// TODO:...
//                    if ( failOnError )
//                    {
                        throw new IOException( message );
//                    }
//                    else
//                    {
//                        getLog().info( message );
//                    }
                }
            }
        }

        if ( !delete( dir ) )
        {
            String message = "Unable to delete directory " + dir.getAbsolutePath();
// TODO:...
//            if ( failOnError )
//            {
                throw new IOException( message );
//            }
//            else
//            {
//                getLog().info( message );
//            }
        }
    }
    
    /**
     * Accommodate Windows bug encountered in both Sun and IBM JDKs.
     * Others possible. If the delete does not work, call System.gc(),
     * wait a little and try again.
     */
    private boolean delete( File f )
    {
        if ( !f.delete() )
        {
            if ( System.getProperty( "os.name" ).toLowerCase().indexOf( "windows" ) > -1 )
            {
                System.gc();
            }
            try
            {
                Thread.sleep( DELETE_RETRY_SLEEP_MILLIS );
                return f.delete();
            }
            catch ( InterruptedException ex )
            {
                return f.delete();
            }
        }
        
        return true;
    }

    private DirectoryScanner scan( FileSet fileSet )
    {
        File basedir = new File( fileSet.getDirectory() );
        if ( !basedir.exists() )
        {
            return null;
        }
        
        DirectoryScanner scanner = new DirectoryScanner();
        
        String[] includesArray = fileSet.getIncludesArray();
        String[] excludesArray = fileSet.getExcludesArray();
        
        if ( includesArray.length < 1 && excludesArray.length < 1 )
        {
            scanner.setIncludes( new String[]{ "**" } );
        }
        else
        {
            scanner.setIncludes( includesArray );
            scanner.setExcludes( excludesArray );
        }
        
        scanner.setBasedir( basedir );
        scanner.setFollowSymlinks( fileSet.isFollowSymlinks() );

        scanner.scan();
        
        return scanner;
    }

}
