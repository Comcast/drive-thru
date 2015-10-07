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

import static org.testng.Assert.assertSame;

import org.testng.annotations.Test;

import com.comcast.drivethru.misc.CatPhoto;

public class MappedRestFactoryTest {

    @Test
    public void testMapping() {
        final String url1 = "http://1.com/";
        final String url2 = "http://2.com/";

        MappedRestFactory factory = new MappedRestFactory();
        MockRestClient client1 = factory.createMappedClient(url1);
        MockEasyRestClient<CatPhoto> client2 = factory.createMappedEasyClient(CatPhoto.class, url2);

        /* Check the mapping twice each */
        assertSame(factory.getClient(url1), client1);
        assertSame(factory.getClient(url1), client1);

        assertSame(factory.getEasyClient(CatPhoto.class, url2), client2);
        assertSame(factory.getEasyClient(CatPhoto.class, url2), client2);

        assertSame(factory.getClient(url2), client2);
        assertSame(factory.getClient(url2), client2);
    }
}
