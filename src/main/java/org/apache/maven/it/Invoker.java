package org.apache.maven.it;

public interface Invoker
{
    void invoke( InvocationRequest request )
        throws IntegrationTestException;

    String getExecutable();

    String getMavenVersion()
        throws IntegrationTestException;
}
