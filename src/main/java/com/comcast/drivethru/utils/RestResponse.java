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
package com.comcast.drivethru.utils;

import java.util.HashMap;
import java.util.Map;

import org.apache.http.Header;
import org.apache.http.StatusLine;

/**
 * A <i>RestResponse</i> represents the result of executing a HTTP request.
 *
 * @author <a href="mailto:cmalmgren@gmail.com">Clark Malmgren</a>
 */
public class RestResponse {

    private int statusCode;
    private String statusMessage;
    private Map<String, String> headers;
    private byte[] body;

    /**
     * Construct a new {@link RestResponse} with the given status.
     *
     * @param status
     *            the status
     */
    public RestResponse(StatusLine status) {
        this(status.getStatusCode(), status.getReasonPhrase());
    }

    /**
     * Construct a new {@link RestResponse} with the given status code and message.
     *
     * @param statusCode
     *            the integer status code
     * @param statusMessage
     *            the status message
     */
    public RestResponse(int statusCode, String statusMessage) {
        this.statusCode = statusCode;
        this.statusMessage = statusMessage;
        this.headers = new HashMap<>();
        this.body = null;
    }

    /**
     * Add all headers to this response
     *
     * @param headers
     *            the headers to include
     */
    public void addAll(Header... headers) {
        for (Header header : headers) {
            this.headers.put(header.getName(), header.getValue());
        }
    }

    /**
     * Add a single header to this response.
     *
     * @param name
     *            the name of the header
     * @param value
     *            the value of the header
     */
    public void addHeader(String name, String value) {
        this.headers.put(name, value);
    }

    /**
     * Set the body of the response.
     *
     * @param body
     *            the body of the response
     */
    public void setBody(byte[] body) {
        this.body = body;
    }

    /**
     * Get the integer status code.
     *
     * @return the integer status code
     */
    public int getStatusCode() {
        return statusCode;
    }

    /**
     * Get the status message
     *
     * @return the status message
     */
    public String getStatusMessage() {
        return statusMessage;
    }

    /**
     * Get the content type of the response. This will remove any trailing charset information that
     * is sometimes returned in responses.
     *
     * @return the content type or <code>null</code> if the "Content-Type" header was not present
     */
    public String getContentType() {
        String value = headers.get("Content-Type");
        if (value == null) {
            return null;
        }
        int index = value.indexOf(';');
        return (-1 == index) ? value : value.substring(0, index);
    }

    /**
     * Get the value of a header by name.
     *
     * @param name header key
     * @return the header value or <code>null</code> if the header was not present
     */
    public String getHeaderValue(String name) {
        return headers.get(name);
    }

    /**
     * Get the body of the response.
     *
     * @return the body of the response
     */
    public byte[] getBody() {
        return body;
    }

    /**
     * Get the body of the response as a string.
     *
     * @return the body of the response as a string
     */
    public String getBodyString() {
        return new String(body);
    }
}
