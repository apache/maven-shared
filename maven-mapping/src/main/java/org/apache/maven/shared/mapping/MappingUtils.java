package org.apache.maven.shared.mapping;

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

import org.apache.maven.artifact.Artifact;
import org.codehaus.plexus.interpolation.InterpolationException;
import org.codehaus.plexus.interpolation.ObjectBasedValueSource;
import org.codehaus.plexus.interpolation.RegexBasedInterpolator;

/**
 * <p>
 * Utilities used to evaluate expression.
 * </p>
 * <p>
 * The expression might use any field of the {@link Artifact} interface. Some
 * examples might be:
 * </p>
 * <ul>
 * <li>@{artifactId}@-@{version}@@{dashClassifier?}@.@{extension}@</li>
 * <li>@{artifactId}@-@{version}@.@{extension}@</li>
 * <li>@{artifactId}@.@{extension}@</li>
 * </ul>
 * <p>
 * Although parts of this code comes from the Assembly Plugin, it cannot be
 * shared with the Assembly Plugin. The reason for this is that the Assembly
 * Plugin always uses a prefix for the expressions, whereas this code does not.
 * <p/>
 *
 * @author Stephane Nicoll
 * @author Dennis Lundberg
 * @version $Id$
 */
public class MappingUtils
{
    public static final String DEFAULT_FILE_NAME_MAPPING = "@{artifactId}@-@{baseVersion}@.@{extension}@";
    public static final String DEFAULT_FILE_NAME_MAPPING_CLASSIFIER =
        "@{artifactId}@-@{baseVersion}@-@{classifier}@.@{extension}@";

    /**
     * Evaluates the specified expression for the given artifact.
     *
     * @param expression the expression to evaluate
     * @param artifact   the artifact to use as value object for tokens
     * @return expression the evaluated expression
     */
    public static String evaluateFileNameMapping( String expression, Artifact artifact )
        throws InterpolationException
    {
        String value = expression;

        // FIXME: This is BAD! Accessors SHOULD NOT change the behavior of the object.
        // [dennisl; 2013-07-30] This was fixed in Maven 2.0.8
        artifact.isSnapshot();

        RegexBasedInterpolator interpolator = new RegexBasedInterpolator( "\\@\\{(", ")?([^}]+)\\}@" );
        interpolator.addValueSource( new ObjectBasedValueSource( artifact ) );
        interpolator.addValueSource( new ObjectBasedValueSource( artifact.getArtifactHandler() ) );

        // Support for special expressions, like @{dashClassifier?}@, see MWAR-212
        interpolator.addValueSource( new DashClassifierValueSource( artifact.getClassifier() ) );

        value = interpolator.interpolate( value, "__artifact" );

        return value;
    }
}
