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
package com.comcast.drivethru.security;

import oauth.signpost.OAuthConsumer;
import oauth.signpost.commonshttp.CommonsHttpOAuthConsumer;
import oauth.signpost.exception.OAuthException;

import org.apache.http.client.methods.HttpUriRequest;

import com.comcast.drivethru.exception.HttpException;

/**
 * A {@link SecurityProvider} that provides OAUTH security and signing.
 *
 * @author <a href="mailto:clark_malmgren@cable.comcast.com">Clark Malmgren</a>
 */
public class OAuthSecurityProvider implements SecurityProvider {

    private OAuthConsumer consumer;

    /**
     * Construct a new {@link OAuthSecurityProvider} using the given OAUTH key and secret.
     *
     * @param key
     *            the OAUTH key
     * @param secret
     *            the OAUTH secret
     */
    public OAuthSecurityProvider(String key, String secret) {
        this(new CommonsHttpOAuthConsumer(key, secret));
    }

    /**
     * Construct a new {@link OAuthSecurityProvider} using the given {@link OAuthConsumer}.
     *
     * @param consumer
     *            the internal OAUTH consumer
     */
    public OAuthSecurityProvider(OAuthConsumer consumer) {
        this.consumer = consumer;
    }

    /*
     * (non-Javadoc)
     * @see com.comcast.tvx.megahttp.security.SecurityProvider#sign(org.apache.http.client.methods.HttpUriRequest)
     */
    public synchronized void sign(HttpUriRequest request) throws HttpException {
        try {
            consumer.sign(request);
        } catch (OAuthException oaex) {
            throw new HttpException("OAUTH failed to secure request", oaex);
        }
    }
}
