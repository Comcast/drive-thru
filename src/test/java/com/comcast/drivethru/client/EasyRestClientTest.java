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

import static com.comcast.drivethru.test.MockRestFactory.getMockEasyClient;
import static com.comcast.drivethru.utils.Method.GET;
import static org.testng.Assert.assertEquals;

import org.testng.annotations.Test;

import com.comcast.drivethru.exception.HttpException;
import com.comcast.drivethru.exception.HttpStatusException;
import com.comcast.drivethru.misc.CatPhoto;
import com.comcast.drivethru.test.MockEasyRestClient;
import com.comcast.drivethru.utils.URL;
import com.comcast.pantry.test.RandomProvider;

public class EasyRestClientTest {

    private static final String BASE_URL = "http://cheezburger.com";
    private static final RandomProvider RANDOM = new RandomProvider(29837498734852l);

    /* START GET TESTS */

    @Test
    public void testGetPath() throws Exception {
        CatPhoto photo = new CatPhoto().randomize(RANDOM);

        MockEasyRestClient<CatPhoto> client = getMockEasyClient(CatPhoto.class, BASE_URL);
        client.expect(GET, ".*/stuff").andReturn(200).withJsonBody(photo);

        CatPhoto actual = client.get("/stuff");
        assertEquals(actual, photo);

        client.close();
    }

    @Test
    public void testGetURL() throws Exception {
        CatPhoto photo = new CatPhoto().randomize(RANDOM);

        MockEasyRestClient<CatPhoto> client = getMockEasyClient(CatPhoto.class, BASE_URL);
        client.expect(GET, ".*/stuff\\?a=2").andReturn(200).withJsonBody(photo);

        URL url = new URL().setPath("/stuff").addQuery("a", "2");
        CatPhoto actual = client.get(url);
        assertEquals(actual, photo);

        client.close();
    }

    @Test(expectedExceptions = HttpStatusException.class)
    public void testGetNon200Response() throws Exception {
        CatPhoto photo = new CatPhoto().randomize(RANDOM);

        MockEasyRestClient<CatPhoto> client = getMockEasyClient(CatPhoto.class, BASE_URL);
        client.expect(GET, ".*/stuff\\?a=2").andReturn(201).withJsonBody(photo);

        try {
            URL url = new URL().setPath("/stuff").addQuery("a", "2");
            client.get(url);
        } finally {
            client.close();
        }
    }

    @Test(expectedExceptions = HttpException.class)
    public void testGetWrongContentType() throws Exception {
        CatPhoto photo = new CatPhoto().randomize(RANDOM);

        MockEasyRestClient<CatPhoto> client = getMockEasyClient(CatPhoto.class, BASE_URL);
        client.expect(GET, ".*/stuff\\?a=2").andReturn(200).withJsonBody(photo).withHeader("Content-Type", "text/plain");

        try {
            URL url = new URL().setPath("/stuff").addQuery("a", "2");
            client.get(url);
        } finally {
            client.close();
        }
    }
}
