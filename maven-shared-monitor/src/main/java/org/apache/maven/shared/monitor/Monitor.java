package org.apache.maven.shared.monitor;

/*
 * Copyright 2005 The Apache Software Foundation.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

/**
 * Interface for logging within maven-antcall. CharSequence's are used to avoid the need
 * to convert StringBuffers to Strings.
 */
public interface Monitor
{

    String VERBOSE = "verbose";

    int VERBOSE_LEVEL = 4;

    String DEBUG = "debug";

    int DEBUG_LEVEL = 3;

    String INFO = "info";

    int INFO_LEVEL = 2;

    String WARN = "warn";

    int WARN_LEVEL = 1;

    String ERROR = "error";

    int ERROR_LEVEL = 0;

    String[] MESSAGE_LEVELS = { ERROR, WARN, INFO, DEBUG, VERBOSE };

    void verbose( CharSequence message );

    void verbose( CharSequence message, Throwable error );

    boolean isVerboseEnabled();

    void debug( CharSequence message );

    void debug( CharSequence message, Throwable error );

    boolean isDebugEnabled();

    void info( CharSequence message );

    void info( CharSequence message, Throwable error );

    boolean isInfoEnabled();

    void warn( CharSequence message );

    void warn( CharSequence message, Throwable error );

    boolean isWarnEnabled();

    void error( CharSequence message );

    void error( CharSequence message, Throwable error );

    boolean isErrorEnabled();

}
