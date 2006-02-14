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

import org.apache.maven.shared.model.fileset.FileSet;
import org.codehaus.plexus.util.DirectoryScanner;
import org.codehaus.plexus.util.FileUtils;

public final class FileSetUtils
{
    
    private static final int DELETE_RETRY_SLEEP_MILLIS = 10;
    private static final String[] EMPTY_STRING_ARRAY = new String[0];

    private FileSetUtils()
    {
        // deny construction.
    }
    
    public static String[] getIncludedFiles( FileSet fileSet )
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

    public static String[] getIncludedDirectories( FileSet fileSet )
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

    public static String[] getExcludedFiles( FileSet fileSet )
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

    public static String[] getExcludedDirectories( FileSet fileSet )
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
    
    public static void delete( FileSet fileSet ) throws IOException
    {
        Set deletablePaths = findDeletablePaths( fileSet );
        
        for ( Iterator it = deletablePaths.iterator(); it.hasNext(); )
        {
            String path = (String) it.next();
            
            File file = new File( fileSet.getDirectory(), path );
            
            if ( file.exists() )
            {
                if ( file.isDirectory() && ( fileSet.isFollowSymlinks() || !isSymlink( file ) ) )
                {
                    FileUtils.deleteDirectory( file );
                }
                else
                {
                    if ( !delete( file ) )
                    {
                        throw new IOException( "Failed to delete file: " + file + ". Reason is unknown." );
                    }
                }
            }            
        }
    }
    
    private static boolean isSymlink( File file ) throws IOException
    {
        File parent = file.getParentFile();
        File canonicalFile = file.getCanonicalFile();
        
        return parent != null && ( !canonicalFile.getName().equals( file.getName() ) || !canonicalFile.getPath().startsWith( parent.getCanonicalPath() ) );
    }

    private static Set findDeletablePaths( FileSet fileSet )
    {
        Set includes = findDeletableDirectories( fileSet );
        includes.addAll( findDeletableFiles( fileSet, includes ) );
        
        return includes;
    }

    private static Set findDeletableDirectories( FileSet fileSet )
    {
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
            // we need to see which entries were excluded because they're symlinks...
            scanner.setFollowSymlinks( true );
            scanner.scan();
            
            List notSymlinks = Arrays.asList( scanner.getIncludedDirectories() );
            
            linksForDeletion.addAll( excludes );
            linksForDeletion.retainAll( notSymlinks );
            
            excludes.removeAll( notSymlinks );
        }
        
        for ( int i = 0; i < excludedDirs.length; i++ )
        {
            String path = excludedDirs[i];
            
            File excluded = new File( path );
            
            String parentPath = excluded.getParent();
            
            while( parentPath != null )
            {
                includes.remove( parentPath );
                
                parentPath = new File( parentPath ).getParent();
            }
        }
        
        includes.addAll( linksForDeletion );
        
        return includes;
    }

    private static Set findDeletableFiles( FileSet fileSet, Set deletableDirectories )
    {
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
            // we need to see which entries were excluded because they're symlinks...
            scanner.setFollowSymlinks( true );
            scanner.scan();
            
            List notSymlinks = Arrays.asList( scanner.getExcludedFiles() );
            
            linksForDeletion.addAll( excludes );
            linksForDeletion.retainAll( notSymlinks );
            
            excludes.removeAll( notSymlinks );
        }
        
        for ( int i = 0; i < excludedFiles.length; i++ )
        {
            String path = excludedFiles[i];
            
            File excluded = new File( path );
            
            String parentPath = excluded.getParent();
            
            while( parentPath != null )
            {
                includes.remove( parentPath );
                
                parentPath = new File( parentPath ).getParent();
            }
        }
        
        includes.addAll( linksForDeletion );
        
        for ( Iterator it = includes.iterator(); it.hasNext(); )
        {
            String path = (String) it.next();
            
            if ( includes.contains( new File( path ).getParent() ) )
            {
                it.remove();
            }
        }
        
        return includes;
    }

    /**
     * Accommodate Windows bug encountered in both Sun and IBM JDKs.
     * Others possible. If the delete does not work, call System.gc(),
     * wait a little and try again.
     */
    private static boolean delete( File f )
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

    private static DirectoryScanner scan( FileSet fileSet )
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
