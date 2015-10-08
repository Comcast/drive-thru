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

import com.comcast.drivethru.client.DefaultEasyRestClient;
import com.comcast.drivethru.client.DefaultRestClient;
import com.comcast.drivethru.exception.HttpException;
import com.comcast.drivethru.transform.Transformer;
import com.comcast.drivethru.utils.Method;
import com.comcast.drivethru.utils.RestRequest;
import com.comcast.drivethru.utils.RestResponse;

/**
 * The {@link MockEasyRestClient} extends the {@link DefaultEasyRestClient} and overrides the
 * {@link DefaultRestClient#execute(RestRequest)} method to only replay recorded responses.
 *
 * @author <a href="mailto:cmalmgren@gmail.com">Clark Malmgren</a>
 */
public class MockEasyRestClient<R> extends DefaultEasyRestClient<R> implements RecordingRestClient {

    private Recorder recorder = new Recorder();

    /**
     * Construct a new MockEasyRestClient for the given type that uses the given defaultBaseUrl.
     *
     * @param type
     *            the REST type
     * @param defaultBaseUrl
     *            the defaultBaseUrl
     */
    public MockEasyRestClient(Class<R> type, String defaultBaseUrl) {
        super(type, defaultBaseUrl, (HttpClient) null);
    }

    /**
     * Construct a new MockEasyRestClient for the given type that uses the given defaultBaseUrl and
     * transformer.
     *
     * @param type
     *            the REST type
     * @param defaultBaseUrl
     *            the defaultBaseUrl
     * @param transformer
     *            the transformer to use
     */
    public MockEasyRestClient(Class<R> type, String defaultBaseUrl, Transformer transformer) {
        super(type, defaultBaseUrl, transformer, null);
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
