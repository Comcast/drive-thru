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

import java.io.IOException;

import org.apache.http.client.HttpClient;

import com.comcast.drivethru.client.DefaultRestClient;
import com.comcast.drivethru.exception.HttpException;
import com.comcast.drivethru.transform.Transformer;
import com.comcast.drivethru.utils.Method;
import com.comcast.drivethru.utils.RestRequest;
import com.comcast.drivethru.utils.RestResponse;

/**
 * The {@link MockRestClient} extends the {@link DefaultRestClient} and overrides the
 * {@link DefaultRestClient#execute(RestRequest)} method to only replay recorded responses.
 *
 * @author <a href="mailto:clark_malmgren@cable.comcast.com">Clark Malmgren</a>
 */
public class MockRestClient extends DefaultRestClient implements RecordingRestClient {

    private Recorder recorder = new Recorder();

    /**
     * Construct a new MockRestClient that uses the given defaultBaseUrl.
     *
     * @param defaultBaseUrl
     *            the defaultBaseUrl
     */
    public MockRestClient(String defaultBaseUrl) {
        super(defaultBaseUrl, (HttpClient) null);
    }

    /**
     * Construct a new MockRestClient that uses the given defaultBaseUrl and transformer.
     *
     * @param defaultBaseUrl
     *            the defaultBaseUrl
     * @param transformer
     *            the transformer to use
     */
    public MockRestClient(String defaultBaseUrl, Transformer transformer) {
        super(defaultBaseUrl, transformer, null);
    }

    /*
     * (non-Javadoc)
     * @see com.comcast.tvx.megahttp.test.RecordingRestClient#expect()
     */
    @Override
    public ResponseBuilder expect() {
        return recorder.expect();
    }

    /*
     * (non-Javadoc)
     * @see com.comcast.tvx.megahttp.test.RecordingRestClient#expect(java.lang.String)
     */
    @Override
    public ResponseBuilder expect(String pattern) {
        return recorder.expect(pattern);
    }

    /*
     * (non-Javadoc)
     * @see com.comcast.tvx.megahttp.test.RecordingRestClient#expect(java.lang.String, boolean)
     */
    @Override
    public ResponseBuilder expect(String pattern, boolean regex) {
        return recorder.expect(pattern, regex);
    }

    /*
     * (non-Javadoc)
     * @see com.comcast.tvx.megahttp.test.RecordingRestClient#expect(com.comcast.tvx.megahttp.utils.Method)
     */
    @Override
    public ResponseBuilder expect(Method method) {
        return recorder.expect(method);
    }

    /*
     * (non-Javadoc)
     * @see com.comcast.tvx.megahttp.test.RecordingRestClient#expect(com.comcast.tvx.megahttp.utils.Method, java.lang.String)
     */
    @Override
    public ResponseBuilder expect(Method method, String pattern) {
        return recorder.expect(method, pattern);
    }

    /*
     * (non-Javadoc)
     * @see com.comcast.tvx.megahttp.test.RecordingRestClient#expect(com.comcast.tvx.megahttp.utils.Method, java.lang.String, boolean)
     */
    @Override
    public ResponseBuilder expect(Method method, String pattern, boolean regex) {
        return recorder.expect(method, pattern, regex);
    }

    /*
     * (non-Javadoc)
     * @see com.comcast.tvx.megahttp.client.DefaultBasicHttpClient#execute(com.comcast.tvx.megahttp.utils.RestRequest)
     */
    @Override
    public RestResponse execute(RestRequest request) throws HttpException {
        return recorder.replay(request.getUrl(), getDefaultBaseUrl(), request.getMethod());
    }

    /*
     * (non-Javadoc)
     * @see com.comcast.tvx.megahttp.client.DefaultBasicHttpClient#close()
     */
    @Override
    public void close() throws IOException {}
}
