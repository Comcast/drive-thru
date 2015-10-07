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
package com.comcast.drivethru.client;

import org.apache.http.client.HttpClient;

import com.comcast.drivethru.EasyRestClient;
import com.comcast.drivethru.RestClient;
import com.comcast.drivethru.exception.HttpException;
import com.comcast.drivethru.transform.JsonTransformer;
import com.comcast.drivethru.transform.Transformer;
import com.comcast.drivethru.utils.URL;

/**
 * The default implementation of a {@link EasyRestClient}.
 *
 * @author <a href="mailto:clark_malmgren@cable.comcast.com">Clark Malmgren</a>
 */
public class DefaultEasyRestClient<R> extends DefaultRestClient implements EasyRestClient<R> {

    private Class<R> type;

    /**
     * Constructs a new {@link DefaultEasyRestClient} with the given Java REST <code>type</code> and
     * no <code>defaultBaseUrl</code> that will use the the standard {@link JsonTransformer} and a
     * default {@link HttpClient} with a default timeout of 10 seconds (
     * {@link RestClient#DEFAULT_TIMEOUT}).
     * <p>
     * Because no <code>defaultBaseUrl</code> has been defined, all executions must provide fully
     * defined {@link URL}s or they will fail. Use of this constructor is discouraged.
     * </p>
     *
     * @param type
     *            the Java REST type to use for get methods that don't take an explicit type
     */
    public DefaultEasyRestClient(Class<R> type) {
        this(type, null);
    }

    /**
     * Constructs a new {@link DefaultEasyRestClient} with the given Java REST <code>type</code> and
     * the given <code>defaultBaseUrl</code> that will use the the standard {@link JsonTransformer}
     * and a default {@link HttpClient} with a default timeout of 10 seconds (
     * {@link RestClient#DEFAULT_TIMEOUT}).
     *
     *
     * @param type
     *            the Java REST type to use for get methods that don't take an explicit type
     * @param defaultBaseUrl
     *            the default base URL used for this connection
     */
    public DefaultEasyRestClient(Class<R> type, String defaultBaseUrl) {
        super(defaultBaseUrl);
        this.type = type;
    }

    /**
     * Constructs a new {@link DefaultEasyRestClient} with the given Java REST <code>type</code> and
     * the given <code>defaultBaseUrl</code> that will use the given {@link Transformer} and a
     * default {@link HttpClient} with a default timeout of 10 seconds (
     * {@link RestClient#DEFAULT_TIMEOUT}).
     *
     *
     * @param type
     *            the Java REST type to use for get methods that don't take an explicit type
     * @param defaultBaseUrl
     *            the default base URL used for this connection
     * @param transformer
     *            the transformer for handling serialization of HTTP body contents
     */
    public DefaultEasyRestClient(Class<R> type, String defaultBaseUrl, Transformer transformer) {
        super(defaultBaseUrl, transformer);
        this.type = type;
    }

    /**
     * Constructs a new {@link DefaultEasyRestClient} with the given Java REST <code>type</code> and
     * the given <code>defaultBaseUrl</code> that will use the given {@link HttpClient} and the
     * standard {@link JsonTransformer}.
     *
     *
     * @param type
     *            the Java REST type to use for get methods that don't take an explicit type
     * @param defaultBaseUrl
     *            the default base URL used for this connection
     * @param delegate
     *            the inner HTTP connection component
     */
    public DefaultEasyRestClient(Class<R> type, String defaultBaseUrl, HttpClient delegate) {
        super(defaultBaseUrl, delegate);
        this.type = type;
    }

    /**
     * Constructs a new {@link DefaultEasyRestClient} with the given Java REST <code>type</code> and
     * the given <code>defaultBaseUrl</code> that will use the given {@link Transformer} and
     * {@link HttpClient}.
     *
     * @param type
     *            the Java REST type to use for get methods that don't take an explicit type
     * @param defaultBaseUrl
     *            the default base URL used for this connection
     * @param transformer
     *            the transformer for handling serialization of HTTP body contents
     * @param delegate
     *            the inner HTTP connection component
     */
    public DefaultEasyRestClient(Class<R> type, String defaultBaseUrl, Transformer transformer,
            HttpClient delegate) {
        super(defaultBaseUrl, transformer, delegate);
        this.type = type;
    }

    /*
     * (non-Javadoc)
     * @see com.comcast.tvx.megahttp.EasyRestClient#get(java.lang.String)
     */
    @Override
    public R get(String path) throws HttpException {
        return this.get(path, type);
    }

    /*
     * (non-Javadoc)
     * @see com.comcast.tvx.megahttp.EasyRestClient#get(com.comcast.tvx.megahttp.utils.URL)
     */
    @Override
    public R get(URL url) throws HttpException {
        return this.get(url, type);
    }
}
