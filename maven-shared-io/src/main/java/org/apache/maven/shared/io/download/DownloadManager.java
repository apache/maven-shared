package org.apache.maven.shared.io.download;

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

import java.io.File;
import java.util.List;

import org.apache.maven.shared.io.logging.MessageHolder;
import org.apache.maven.wagon.events.TransferListener;

/**
 * The Download Manager interface.
 *
 */
public interface DownloadManager
{
    /**
     * The Role.
     */
    String ROLE = DownloadManager.class.getName();

    /**
     * @param url The URL.
     * @param messageHolder {@link MessageHolder}
     * @return {@link File}
     * @throws DownloadFailedException in case of exception.
     */
    File download( String url, MessageHolder messageHolder )
        throws DownloadFailedException;

    /**
     * @param url The URL.
     * @param transferListeners {@link TransferListener}
     * @param messageHolder {@link MessageHolder}
     * @return {@link File}
     * @throws DownloadFailedException in case of exception.
     */
    File download( String url, List<TransferListener> transferListeners, MessageHolder messageHolder )
        throws DownloadFailedException;

}