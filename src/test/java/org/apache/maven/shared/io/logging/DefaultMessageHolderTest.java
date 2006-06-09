package org.apache.maven.shared.io.logging;

import java.io.PrintWriter;
import java.io.StringWriter;

import junit.framework.TestCase;

public class DefaultMessageHolderTest
    extends TestCase
{

    // MessageHolder newMessage();
    // int size();
    // String render();
    public void testNewMessageIncrementsSizeWhenEmpty()
    {
        MessageHolder mh = new DefaultMessageHolder();

        assertEquals( 0, mh.size() );

        MessageHolder test = mh.newMessage();
        
        assertSame( mh, test );

        assertEquals( 1, mh.size() );

        assertEquals( "", mh.render() );
    }

    // MessageHolder append( CharSequence messagePart );
    // int size();
    // String render();
    public void testAppendCreatesNewMessageIfNoneCurrent()
    {
        MessageHolder mh = new DefaultMessageHolder();

        assertEquals( 0, mh.size() );

        MessageHolder test = mh.append( "test" );
        
        assertSame( mh, test );

        assertEquals( 1, mh.size() );

        assertEquals( "[1] test", mh.render() );
    }

    // MessageHolder append( Throwable error );
    // int size();
    // String render();
    public void testAppendErrorCreatesNewMessageIfNoneCurrent()
    {
        MessageHolder mh = new DefaultMessageHolder();

        assertEquals( 0, mh.size() );

        NullPointerException npe = new NullPointerException();
        
        MessageHolder test = mh.append( npe );
        
        assertSame( mh, test );
        
        assertEquals( 1, mh.size() );
        
        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter( sw );
        
        npe.printStackTrace( pw );

        assertEquals( "[1] Error:\n" + sw.toString(), mh.render() );
    }

    // MessageHolder newMessage();
    // MessageHolder append( CharSequence messagePart );
    // int size();
    // String render();
    public void testNewMessageThenAppendOnlyIncrementsSizeByOne()
    {
        MessageHolder mh = new DefaultMessageHolder();

        assertEquals( 0, mh.size() );

        MessageHolder test = mh.newMessage();
        
        assertSame( mh, test );
        
        test = mh.append( "test" );
        
        assertSame( mh, test );

        assertEquals( 1, mh.size() );

        assertEquals( "[1] test", mh.render() );
    }

    // MessageHolder newMessage();
    // MessageHolder append( CharSequence messagePart );
    // MessageHolder append( CharSequence messagePart );
    // int size();
    // String render();
    public void testNewMessageThenAppendTwiceOnlyIncrementsSizeByOne()
    {
        MessageHolder mh = new DefaultMessageHolder();

        assertEquals( 0, mh.size() );

        MessageHolder test = mh.newMessage();
        
        assertSame( mh, test );
        
        test = mh.append( "test" );
        
        assertSame( mh, test );
        
        test = mh.append( " again" );
        
        assertSame( mh, test );

        assertEquals( 1, mh.size() );

        assertEquals( "[1] test again", mh.render() );
    }

    // MessageHolder newMessage();
    // MessageHolder append( CharSequence messagePart );
    // MessageHolder append( Throwable error );
    // int size();
    // String render();
    public void testNewMessageThenAppendBothMessageAndErrorOnlyIncrementsSizeByOne()
    {
        MessageHolder mh = new DefaultMessageHolder();

        assertEquals( 0, mh.size() );

        MessageHolder test = mh.newMessage();
        
        assertSame( mh, test );
        
        test = mh.append( "test" );
        
        assertSame( mh, test );
        
        NullPointerException npe = new NullPointerException();
        
        test = mh.append( npe );
        
        assertSame( mh, test );

        assertEquals( 1, mh.size() );
        
        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter( sw );
        
        npe.printStackTrace( pw );

        assertEquals( "[1] test\nError:\n" + sw.toString(), mh.render() );
    }
    
    // MessageHolder addMessage( CharSequence messagePart );
    // int size();
    // String render();
    public void testAddMessageIncrementsSizeByOne()
    {
        MessageHolder mh = new DefaultMessageHolder();
        MessageHolder check = mh.addMessage( "test" );
        
        assertSame( mh, check );
        
        assertEquals( 1, mh.size() );
        assertEquals( "[1] test", mh.render() );
    }
    
    // MessageHolder addMessage( CharSequence messagePart );
    // int size();
    // String render();
    public void testAddMessageTwiceIncrementsSizeByTwo()
    {
        MessageHolder mh = new DefaultMessageHolder();
        MessageHolder check = mh.addMessage( "test" );
        
        assertSame( mh, check );
        
        check = mh.addMessage( "test2" );
        
        assertSame( mh, check );
        
        assertEquals( 2, mh.size() );
        assertEquals( "[1] test\n\n[2] test2", mh.render() );
    }
    
    // MessageHolder addMessage( CharSequence messagePart, Throwable error );
    // int size();
    // String render();
    public void testAddMessageWithErrorIncrementsSizeByOne()
    {
        MessageHolder mh = new DefaultMessageHolder();
        
        NullPointerException npe = new NullPointerException();
        
        MessageHolder check = mh.addMessage( "test", npe );
        
        assertSame( mh, check );
        
        assertEquals( 1, mh.size() );
        
        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter( sw );
        
        npe.printStackTrace( pw );

        assertEquals( "[1] test\nError:\n" + sw.toString(), mh.render() );
    }
    
    // MessageHolder addMessage( CharSequence messagePart, Throwable error );
    // MessageHolder addMessage( CharSequence messagePart );
    // int size();
    // String render();
    public void testAddMessageWithErrorThenWithJustMessageIncrementsSizeByTwo()
    {
        MessageHolder mh = new DefaultMessageHolder();
        
        NullPointerException npe = new NullPointerException();
        
        MessageHolder check = mh.addMessage( "test", npe );
        
        assertSame( mh, check );
        
        check = mh.addMessage( "test2" );
        
        assertSame( mh, check );
        
        assertEquals( 2, mh.size() );
        
        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter( sw );
        
        npe.printStackTrace( pw );

        assertEquals( "[1] test\nError:\n" + sw.toString() + "\n\n[2] test2", mh.render() );
    }
    
    // MessageHolder addMessage( Throwable error );
    // int size();
    // String render();
    public void testAddMessageWithJustErrorIncrementsSizeByOne()
    {
        MessageHolder mh = new DefaultMessageHolder();
        
        NullPointerException npe = new NullPointerException();
        
        MessageHolder check = mh.addMessage( npe );
        
        assertSame( mh, check );
        
        assertEquals( 1, mh.size() );
        
        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter( sw );
        
        npe.printStackTrace( pw );

        assertEquals( "[1] Error:\n" + sw.toString(), mh.render() );
    }
    
    // boolean isEmpty();
    public void testIsEmptyAfterConstruction()
    {
        assertTrue( new DefaultMessageHolder().isEmpty() );
    }

    // boolean isEmpty();
    public void testIsNotEmptyAfterConstructionAndNewMessageCall()
    {
        assertFalse( new DefaultMessageHolder().newMessage().isEmpty() );
    }
    
    public void testAppendCharSequence()
    {
        MessageHolder mh = new DefaultMessageHolder();
        mh.newMessage().append( new StringBuffer( "This is a test" ) );
        
        assertTrue( mh.render().indexOf( "This is a test" ) > -1 );
    }
    
}
