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
/**
 * @author Dmitry Jerusalimsky
 */

package com.comcast.drivethru.test;

import java.io.IOException;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;

import org.junit.Assert;
import org.junit.Test;

import com.comcast.drivethru.api.HTTPRequestManager;
import com.comcast.drivethru.api.HTTPRequestManager.METHOD;
import com.comcast.drivethru.constants.ServerStatusCodes;
import com.comcast.drivethru.model.ResponseContainer;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

public class HTTPRequestManagerTest
{
    private static final String BASE_URL = "http://dawg-house-dev.cvs-a.ula.comcast.net:8080/dawg-house";

    private static final JsonObject NEW_DEVICE_JSON;
    static
    {
        NEW_DEVICE_JSON = new JsonObject();
        NEW_DEVICE_JSON.addProperty("id", "testdevice");
        NEW_DEVICE_JSON.addProperty("name", "test device 123");
    }

    /**
     * Test a request with a missing url.
     * @author Dmitry Jerusalimsky
     * @throws KeyManagementException
     * @throws NoSuchAlgorithmException
     * @throws IOException
     */
    @Test(expected = IllegalStateException.class)
    public void testRequestNoURL() throws KeyManagementException, NoSuchAlgorithmException, IOException
    {
        HTTPRequestManager manager = new HTTPRequestManager.Builder().build();
        manager.sendRequest();
    }

    /**
     * Test a POST request.
     * @author Dmitry Jerusalimsky
     * @throws KeyManagementException
     * @throws IOException
     * @throws NoSuchAlgorithmException
     */
    @Test
    public void sendPost() throws KeyManagementException, IOException, NoSuchAlgorithmException
    {
        // make sure that something is in the dawg-house
        sendPut();

        String url = BASE_URL + "/devices/id";
        String contentType = "application/x-www-form-urlencoded";
        String data = "id=testdevice";

        HTTPRequestManager manager = new HTTPRequestManager.Builder()
            .url(url)
            .data(data)
            .contentType(contentType)
            .method(METHOD.POST)
            .build();

        ResponseContainer response = manager.sendRequest();
        Assert.assertEquals("Status code wasn't 200", ServerStatusCodes.OK, response.getStatusCode());

        String responseBody = response.getResponseBody();
        JsonObject jsonObject = null;
        JsonParser parser = new JsonParser();
        if (responseBody.startsWith("["))
        {
            jsonObject = (JsonObject) ((JsonArray) parser.parse(responseBody)).get(0);
        }
        else
        {

            jsonObject = (JsonObject) parser.parse(responseBody);
        }

        String name = jsonObject.get("name").getAsString();
        String id = jsonObject.get("id").getAsString();

        Assert.assertEquals("Device names didn't match", "test device 123", name);
        Assert.assertEquals("Device IDs didn't match", "testdevice", id);
    }

    /**
     * Test a PUT request.
     * @author Dmitry Jerusalimsky
     * @throws KeyManagementException
     * @throws NoSuchAlgorithmException
     * @throws IOException
     */
    @Test
    public void sendPut() throws KeyManagementException, NoSuchAlgorithmException, IOException
    {
        String url = BASE_URL + "/devices/id/testdevice";
        String contentType = "application/json";

        HTTPRequestManager manager = new HTTPRequestManager.Builder()
            .url(url)
            .data(NEW_DEVICE_JSON.toString())
            .contentType(contentType)
            .method(METHOD.PUT)
            .build();

        ResponseContainer response = manager.sendRequest();
        Assert.assertEquals("Status code wasn't 200", ServerStatusCodes.OK, response.getStatusCode());
    }

    /**
     * Test a GET request.
     * @author Dmitry Jerusalimsky
     * @throws KeyManagementException
     * @throws NoSuchAlgorithmException
     * @throws IOException
     * @throws InstantiationException
     * @throws IllegalAccessException
     */
    @Test
    public void testGetRequest() throws KeyManagementException, NoSuchAlgorithmException, IOException, InstantiationException, IllegalAccessException
    {
        // make sure that something is in the dawg-house
        sendPut();

        String url = BASE_URL + "/devices/id/testdevice";
        HTTPRequestManager manager = new HTTPRequestManager.Builder()
            .url(url)
            .build();

        ResponseContainer response = manager.sendRequest();
        Assert.assertEquals("Status code wasn't 200", ServerStatusCodes.OK, response.getStatusCode());

        JsonParser parser = new JsonParser();
        JsonObject responseBody = (JsonObject) parser.parse(response.getResponseBody());


        String name = responseBody.get("name").getAsString();
        String id = responseBody.get("id").getAsString();

        Assert.assertEquals("Device names didn't match", "test device 123", name);
        Assert.assertEquals("Device IDs didn't match", "testdevice", id);
    }

    /**
     * Test a DELETE request.
     * @author Dmitry Jerusalimsky
     * @throws KeyManagementException
     * @throws NoSuchAlgorithmException
     * @throws IOException
     */
    @Test
    public void testDeleteRequest() throws KeyManagementException, NoSuchAlgorithmException, IOException
    {
        // make sure there's something to delete in the dawg-house
        sendPut();

        String url = BASE_URL + "/devices/id/testdevice";
        HTTPRequestManager manager = new HTTPRequestManager.Builder()
            .url(url)
            .method(METHOD.DELETE)
            .build();

        ResponseContainer response = manager.sendRequest();
        Assert.assertEquals("Status code wasn't 200", ServerStatusCodes.OK, response.getStatusCode());
    }
}
