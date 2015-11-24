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

import com.comcast.drivethru.EasyRestClient;
import com.comcast.drivethru.RestClient;
import com.comcast.drivethru.RestFactory;

/**
 * Factory for returning Mock HttpClients.
 *
 * @author <a href="mailto:cmalmgren@gmail.com">Clark Malmgren</a>
 */
public class MockRestFactory extends RestFactory {

    /*
     * (non-Javadoc)
     * @see com.comcast.drivethru.RestFactory#getClient(java.lang.String)
     */
    public RestClient getClient(String defaultBaseUrl) {
        return getMockClient(defaultBaseUrl);
    }

    /*
     * (non-Javadoc)
     * @see com.comcast.drivethru.RestFactory#getEasyClient(java.lang.Class, java.lang.String)
     */
    public <T> EasyRestClient<T> getEasyClient(Class<T> type, String defaultBaseUrl) {
        return getMockEasyClient(type, defaultBaseUrl);
    }

    /**
     * Get a new {@link MockRestClient} with the given <code>defaultBaseUrl</code>.
     *
     * @param defaultBaseUrl
     *            the default base URL to use for the created HttpClient
     *
     * @return a new {@link MockRestClient}
     */
    public static MockRestClient getMockClient(String defaultBaseUrl) {
        return new MockRestClient(defaultBaseUrl);
    }

    /**
     * Get a new {@link MockEasyRestClient} with the given REST <code>type</code> and
     * <code>defaultBaseUrl</code>.
     *
     * @param type
     *            the Java object type used for default REST GET calls
     * @param defaultBaseUrl
     *            the default base URL to use for the created HttpClient
     *
     * @return a new {@link MockEasyRestClient}
     */
    public static <T> MockEasyRestClient<T> getMockEasyClient(Class<T> type, String defaultBaseUrl) {
        return new MockEasyRestClient<>(type, defaultBaseUrl);
    }
}
