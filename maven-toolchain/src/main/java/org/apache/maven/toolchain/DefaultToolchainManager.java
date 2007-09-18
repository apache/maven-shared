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

package org.apache.maven.toolchain;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import org.apache.maven.context.BuildContext;
import org.apache.maven.toolchain.model.PersistedToolchains;
import org.apache.maven.toolchain.model.ToolchainModel;
import org.apache.maven.toolchain.model.io.xpp3.MavenToolchainsXpp3Reader;
import org.codehaus.plexus.PlexusConstants;
import org.codehaus.plexus.PlexusContainer;
import org.codehaus.plexus.component.repository.exception.ComponentLookupException;
import org.codehaus.plexus.context.Context;
import org.codehaus.plexus.context.ContextException;
import org.codehaus.plexus.personality.plexus.lifecycle.phase.Contextualizable;
import org.codehaus.plexus.util.IOUtil;

/**
 *
 * @author mkleint
 */
public class DefaultToolchainManager implements ToolchainManager, Contextualizable {

    /** 
     * @component
     */
    private PlexusContainer container;

    public DefaultToolchainManager() {
    }

    public void contextualize(Context context) throws ContextException {
        container = (PlexusContainer)context.get(PlexusConstants.PLEXUS_KEY);
    }

    public ToolchainPrivate[] getToolchainsForType(String type) throws MisconfiguredToolchainException {
        try {
            PersistedToolchains pers = readToolchainSettings();
            Map factories = container.lookupMap(ToolchainFactory.ROLE);
            List toRet = new ArrayList();
            if (pers != null) {
                List lst = pers.getToolchains();
                if (lst != null) {
                    Iterator it = lst.iterator();
                    while (it.hasNext()) {
                        ToolchainModel toolchainModel = (ToolchainModel) it.next();
                        ToolchainFactory fact = (ToolchainFactory)factories.get(toolchainModel.getType());
                        if (fact != null) {
                            toRet.add(fact.createToolchain(toolchainModel));
                        } else {
                            //TODO log the missing factory.
                            System.out.println("missing factory.." + toolchainModel);
                        }
                    }
                }
            }
            Iterator it = factories.values().iterator();
            while (it.hasNext()) {
                ToolchainFactory fact = (ToolchainFactory)it.next();
                ToolchainPrivate tool = fact.createDefaultToolchain();
                if (tool != null) {
                    toRet.add(tool);
                }
            }
            ToolchainPrivate[] tc = new ToolchainPrivate[toRet.size()];
            return (ToolchainPrivate[]) toRet.toArray(tc);
        } catch (ComponentLookupException ex) {
            //TODO
            ex.printStackTrace();
        }
        return new ToolchainPrivate[0];
    }

    public Toolchain getToolchainFromBuildContext(String type, BuildContext context) {
        try {
            ToolchainFactory fact = (ToolchainFactory) container.lookup(ToolchainFactory.ROLE, type);
            Toolchain dt = fact.createToolchain(context);
            if (dt != null) {
                return dt;
            }
            return null;
        } catch (ComponentLookupException ex) {
            //TODO report
            ex.printStackTrace();
        } catch (MisconfiguredToolchainException ex) {
            //TODO report
            ex.printStackTrace();
        }
        return null;
    }
    
    public void storeToolchainToBuildContext(ToolchainPrivate toolchain, BuildContext context) {
        context.store(toolchain);
    }
    
    private PersistedToolchains readToolchainSettings() throws MisconfiguredToolchainException {
        //TODO how to point to the local path?
        File tch = new File(System.getProperty("user.home"), ".m2/toolchains.xml");
        if (tch.exists()) {
            MavenToolchainsXpp3Reader reader = new MavenToolchainsXpp3Reader();
            InputStreamReader in = null;
            try {
                in = new InputStreamReader(new BufferedInputStream(new FileInputStream(tch)));
                return reader.read(in);
            } catch (Exception ex) {
                throw new MisconfiguredToolchainException("Cannot read toolchains file at " + tch.getAbsolutePath(), ex);
            } finally {
                IOUtil.close(in);
            }
        } else {
            //TODO log the fact that no toolchains file was found.
        }
        return null;
    }
}
