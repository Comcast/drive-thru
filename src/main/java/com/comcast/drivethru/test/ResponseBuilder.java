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

import com.comcast.cvs.cereal.CerealException;
import com.comcast.cvs.cereal.engines.JsonCerealEngine;
import com.comcast.drivethru.exception.HttpException;
import com.comcast.drivethru.utils.Method;
import com.comcast.drivethru.utils.RestResponse;

/**
 * The {@link ResponseBuilder} provides a way for building up expectations and recored actions to
 * take when a MockHttpClient gets a matching execute call. Most methods return <code>this</code> to
 * allow for method chaining when recording.
 *
 * @author <a href="mailto:cmalmgren@gmail.com">Clark Malmgren</a>
 */
public class ResponseBuilder {

    /* Used for matching */
    private String pattern;
    private boolean regex;
    private Method method;
    private int times;

    /* Used for replay */
    private RestResponse response;
    private HttpException exception;
    private long delay;

    /**
     * Construct a new {@link ResponseBuilder} that will match anything.
     */
    public ResponseBuilder() {
        this(null, ".*", true);
    }

    /**
     * Construct a new {@link ResponseBuilder} that will match any request whose URL matches the
     * given pattern as a regular expression.
     *
     * @param pattern
     *            the regular expression pattern to match
     */
    public ResponseBuilder(String pattern) {
        this(null, pattern, true);
    }

    /**
     * Construct a new {@link ResponseBuilder} that will match any request whose URL matches the
     * given pattern.
     *
     * @param pattern
     *            the pattern to match
     * @param regex
     *            if <code>true</code>, the pattern is a regular expression, otherwise the pattern
     *            is a string that must equal an incoming URL
     */
    public ResponseBuilder(String pattern, boolean regex) {
        this(null, pattern, regex);
    }

    /**
     * Construct a new {@link ResponseBuilder} that will match any request with the given method.
     *
     * @param method
     *            the method that must be matched or <code>null</code> to match any method
     */
    public ResponseBuilder(Method method) {
        this(method, ".*", true);
    }

    /**
     * Construct a new {@link ResponseBuilder} that will match requests with the given method whose
     * URL matches the given pattern as a regular expression.
     *
     *
     * @param method
     *            the method that must be matched or <code>null</code> to match any method
     * @param pattern
     *            the regular expression pattern to match
     */
    public ResponseBuilder(Method method, String pattern) {
        this(method, pattern, true);
    }

    /**
     * Construct a new {@link ResponseBuilder} that will match any request with the given method
     * whose URL matches the given pattern.
     *
     * @param method
     *            the method that must be matched or <code>null</code> to match any method
     * @param pattern
     *            the pattern to match
     * @param regex
     *            if <code>true</code>, the pattern is a regular expression, otherwise the pattern
     *            is a string that must equal an incoming URL
     */
    public ResponseBuilder(Method method, String pattern, boolean regex) {
        this.pattern = pattern;
        this.regex = regex;
        this.method = method;
        this.times = -1;

        this.response = null;
        this.exception = null;
        this.delay = -1;
    }

    /**
     * Returns <code>true</code> if this {@link ResponseBuilder} matches the given request.
     *
     * @param url
     *            the URL
     * @param method
     *            the method
     *
     * @return <code>true</code> if this {@link ResponseBuilder} matches the given request
     */
    public boolean matches(String url, Method method) {
        if (times == 0) {
            return false;
        } else if ((null != this.method) && (!this.method.equals(method))) {
            return false;
        } else if (regex) {
            return url.matches(pattern);
        } else {
            return pattern.equals(url);
        }
    }

    /**
     * Execute the recorded actions.
     *
     * @return the recorded response
     *
     * @throws HttpException
     *             the recorded exception
     */
    public RestResponse replay() throws HttpException {
        this.times--;

        if (delay > 0) {
            try {
                Thread.sleep(delay);
            } catch (InterruptedException iex) {
                Thread.interrupted(); // clear the flag
            }
        }

        if (null != exception) {
            throw exception;
        } else {
            return response;
        }
    }

    /**
     * Introduce a delay in processing. This should only be used when testing multi-threaded code
     * for synchronization.
     *
     * @param delay
     *            the delay in millisecond
     *
     * @return <code>this</code>
     */
    public ResponseBuilder after(long delay) {
        this.delay = delay;
        return this;
    }

    /**
     * Throw the given <code>exception</code> on replay.
     *
     * @param exception
     *            the exception to throw.
     *
     * @return <code>this</code>
     */
    public ResponseBuilder andThrow(HttpException exception) {
        this.exception = exception;
        return this;
    }

    /**
     * Get the recorded exception to throw.
     *
     * @return the recorded exception or <code>null</code> if none has been recorded.
     */
    public HttpException getException() {
        return exception;
    }

    /**
     * Return with the given status code on replay
     *
     * @param statusCode
     *            the status code
     *
     * @return <code>this</code>
     */
    public ResponseBuilder andReturn(int statusCode) {
        return this.andReturn(statusCode, "NOT_DEFINED");
    }

    /**
     * Return with the given status code and message on replay
     *
     * @param statusCode
     *            the status code
     * @param statusMessage
     *            the status message
     *
     * @return <code>this</code>
     */
    public ResponseBuilder andReturn(int statusCode, String statusMessage) {
        this.response = new RestResponse(statusCode, statusMessage);
        return this;
    }

    /**
     * Attach the given body to the returned response. This must be called AFTER a call to
     * {@link #andReturn(int)} or {@link #andReturn(int, String)}.
     *
     * @param body
     *            the body
     * @param contentType
     *            the content type
     *
     * @return <code>this</code>
     */
    public ResponseBuilder withBody(byte[] body, String contentType) {
        this.response.setBody(body);
        this.response.addHeader("Content-Type", contentType);
        return this;
    }

    /**
     * Attach the given body to the returned response. This must be called AFTER a call to
     * {@link #andReturn(int)} or {@link #andReturn(int, String)}.
     *
     * @param body
     *            the body
     * @param contentType
     *            the content type
     *
     * @return <code>this</code>
     */
    public ResponseBuilder withBody(String body, String contentType) {
        return withBody(body.getBytes(), contentType);
    }

    /**
     * Attach the given object encoded to JSON to the returned response. This must be called AFTER a
     * call to {@link #andReturn(int)} or {@link #andReturn(int, String)}.
     *
     * @param object
     *            the object to use as the body of the response
     *
     * @return <code>this</code>
     */
    public ResponseBuilder withJsonBody(Object object) throws CerealException {
        JsonCerealEngine jsonEngine = new JsonCerealEngine();
        return withBody(jsonEngine.writeToString(object), "application/json");
    }

    /**
     * Add a header to the returned response. This must be called AFTER a call to
     * {@link #andReturn(int)} or {@link #andReturn(int, String)}.
     *
     * @param name
     *            the header name
     * @param value
     *            the header value
     *
     * @return <code>this</code>
     */
    public ResponseBuilder withHeader(String name, String value) {
        this.response.addHeader(name, value);
        return this;
    }

    /**
     * Indicates that this response builder can replay once before it will stop matching. This
     * should be used when creating tests that should return different responses given the same URL
     * and method.
     *
     * @return <code>this</code>
     */
    public ResponseBuilder once() {
        return times(1);
    }

    /**
     * Indicate a maximum number of times this response builder can replay before it will stop
     * matching. This should be used when creating tests that should return different responses
     * given the same URL and method.
     *
     * @param count
     *            the maximum number of times this response builder can replay before it will stop
     *            matching
     *
     * @return <code>this</code>
     */
    public ResponseBuilder times(int count) {
        this.times = count;
        return this;
    }
}
