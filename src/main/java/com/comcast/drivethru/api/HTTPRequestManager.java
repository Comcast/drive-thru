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
/**
 * @author Bobby Jap
 */

package com.comcast.drivethru.api;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import javax.net.ssl.TrustManager;

import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpEntityEnclosingRequestBase;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpHead;
import org.apache.http.client.methods.HttpOptions;
import org.apache.http.client.methods.HttpPatch;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.client.methods.HttpTrace;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.entity.ByteArrayEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.comcast.drivethru.constants.ServerStatusCodes;
import com.comcast.drivethru.model.ResponseContainer;

public final class HTTPRequestManager
{
    // PROPERTIES ----------------------------------------------------------------------------------------------------------

    private static final Logger LOGGER = LoggerFactory.getLogger(HTTPRequestManager.class);

    // HTTP Request properties
    private String mUrl;
    private byte[] mData;
    private SSLConnectionSocketFactory mSocketFactory;
    private String mContentType;
    private Entry<String, String> mAuth;
    private METHOD mMethod;
    private Map<String, String> mHeaders;
    private String[] mCookies;
    private String mUserAgent;

    // HTTP Request Types
    public enum METHOD
    {
        GET,
        POST,
        PATCH,
        OPTIONS,
        DELETE,
        PUT,
        HEAD,
        TRACE
    }

    // CONSTRUCTORS --------------------------------------------------------------------------------------------------------

    /**
     * Private constructor used by the Builder subclass to create and initialize the HTTPResposneManager object.
     * @param builder Builder object that has all the needed parts to make a HTTP(S) request
     */
    private HTTPRequestManager(Builder builder)
    {
        mUrl = builder.mUrl;
        mData = builder.mData;
        mSocketFactory = builder.mSocketFactory;
        mContentType = builder.mContentType;
        mAuth = builder.mAuth;
        mMethod = builder.mMethod;
        mHeaders = builder.mHeaders;
        mUserAgent = builder.mUserAgent;
        mCookies = builder.mCookies;

        // verify that all required members have been set
        if (mUrl == null) throw new IllegalStateException("URL is a required field");
        if (mMethod == null) throw new IllegalStateException("Method is a required field");
    }

    // REQUEST GENERATION --------------------------------------------------------------------------------------------------

    /**
     * Creates a HttpClient object.
     * @return client
     */
    private CloseableHttpClient createHttpClient()
    {
        CloseableHttpClient client = null;

        if (mSocketFactory != null && mUrl.startsWith("https"))
        {
            client = HttpClients.custom().setSSLSocketFactory(
                    mSocketFactory).build();
        }
        else
        {
            client = HttpClients.custom().build();
        }

        return client;
    }

    /**
     * Configures HttpUrlConnection headers.
     * This includes adding Cookies, Content-Type, User-Agent, Auth, and other headers
     * @param message
     */
    private void setHeaders(HttpUriRequest message)
    {
        // set cookies to connection (if needed)
        if (mCookies != null)
        {
            for (String cookie : mCookies)
            {
                message.addHeader("Cookie", cookie);
            }
        }

        // set content type
        if ((mContentType != null) && !mContentType.isEmpty()) message.setHeader("Content-Type", mContentType);

        // set user agent
        if ((mUserAgent != null) && !mUserAgent.isEmpty()) message.setHeader("User-Agent", mUserAgent);

        // add headers
        for (String key : mHeaders.keySet())
        {
            message.setHeader(key, mHeaders.get(key));
        }

        // add auth header
        if (mAuth != null) message.setHeader(mAuth.getKey(), mAuth.getValue());
    }

    /**
     * Create the HTTP Method.
     * @return the method
     */
    private HttpUriRequest createMethod()
    {
        HttpUriRequest request = null;

        switch (mMethod)
        {
            case GET:
                request = new HttpGet(mUrl);
                break;

            case POST:
                request = new HttpPost(mUrl);
                break;

            case PATCH:
                request = new HttpPatch(mUrl);
                break;

            case OPTIONS:
                request = new HttpOptions(mUrl);
                break;

            case DELETE:
                request = new HttpDelete(mUrl);
                break;

            case PUT:
                request = new HttpPut(mUrl);
                break;

            case HEAD:
                request = new HttpHead(mUrl);
                break;

            case TRACE:
                request = new HttpTrace(mUrl);
                break;

            default:
                throw new UnsupportedOperationException("Unknown method: " + mMethod.toString());
        }

        return request;
    }

    // REQUEST DEPLOYMENT --------------------------------------------------------------------------------------------------

    /**
     * Send HTTP(S) Request.
     * @return {@link ResponseContainer}
     * @throws IOException When sending data to the server via HttpClient
     */
    public ResponseContainer sendRequest() throws IOException
    {
        // create a http method based on provided method
        HttpUriRequest httpMethod = createMethod();

        // Create a HttpClient
        CloseableHttpClient client = createHttpClient();

        // set cookies
        setHeaders(httpMethod);

        ResponseContainer container = null;

        if ((mData != null) && (mData.length > 0))
        {
            container = sendRequestWithData(client, httpMethod);
        }
        else
        {
            container = sendRequest(client, httpMethod);
        }

        return container;
    }

    /**
     * Sends request with data and returns {@link ResponseContainer} object containing response's status code and body.
     * @param client HttpClient use to send request
     * @param request Http request object to be sent out
     * @return {@link ResponseContainer} with response data
     * @throws IOException When there's an error sending out request
     */
    private ResponseContainer sendRequestWithData(CloseableHttpClient client, HttpUriRequest request) throws IOException
    {
        HttpEntityEnclosingRequestBase httpMethod = (HttpEntityEnclosingRequestBase) request;
        HttpEntity entity = new ByteArrayEntity(mData);

        httpMethod.setEntity(entity);

        return sendRequest(client, httpMethod);
    }

    /**
     * Sends request without data and returns {@link ResponseContainer} object containing response's status code and body.
     * @param client HttpClient use to send request
     * @param request Http request object to be sent out
     * @return {@link ResponseContainer} with response data
     * @throws IOException When there's an error sending out request
     */
    private ResponseContainer sendRequest(CloseableHttpClient client, Object request) throws IOException
    {
        ResponseContainer responseContainer = null;

        try
        {
            HttpResponse response = null;

            LOGGER.info("Sending request to " + mUrl);

            if (request instanceof HttpEntityEnclosingRequestBase)
            {
                response = client.execute((HttpEntityEnclosingRequestBase) request);
            }
            else
            {
                response = client.execute((HttpUriRequest) request);
            }

            int responseCode = response.getStatusLine().getStatusCode();
            String responseText = null;
            if (null != response.getEntity()) {
                responseText = EntityUtils.toString(response.getEntity());
            }
            Header[] headers = response.getAllHeaders();
            String responseLog = "Response: " + responseCode + " - ";

            if (responseText != null) responseLog += responseText;

            if (responseCode >= ServerStatusCodes.OK && responseCode <= ServerStatusCodes.PARTIAL_CONTENT)
            {
                LOGGER.info(responseLog);
            }
            else
            {
                LOGGER.error(responseLog);
            }

            responseContainer = new ResponseContainer(responseCode, responseText, headers);
        }
        catch (IOException e)
        {
            throw new IOException("Connection failed. Request not sent");
        }
        finally
        {
            client.close();
        }

        return responseContainer;
    }

    /**
     * Builder sub-class that is used to setup HTTPRequestHelper by providing necessary pieces.
     * @author Dmitry Jerusalimsky
     */
    public static class Builder
    {
        // PROPERTIES ----------------------------------------------------------------------------------------------------------

        /**
         * Default content type is: 'application/x-www-form-urlencoded'.
         */
        private static final String DEFAULT_CONTENT_TYPE = "application/x-www-form-urlencoded";
        /**
         * Default request method: GET.
         */
        private static final METHOD DEFAULT_METHOD = METHOD.GET;
        /**
         * Default encoding: UTF-8.
         */
        private static final String DEFAULT_ENCODING = "UTF-8";
        /**
         * Default set of headers are: .
         * <ul>
         * <li>Accept-Language = en-US,en;q-0.5</li>
         * <li>accept-charset = UTF-8
         * </ul>
         */
        private static final Map<String, String> DEFAULT_HEADERS;
        static
        {
            DEFAULT_HEADERS = new HashMap<>();
            DEFAULT_HEADERS.put("Accept-Language", "en-US,en;q=0.5");
            DEFAULT_HEADERS.put("accept-charset", DEFAULT_ENCODING);
        }

        // HTTP Request properties
        private String mContentType;
        private Entry<String, String> mAuth;
        private String mUrl;
        private METHOD mMethod;
        private Map<String, String> mHeaders;
        private SSLConnectionSocketFactory mSocketFactory = null;
        private byte[] mData;
        private String mUserAgent;
        private String[] mCookies;

        // CONSTRUCTORS ----------------------------------------------------------------------------------------------------

        /**
         * Default constructor.
         */
        public Builder()
        {
            mContentType = DEFAULT_CONTENT_TYPE;
            mMethod = DEFAULT_METHOD;
            mHeaders = DEFAULT_HEADERS;
        }

        // ACCESSORS AND MUTATORS ------------------------------------------------------------------------------------------

        /**
         * Sets request's User-Agent header.
         * @param userAgent Desired User-Agent string
         * @return {@link Builder} object
         */
        public Builder userAgent(String userAgent)
        {
            mUserAgent = userAgent;
            return this;
        }

        /**
         * Sets request's Cookies header.
         * @param cookies Array of cookies to be used by the request
         * @return {@link Builder} object
         */
        public Builder cookies(String[] cookies)
        {
            mCookies = cookies;
            return this;
        }

        /**
         * Sets request's data to be sent over.
         * @param data Data to send to the server
         * @return {@link Builder} object
         */
        public Builder data(String data)
        {
            mData = data.getBytes();
            return this;
        }

        /**
         * Sets request's data to be sent over in byte format.
         * @param data Data to send to the server
         * @return {@link Builder} object
         */
        public Builder data(byte[] data)
        {
            mData = data;
            return this;
        }

        /**
         * Sets request's url.
         * @param url URL to send request to
         * @return {@link Builder} object
         */
        public Builder url(String url)
        {
            mUrl = url;
            return this;
        }

        /**
         * Sets request's authorization header.
         * @param auth {@link Entry} object containing header name and it's value.
         * @return {@link Builder} object
         */
        public Builder auth(Entry<String, String> auth)
        {
            mAuth = auth;
            return this;
        }

        /**
         * Sets request's Content-Type header.
         * @param contentType Content-Type to be used. Default value is: application/x-www-form-urlencoded
         * @return {@link Builder} object
         */
        public Builder contentType(String contentType)
        {
            mContentType = contentType;
            return this;
        }

        /**
         * Sets request's method type (GET, POST, PUT, PATCH, DELETE, HEAD, OPTIONS, TRACE).
         * @param method Method to be used by the request
         * @return {@link Builder} object
         * @Deprecated Please use {@link method(METHOD))} method
         */
        public Builder method(String method)
        {
            if (method.equals("GET"))
            {
                mMethod = METHOD.GET;
            }
            else if (method.equals("POST"))
            {
                mMethod = METHOD.POST;
            }
            else if (method.equals("PATCH"))
            {
                mMethod = METHOD.PATCH;
            }
            else if (method.equals("OPTION"))
            {
                mMethod = METHOD.OPTIONS;
            }
            else if (method.equals("DELETE"))
            {
                mMethod = METHOD.DELETE;
            }
            else if (method.equals("PUT"))
            {
                mMethod = METHOD.PUT;
            }
            else if (method.equals("HEAD"))
            {
                mMethod = METHOD.HEAD;
            }
            else if (method.equals("TRACE"))
            {
                mMethod = METHOD.TRACE;
            }
            return this;
        }

        /**
         * Sets request's method type.
         * @param method Method type to use
         * @return {@link Builder} object
         */
        public Builder method(METHOD method)
        {
            mMethod = method;
            return this;
        }

        /**
         * Sets request's custom headers.
         * @param headers Array of custom headers to be used by the request
         * @return {@link Builder} object
         */
        public Builder headers(Map<String, String> headers)
        {
            mHeaders = headers;
            return this;
        }

        /**
         * Sets {@link TrustManager} array to allow for custom handling of SSL.
         * @param socketFactory Array of {@link SSLConnectionSocketFactory} objects for SSL validation
         * @return {@link Builder} object
         */
        public Builder socketFactory(SSLConnectionSocketFactory socketFactory)
        {
            mSocketFactory = socketFactory;
            return this;
        }

        /**
         * Creates an instance of {@link HTTPRequestManager} class.
         * @return A fully configured {@link HTTPRequestManager} object that is ready to send the request
         */
        public HTTPRequestManager build()
        {
            return new HTTPRequestManager(this);
        }
    }
}
