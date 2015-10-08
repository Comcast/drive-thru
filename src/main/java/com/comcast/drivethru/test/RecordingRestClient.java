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
package com.comcast.drivethru.test;

import com.comcast.drivethru.utils.Method;


/**
 * A <i>RecordingRestClient</i> provides test-only capacity to record responses that will be
 * returned when matching requests are executed.
 *
 * @author <a href="mailto:cmalmgren@gmail.com">Clark Malmgren</a>
 */
public interface RecordingRestClient {

    /**
     * Get a response builder that will match everything.
     *
     * @return a {@link ResponseBuilder}
     */
    ResponseBuilder expect();

    /**
     * Get a response builder that will match any request whose URL matches the given pattern as a
     * regular expression.
     *
     * @param pattern
     *            the regular expression pattern to match
     *
     * @return a {@link ResponseBuilder}
     */
    ResponseBuilder expect(String pattern);

    /**
     * Get a response builder that will match any request whose URL matches the given pattern.
     *
     * @param pattern
     *            the pattern to match
     * @param regex
     *            if <code>true</code>, the pattern is a regular expression, otherwise the pattern
     *            is a string that must equal an incoming URL
     *
     * @return a {@link ResponseBuilder}
     */
    ResponseBuilder expect(String pattern, boolean regex);

    /**
     * Get a response builder that will match any request with the given method.
     *
     * @param method
     *            the method that must be matched or <code>null</code> to match any method
     *
     * @return a {@link ResponseBuilder}
     */
    ResponseBuilder expect(Method method);

    /**
     * Get a response builder that will match requests with the given method whose URL matches the
     * given pattern as a regular expression.
     *
     *
     * @param method
     *            the method that must be matched or <code>null</code> to match any method
     * @param pattern
     *            the regular expression pattern to match
     *
     * @return a {@link ResponseBuilder}
     */
    ResponseBuilder expect(Method method, String pattern);

    /**
     * Get a response builder that will match any request with the given method whose URL matches
     * the given pattern.
     *
     * @param method
     *            the method that must be matched or <code>null</code> to match any method
     * @param pattern
     *            the pattern to match
     * @param regex
     *            if <code>true</code>, the pattern is a regular expression, otherwise the pattern
     *            is a string that must equal an incoming URL
     *
     * @return a {@link ResponseBuilder}
     */
    ResponseBuilder expect(Method method, String pattern, boolean regex);
}
