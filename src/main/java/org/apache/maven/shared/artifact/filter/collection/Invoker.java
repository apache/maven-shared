package org.apache.maven.shared.artifact.filter.collection;

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

import java.lang.reflect.InvocationTargetException;

/**
 * Invokes method on objects using reflection.
 */
final class Invoker
{
    private Invoker()
    {
        // do not instantiate
    }

    public static Object invoke( Object object, String method )
        throws IllegalAccessException, InvocationTargetException, NoSuchMethodException
    {
        return invoke( object.getClass(), object, method );
    }

    public static Object invoke( Class<?> objectClazz, Object object, String method )
        throws IllegalAccessException, InvocationTargetException, NoSuchMethodException
    {
        return objectClazz.getMethod( method ).invoke( object );
    }

    public static Object invoke( Object object, String method, Class<?> clazz, Object arg )
        throws IllegalAccessException, InvocationTargetException, NoSuchMethodException
    {
        final Class<?> objectClazz = object.getClass();
        return objectClazz.getMethod( method, clazz ).invoke( object, arg );
    }
}
