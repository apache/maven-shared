package org.apache.maven.shared.web.test;

/*
 * Copyright 2005-2006 The Apache Software Foundation.
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

import com.thoughtworks.selenium.DefaultSelenium;
import com.thoughtworks.selenium.Selenium;
import junit.framework.TestCase;
import org.codehaus.plexus.util.StringUtils;
import org.openqa.selenium.server.SeleniumServer;

import java.io.File;
import java.util.Calendar;
import java.util.Iterator;
import java.util.Map;

/**
 * @author <a href="mailto:evenisse@apache.org">Emmanuel Venisse</a>
 * @version $Id$
 */
public abstract class AbstractSeleniumTestCase
    extends TestCase
{
    final public static String CHECKBOX_CHECK = "on";

    final public static String CHECKBOX_UNCHECK = "off";

    private Selenium sel;

    public void setUp()
        throws Exception
    {
        super.setUp();
        String browser = System.getProperty( "browser" );
        if ( StringUtils.isEmpty( browser ) )
        {
            browser = "*firefox";
        }

        sel = new DefaultSelenium( "localhost", SeleniumServer.DEFAULT_PORT, browser, getBaseUrl() );
        sel.start();
        initialize();
    }

    public void tearDown()
    {
        sel.stop();
    }

    public Selenium getSelenium()
    {
        return sel;
    }

    public abstract String getBaseUrl();

    protected abstract void initialize();

    protected abstract String getApplicationName();

    protected abstract String getInceptionYear();

    public void open( String url )
    {
        sel.open( url );
    }

    public String getTitle()
    {
        return sel.getTitle();
    }

    public String getHtmlContent()
    {
        return getSelenium().getHtmlSource();
    }

    public void assertTextPresent( String text )
    {
        assertTrue( "'" + text + "' isn't present.", sel.isTextPresent( text ) );
    }

    public void assertTextNotPresent( String text )
    {
        assertFalse( "'" + text + "' is present.", sel.isTextPresent( text ) );
    }

    public void assertElementPresent( String elementLocator )
    {
        assertTrue( "'" + elementLocator + "' isn't present.", isElementPresent( elementLocator ) );
    }

    public void assertElementNotPresent( String elementLocator )
    {
        assertFalse( "'" + elementLocator + "' is present.", isElementPresent( elementLocator ) );
    }

    public void assertLinkPresent( String text )
    {
        assertTrue( "The link '" + text + "' isn't present.", isElementPresent( "link=" + text ) );
    }

    public void assertLinkNotPresent( String text )
    {
        assertFalse( "The link '" + text + "' is present.", isElementPresent( "link=" + text ) );
    }

    public void assertImgWithAlt( String alt )
    {
        assertElementPresent( "//img[@alt='" + alt + "']" );
    }

    public void assertImgWithAltAtRowCol( boolean isALink, String alt, int row, int column )
    {
        String locator = "//tr[" + row + "]/td[" + column + "]/";
        locator += isALink ? "a/" : "";
        locator += "img[@alt='" + alt + "']";

        assertElementPresent( locator );
    }

    public void assertCellValueFromTable( String expected, String tableElement, int row, int column )
    {
        assertEquals( expected, getCellValueFromTable( tableElement, row, column ) );
    }

    public boolean isTextPresent( String text )
    {
        return sel.isTextPresent( text );
    }

    public boolean isLinkPresent( String text )
    {
        return isElementPresent( "link=" + text );
    }

    public boolean isElementPresent( String locator )
    {
        return sel.isElementPresent( locator );
    }

    public void waitPage()
    {
        waitPage( 30000 );
    }

    public void waitPage( int nbMillisecond )
    {
        sel.waitForPageToLoad( String.valueOf( nbMillisecond ) );
    }

    public void assertPage( String title )
    {
        assertEquals( title, getTitle() );
        assertHeader();
        assertFooter();
    }

    public abstract void assertHeader();

    public void assertFooter()
    {
        assertTrue(
            sel.getText( "xpath=//div[@id='footer']/table/tbody/tr/td" ).startsWith( getApplicationName() + " " ) );
        int currentYear = Calendar.getInstance().get( Calendar.YEAR );
        assertTrue( sel.getText( "xpath=//div[@id='footer']/table/tbody/tr/td" ).endsWith(
            " " + getInceptionYear() + "-" + currentYear + " Apache Software Foundation" ) );
    }

    public String getFieldValue( String fieldName )
    {
        return sel.getValue( fieldName );
    }

    public String getCellValueFromTable( String tableElement, int row, int column )
    {
        return getSelenium().getTable( tableElement + "." + row + "." + column );
    }

    public void selectValue( String locator, String value )
    {
        getSelenium().select( locator, "label=" + value );
    }

    public void submit()
    {
        clickLinkWithXPath( "//input[@type='submit']" );
    }

    public void assertButtonWithValuePresent( String text )
    {
        assertTrue( "'" + text + "' button isn't present", isButtonWithValuePresent( text ) );
    }

    public void assertButtonWithValueNotPresent( String text )
    {
        assertFalse( "'" + text + "' button is present", isButtonWithValuePresent( text ) );
    }

    public boolean isButtonWithValuePresent( String text )
    {
        return isElementPresent( "//button[@value='" + text + "']" ) || isElementPresent( "//input[@value='" + text + "']" );
    }

    public void clickButtonWithValue( String text )
    {
        clickButtonWithValue( text, true );
    }

    public void clickButtonWithValue( String text, boolean wait )
    {
        assertButtonWithValuePresent( text );

        if ( isElementPresent( "//button[@value='" + text + "']" ) )
        {
            clickLinkWithXPath( "//button[@value='" + text + "']", wait );
        }
        else
        {
            clickLinkWithXPath( "//input[@value='" + text + "']", wait );
        }
    }

    public void clickSubmitWithLocator( String locator )
    {
        clickLinkWithLocator( locator );
    }

    public void clickSubmitWithLocator( String locator, boolean wait )
    {
        clickLinkWithLocator( locator, wait );
    }

    public void clickImgWithAlt( String alt )
    {
        clickLinkWithLocator( "//img[@alt='" + alt + "']" );
    }

    public void clickLinkWithText( String text )
    {
        clickLinkWithText( text, true );
    }

    public void clickLinkWithText( String text, boolean wait )
    {
        clickLinkWithLocator( "link=" + text, wait );
    }

    public void clickLinkWithXPath( String xpath )
    {
        clickLinkWithXPath( xpath, true );
    }

    public void clickLinkWithXPath( String xpath, boolean wait )
    {
        clickLinkWithLocator( "xpath=" + xpath, wait );
    }

    public void clickLinkWithLocator( String locator )
    {
        clickLinkWithLocator( locator, true );
    }

    public void clickLinkWithLocator( String locator, boolean wait )
    {
        assertElementPresent( locator );
        sel.click( locator );
        if ( wait )
        {
            waitPage();
        }
    }

    public void setFieldValues( Map fieldMap )
    {
        Map.Entry entry;

        for ( Iterator entries = fieldMap.entrySet().iterator(); entries.hasNext(); )
        {
            entry = (Map.Entry) entries.next();

            sel.type( (String) entry.getKey(), (String) entry.getValue() );
        }
    }

    public void setFieldValue( String fieldName, String value )
    {
        sel.type( fieldName, value );
    }

    public void checkField( String locator )
    {
        sel.check( locator );
    }

    public void uncheckField( String locator )
    {
        sel.uncheck( locator );
    }

    public boolean isChecked( String locator )
    {
        return sel.isChecked( locator );
    }

    //////////////////////////////////////
    // Login
    //////////////////////////////////////
    public void goToLoginPage()
    {
        clickLinkWithText( "Login" );

        assertLoginPage();
    }

    public void login( String username, String password )
    {
        login( username, password, true, "Login Page" );
    }

    public void login( String username, String password, boolean valid, String assertReturnPage )
    {
        goToLoginPage();

        submitLoginPage( username, password, false, valid, assertReturnPage );
    }

    public void assertLoginPage()
    {
        assertPage( "Login Page" );
        assertTextPresent( "Login" );
        assertTextPresent( "Username" );
        assertTextPresent( "Password" );
        assertTextPresent( "Remember Me" );
        assertFalse( isChecked( "rememberMe" ) );
    }

    public void submitLoginPage( String username, String password )
    {
        submitLoginPage( username, password, false, true, "Login Page" );
    }

    public void submitLoginPage( String username, String password, boolean validUsernamePassword )
    {
        submitLoginPage( username, password, false, validUsernamePassword, "Login Page" );
    }

    public void submitLoginPage( String username, String password, boolean rememberMe, boolean validUsernamePassword,
                                 String assertReturnPage )
    {
        assertLoginPage();
        setFieldValue( "username", username );
        setFieldValue( "password", password );
        if ( rememberMe )
        {
            checkField( "rememberMe" );
        }
        clickButtonWithValue( "Login" );

        if ( validUsernamePassword )
        {
            assertTextPresent( "Current User:" );
            assertTextPresent( username );
            assertLinkPresent( "Edit Details" );
            assertLinkPresent( "Logout" );
        }
        else
        {
            if ( "Login Page".equals( assertReturnPage ) )
            {
                assertLoginPage();
            }
            else
            {
                assertPage( assertReturnPage );
            }
        }
    }

    public boolean isAuthenticated()
    {
        return !( isLinkPresent( "Login" ) && isLinkPresent( "Register" ) );
    }

    //////////////////////////////////////
    // Logout
    //////////////////////////////////////
    public void logout()
    {
        assertTrue( "User wasn't authenticated.", isAuthenticated() );
        clickLinkWithText( "Logout" );
        assertFalse( "The user is always authenticated after a logout.", isAuthenticated() );
    }

    //////////////////////////////////////
    // My Account
    //////////////////////////////////////
    public void goToMyAccount()
    {
        clickLinkWithText( "Edit Details" );
    }

    public void assertMyAccountDetails( String username, String newFullName, String newEmailAddress )
        throws Exception
    {
        assertPage( "Account Details" );

        isTextPresent( "Username" );
        assertTextPresent( "Username" );
        assertElementPresent( "registerForm_user_username" );
        assertCellValueFromTable( username, "//form/table", 0, 1 );

        assertTextPresent( "Full Name" );
        assertElementPresent( "user.fullName" );
        assertEquals( newFullName, getFieldValue( "user.fullName" ) );

        assertTextPresent( "Email Address" );
        assertElementPresent( "user.email" );
        assertEquals( newEmailAddress, getFieldValue( "user.email" ) );

        assertTextPresent( "Password" );
        assertElementPresent( "user.password" );

        assertTextPresent( "Confirm Password" );
        assertElementPresent( "user.confirmPassword" );

        assertTextPresent( "Last Password Change" );
        assertElementPresent( "registerForm_user_timestampLastPasswordChange" );

    }

    public void editMyUserInfo( String newFullName, String newEmailAddress, String newPassword,
                                String confirmNewPassword )
    {
        goToMyAccount();

        setFieldValue( "user.fullName", newFullName );
        setFieldValue( "user.email", newEmailAddress );
        setFieldValue( "user.password", newPassword );
        setFieldValue( "user.confirmPassword", confirmNewPassword );
    }

    //////////////////////////////////////
    // Users
    //////////////////////////////////////
    public void assertUsersListPage()
    {
        assertPage( "[Admin] User List" );
    }

    public void assertCreateUserPage()
    {
        assertPage( "[Admin] User Create" );
        assertTextPresent( "Username" );
        assertTextPresent( "Full Name" );
        assertTextPresent( "Email Address" );
        assertTextPresent( "Password" );
        assertTextPresent( "Confirm Password" );
    }

    public void assertDeleteUserPage( String username )
    {
        assertPage( "[Admin] User Delete" );
        assertTextPresent( "[Admin] User Delete" );
        assertTextPresent( "The following user will be deleted: " + username );
        assertButtonWithValuePresent( "Delete User" );
    }

    public String getBasedir()
    {
        String basedir = System.getProperty( "basedir" );

        if ( basedir == null )
        {
            basedir = new File( "" ).getAbsolutePath();
        }

        return basedir;
    }
}
