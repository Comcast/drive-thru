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
import static org.easymock.EasyMock.expectLastCall;
import static org.easymock.EasyMock.replay;
import static org.easymock.EasyMock.verify;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertSame;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletResponse;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.servlet.ModelAndView;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import com.comcast.pantry.test.TestList;

public class ExceptionHandlerTest {

    @Test
    public void testPriorityOrder() {
        ExceptionHandler handler = new ExceptionHandler();
        assertEquals(handler.getOrder(), Integer.MIN_VALUE);
    }

    @Test
    public void testGettersAndSetters() {
        ExceptionHandler handler = new ExceptionHandler();

        handler.setViewName("apple");
        assertEquals(handler.getViewName(), "apple");

        handler.setDefaultResponse(502);
        assertEquals(handler.getDefaultResponse(), 502);

        Map<Class<? extends Exception>, Integer> exceptionMap = new HashMap<>();
        handler.setExceptionMap(exceptionMap);
        assertSame(handler.getExceptionMap(), exceptionMap);
    }

    @DataProvider(name = "exceptions")
    public TestList getExceptions() {
        TestList list = new TestList();

        /* Tests without custom map */
        list.add(false, new Exception(), 500, "Internal Server Error");
        list.add(false, new TestException1(), 406, "Not Acceptable");
        list.add(false, new TestException2(), 418, "Magic and Bad Luck");
        list.add(false, new NullPointerException(), 500, "Internal Server Error");

        /* Tests with custom map */
        list.add(true, new Exception(), 500, "Internal Server Error");
        list.add(true, new TestException1(), 406, "Not Acceptable");
        list.add(true, new TestException2(), 418, "Magic and Bad Luck");
        list.add(true, new NullPointerException(), 404, "Not Found");

        return list;
    }

    @Test(dataProvider = "exceptions")
    public void testHandleException(boolean includeMap, Exception ex, int code, String reason) {
        HttpServletResponse response = createMock(HttpServletResponse.class);
        response.setStatus(code);
        expectLastCall();

        replay(response);


        ExceptionHandler handler = new ExceptionHandler();

        if (includeMap) {
            Map<Class<? extends Exception>, Integer> exceptionMap = new HashMap<>();
            exceptionMap.put(NullPointerException.class, 404);
            handler.setExceptionMap(exceptionMap);
        }

        ModelAndView mav = handler.resolveException(null, response, null, ex);

        assertEquals(mav.getViewName(), "error.html");
        assertEquals(mav.getModel().get("status"), code);
        assertEquals(mav.getModel().get("reason"), reason);
        assertSame(mav.getModel().get("exception"), ex);

        verify(response);
    }

    @SuppressWarnings("serial")
    @ResponseStatus(HttpStatus.NOT_ACCEPTABLE)
    class TestException1 extends Exception {

    }

    @SuppressWarnings("serial")
    @ResponseStatus(value = HttpStatus.I_AM_A_TEAPOT, reason = "Magic and Bad Luck")
    class TestException2 extends Exception {

    }
}
