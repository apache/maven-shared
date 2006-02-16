package org.apache.maven.shared.model.fileset.util;

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
import org.apache.maven.shared.model.fileset.FileSet;
import org.codehaus.plexus.util.DirectoryScanner;

public class FileSetManager
{
    
    private static final int DELETE_RETRY_SLEEP_MILLIS = 10;
    private static final String[] EMPTY_STRING_ARRAY = new String[0];
    
    private final boolean verbose;
    private final Log log;

    public FileSetManager( Log log, boolean verbose )
    {
        this.log = log;
        this.verbose = verbose;
    }
    
    public FileSetManager( Log log )
    {
        this.log = log;
        this.verbose = false;
    }
    
    public FileSetManager()
    {
        this.log = null;
        this.verbose = false;
    }
    
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
    
    public void delete( FileSet fileSet ) throws IOException
    {
        Set deletablePaths = findDeletablePaths( fileSet );
        
        if ( log != null && log.isDebugEnabled() )
        {
            log.debug( "Found deletable paths: " + String.valueOf( deletablePaths ).replace( ',', '\n' ) );
        }        
        
        for ( Iterator it = deletablePaths.iterator(); it.hasNext(); )
        {
            String path = (String) it.next();
            
            File file = new File( fileSet.getDirectory(), path );
            
            if ( file.exists() )
            {
                if ( file.isDirectory() && ( fileSet.isFollowSymlinks() || !isSymlink( file ) ) )
                {
                    if ( verbose && log != null )
                    {
                        log.info( "Deleting directory: " + file );
                    }
                    
                    removeDir( file, fileSet.isFollowSymlinks() );
                }
                else
                {
                    if ( verbose && log != null )
                    {
                        log.info( "Deleting file: " + file );
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
        
        if ( log != null && log.isDebugEnabled() )
        {
            log.debug( "Checking for symlink:\nParent file's canonical path: " + parent.getCanonicalPath()
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
        if ( verbose && log != null )
        {
            log.info( "Scanning for deletable directories." );
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
            if ( verbose && log != null )
            {
                log.info( "Adding symbolic link dirs which were previously excluded to the list being deleted." );
            }
            
            // we need to see which entries were excluded because they're symlinks...
            scanner.setFollowSymlinks( true );
            scanner.scan();
            
            if ( log != null && log.isDebugEnabled() )
            {
                log.debug( "Originally marked for delete: " + includes );
                log.debug( "Marked for preserve (with followSymlinks == false): " + excludes );
            }
            
            List notSymlinks = Arrays.asList( scanner.getIncludedDirectories() );
            
            linksForDeletion.addAll( excludes );
            linksForDeletion.retainAll( notSymlinks );
            
            if ( log != null && log.isDebugEnabled() )
            {
                log.debug( "Symlinks marked for deletion (originally mismarked): " + linksForDeletion );
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
                if ( log != null && log.isDebugEnabled() )
                {
                    log.debug( "Verifying path: " + parentPath + " is not present; contains file which is excluded." );
                }
                
                boolean removed = includes.remove( parentPath );
                
                if ( removed && log != null && log.isDebugEnabled() )
                {
                    log.debug( "Path: " + parentPath + " was removed from delete list." );
                }
                
                parentPath = new File( parentPath ).getParent();
            }
        }
        
        includes.addAll( linksForDeletion );
        
        return includes;
    }

    private Set findDeletableFiles( FileSet fileSet, Set deletableDirectories )
    {
        if ( verbose && log != null )
        {
            log.info( "Re-scanning for deletable files." );
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
            if ( verbose && log != null )
            {
                log.info( "Adding symbolic link files which were previously excluded to the list being deleted." );
            }
            
            // we need to see which entries were excluded because they're symlinks...
            scanner.setFollowSymlinks( true );
            scanner.scan();
            
            if ( log != null && log.isDebugEnabled() )
            {
                log.debug( "Originally marked for delete: " + includes );
                log.debug( "Marked for preserve (with followSymlinks == false): " + excludes );
            }
            
            List notSymlinks = Arrays.asList( scanner.getExcludedFiles() );
            
            linksForDeletion.addAll( excludes );
            linksForDeletion.retainAll( notSymlinks );
            
            if ( log != null && log.isDebugEnabled() )
            {
                log.debug( "Symlinks marked for deletion (originally mismarked): " + linksForDeletion );
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
                if ( log != null && log.isDebugEnabled() )
                {
                    log.debug( "Verifying path: " + parentPath + " is not present; contains file which is excluded." );
                }
                
                boolean removed = includes.remove( parentPath );
                
                if ( removed && log != null && log.isDebugEnabled() )
                {
                    log.debug( "Path: " + parentPath + " was removed from delete list." );
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
    protected void removeDir( File dir, boolean followSymlinks )
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
