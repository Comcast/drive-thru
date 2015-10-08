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

import java.lang.reflect.Constructor;

import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.client.methods.HttpRequestBase;

import com.comcast.drivethru.exception.HttpException;

/**
 * An enum representing the full set of supported HTTP methods.
 *
 * @author <a href="mailto:cmalmgren@gmail.com">Clark Malmgren</a>
 */
public enum Method {

    /** HTTP GET Method */
    GET(HttpGet.class),

    /** HTTP POST Method */
    POST(HttpPost.class),

    /** HTTP PUT Method */
    PUT(HttpPut.class),

    /** HTTP DELETE Method */
    DELETE(HttpDelete.class);

    private Class<? extends HttpRequestBase> type;

    /**
     * Internal constructor that maps this Method to the Apache HTTP class that represents the given
     * request type.
     *
     * @param type
     *            the Apache HTTP request class for this method
     */
    private Method(Class<? extends HttpRequestBase> type) {
        this.type = type;
    }

    /**
     * Get a new instance of the appropriate Apache HTTP request object for this HTTP method and
     * pass it the given URL.
     *
     * @param url
     *            the URL to pass into the constructor of the request object
     *
     * @return a new instance of the appropriate Apache HTTP request object for this HTTP method
     *
     * @throws HttpException
     *             if the request object could not be created
     */
    public HttpRequestBase getRequest(String url) throws HttpException {
        try {
            Constructor<? extends HttpRequestBase> constructor = type.getConstructor(String.class);
            return constructor.newInstance(url);
        } catch (Exception ex) {
            throw new HttpException("Failed to create an RestRequest", ex);
        }
    }
}
