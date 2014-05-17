package org.apache.maven.shared.dependency;

public final class DependentClass
{
    public String someOtherMethod()
    {
        final SimpleClass clazz = new SimpleClass();
        return "bar" + clazz.someMethod();
    }
}
