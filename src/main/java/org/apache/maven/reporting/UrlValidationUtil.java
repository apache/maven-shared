package org.apache.maven.reporting;

/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

import org.apache.commons.validator.routines.RegexValidator;
import org.apache.commons.validator.routines.UrlValidator;

/**
 * Static utility class intended to help {@link AbstractMavenReportRenderer} in validating URLs. Validation uses two
 * UrlValidator instances. The first validates public URLs, the second validates local URLs. At least one validator has
 * to accept the given URL. A URL is called local if it uses an unqualified hostname (such as "localhost" or
 * "workstation-12") or qualified domain names within the special use top level domain ".local".
 *
 * @author <a href="mailto:jan.schultze@gmail.com">Jan Schultze</a>
 */
final class UrlValidationUtil
{

    private static final String LETTERS_DIGITS = "[a-zA-Z0-9]";

    private static final String LETTERS_DIGITS_HYPHEN = "[a-zA-Z0-9\\-]";

    private static final String LABEL = LETTERS_DIGITS + "(" + LETTERS_DIGITS_HYPHEN + "{0,61}" + LETTERS_DIGITS + ")?";

    private static final String OPTIONAL_PORT = "(:(([1-5]\\d{1,4})|([1-9]\\d{1,3})))?";

    private static final String AUTHORITY_REGEX = LABEL + "(\\." + LABEL + ")*\\.local\\.?" + OPTIONAL_PORT;

    private static final String[] SCHEMES = { "http", "https" };

    private UrlValidationUtil()
    {
        throw new RuntimeException( "Instantiation of " + UrlValidationUtil.class.getName() + " is not allowed." );
    }

    static boolean isValidUrl( final String url )
    {
        return isValidPublicUrl( url ) || isValidLocalUrl( url );
    }

    private static boolean isValidPublicUrl( final String url )
    {
        UrlValidator validator = configurePublicUrlValidator();
        return validator.isValid( url );
    }

    private static UrlValidator configurePublicUrlValidator()
    {
        return new UrlValidator( SCHEMES );
    }

    private static boolean isValidLocalUrl( final String url )
    {
        UrlValidator validator = configureLocalUrlValidator();
        return validator.isValid( url );
    }

    private static UrlValidator configureLocalUrlValidator()
    {
        RegexValidator authorityValidator = configureLocalAuthorityValidator();
        return new UrlValidator( SCHEMES, authorityValidator, UrlValidator.ALLOW_LOCAL_URLS );
    }

    /* package-private for testing purposes */
    static RegexValidator configureLocalAuthorityValidator()
    {
        return new RegexValidator( AUTHORITY_REGEX, false );
    }

}
