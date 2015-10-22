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
 * @author Dmitry Jerusalimsky
 */
package com.comcast.csv.drivethru.model;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.Header;


/**
 * Class that contains HTTP Response code and message.
 * @author djerusalimsky
 */
public class ResponseContainer
{
    // PROPERTIES ----------------------------------------------------------------------------------------------------------

    private int mStatusCode;
    private String mResponse;
    private Header[] mHeaders;

    // CONSTRUCTORS --------------------------------------------------------------------------------------------------------

    /**
     * Constructor used to initialize the status code.
     * @param statusCode Status Code of the HTTP(S) response
     */
    public ResponseContainer(int statusCode)
    {
        this(statusCode, null, null);
    }

    /**
     * Constructor used to initialize the response.
     * @param response Response Body of the HTTP(S) response
     */
    public ResponseContainer(String response)
    {
        this(0, response, null);
    }

    /**
     * Constructor used to initialize the headers.
     * @param headers Header of the HTTP(S) response
     */
    public ResponseContainer(Header[] headers)
    {
        this(0, null, headers);
    }

    /**
     * Constructor used to initialize the statusCode, response, headers.
     * @param statusCode Status Code of the HTTP(S) response
     * @param response Response Body of the HTTP(S) response
     * @param headers Header of the HTTP(S) response
     */
    public ResponseContainer(int statusCode, String response, Header[] headers)
    {
        mStatusCode = statusCode;
        mResponse = response;
        mHeaders = headers;
    }

    // ACCESSORS AND MUTATORS ----------------------------------------------------------------------------------------------

    /**
     * Get the status code of the response.
     * @return Status code (i.e. 200)
     */
    public int getStatusCode()
    {
        return mStatusCode;
    }

    /**
     * Sets status code.
     * @param statusCode Status Code of the HTTP(S) response
     */
    protected void setStatusCode(int statusCode)
    {
        mStatusCode = statusCode;
    }

    /**
     * Get HTTP(S) response body.
     * @return Response body
     */
    public String getResponseBody()
    {

        return mResponse;
    }

    /**
     * Sets HTTP(S) Response body.
     * @param responseBody Response Body of the HTTP(S) response
     */
    protected void setResponseBody(String responseBody)
    {
        mResponse = responseBody;
    }

    /**
     * Get the response headers.
     * @return headers
     */
    public Header[] getResponseHeaders()
    {
        return mHeaders;
    }

    /**
     * Sets the response headers.
     * @param responseHeaders Headers of the HTTP(S) response
     */
    protected void setResponseHeaders(Header[] responseHeaders)
    {
        mHeaders = responseHeaders;
    }

    /**
     * Gets all Cookies from response headers.
     * @return cookies
     */
    public List<String> getCookies()
    {
        List<String> cookies = new ArrayList<String>();

        for (Header h : mHeaders)
        {
            if (h.getName().equalsIgnoreCase("set-cookie"))
            {
                cookies.add(h.getValue());
            }
        }

        return cookies;
    }
}
