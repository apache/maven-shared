/*
 * InvocationResult.java
 *
 * Created on May 30, 2006, 11:25 AM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package org.apache.maven.shared.invoker;

import org.codehaus.plexus.util.cli.CommandLineException;

/**
 *
 * @author jdcasey
 */
public interface InvocationResult
{
    CommandLineException getExecutionException();

    int getExitCode();
    
}
