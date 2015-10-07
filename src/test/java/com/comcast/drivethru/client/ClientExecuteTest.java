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

import static com.comcast.drivethru.utils.Method.GET;
import static com.comcast.drivethru.utils.Method.POST;
import static org.easymock.EasyMock.capture;
import static org.easymock.EasyMock.createMock;
import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.expectLastCall;
import static org.easymock.EasyMock.replay;
import static org.easymock.EasyMock.verify;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.assertSame;
import static org.testng.Assert.assertTrue;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.io.IOUtils;
import org.apache.http.HttpEntity;
import org.apache.http.ProtocolVersion;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.HttpResponseException;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ByteArrayEntity;
import org.apache.http.message.BasicHttpResponse;
import org.easymock.Capture;
import org.easymock.EasyMock;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import com.comcast.drivethru.exception.HttpException;
import com.comcast.drivethru.exception.HttpStatusException;
import com.comcast.drivethru.security.SecurityProvider;
import com.comcast.drivethru.utils.RestRequest;
import com.comcast.drivethru.utils.RestResponse;
import com.comcast.drivethru.utils.URL;

public class ClientExecuteTest {

    @Test
    public void testExecuteSuccess() throws Exception {
        String body = "this is a response from the internet";
        HttpEntity entity = new ByteArrayEntity(body.getBytes());

        BasicHttpResponse resp = new BasicHttpResponse(new ProtocolVersion("HTTP", 1, 1), 200, "OK");
        resp.setHeader("a", "apple");
        resp.setHeader("b", "bobcat");
        resp.setHeader("Content-Type", "text/plain");
        resp.setEntity(entity);

        RestRequest request = new RestRequest(new URL().setPath("/").addQuery("q", "stuff"), POST);
        request.setContentType("application/json");
        request.setBody("{ \"name\" : \"Clark\" }");
        request.addHeader("x-transaction-id", "0011223344556677");

        Capture<HttpPost> capture = EasyMock.newCapture();
        Capture<HttpPost> capture2 = EasyMock.newCapture();

        HttpClient delegate = createMock(HttpClient.class);
        expect(delegate.execute(capture(capture))).andReturn(resp);

        SecurityProvider securityProvider = createMock(SecurityProvider.class);
        securityProvider.sign(capture(capture2));
        expectLastCall();

        replay(delegate, securityProvider);

        String base = "http://www.google.com";
        DefaultRestClient client = new DefaultRestClient(base, delegate);
        client.addDefaultHeader("Fintan", "The Salmon of Knowledge");
        client.setSecurityProvider(securityProvider);

        RestResponse response = client.execute(request);
        client.close();

        assertEquals(response.getBodyString(), body);
        assertEquals(response.getStatusCode(), 200);
        assertEquals(response.getStatusMessage(), "OK");
        assertEquals(response.getContentType(), "text/plain");
        assertEquals(response.getHeaderValue("a"), "apple");
        assertEquals(response.getHeaderValue("b"), "bobcat");

        /* Verify the constructed request object */
        assertTrue(capture.hasCaptured());
        HttpPost req = capture.getValue();
        assertEquals(req.getLastHeader("Content-Type").getValue(), "application/json");
        assertEquals(req.getLastHeader("x-transaction-id").getValue(), "0011223344556677");
        assertEquals(req.getLastHeader("Fintan").getValue(), "The Salmon of Knowledge");
        assertEquals(req.getURI().toString(), "http://www.google.com/?q=stuff");

        /* Verify the "aborted" status of the request */
        assertEquals(req.isAborted(), true);

        /* Read and verify the contents of the request */
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        IOUtils.copy(req.getEntity().getContent(), baos);
        assertEquals(baos.toString(), "{ \"name\" : \"Clark\" }");

        /* Verify Security was called with same element */
        assertTrue(capture2.hasCaptured());
        assertSame(capture2.getValue(), req);

        verify(delegate, securityProvider);
    }

    @SuppressWarnings("resource")
    @Test(expectedExceptions = HttpException.class)
    public void testExecuteAttachBodyToGet() throws Exception {
        RestRequest request = new RestRequest(new URL().setPath("/").addQuery("q", "stuff"), GET);
        request.setContentType("application/json");
        request.setBody("{ \"name\" : \"Clark\" }");
        request.addHeader("x-transaction-id", "0011223344556677");

        HttpClient delegate = createMock(HttpClient.class);
        SecurityProvider securityProvider = createMock(SecurityProvider.class);

        replay(delegate, securityProvider);

        String base = "http://www.google.com";
        DefaultRestClient client = new DefaultRestClient(base, delegate);
        client.addDefaultHeader("Fintan", "The Salmon of Knowledge");
        client.setSecurityProvider(securityProvider);

        client.execute(request);
    }

    @DataProvider(name = "exceptionTests")
    public Iterator<Object[]> getExceptionTests() {
        List<Object[]> tests = new ArrayList<>();

        tests.add(new Object[] { new RuntimeException("example"), RuntimeException.class, "example" });
        tests.add(new Object[] { new HttpResponseException(500, "Internal Server Error"),
            HttpStatusException.class, "500" });
        tests.add(new Object[] { new ClientProtocolException(), HttpException.class,
            "HTTP Protocol" });
        tests.add(new Object[] { new IOException(), HttpException.class,
            "Error establishing connection" });

        return tests.iterator();
    }

    @Test(dataProvider = "exceptionTests")
    public void testExecuteExceptions(Throwable thrown, Class<?> expected, String messagePart) throws Exception {

        RestRequest request = new RestRequest(new URL().setPath("/").addQuery("q", "stuff"), POST);
        request.setContentType("application/json");
        request.setBody("{ \"name\" : \"Clark\" }");
        request.addHeader("x-transaction-id", "0011223344556677");

        Capture<HttpPost> capture = EasyMock.newCapture();
        Capture<HttpPost> capture2 = EasyMock.newCapture();

        HttpClient delegate = createMock(HttpClient.class);
        expect(delegate.execute(capture(capture))).andThrow(thrown);

        SecurityProvider securityProvider = createMock(SecurityProvider.class);
        securityProvider.sign(capture(capture2));
        expectLastCall();

        replay(delegate, securityProvider);

        String base = "http://www.google.com";
        DefaultRestClient client = new DefaultRestClient(base, delegate);
        client.addDefaultHeader("Fintan", "The Salmon of Knowledge");
        client.setSecurityProvider(securityProvider);

        Throwable actual = null;
        try {
            client.execute(request);
        } catch (Throwable t) {
            actual = t;
        } finally {
            client.close();
        }

        /* Verify the thrown exception */
        assertNotNull(actual);
        assertEquals(actual.getClass(), expected);
        assertTrue(actual.getMessage().contains(messagePart));

        /* Verify the constructed request object */
        assertTrue(capture.hasCaptured());
        HttpPost req = capture.getValue();
        assertEquals(req.getLastHeader("Content-Type").getValue(), "application/json");
        assertEquals(req.getLastHeader("x-transaction-id").getValue(), "0011223344556677");
        assertEquals(req.getLastHeader("Fintan").getValue(), "The Salmon of Knowledge");
        assertEquals(req.getURI().toString(), "http://www.google.com/?q=stuff");

        /* Verify the "aborted" status of the request */
        assertEquals(req.isAborted(), true);

        /* Read and verify the contents of the request */
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        IOUtils.copy(req.getEntity().getContent(), baos);
        assertEquals(baos.toString(), "{ \"name\" : \"Clark\" }");

        /* Verify Security was called with same element */
        assertTrue(capture2.hasCaptured());
        assertSame(capture2.getValue(), req);

        verify(delegate, securityProvider);
    }
}
