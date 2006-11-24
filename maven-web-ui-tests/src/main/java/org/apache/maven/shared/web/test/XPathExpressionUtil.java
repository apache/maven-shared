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

/**
 * Utility class for creating xpath expressions
 */
public class XPathExpressionUtil
{
    public static final String CONTAINS = "contains";

    public static final String AND = " and ";

    public static final String CURRENT_NODE = "./";

    public static final String PARENT_NODE = "../";

    public static final String GRANDPARENT_NODE = "../../";

    public static final String ELEMENT_ANY_LEVEL = "//";

    public static final String TABLE_COLUMN = "td";

    public static final String TABLE_ROW = "tr";

    public static final String START_NODE_TEST = "[";

    public static final String END_NODE_TEST = "]";

    public static final String ANCHOR = "a";

    /**
     * expression for acquiring an element in one of the table columns
     *
     * @param element      the node element
     * @param elementIndex column index of the element, used for skipping
     * @param columnValues the values to be matched in each column, element column is included
     * @return
     */
    public static String getColumnElement( String element, int elementIndex, String[] columnValues )
    {
        return getColumnElement( element, elementIndex, null, columnValues );
    }

    /**
     * expression for acquiring an element in one of the table columns
     *
     * @param element      the node element
     * @param elementIndex column index of the element, used for skipping
     * @param elementValue the matched element value
     * @param columnValues the values to be matched in each column, element column is included
     * @return
     */
    public static String getColumnElement( String element, int elementIndex, String elementValue,
                                                               String[] columnValues )
    {
        String xpathExpression = null;

        if ( ( columnValues != null ) && ( columnValues.length > 0 ) )
        {
            xpathExpression = ELEMENT_ANY_LEVEL + element;
            xpathExpression += START_NODE_TEST;

            if ( elementValue != null )
            {
                xpathExpression += contains( elementValue );
                xpathExpression += ( columnValues.length > 0 ) ? AND : "";
            }

            // we are two levels below the table row element ( tr/td/<element> )
            xpathExpression += matchColumns( GRANDPARENT_NODE, columnValues, elementIndex );

            xpathExpression += END_NODE_TEST;
        }

        return xpathExpression;
    }

    /**
     * expression for acquiring the table row that matches all column values with the same order
     * as the list
     *
     * @param columnValues the matched list of columnValues
     * @return
     */
    public static String getTableRow( String[] columnValues )
    {
        String xpathExpression = null;

        if ( ( columnValues != null ) && ( columnValues.length > 0 ) )
        {
            xpathExpression = new String( ELEMENT_ANY_LEVEL + TABLE_ROW + START_NODE_TEST );
            xpathExpression += matchColumns( columnValues );
            xpathExpression += END_NODE_TEST;
        }

        return xpathExpression;
    }

    private static String matchColumns( String[] columnValues )
    {
        return matchColumns( columnValues, -1 );
    }

    private static String matchColumns( String[] columnValues, int skipIndex )
    {
        return matchColumns( null, columnValues, skipIndex );
    }

    private static String matchColumns( String parent, String[] columnValues, int skipIndex )
    {
        String xpathExpression = "";

        for ( int nIndex = 0; nIndex < columnValues.length; nIndex++ )
        {
            if ( ( skipIndex != nIndex ) || ( skipIndex == -1 ) )
            {
                // prepend "and" if index > 0
                xpathExpression += ( ( nIndex > 0 ) ? AND : "" );
                xpathExpression += contains( parent, columnPosition( nIndex + 1 ), columnValues[nIndex] );
            }
        }

        return xpathExpression;
    }

    private static String columnPosition( int nIndex )
    {
        return new String( TABLE_COLUMN + "[" + nIndex + "]" );
    }

    private static String contains( String parent, String element, String matchedString )
    {
        String finalElement = ( parent != null ) ? parent : "";
        finalElement += element;

        return contains( finalElement, matchedString );
    }

    private static String contains( String matchedString )
    {
        return contains( ".", matchedString );
    }

    private static String contains( String axis, String matchedString )
    {
        return new String( CONTAINS + "(" + axis + "," + "'" + matchedString + "')" );
    }
}
