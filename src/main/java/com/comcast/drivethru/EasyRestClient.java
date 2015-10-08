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
package com.comcast.drivethru;

import com.comcast.drivethru.exception.HttpException;
import com.comcast.drivethru.exception.HttpStatusException;
import com.comcast.drivethru.utils.URL;

/**
 * The <i>EasyRestClient</i> is an extension to a {@link RestClient} that provides additional
 * utility for getting objects over HTTP using a Java type defined at construction.
 *
 * @author <a href="mailto:cmalmgren@gmail.com">Clark Malmgren</a>
 */
public interface EasyRestClient<R> extends RestClient {

    /**
     * Execute a HTTP GET on the given relative path from the set default base URL (
     * {@link #getDefaultBaseUrl()}) and transform the resulting contents into a java object of the
     * REST <code>type</code> defined when the {@link EasyRestClient} was constructed.
     *
     * <h2>Status Code Implications</h2>
     * <p>
     * The following rules are applied as per the <a
     * href=http://www.w3.org/Protocols/rfc2616/rfc2616-sec9.html">HTTP 1.1 Method Definitions</a>:
     * </p>
     * <ul>
     * <li>
     * This method SHALL throw a {@link HttpStatusException} if the response code is not
     * <code>200 OK</code></li>
     * </ul>
     *
     * @param path
     *            the relative path to the URL to execute a GET against
     *
     * @return the Java object that was transformed from the response of executing the HTTP GET
     *         action
     *
     * @throws HttpException
     *             if an error occurred while making a connection
     * @throws HttpStatusException
     *             if the response status code did not meet the requirements of this method
     */
    R get(String path) throws HttpException;

    /**
     * Execute a HTTP GET on the given URL and transform the resulting contents into a java object
     * of the REST <code>type</code> defined when the {@link EasyRestClient} was constructed. If the
     * baseUrl has not been set on the given <code>url</code> object, the default base URL (
     * {@link #getDefaultBaseUrl()}) will be used.
     *
     * <h2>Status Code Implications</h2>
     * <p>
     * The following rules are applied as per the <a
     * href=http://www.w3.org/Protocols/rfc2616/rfc2616-sec9.html">HTTP 1.1 Method Definitions</a>:
     * </p>
     * <ul>
     * <li>
     * This method SHALL throw a {@link HttpStatusException} if the response code is not
     * <code>200 OK</code></li>
     * </ul>
     *
     * @param url
     *            the URL to execute a GET against
     *
     * @return the Java object that was transformed from the response of executing the HTTP GET
     *         action
     *
     * @throws HttpException
     *             if an error occurred while making a connection
     * @throws HttpStatusException
     *             if the response status code did not meet the requirements of this method
     */
    R get(URL url) throws HttpException;
}
