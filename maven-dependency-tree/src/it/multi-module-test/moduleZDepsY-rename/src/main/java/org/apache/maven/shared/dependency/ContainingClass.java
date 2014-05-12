package org.apache.maven.shared.dependency;

public final class ContainingClass
{
    public String someOtherMethod()
    {
        final DependentClass clazz = new DependentClass();
        return "foo" + clazz.someOtherMethod();
    }
}
