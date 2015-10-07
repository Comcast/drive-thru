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

import org.apache.http.client.config.RequestConfig;

/**
 * A <i>RestRequest</i> represents a HTTP request to execute.
 *
 * @author <a href="mailto:clark_malmgren@cable.comcast.com">Clark Malmgren</a>
 */
public class RestRequest {

    private URL url;
    private Method method;
    private Map<String, String> headers;
    private byte[] body;
    private RequestConfig config = null;
    private RequestConfig.Builder configBuilder = RequestConfig.custom();

    /**
     * Construct a new {@link RestRequest} with the given relative path and method.
     *
     * @param path
     *            the relative path for the request
     * @param method
     *            the HTTP method
     */
    public RestRequest(String path, Method method) {
        this(new URL().setPath(path), method);
    }

    /**
     * Construct a new {@link RestRequest} with the given URL and method.
     *
     * @param url
     *            the URL for the request
     * @param method
     *            the HTTP method
     */
    public RestRequest(URL url, Method method) {
        this.url = url;
        this.method = method;
        this.headers = new HashMap<>();
        this.body = null;
    }

    /**
     * Get the URL
     *
     * @return the URL
     */
    public URL getUrl() {
        return url;
    }

    /**
     * Get the HTTP method.
     *
     * @return the HTTP method
     */
    public Method getMethod() {
        return method;
    }

    /**
     * Get all headers as a map.
     *
     * @return all headers
     */
    public Map<String, String> getHeaders() {
        return headers;
    }

    /**
     * Add a header to this request.
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
     * Append a query key=value pair to the end of this request URL.
     *
     * @param key
     *            the key
     * @param value
     *            the value
     */
    public void addQuery(String key, String value) {
        this.url.addQuery(key, value);
    }

    /**
     * Set the content type header for this request.
     *
     * @param type
     *            the content type
     */
    public void setContentType(String type) {
        this.headers.put("Content-Type", type);
    }

    /**
     * Set the body as a string.
     *
     * @param body
     *            the body as a string
     */
    public void setBody(String body) {
        this.body = body.getBytes();
    }

    /**
     * Set the body as an array of bytes
     *
     * @param body
     *            the body as an array of bytes
     */
    public void setBody(byte[] body) {
        this.body = body;
    }

    /**
     * Get the body as an array of bytes.
     *
     * @return the body as an array of bytes
     */
    public byte[] getBody() {
        return body;
    }

    public void setTimeout(int timeout) {
        this.configBuilder.setSocketTimeout(timeout);
    }

    public void setRedirectsEnabled(boolean redirectsEnabled) {
        this.configBuilder.setRedirectsEnabled(redirectsEnabled);
    }

    public void setConfig(RequestConfig config) {
        this.config = config;
    }

    public RequestConfig getConfig() {
        if (config != null) return config;
        return configBuilder.build();
    }
}
