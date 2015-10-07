/**
 * Copyright 2013 Comcast Cable Communications Management, LLC
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.comcast.drivethru.exception;

/**
 * An Exception to indicate that a problem occurred while executing an HTTP action.
 *
 * @author <a href="mailto:clark_malmgren@cable.comcast.com">Clark Malmgren</a>
 */
public class HttpException extends Exception {

    /** Generated Serial Version UID */
    private static final long serialVersionUID = 8082085383132560383L;

    /**
     * Construct a new {@link HttpException} with the given message.
     *
     * @param message
     *            the message
     */
    public HttpException(String message) {
        super(message);
    }

    /**
     * Construct a new {@link HttpException} with the given message and cause.
     *
     * @param message
     *            the message
     * @param cause
     *            the cause
     */
    public HttpException(String message, Throwable cause) {
        super(message, cause);
    }
}
