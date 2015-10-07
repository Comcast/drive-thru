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

import com.comcast.drivethru.client.DefaultEasyRestClient;
import com.comcast.drivethru.client.DefaultRestClient;

/**
 * Factory for creating various HttpClients.
 *
 * @author <a href="mailto:clark_malmgren@cable.comcast.com">Clark Malmgren</a>
 */
public class RestFactory {

    /**
     * Get a new {@link RestClient} with the given <code>defaultBaseUrl</code>.
     *
     * @param defaultBaseUrl
     *            the default base URL to use for the created HttpClient
     *
     * @return a new {@link RestClient}
     */
    public RestClient getClient(String defaultBaseUrl) {
        return new DefaultRestClient(defaultBaseUrl);
    }

    /**
     * Get a new {@link EasyRestClient} with the given REST <code>type</code> and
     * <code>defaultBaseUrl</code>.
     *
     * @param type
     *            the Java object type used for default REST GET calls
     * @param defaultBaseUrl
     *            the default base URL to use for the created HttpClient
     *
     * @return a new {@link EasyRestClient}
     */
    public <T> EasyRestClient<T> getEasyClient(Class<T> type, String defaultBaseUrl) {
        return new DefaultEasyRestClient<>(type, defaultBaseUrl);
    }
}
