package org.apache.maven.shared.model.fileset.util;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import junit.framework.Assert;
import junit.framework.TestCase;

import org.apache.maven.shared.model.fileset.FileSet;
import org.codehaus.plexus.util.FileUtils;
import org.codehaus.plexus.util.cli.CommandLineException;
import org.codehaus.plexus.util.cli.Commandline;

public class FileSetUtilsTest
    extends TestCase
{
    private static final List LINK_ENABLED_OSES = Arrays.asList( new String[]{ "unix", "osx", "bsd" } );
    
    private Set testDirectories = new HashSet();
    private Set linkFiles = new HashSet();
    
    public void tearDown() throws IOException
    {
        for ( Iterator it = linkFiles.iterator(); it.hasNext(); )
        {
            File linkFile = (File) it.next();
            
            linkFile.delete();
        }
        
        for ( Iterator it = testDirectories.iterator(); it.hasNext(); )
        {
            File dir = (File) it.next();
            
            FileUtils.deleteDirectory( dir );
        }
    }
    
    public void testGetIncludedFiles() throws IOException
    {
        File directory = setupTestDirectory( "testGetIncludedFiles" );
        
        FileSet set = new FileSet();
        set.setDirectory( directory.getPath() );
        set.addInclude( "**/included.txt" );
        
        String[] included = FileSetUtils.getIncludedFiles( set );
        
        Assert.assertEquals( 1, included.length );
    }
    
    public void testIncludesDontFollowSymlinks() throws IOException, InterruptedException, CommandLineException
    {
        if ( operatingSystemAllowsLinking() )
        {
            File directory = setupTestDirectory( "testIncludesDontFollowSymlinks" );
            File subdir = new File( directory, "linked-to-self" );
            
            if ( !createSymlink( directory, subdir ) )
            {
                fail( "Cannot create symlink." );
            }
            
            FileSet set = new FileSet();
            set.setDirectory( directory.getPath() );
            set.addInclude( "**/included.txt" );
            set.setFollowSymlinks( false );
            
            String[] included = FileSetUtils.getIncludedFiles( set );
            
            Assert.assertEquals( 1, included.length );
        }        
    }
    
    public void testDeleteDontFollowSymlinks() throws IOException, InterruptedException, CommandLineException
    {
        if ( operatingSystemAllowsLinking() )
        {
            File directory = setupTestDirectory( "testDeleteDontFollowSymlinks" );
            File subdir = new File( directory, "linked-to-self" );
            
            if ( !createSymlink( directory, subdir ) )
            {
                fail( "Cannot create symlink." );
            }
            
            FileSet set = new FileSet();
            set.setDirectory( directory.getPath() );
            set.addInclude( "**/included.txt" );
            set.addInclude( "**/linked-to-self" );
            set.setFollowSymlinks( false );
            
            FileSetUtils.delete( set );
            
            Assert.assertFalse( subdir.exists() );
        }        
    }
    
    public void testDelete() throws IOException
    {
        File directory = setupTestDirectory( "testDelete" );
        File subdirFile = new File( directory, "subdir/excluded.txt" );
        
        FileSet set = new FileSet();
        set.setDirectory( directory.getPath() );
        set.addInclude( "**/included.txt" );
        set.addInclude( "**/subdir" );
        
        FileSetUtils.delete( set );
        
        Assert.assertFalse( "file in marked subdirectory still exists.", subdirFile.exists() );
    }
    
    private boolean operatingSystemAllowsLinking()
    {
        String osFamily = System.getProperty( "os.family", "unix" );
        
        return LINK_ENABLED_OSES.contains( osFamily );
    }

    private boolean createSymlink( File from, File to ) throws InterruptedException, CommandLineException
    {
        if ( to.exists() )
        {
            to.delete();
        }
        
        Commandline cli = new Commandline();
        cli.setExecutable( "ln" );
        cli.createArgument().setLine( "-s" );
        cli.createArgument().setLine( from.getPath() );
        cli.createArgument().setLine( to.getPath() );
        
        int result = cli.execute().waitFor();
        
        linkFiles.add( to );

        return result == 0;
    }
    
    private File setupTestDirectory( String directoryName ) throws IOException
    {
        System.out.println( "Setting up directory for test: " + directoryName );
        
        URL sourceResource = getClass().getClassLoader().getResource( directoryName );
        
        if ( sourceResource == null )
        {
            Assert.fail( "Source directory for test: " + directoryName + " cannot be found." );
        }
        
        File sourceDir = new File( sourceResource.getPath() );
        
        String basedir = System.getProperty( "basedir", System.getProperty( "user.dir" ) );
        String testBase = System.getProperty( "testBase", "target/test-directories" );
        
        File testDir = new File( basedir, testBase + "/" + directoryName );
        testDir.mkdirs();
        
        FileUtils.copyDirectoryStructure( sourceDir, testDir );
        
        testDirectories.add( testDir );
        
        return testDir;
    }

}
