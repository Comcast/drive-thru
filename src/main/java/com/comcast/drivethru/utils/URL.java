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

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

import com.comcast.drivethru.exception.HttpException;

/**
 * Simple utility class for building up URLs.
 *
 * @author <a href="mailto:cmalmgren@gmail.com">Clark Malmgren</a>
 */
public class URL {

    private String baseUrl;
    private String path;
    private List<String[]> query;

    /**
     * Default constructor with no fields filled out yet.
     */
    public URL() {
        this(null);
    }

    /**
     * Construct a URL starting with the given baseUrl.
     *
     * @param baseUrl
     *            the baseUrl to start with
     */
    public URL(String baseUrl) {
        this(baseUrl, null);
    }

    /**
     * Construct a URL starting with the given baseUrl and path.
     *
     * @param baseUrl
     *            the baseUrl to start with
     * @param path
     *            the path to use
     */
    public URL(String baseUrl, String path) {
        this.baseUrl = baseUrl;
        this.path = path;
        this.query = new ArrayList<>();
    }

    /**
     * Set the baseUrl for this fully built URL.
     *
     * @param baseUrl
     *            the base URL
     *
     * @return <code>this</code>
     */
    public URL setBaseUrl(String baseUrl) {
        this.baseUrl = baseUrl;
        return this;
    }

    /**
     * Set a baseUrl for this fully built URL if and only if this URL does not already have a
     * baseURL set.
     *
     * @param defaultBaseUrl
     *            the default base URL to use if there is no base URL already set.
     *
     * @return <code>this</code>
     */
    public URL setDefaultBaseUrl(String defaultBaseUrl) {
        if (null == baseUrl) {
            this.baseUrl = defaultBaseUrl;
        }
        return this;
    }

    /**
     * Get the base URL for this full built URL.
     *
     * @return the base URL
     */
    public String getBaseUrl() {
        return baseUrl;
    }

    /**
     * Returns <code>true</code> if this has a non-<code>null</code> baseUrl set, otherwise
     * <code>false</code>.
     *
     * @return <code>true</code> if this has a non-<code>null</code> baseUrl set, otherwise
     *         <code>false</code>
     */
    public boolean hasBaseUrl() {
        return (null != baseUrl);
    }

    /**
     * Set the relative path for this URL from the base URL.
     *
     * @param path
     *            the path
     *
     * @return <code>this</code>
     */
    public URL setPath(String path) {
        this.path = path;
        return this;
    }

    /**
     * Add a path element to the underlying path. This is effectively going to execute:
     * <code>this.path += "/" + path;</code>
     *
     * @param path
     *            the path to add to the existing path
     *
     * @return <code>this</code>
     */
    public URL addPath(String path) {
        if (null == this.path) {
            this.path = "/" + path;
        } else {
            this.path += "/" + path;
        }
        return this;
    }

    /**
     * Add the given key-value pair as a query parameter. This will simply call
     * <code>value.toString()</code> to convert the value to a string. This will URL-encode both the
     * key and value so that should NOT be done prior to calling this method. This method returns
     * <code>this</code> as a convenient way to chain adding parameters.
     *
     * @param key
     *            the key of the query parameter
     * @param value
     *            the value of that parameter
     *
     * @return <code>this</code>
     */
    public URL addQuery(String key, Object value) {
        return addQuery(key, value.toString());
    }

    /**
     * Add the given key-value pair as a query parameter. This will URL-encode both the key and
     * value so that should NOT be done prior to calling this method. This method returns
     * <code>this</code> as a convenient way to chain adding parameters.
     *
     * @param key
     *            the key of the query parameter
     * @param value
     *            the value of that parameter
     *
     * @return <code>this</code>
     */
    public URL addQuery(String key, String value) {
        String[] pair = new String[] { encode(key), encode(value) };
        query.add(pair);
        return this;
    }

    /**
     * Build the full URL.
     *
     * @return the full URL
     * @throws HttpException
     */
    public String build() throws HttpException {
        if (null == baseUrl) {
            throw new HttpException("URL must set a baseUrl");
        }

        StringBuilder sb = new StringBuilder(baseUrl);
        if (null != path) {
            /* If the baseUrl ends with a '/', don't use any beginning '/' from the path */
            if (baseUrl.endsWith("/")) {
                int index = 0;
                for (; index < path.length() && path.charAt(index) == '/'; index++);
                sb.append(path.substring(index));
            } else {
                sb.append(path);
            }
        }

        if (query.size() > 0) {
            String[] pair = query.get(0);
            sb.append('?');
            sb.append(pair[0]);
            sb.append('=');
            sb.append(pair[1]);
        }

        for (int i = 1; i < query.size(); i++) {
            String[] pair = query.get(i);
            sb.append('&');
            sb.append(pair[0]);
            sb.append('=');
            sb.append(pair[1]);
        }

        return sb.toString();
    }

    /**
     * URL encode the given string changing all spaces to <code>%20</code>, not <code>+</code>.
     *
     * @param val
     *            the string to encode
     *
     * @return the encoded string
     */
    public static String encode(String val) {
        try {
            return URLEncoder.encode(val, "UTF-8").replace("+", "%20");
        } catch (UnsupportedEncodingException ueex) {
            throw new RuntimeException("UTF-8 not supported on this machine", ueex);
        }
    }
}
