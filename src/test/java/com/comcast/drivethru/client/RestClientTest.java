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

import static com.comcast.drivethru.test.MockRestFactory.getMockClient;
import static com.comcast.drivethru.utils.Method.DELETE;
import static com.comcast.drivethru.utils.Method.GET;
import static com.comcast.drivethru.utils.Method.POST;
import static com.comcast.drivethru.utils.Method.PUT;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNull;

import org.apache.http.HttpStatus;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import com.comcast.drivethru.exception.HttpException;
import com.comcast.drivethru.exception.HttpStatusException;
import com.comcast.drivethru.misc.CatPhoto;
import com.comcast.drivethru.test.MockRestClient;
import com.comcast.drivethru.utils.URL;
import com.comcast.pantry.test.RandomProvider;
import com.comcast.pantry.test.TestList;

public class RestClientTest {

    private static final String BASE_URL = "http://cheezburger.com";
    private static final RandomProvider RANDOM = new RandomProvider(29837498734852l);

    /* START GET TESTS */

    @Test
    public void testGetPath() throws Exception {
        CatPhoto photo = new CatPhoto().randomize(RANDOM);

        MockRestClient client = getMockClient(BASE_URL);
        client.expect(GET, ".*/stuff").andReturn(200).withJsonBody(photo);

        CatPhoto actual = client.get("/stuff", CatPhoto.class);
        assertEquals(actual, photo);

        client.close();
    }

    @Test
    public void testGetURL() throws Exception {
        CatPhoto photo = new CatPhoto().randomize(RANDOM);

        MockRestClient client = getMockClient(BASE_URL);
        client.expect(GET, ".*/stuff\\?a=2").andReturn(200).withJsonBody(photo);

        URL url = new URL().setPath("/stuff").addQuery("a", "2");
        CatPhoto actual = client.get(url, CatPhoto.class);
        assertEquals(actual, photo);

        client.close();
    }

    @Test(expectedExceptions = HttpStatusException.class)
    public void testGetNon200Response() throws Exception {
        CatPhoto photo = new CatPhoto().randomize(RANDOM);

        MockRestClient client = getMockClient(BASE_URL);
        client.expect(GET, ".*/stuff\\?a=2").andReturn(201).withJsonBody(photo);

        try {
            URL url = new URL().setPath("/stuff").addQuery("a", "2");
            client.get(url, CatPhoto.class);
        } finally {
            client.close();
        }
    }

    @Test(expectedExceptions = HttpException.class)
    public void testGetWrongContentType() throws Exception {
        CatPhoto photo = new CatPhoto().randomize(RANDOM);

        MockRestClient client = getMockClient(BASE_URL);
        client.expect(GET, ".*/stuff\\?a=2").andReturn(200).withJsonBody(photo)
            .withHeader("Content-Type", "text/plain");

        try {
            URL url = new URL().setPath("/stuff").addQuery("a", "2");
            client.get(url, CatPhoto.class);
        } finally {
            client.close();
        }
    }

    /* START POST TESTS */

    @Test
    public void testPostPath() throws Exception {
        CatPhoto photo = new CatPhoto().randomize(RANDOM);

        MockRestClient client = getMockClient(BASE_URL);
        client.expect(POST, ".*/stuff").andReturn(200).withJsonBody(photo);

        CatPhoto actual = client.post("/stuff", CatPhoto.class);
        assertEquals(actual, photo);

        client.close();
    }

    @Test
    public void testPostPathWithPayload() throws Exception {
        CatPhoto photo = new CatPhoto().randomize(RANDOM);

        MockRestClient client = getMockClient(BASE_URL);
        client.expect(POST, ".*/post").andReturn(200).withJsonBody(true);

        boolean actual = client.post("/post", photo, boolean.class);
        assertEquals(actual, true);

        client.close();
    }

    @Test
    public void testPostPathWithoutResponse() throws Exception {
        CatPhoto photo = new CatPhoto().randomize(RANDOM);

        MockRestClient client = getMockClient(BASE_URL);
        client.expect(POST, ".*/post").andReturn(200);

        Object actual = client.post("/post", photo, void.class);
        assertNull(actual);

        client.close();
    }

    @Test
    public void testPostURL() throws Exception {
        CatPhoto photo = new CatPhoto().randomize(RANDOM);

        MockRestClient client = getMockClient(BASE_URL);
        client.expect(POST, ".*/stuff\\?a=2").andReturn(200).withJsonBody(photo);

        URL url = new URL().setPath("/stuff").addQuery("a", "2");
        CatPhoto actual = client.post(url, CatPhoto.class);
        assertEquals(actual, photo);

        client.close();
    }

    @Test(dataProvider = "validPostStatusCodes")
    public void testPostAllValidStatusCodes(int status) throws Exception {
        CatPhoto photo = new CatPhoto().randomize(RANDOM);

        MockRestClient client = getMockClient(BASE_URL);
        client.expect(POST, ".*/stuff").andReturn(status).withJsonBody(photo);

        CatPhoto actual = client.post("/stuff", CatPhoto.class);
        assertEquals(actual, photo);

        client.close();
    }

    @DataProvider(name = "validPostStatusCodes")
    public TestList getValidPostStatusCodes() {
        TestList tests = new TestList();

        tests.add(HttpStatus.SC_OK);
        tests.add(HttpStatus.SC_CREATED);
        tests.add(HttpStatus.SC_NO_CONTENT);

        return tests;
    }

    @Test(expectedExceptions = HttpStatusException.class)
    public void testPostIllegalResponseCode() throws Exception {
        CatPhoto photo = new CatPhoto().randomize(RANDOM);

        MockRestClient client = getMockClient(BASE_URL);
        client.expect(POST, ".*/stuff\\?a=2").andReturn(500).withJsonBody(photo);

        try {
            URL url = new URL().setPath("/stuff").addQuery("a", "2");
            client.post(url, CatPhoto.class);
        } finally {
            client.close();
        }
    }

    @Test(expectedExceptions = HttpException.class)
    public void testPostWrongContentType() throws Exception {
        CatPhoto photo = new CatPhoto().randomize(RANDOM);

        MockRestClient client = getMockClient(BASE_URL);
        client.expect(POST, ".*/stuff\\?a=2").andReturn(200).withJsonBody(photo)
            .withHeader("Content-Type", "text/plain");

        try {
            URL url = new URL().setPath("/stuff").addQuery("a", "2");
            client.post(url, CatPhoto.class);
        } finally {
            client.close();
        }
    }

    /* START PUT TESTS */

    @Test
    public void testPutPath() throws Exception {
        CatPhoto photo = new CatPhoto().randomize(RANDOM);

        MockRestClient client = getMockClient(BASE_URL);
        client.expect(PUT, ".*/stuff").andReturn(201);

        boolean actual = client.put("/stuff", photo);
        assertEquals(actual, true);

        client.close();
    }

    @Test
    public void testPutURL() throws Exception {
        CatPhoto photo = new CatPhoto().randomize(RANDOM);

        MockRestClient client = getMockClient(BASE_URL);
        client.expect(PUT, ".*/stuff\\?a=2").andReturn(201);

        URL url = new URL().setPath("/stuff").addQuery("a", "2");
        boolean actual = client.put(url, photo);
        assertEquals(actual, true);

        client.close();
    }

    @Test(dataProvider = "validPutStatusCodes")
    public void testPutAllValidStatusCodes(int status, boolean expected) throws Exception {
        CatPhoto photo = new CatPhoto().randomize(RANDOM);

        MockRestClient client = getMockClient(BASE_URL);
        client.expect(PUT, ".*/stuff").andReturn(status);

        boolean actual = client.put("/stuff", photo);
        assertEquals(actual, expected);

        client.close();
    }

    @DataProvider(name = "validPutStatusCodes")
    public TestList getValidPutStatusCodes() {
        TestList tests = new TestList();

        tests.add(HttpStatus.SC_CREATED, true);
        tests.add(HttpStatus.SC_OK, false);
        tests.add(HttpStatus.SC_NO_CONTENT, false);

        return tests;
    }

    @Test(expectedExceptions = HttpStatusException.class)
    public void testPutIllegalResponseCode() throws Exception {
        CatPhoto photo = new CatPhoto().randomize(RANDOM);

        MockRestClient client = getMockClient(BASE_URL);
        client.expect(PUT, ".*/stuff\\?a=2").andReturn(500);

        try {
            URL url = new URL().setPath("/stuff").addQuery("a", "2");
            client.put(url, photo);
        } finally {
            client.close();
        }
    }

    /* START DELETE TESTS */

    @Test
    public void testDeletePath() throws Exception {
        MockRestClient client = getMockClient(BASE_URL);
        client.expect(DELETE, ".*/stuff").andReturn(200);

        boolean actual = client.delete("/stuff");
        assertEquals(actual, true);

        client.close();
    }

    @Test
    public void testDeleteURL() throws Exception {
        MockRestClient client = getMockClient(BASE_URL);
        client.expect(DELETE, ".*/stuff\\?a=2").andReturn(200);

        URL url = new URL().setPath("/stuff").addQuery("a", "2");
        boolean actual = client.delete(url);
        assertEquals(actual, true);

        client.close();
    }

    @Test(dataProvider = "validDeleteStatusCodes")
    public void testDeleteAllValidStatusCodes(int status, boolean expected) throws Exception {
        MockRestClient client = getMockClient(BASE_URL);
        client.expect(DELETE, ".*/stuff").andReturn(status);

        boolean actual = client.delete("/stuff");
        assertEquals(actual, expected);

        client.close();
    }

    @DataProvider(name = "validDeleteStatusCodes")
    public TestList getValidDeleteStatusCodes() {
        TestList tests = new TestList();

        tests.add(HttpStatus.SC_OK, true);
        tests.add(HttpStatus.SC_ACCEPTED, false);
        tests.add(HttpStatus.SC_NO_CONTENT, false);

        return tests;
    }

    @Test(expectedExceptions = HttpStatusException.class)
    public void testDeleteIllegalResponseCode() throws Exception {
        MockRestClient client = getMockClient(BASE_URL);
        client.expect(DELETE, ".*/stuff\\?a=2").andReturn(500);

        try {
            URL url = new URL().setPath("/stuff").addQuery("a", "2");
            client.delete(url);
        } finally {
            client.close();
        }
    }
}
