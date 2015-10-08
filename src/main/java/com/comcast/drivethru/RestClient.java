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

import java.io.Closeable;

import com.comcast.drivethru.exception.HttpException;
import com.comcast.drivethru.exception.HttpStatusException;
import com.comcast.drivethru.security.SecurityProvider;
import com.comcast.drivethru.transform.JsonTransformer;
import com.comcast.drivethru.transform.Transformer;
import com.comcast.drivethru.utils.RestRequest;
import com.comcast.drivethru.utils.RestResponse;
import com.comcast.drivethru.utils.URL;

/**
 * A <i>RestClient</i> provides basic HTTP connection utilities. For serialization of the HTTP body
 * contents, this will delegate to the internal {@link Transformer}. The default {@link Transformer}
 * is a {@link JsonTransformer}.
 * <p>
 * If any REST connection returns a response that cannot be converted into the requested java
 * object, that method will throw an {@link HttpException}.
 * </p>
 *
 * @author <a href="mailto:cmalmgren@gmail.com">Clark Malmgren</a>
 */
public interface RestClient extends Closeable {

    /** The default timeout (10 seconds) for all HttpClients in milliseconds */
    public static final int DEFAULT_TIMEOUT = 10000; // 10 seconds

    /**
     * Get the default base URL used for this connection. Any connection that does not explicitly
     * set a <code>baseUrl</code> in a {@link URL} object, or uses a "path" based method will use
     * this as the base URL.
     *
     * @return the default base URL
     *
     * @see URL#setBaseUrl(String)
     * @see URL#setDefaultBaseUrl(String)
     */
    String getDefaultBaseUrl();

    /**
     * Add a "default" header that should be included with every connection made by this HttpClient.
     *
     * @param name
     *            the name of the header
     * @param value
     *            the value of the header
     */
    void addDefaultHeader(String name, String value);

    /**
     * Add a {@link SecurityProvider} to provide additional encryption and security for this
     * connection.
     *
     * @param securityProvider
     *            the security provider to use for all connections made by this HttpClient
     */
    void setSecurityProvider(SecurityProvider securityProvider);

    /**
     * Set the transformer for handling serialization of HTTP body contents.
     *
     * @param transformer
     *            the new transformer to use
     */
    void setTransformer(Transformer transformer);

    /**
     * Execute the given {@link RestRequest} and return the Response. If the connection produces an
     * HTTP error status code, this will still return a {@link RestResponse} with all the obtainable
     * fields set. This means that the execute method will attempt to return instead of throwing an
     * exception. All other HttpClient methods delegate the execution to this method which can
     * generically handle any request.
     *
     * @param request
     *            the request to execute
     *
     * @return the response from the target server
     *
     * @throws HttpException
     *             if an error occurred while making a connection
     */
    RestResponse execute(RestRequest request) throws HttpException;

    /**
     * Execute a HTTP GET on the given relative path from the set default base URL (
     * {@link #getDefaultBaseUrl()}) and transform the resulting contents into a java object of the
     * given <code>type</code>.
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
     * @param type
     *            the Java object type to transform the response into
     *
     * @return the Java object that was transformed from the response of executing the HTTP GET
     *         action
     *
     * @throws HttpException
     *             if an error occurred while making a connection
     * @throws HttpStatusException
     *             if the response status code did not meet the requirements of this method
     */
    <T> T get(String path, Class<T> type) throws HttpException;

    /**
     * Execute a HTTP GET on the given URL and transform the resulting contents into a java object
     * of the given <code>type</code>. If the baseUrl has not been set on the given <code>url</code>
     * object, the default base URL ({@link #getDefaultBaseUrl()}) will be used.
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
     * @param type
     *            the Java object type to transform the response into
     *
     * @return the Java object that was transformed from the response of executing the HTTP GET
     *         action
     *
     * @throws HttpException
     *             if an error occurred while making a connection
     * @throws HttpStatusException
     *             if the response status code did not meet the requirements of this method
     */
    <T> T get(URL url, Class<T> type) throws HttpException;

    /**
     * Execute a HTTP PUT on the given relative path from the set default base URL (
     * {@link #getDefaultBaseUrl()}) transforming the passed java object into the body of the
     * request using the internal transformer.
     *
     * <h2>Status Code Implications</h2>
     * <p>
     * The following rules are applied as per the <a
     * href=http://www.w3.org/Protocols/rfc2616/rfc2616-sec9.html">HTTP 1.1 Method Definitions</a>:
     * </p>
     * <ul>
     * <li>This method SHALL return <code>true</code> if the response code is
     * <code>201 Created</code></li>
     * <li>This method SHALL return <code>false</code> if the response code is <code>200 OK</code>
     * or <code>204 No Content</code></li>
     * <li>This method SHALL throw a {@link HttpStatusException} in all other cases</li>
     * </ul>
     *
     * @param <T>
     *            the Java type of the request body
     *
     * @param path
     *            the relative path to the URL to execute a PUT against
     * @param t
     *            the Java object to transform into the request body
     *
     * @return <code>true</code> or <code>false</code> as defined by the status code implications
     *         for this method
     *
     * @throws HttpException
     *             if an error occurred while making a connection
     * @throws HttpStatusException
     *             if the response status code did not meet the requirements of this method
     */
    <T> boolean put(String path, T t) throws HttpException;

    /**
     * Execute a HTTP PUT on the given URL transforming the passed java object into the body of the
     * request using the internal transformer. If the baseUrl has not been set on the given
     * <code>url</code> object, the default base URL ({@link #getDefaultBaseUrl()}) will be used.
     *
     * <h2>Status Code Implications</h2>
     * <p>
     * The following rules are applied as per the <a
     * href=http://www.w3.org/Protocols/rfc2616/rfc2616-sec9.html">HTTP 1.1 Method Definitions</a>:
     * </p>
     * <ul>
     * <li>This method SHALL return <code>true</code> if the response code is
     * <code>201 Created</code></li>
     * <li>This method SHALL return <code>false</code> if the response code is <code>200 OK</code>
     * or <code>204 No Content</code></li>
     * <li>This method SHALL throw a {@link HttpStatusException} in all other cases</li>
     * </ul>
     *
     * @param <T>
     *            the Java type of the request body
     *
     * @param url
     *            the URL to execute a PUT against
     * @param t
     *            the Java object to transform into the request body
     *
     * @return <code>true</code> or <code>false</code> as defined by the status code implications
     *         for this method
     *
     * @throws HttpException
     *             if an error occurred while making a connection
     * @throws HttpStatusException
     *             if the response status code did not meet the requirements of this method
     */
    <T> boolean put(URL url, T t) throws HttpException;

    /**
     * Execute a HTTP DELETE on the given relative path from the set default base URL (
     * {@link #getDefaultBaseUrl()}).
     *
     * <h2>Status Code Implications</h2>
     * <p>
     * The following rules are applied as per the <a
     * href=http://www.w3.org/Protocols/rfc2616/rfc2616-sec9.html">HTTP 1.1 Method Definitions</a>:
     * </p>
     * <ul>
     * <li>This method SHALL return <code>true</code> if the response code is <code>200 OK</code></li>
     * <li>This method SHALL return <code>false</code> if the response code is
     * <code>202 Accepted</code> or <code>204 No Content</code></li>
     * <li>This method SHALL throw a {@link HttpStatusException} in all other cases</li>
     * </ul>
     *
     * @param path
     *            the relative path to the URL to execute a DELETE against
     *
     * @return <code>true</code> or <code>false</code> as defined by the status code implications
     *         for this method
     *
     * @throws HttpException
     *             if an error occurred while making a connection
     * @throws HttpStatusException
     *             if the response status code did not meet the requirements of this method
     */
    boolean delete(String path) throws HttpException;

    /**
     * Execute a HTTP DELETE on the given URL. If the baseUrl has not been set on the given
     * <code>url</code> object, the default base URL ({@link #getDefaultBaseUrl()}) will be used.
     *
     * <h2>Status Code Implications</h2>
     * <p>
     * The following rules are applied as per the <a
     * href=http://www.w3.org/Protocols/rfc2616/rfc2616-sec9.html">HTTP 1.1 Method Definitions</a>:
     * </p>
     * <ul>
     * <li>This method SHALL return <code>true</code> if the response code is <code>200 OK</code></li>
     * <li>This method SHALL return <code>false</code> if the response code is
     * <code>202 Accepted</code> or <code>204 No Content</code></li>
     * <li>This method SHALL throw a {@link HttpStatusException} in all other cases</li>
     * </ul>
     *
     * @param url
     *            the URL to execute a DELETE against
     *
     * @return <code>true</code> or <code>false</code> as defined by the status code implications
     *         for this method
     *
     * @throws HttpException
     *             if an error occurred while making a connection
     * @throws HttpStatusException
     *             if the response status code did not meet the requirements of this method
     */
    boolean delete(URL url) throws HttpException;

    /**
     * Execute a HTTP POST on the given relative path from the set default base URL (
     * {@link #getDefaultBaseUrl()}) with nothing in the request body and transform the resulting
     * contents into a java object of the given <code>responseType</code> using the internal
     * transformer.
     *
     * <h2>Status Code Implications</h2>
     * <p>
     * The following rules are applied as per the <a
     * href=http://www.w3.org/Protocols/rfc2616/rfc2616-sec9.html">HTTP 1.1 Method Definitions</a>:
     * </p>
     * <p>
     * This method SHALL only support the following status codes. All other status codes SHALL cause
     * a {@link HttpStatusException} to be thrown.
     * </p>
     * <ul>
     * <li><code>200 OK</code></li>
     * <li><code>201 Created</code></li>
     * <li><code>204 No Content</code></li>
     * </ul>
     *
     * @param <T>
     *            the Java type of the response body
     *
     * @param path
     *            the relative path to the URL to execute a PUT against
     * @param responseType
     *            the Java type of the response body
     *
     * @return the Java object that was transformed from the response of executing the HTTP POST
     *         action
     *
     * @throws HttpException
     *             if an error occurred while making a connection
     * @throws HttpStatusException
     *             if the response status code did not meet the requirements of this method
     */
    <T> T post(String path, Class<T> responseType) throws HttpException;

    /**
     * Execute a HTTP POST on the given URL twith nothing in the request and transform the resulting
     * contents into a java object of the given <code>responseType</code> using the internal
     * transformer. If the baseUrl has not been set on the given <code>url</code> object, the
     * default base URL ({@link #getDefaultBaseUrl()}) will be used.
     *
     * <h2>Status Code Implications</h2>
     * <p>
     * The following rules are applied as per the <a
     * href=http://www.w3.org/Protocols/rfc2616/rfc2616-sec9.html">HTTP 1.1 Method Definitions</a>:
     * </p>
     * <p>
     * This method SHALL only support the following status codes. All other status codes SHALL cause
     * a {@link HttpStatusException} to be thrown.
     * </p>
     * <ul>
     * <li><code>200 OK</code></li>
     * <li><code>201 Created</code></li>
     * <li><code>204 No Content</code></li>
     * </ul>
     *
     * @param <T>
     *            the Java type of the response body
     *
     * @param url
     *            the URL to execute a PUT against
     * @param responseType
     *            the Java type of the response body
     *
     * @return the Java object that was transformed from the response of executing the HTTP POST
     *         action
     *
     * @throws HttpException
     *             if an error occurred while making a connection
     * @throws HttpStatusException
     *             if the response status code did not meet the requirements of this method
     */
    <T> T post(URL url, Class<T> responseType) throws HttpException;

    /**
     * Execute a HTTP POST on the given relative path from the set default base URL (
     * {@link #getDefaultBaseUrl()}) transforming the passed payload object into the body of the
     * request and transform the resulting contents into a java object of the given
     * <code>responseType</code> using the internal transformer.
     *
     * <h2>Status Code Implications</h2>
     * <p>
     * The following rules are applied as per the <a
     * href=http://www.w3.org/Protocols/rfc2616/rfc2616-sec9.html">HTTP 1.1 Method Definitions</a>:
     * </p>
     * <p>
     * This method SHALL only support the following status codes. All other status codes SHALL cause
     * a {@link HttpStatusException} to be thrown.
     * </p>
     * <ul>
     * <li><code>200 OK</code></li>
     * <li><code>201 Created</code></li>
     * <li><code>204 No Content</code></li>
     * </ul>
     *
     * @param <P>
     *            the Java type of the request body
     * @param <T>
     *            the Java type of the response body
     *
     * @param path
     *            the relative path to the URL to execute a PUT against
     * @param payload
     *            the Java object to transform into the request body
     * @param responseType
     *            the Java type of the response body
     *
     * @return the Java object that was transformed from the response of executing the HTTP POST
     *         action
     *
     * @throws HttpException
     *             if an error occurred while making a connection
     * @throws HttpStatusException
     *             if the response status code did not meet the requirements of this method
     */
    <P, T> T post(String path, P payload, Class<T> responseType) throws HttpException;

    /**
     * Execute a HTTP POST on the given URL transforming the passed payload object into the body of
     * the request and transform the resulting contents into a java object of the given
     * <code>responseType</code> using the internal transformer. If the baseUrl has not been set on
     * the given <code>url</code> object, the default base URL ({@link #getDefaultBaseUrl()}) will
     * be used.
     *
     * <h2>Status Code Implications</h2>
     * <p>
     * The following rules are applied as per the <a
     * href=http://www.w3.org/Protocols/rfc2616/rfc2616-sec9.html">HTTP 1.1 Method Definitions</a>:
     * </p>
     * <p>
     * This method SHALL only support the following status codes. All other status codes SHALL cause
     * a {@link HttpStatusException} to be thrown.
     * </p>
     * <ul>
     * <li><code>200 OK</code></li>
     * <li><code>201 Created</code></li>
     * <li><code>204 No Content</code></li>
     * </ul>
     *
     * @param <P>
     *            the Java type of the request body
     * @param <T>
     *            the Java type of the response body
     *
     * @param url
     *            the URL to execute a PUT against
     * @param payload
     *            the Java object to transform into the request body
     * @param responseType
     *            the Java type of the response body
     *
     * @return the Java object that was transformed from the response of executing the HTTP POST
     *         action
     *
     * @throws HttpException
     *             if an error occurred while making a connection
     * @throws HttpStatusException
     *             if the response status code did not meet the requirements of this method
     */
    <P, T> T post(URL url, P payload, Class<T> responseType) throws HttpException;
}
