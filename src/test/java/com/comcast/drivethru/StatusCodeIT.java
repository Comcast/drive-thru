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

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.http.client.HttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.comcast.drivethru.client.DefaultRestClient;
import com.comcast.drivethru.exception.HttpException;
import com.comcast.drivethru.utils.Method;
import com.comcast.drivethru.utils.RestRequest;
import com.comcast.drivethru.utils.RestResponse;

/**
 * This Class is intentionally disabled because we don't need to run this constantly and it requires
 * real Internet traffic. We disable by commenting out the TestNG annotations.
 *
 * @author <a href="mailto:clark_malmgren@cable.comcast.com">Clark Malmgren</a>
 */
public class StatusCodeIT {

    private static final Logger LOGGER = LoggerFactory.getLogger(StatusCodeIT.class);

    private DefaultRestClient client;

    // @BeforeClass
    public void setupClient() {
        HttpClient client = HttpClientBuilder.create().disableRedirectHandling().build();
        this.client = new DefaultRestClient("http://httpstat.us/", client);
    }

    // @AfterClass
    public void closeClient() throws IOException {
        this.client.close();
    }

    // @Test(dataProvider = "statusCodes")
    public void test(int code, String message) throws HttpException, IOException {
        RestRequest request = new RestRequest(Integer.toString(code), Method.GET);

        RestResponse response = client.execute(request);
        assertEquals(response.getStatusCode(), code);

        if (!response.getStatusMessage().equalsIgnoreCase(message)) {
            LOGGER.warn("Message Mismatch for Status [" + code + "]: found '"
                    + response.getStatusMessage() + "', expected '" + message + "'");
        }
    }

    // @DataProvider(name = "statusCodes")
    public Iterator<Object[]> getStatusCodes() {
        List<Object[]> tests = new ArrayList<>();

        tests.add(new Object[] { 200, "OK" });
        tests.add(new Object[] { 201, "Created" });
        tests.add(new Object[] { 202, "Accepted" });
        tests.add(new Object[] { 203, "Non-Authoritative Information" });
        tests.add(new Object[] { 204, "No Content" });
        tests.add(new Object[] { 205, "Reset Content" });
        tests.add(new Object[] { 206, "Partial Content" });
        tests.add(new Object[] { 300, "Multiple Choices" });
        tests.add(new Object[] { 301, "Moved Permanently" });
        tests.add(new Object[] { 302, "Found" });
        tests.add(new Object[] { 303, "See Other" });
        tests.add(new Object[] { 304, "Not Modified" });
        tests.add(new Object[] { 305, "Use Proxy" });
        tests.add(new Object[] { 306, "Unused" });
        tests.add(new Object[] { 307, "Temporary Redirect" });
        tests.add(new Object[] { 400, "Bad Request" });
        tests.add(new Object[] { 401, "Unauthorized" });
        tests.add(new Object[] { 402, "Payment Required" });
        tests.add(new Object[] { 403, "Forbidden" });
        tests.add(new Object[] { 404, "Not Found" });
        tests.add(new Object[] { 405, "Method Not Allowed" });
        tests.add(new Object[] { 406, "Not Acceptable" });

        /* 407 is not supported because we don't support authentication at all */
        /* tests.add(new Object[] { 407, "Proxy Authentication Required" }); */

        tests.add(new Object[] { 408, "Request Timeout" });
        tests.add(new Object[] { 409, "Conflict" });
        tests.add(new Object[] { 410, "Gone" });
        tests.add(new Object[] { 411, "Length Required" });
        tests.add(new Object[] { 412, "Precondition Required" });
        tests.add(new Object[] { 413, "Request Entity Too Large" });
        tests.add(new Object[] { 414, "Request-URI Too Long" });
        tests.add(new Object[] { 415, "Unsupported Media Type" });
        tests.add(new Object[] { 416, "Requested Range Not Satisfiable" });
        tests.add(new Object[] { 417, "Expectation Failed" });
        tests.add(new Object[] { 418, "I'm a Teapot" });
        tests.add(new Object[] { 500, "Internal Server Error" });
        tests.add(new Object[] { 501, "Not Implemented" });
        tests.add(new Object[] { 502, "Bad Gateway" });
        tests.add(new Object[] { 503, "Service Unavailable" });
        tests.add(new Object[] { 504, "Gateway Timeout" });
        tests.add(new Object[] { 505, "HTTP Version Not Supported" });

        return tests.iterator();
    }
}
