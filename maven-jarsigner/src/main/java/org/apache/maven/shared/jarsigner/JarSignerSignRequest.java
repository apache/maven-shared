package org.apache.maven.shared.jarsigner;

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

import java.io.File;

/**
 * Specifies the parameters used to control a jar signer sign operation invocation.
 *
 * @author tchemit <chemit@codelutin.com>
 * @version $Id$
 * @since 1.0
 */
public class JarSignerSignRequest
    extends AbstractJarSignerRequest
{

    /**
     * See <a href="http://docs.oracle.com/javase/6/docs/technotes/tools/windows/jarsigner.html#Options">options</a>.
     */
    private String keypass;

    /**
     * See <a href="http://docs.oracle.com/javase/6/docs/technotes/tools/windows/jarsigner.html#Options">options</a>.
     */
    private String sigfile;

    /**
     * See <a href="http://docs.oracle.com/javase/6/docs/technotes/tools/windows/jarsigner.html#Options">options</a>.
     */
    private String tsaLocation;

    /**
     * See <a href="http://docs.oracle.com/javase/6/docs/technotes/tools/windows/jarsigner.html#Options">options</a>.
     */
    private String tsaAlias;

    /**
     * See <a href="http://docs.oracle.com/javase/6/docs/technotes/tools/windows/jarsigner.html#Options">options</a>.
     */
    protected File signedjar;


    public String getKeypass()
    {
        return keypass;
    }

    public String getSigfile()
    {
        return sigfile;
    }

    public String getTsaLocation()
    {
        return tsaLocation;
    }

    public String getTsaAlias()
    {
        return tsaAlias;
    }

    public void setKeypass( String keypass )
    {
        this.keypass = keypass;
    }

    public void setSigfile( String sigfile )
    {
        this.sigfile = sigfile;
    }

    public void setTsaLocation( String tsaLocation )
    {
        this.tsaLocation = tsaLocation;
    }

    public void setTsaAlias( String tsaAlias )
    {
        this.tsaAlias = tsaAlias;
    }

    public File getSignedjar()
    {
        return signedjar;
    }

    public void setSignedjar( File signedjar )
    {
        this.signedjar = signedjar;
    }

}
