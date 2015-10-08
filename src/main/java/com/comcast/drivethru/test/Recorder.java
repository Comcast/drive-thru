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

import java.util.ArrayList;
import java.util.List;

import com.comcast.drivethru.exception.HttpException;
import com.comcast.drivethru.utils.Method;
import com.comcast.drivethru.utils.RestResponse;
import com.comcast.drivethru.utils.URL;

/**
 * The <i>Recorded</i> is a delegate to provide {@link RecordingRestClient} utilities for
 * MockHttpClients.
 *
 * @author <a href="mailto:cmalmgren@gmail.com">Clark Malmgren</a>
 */
public class Recorder implements RecordingRestClient {

    private List<ResponseBuilder> responses = new ArrayList<>();

    /*
     * (non-Javadoc)
     * @see com.comcast.tvx.megahttp.test.RecordingRestClient#expect()
     */
    @Override
    public ResponseBuilder expect() {
        ResponseBuilder response = new ResponseBuilder();
        responses.add(response);
        return response;
    }

    /*
     * (non-Javadoc)
     * @see com.comcast.tvx.megahttp.test.RecordingRestClient#expect(java.lang.String)
     */
    @Override
    public ResponseBuilder expect(String pattern) {
        ResponseBuilder response = new ResponseBuilder(pattern);
        responses.add(response);
        return response;
    }

    /*
     * (non-Javadoc)
     * @see com.comcast.tvx.megahttp.test.RecordingRestClient#expect(java.lang.String, boolean)
     */
    @Override
    public ResponseBuilder expect(String pattern, boolean regex) {
        ResponseBuilder response = new ResponseBuilder(pattern, regex);
        responses.add(response);
        return response;
    }

    /*
     * (non-Javadoc)
     * @see
     * com.comcast.tvx.megahttp.test.RecordingRestClient#expect(com.comcast.tvx.megahttp.utils.Method
     * )
     */
    @Override
    public ResponseBuilder expect(Method method) {
        ResponseBuilder response = new ResponseBuilder(method);
        responses.add(response);
        return response;
    }

    /*
     * (non-Javadoc)
     * @see
     * com.comcast.tvx.megahttp.test.RecordingRestClient#expect(com.comcast.tvx.megahttp.utils.Method
     * , java.lang.String)
     */
    @Override
    public ResponseBuilder expect(Method method, String pattern) {
        ResponseBuilder response = new ResponseBuilder(method, pattern);
        responses.add(response);
        return response;
    }

    /*
     * (non-Javadoc)
     * @see
     * com.comcast.tvx.megahttp.test.RecordingRestClient#expect(com.comcast.tvx.megahttp.utils.Method
     * , java.lang.String, boolean)
     */
    @Override
    public ResponseBuilder expect(Method method, String pattern, boolean regex) {
        ResponseBuilder response = new ResponseBuilder(method, pattern, regex);
        responses.add(response);
        return response;
    }

    /**
     * Find a {@link ResponseBuilder} that matches the given arguments and replay it.
     *
     * @param url
     *            the URL of the incoming request
     * @param defaultBaseUrl
     *            the defaultBaseUrl of the HttpClient
     * @param method
     *            the method being executed
     *
     * @return the recorded response
     *
     * @throws HttpException
     *             if a matching response had a recorded exception to be thrown
     * @throws AssertionError
     *             if no matching response could be found
     */
    public RestResponse replay(URL url, String defaultBaseUrl, Method method) throws HttpException {
        String urlString = url.setDefaultBaseUrl(defaultBaseUrl).build();

        for (ResponseBuilder rr : responses) {
            if (rr.matches(urlString, method)) {
                return rr.replay();
            }
        }

        throw new AssertionError("No response recorded for URL: " + urlString);
    }
}
