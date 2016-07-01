package org.apache.maven.shared.utils.logging;

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

import org.fusesource.jansi.Ansi;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;

import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

public class AnsiMessageBufferTest
{

    @Rule
    public MockitoRule mockitoRule = MockitoJUnit.rule();

    @Mock
    private Ansi ansi;

    private AnsiMessageBuffer ansiMessageBuffer;

    @Before
    public void makesAnsiFluent() throws Exception {
        given( ansi.bold() ).willReturn( ansi );
    }

    @Before
    public void initializeAnsiMessageBuffer() {
        this.ansiMessageBuffer = new AnsiMessageBuffer( ansi );
    }

    @Test
    public void should_color_debug()
    {
        // when
        ansiMessageBuffer.debug();

        // then
        verify( ansi ).bold();
        verify( ansi ).fgCyan();
    }

    @Test
    public void should_color_info()
    {
        // when
        ansiMessageBuffer.info();

        // then
        verify( ansi ).bold();
        verify( ansi ).fgBlue();
    }

    @Test
    public void should_color_warning()
    {
        // when
        ansiMessageBuffer.warning();

        // then
        verify( ansi ).bold();
        verify( ansi ).fgYellow();
    }

    @Test
    public void should_color_error()
    {
        // when
        ansiMessageBuffer.error();

        // then
        verify( ansi ).bold();
        verify( ansi ).fgRed();
    }

    @Test
    public void should_color_success()
    {
        // when
        ansiMessageBuffer.success();

        // then
        verify( ansi ).bold();
        verify( ansi ).fgGreen();
    }

    @Test
    public void should_color_failure()
    {
        // when
        ansiMessageBuffer.failure();

        // then
        verify( ansi ).bold();
        verify( ansi ).fgRed();
    }

    @Test
    public void should_color_strong()
    {
        // when
        ansiMessageBuffer.strong();

        // then
        verify( ansi ).bold();
    }

    @Test
    public void should_color_mojo()
    {
        // when
        ansiMessageBuffer.mojo();

        // then
        verify( ansi ).fgGreen();
    }

    @Test
    public void should_color_project()
    {
        // when
        ansiMessageBuffer.project();

        // then
        verify( ansi ).fgCyan();
    }

}