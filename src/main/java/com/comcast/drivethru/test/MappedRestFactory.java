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
import static com.comcast.drivethru.test.MockRestFactory.getMockClient;
import static com.comcast.drivethru.test.MockRestFactory.getMockEasyClient;

import java.util.HashMap;
import java.util.Map;

import com.comcast.drivethru.EasyRestClient;
import com.comcast.drivethru.RestClient;
import com.comcast.drivethru.RestFactory;

/**
 * A <i>MappedRestFactory</i> allows for a test to map HttpClients to be returned when the
 * defaultBaseUrl is matched. This is particularly useful when testing components that use a factory
 * to get access to HttpClients.
 *
 * @author <a href="mailto:clark_malmgren@cable.comcast.com">Clark Malmgren</a>
 */
public class MappedRestFactory extends RestFactory {

    private Map<String, RestClient> mapping = new HashMap<>();

    /**
     * Map the given client to be returned when the defaultBaseUrl is passed as a request parameter
     *
     * @param defaultBaseUrl
     *            the defaultBaseUrl to match
     * @param client
     *            the client to return
     */
    public void map(String defaultBaseUrl, RestClient client) {
        mapping.put(defaultBaseUrl, client);
    }

    /**
     * Create a new {@link MockRestClient} and map it to the given defaultBaseUrl.
     *
     * @param defaultBaseUrl
     *            the defaultBaseUrl to match
     *
     * @return the newly created {@link MockRestClient}
     */
    public MockRestClient createMappedClient(String defaultBaseUrl) {
        MockRestClient client = getMockClient(defaultBaseUrl);
        map(defaultBaseUrl, client);
        return client;
    }

    /**
     * Create a new {@link MockEasyRestClient} and map it to the given defaultBaseUrl.
     *
     * @param type
     *            the REST type
     * @param defaultBaseUrl
     *            the defaultBaseUrl to match
     *
     * @return the newly created {@link MockEasyRestClient}
     */
    public <T> MockEasyRestClient<T> createMappedEasyClient(Class<T> type, String defaultBaseUrl) {
        MockEasyRestClient<T> client = getMockEasyClient(type, defaultBaseUrl);
        map(defaultBaseUrl, client);
        return client;
    }

    /*
     * (non-Javadoc)
     * @see com.comcast.tvx.megahttp.RestFactory#getClient(java.lang.String)
     */
    public RestClient getClient(String defaultBaseUrl) {
        return (RestClient) mapping.get(defaultBaseUrl);
    }

    /*
     * (non-Javadoc)
     * @see com.comcast.tvx.megahttp.RestFactory#getEasyClient(java.lang.Class, java.lang.String)
     */
    @SuppressWarnings("unchecked")
    public <T> EasyRestClient<T> getEasyClient(Class<T> type, String defaultBaseUrl) {
        return (EasyRestClient<T>) mapping.get(defaultBaseUrl);
    }
}
