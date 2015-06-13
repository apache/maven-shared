package org.apache.maven.shared.artifact.filter.resolve;

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

/**
 * The element interface of the visitor pattern for transforming filters. 
 * 
 * @author Robert Scholte
 * @since 3.0
 */
public interface TransformableFilter
{
    /**
     * Subclasses should include the following code:
     * <pre>
     *   &#64;Override
     *   public abstract &lt;T&gt; T transform( FilterTransformer&lt;T&gt; transformer )
     *   {
     *       return transformer.transform( this );
     *   }
     * </pre>
     * 
     * @param transformer the tool specific transformer, may not be {@code null}
     * @return the transformed value, never {@code null}
     */
    <T> T transform( FilterTransformer<T> transformer );
}
