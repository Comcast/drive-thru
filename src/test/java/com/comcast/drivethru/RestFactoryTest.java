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

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;

import org.testng.annotations.Test;

import com.comcast.drivethru.misc.CatPhoto;

public class RestFactoryTest {

    private static final String BASE_URL = "http://cheezburger.com";

    @Test
    public void testGetRestClient() {
        RestFactory factory = new RestFactory();
        RestClient client = factory.getClient(BASE_URL);

        assertNotNull(client);
        assertEquals(client.getDefaultBaseUrl(), BASE_URL);
    }

    @Test
    public void testGetEasyRestClient() {
        RestFactory factory = new RestFactory();
        EasyRestClient<CatPhoto> client = factory.getEasyClient(CatPhoto.class, BASE_URL);

        assertNotNull(client);
        assertEquals(client.getDefaultBaseUrl(), BASE_URL);
    }
}
