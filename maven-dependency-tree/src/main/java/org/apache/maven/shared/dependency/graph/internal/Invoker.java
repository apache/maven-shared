package org.apache.maven.shared.dependency.graph.internal;

import org.apache.maven.shared.dependency.graph.DependencyGraphBuilderException;

import java.lang.reflect.InvocationTargetException;

/**
 * Invokes method on objects using reflection.
 */
final class Invoker
{
    public Object invoke( Class objectClazz, Object object, String method )
            throws DependencyGraphBuilderException
    {
        try
        {
            return objectClazz.getMethod( method ).invoke( object );
        } catch ( IllegalAccessException e )
        {
            throw new DependencyGraphBuilderException( e.getMessage(), e );
        } catch ( InvocationTargetException e )
        {
            throw new DependencyGraphBuilderException( e.getMessage(), e );
        } catch ( NoSuchMethodException e )
        {
            throw new DependencyGraphBuilderException( e.getMessage(), e );
        }
    }

    public Object invoke( Object object, String method, Class<?> clazz, Object arg )
            throws DependencyGraphBuilderException
    {
        try
        {
            final Class objectClazz = object.getClass();
            return objectClazz.getMethod( method, clazz ).invoke( object, arg );
        } catch ( IllegalAccessException e )
        {
            throw new DependencyGraphBuilderException( e.getMessage(), e );
        } catch ( InvocationTargetException e )
        {
            throw new DependencyGraphBuilderException( e.getMessage(), e );
        } catch ( NoSuchMethodException e )
        {
            throw new DependencyGraphBuilderException( e.getMessage(), e );
        }
    }
}
