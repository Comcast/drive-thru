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
 * An Exception to indicate that a the response status of an HTTP action was unacceptable.
 *
 * @author <a href="mailto:cmalmgren@gmail.com">Clark Malmgren</a>
 */
public class HttpStatusException extends HttpException {

    /** Generated Serial Version UID */
    private static final long serialVersionUID = -5366132858240731981L;

    /**
     * Construct a new {@link HttpStatusException} for the given status code.
     *
     * @param statusCode
     *            the integer status code
     */
    public HttpStatusException(int statusCode) {
        super("Response returned with unacceptable statusCode: " + statusCode);
    }

    /**
     * Construct a new {@link HttpStatusException} for the given status code and message.
     *
     * @param statusCode
     *            the integer status code
     * @param statusMessage
     *            the status message
     */
    public HttpStatusException(int statusCode, String statusMessage) {
        super("Response returned with unacceptable status: " + statusCode + " " + statusMessage);
    }
}
