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

import static org.testng.Assert.assertEquals;
import static com.comcast.drivethru.utils.Method.DELETE;
import static com.comcast.drivethru.utils.Method.GET;
import static com.comcast.drivethru.utils.Method.POST;
import static com.comcast.drivethru.utils.Method.PUT;

import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import com.comcast.drivethru.misc.CatPhoto;
import com.comcast.pantry.test.RandomProvider;
import com.comcast.pantry.test.TestList;

public class RecordingRestClientTest {

    private static final String BASE_URL = "http://cheezburger.com";
    private static final RandomProvider RANDOM = new RandomProvider(65465487451850l);

    @Test(dataProvider = "getRecorders")
    public void testExpect(RecordingRestClient client) {
        ResponseBuilder builder = client.expect();

        assertEquals(builder.matches(RANDOM.nextString(5, 20), GET), true);
        assertEquals(builder.matches(RANDOM.nextString(5, 20), POST), true);
        assertEquals(builder.matches(RANDOM.nextString(5, 20), PUT), true);
        assertEquals(builder.matches(RANDOM.nextString(5, 20), DELETE), true);
    }

    @Test(dataProvider = "getRecorders")
    public void testExpectRegex(RecordingRestClient client) {
        ResponseBuilder builder = client.expect(".*");

        assertEquals(builder.matches(RANDOM.nextString(5, 20), GET), true);
        assertEquals(builder.matches(RANDOM.nextString(5, 20), POST), true);
        assertEquals(builder.matches(RANDOM.nextString(5, 20), PUT), true);
        assertEquals(builder.matches(RANDOM.nextString(5, 20), DELETE), true);
    }

    @Test(dataProvider = "getRecorders")
    public void testExpectString(RecordingRestClient client) {
        ResponseBuilder builder = client.expect("http://1.com", false);

        assertEquals(builder.matches("http://1.com", GET), true);
        assertEquals(builder.matches("http://1.com", POST), true);
        assertEquals(builder.matches("http://1.com", PUT), true);
        assertEquals(builder.matches("http://1.com", DELETE), true);

        assertEquals(builder.matches("http://2.com", GET), false);
        assertEquals(builder.matches("http://2.com", POST), false);
        assertEquals(builder.matches("http://2.com", PUT), false);
        assertEquals(builder.matches("http://2.com", DELETE), false);
    }

    @Test(dataProvider = "getRecorders")
    public void testExpectWithMethod(RecordingRestClient client) {
        ResponseBuilder builder = client.expect(POST);

        assertEquals(builder.matches(RANDOM.nextString(5, 20), GET), false);
        assertEquals(builder.matches(RANDOM.nextString(5, 20), POST), true);
        assertEquals(builder.matches(RANDOM.nextString(5, 20), PUT), false);
        assertEquals(builder.matches(RANDOM.nextString(5, 20), DELETE), false);
    }

    @Test(dataProvider = "getRecorders")
    public void testExpectRegexWithMethod(RecordingRestClient client) {
        ResponseBuilder builder = client.expect(PUT, ".*");

        assertEquals(builder.matches(RANDOM.nextString(5, 20), GET), false);
        assertEquals(builder.matches(RANDOM.nextString(5, 20), POST), false);
        assertEquals(builder.matches(RANDOM.nextString(5, 20), PUT), true);
        assertEquals(builder.matches(RANDOM.nextString(5, 20), DELETE), false);
    }

    @Test(dataProvider = "getRecorders")
    public void testExpectStringWithMethod(RecordingRestClient client) {
        ResponseBuilder builder = client.expect(DELETE, "http://1.com", false);

        assertEquals(builder.matches("http://1.com", GET), false);
        assertEquals(builder.matches("http://1.com", POST), false);
        assertEquals(builder.matches("http://1.com", PUT), false);
        assertEquals(builder.matches("http://1.com", DELETE), true);

        assertEquals(builder.matches("http://2.com", GET), false);
        assertEquals(builder.matches("http://2.com", POST), false);
        assertEquals(builder.matches("http://2.com", PUT), false);
        assertEquals(builder.matches("http://2.com", DELETE), false);
    }

    @DataProvider
    public TestList getRecorders() {
        TestList list = new TestList();

        list.add(new MockRestClient(BASE_URL));
        list.add(new MockEasyRestClient<>(CatPhoto.class, BASE_URL));

        return list;
    }
}
