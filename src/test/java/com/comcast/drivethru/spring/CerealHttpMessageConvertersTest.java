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
package com.comcast.drivethru.spring;

import static org.easymock.EasyMock.createMock;
import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.replay;
import static org.easymock.EasyMock.verify;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Date;

import org.springframework.http.HttpInputMessage;
import org.springframework.http.HttpOutputMessage;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import com.comcast.pantry.test.RandomProvider;
import com.comcast.pantry.test.TestList;

public class CerealHttpMessageConvertersTest {

    private static final RandomProvider RANDOM = new RandomProvider(29387498273498l);

    @DataProvider(name = "converters")
    public TestList getConverters() {
        TestList list = new TestList();

        list.add(new JsonCerealHttpMessageConverter());
        list.add(new YamlCerealHttpMessageConverter());

        return list;
    }

    @Test(dataProvider = "converters")
    public void testSupportsTrue(AbstractCerealHttpMessageConverter converter) {
        assertTrue(converter.supports(CerealHttpMessageConvertersTest.class));
    }

    @Test(dataProvider = "converters")
    public void testConvertSucccess(AbstractCerealHttpMessageConverter converter) throws Exception {
        TestClass tc = new TestClass();
        tc.d = new Date(RANDOM.nextLong(0, Long.MAX_VALUE));
        tc.i = RANDOM.nextInt();
        tc.s = RANDOM.nextString(50, 100);

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        HttpOutputMessage outputMessage = createMock(HttpOutputMessage.class);
        expect(outputMessage.getBody()).andReturn(baos).anyTimes();

        replay(outputMessage);

        converter.writeInternal(tc, outputMessage);

        ByteArrayInputStream bais = new ByteArrayInputStream(baos.toByteArray());
        HttpInputMessage inputMessage = createMock(HttpInputMessage.class);
        expect(inputMessage.getBody()).andReturn(bais).anyTimes();

        replay(inputMessage);

        TestClass actual = (TestClass) converter.readInternal(TestClass.class, inputMessage);

        assertEquals(actual.d, tc.d);
        assertEquals(actual.i, tc.i);
        assertEquals(actual.s, tc.s);

        verify(outputMessage);
    }

    @Test(dataProvider = "converters", expectedExceptions = IOException.class)
    public void testReadFailure(AbstractCerealHttpMessageConverter converter) throws Exception {
        ByteArrayInputStream bais = new ByteArrayInputStream("{}".getBytes());
        HttpInputMessage inputMessage = createMock(HttpInputMessage.class);
        expect(inputMessage.getBody()).andReturn(bais).anyTimes();

        replay(inputMessage);

        converter.readInternal(TestClass.class, inputMessage);
    }

    public static class TestClass {
        Date d;
        int i;
        String s;
    }
}
