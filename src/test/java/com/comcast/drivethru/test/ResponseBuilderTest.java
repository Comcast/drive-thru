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
import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertSame;
import static org.testng.Assert.assertTrue;

import org.testng.annotations.Test;

import com.comcast.drivethru.exception.HttpException;
import com.comcast.drivethru.utils.Method;
import com.comcast.drivethru.utils.RestResponse;

public class ResponseBuilderTest {

    @Test
    public void testMatchCountLimit() throws HttpException {
        ResponseBuilder builder = new ResponseBuilder();
        builder.once().andReturn(200);

        assertTrue(builder.matches("asuj9p08jr32;d", Method.GET));

        builder.replay();
        assertFalse(builder.matches("asuj9p08jr32;d", Method.GET));
    }

    @Test(timeOut = 500)
    public void testWithDelay() throws HttpException {
        ResponseBuilder builder = new ResponseBuilder();
        builder.andReturn(202).after(80);

        RestResponse response = builder.replay();
        assertEquals(response.getStatusCode(), 202);
    }

    @Test
    public void testWithThrow() throws HttpException {
        HttpException expected = new HttpException("Some message");

        ResponseBuilder builder = new ResponseBuilder();
        builder.andThrow(expected);

        HttpException actual = null;
        try {
            builder.replay();
        } catch (HttpException caught) {
            actual = caught;
        }

        assertSame(actual, expected);
    }
}
