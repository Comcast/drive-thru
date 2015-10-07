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

import static org.easymock.EasyMock.createMock;
import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.replay;
import static org.easymock.EasyMock.verify;
import oauth.signpost.OAuthConsumer;
import oauth.signpost.exception.OAuthMessageSignerException;

import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpUriRequest;
import org.testng.annotations.Test;

import com.comcast.drivethru.exception.HttpException;

public class OAuthSecurityProviderTest {

    @Test
    public void testConstructor() throws HttpException {
        OAuthSecurityProvider provider = new OAuthSecurityProvider("key", "secret");

        /* Prove that it works by decorating a really simple HttpGet */
        HttpGet get = new HttpGet("https://www.google.com");
        provider.sign(get);
    }

    @Test
    public void testDelegation() throws Exception {
        HttpUriRequest request = createMock(HttpUriRequest.class);
        OAuthConsumer consumer = createMock(OAuthConsumer.class);
        expect(consumer.sign(request)).andReturn(null).once();

        replay(request, consumer);

        OAuthSecurityProvider provider = new OAuthSecurityProvider(consumer);
        provider.sign(request);

        verify(request, consumer);
    }

    @Test(expectedExceptions = HttpException.class)
    public void testWrappingException() throws Exception {
        HttpUriRequest request = createMock(HttpUriRequest.class);
        OAuthConsumer consumer = createMock(OAuthConsumer.class);
        expect(consumer.sign(request)).andThrow(new OAuthMessageSignerException("oops!")).once();

        replay(request, consumer);

        OAuthSecurityProvider provider = new OAuthSecurityProvider(consumer);
        provider.sign(request);
    }
}
