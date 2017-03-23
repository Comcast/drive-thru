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
package com.comcast.drivethru.utils;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertNull;
import static org.testng.Assert.assertTrue;

import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import com.comcast.drivethru.exception.HttpException;

public class URLTest {

    @Test
    public void testBuildingURL() throws HttpException {
        URL url = new URL();
        url.setBaseUrl("http://1.com");
        url.setPath("/add");
        url.addQuery("user", "my_name");
        url.addQuery("password", "s3cr3t");

        assertEquals(url.build(), "http://1.com/add?user=my_name&password=s3cr3t");
    }

    @Test
    public void testBuildURLAllParts() throws HttpException {
        URL url = new URL("http://1.com");
        url.addPath("users");
        url.addPath("list.json");
        url.addQuery("refresh", true);

        assertEquals(url.build(), "http://1.com/users/list.json?refresh=true");
    }

    @Test
    public void testBuildingURLWithoutPath() throws HttpException {
        URL url = new URL();
        url.setBaseUrl("http://1.com");
        url.addQuery("user", "my_name");
        url.addQuery("password", "s3cr3t");

        assertEquals(url.build(), "http://1.com?user=my_name&password=s3cr3t");
    }

    @Test
    public void testSetDefaultBaseUrl() {
        URL url = new URL();
        assertNull(url.getBaseUrl());
        assertFalse(url.hasBaseUrl());

        url.setDefaultBaseUrl("http://1.com");
        assertEquals(url.getBaseUrl(), "http://1.com");
        assertTrue(url.hasBaseUrl());

        url.setDefaultBaseUrl("http://2.com");
        assertEquals(url.getBaseUrl(), "http://1.com");
        assertTrue(url.hasBaseUrl());
    }

    @Test(expectedExceptions = HttpException.class)
    public void testBuildWithoutBaseURL() throws HttpException {
        new URL().build();
    }

    @Test
    public void testWithDuplicateSlashes() throws HttpException {
        URL url = new URL();
        url.setBaseUrl("http://1.com/");
        url.setPath("//path");

        assertEquals(url.build(), "http://1.com/path");
    }
    
    @DataProvider(name="testSetPathNoSlashData")
    public Object[][] testSetPathNoSlashData() {
        return new Object[][] { {true}, {false} };
    }
    
    @Test(dataProvider="testSetPathNoSlashData")
    public void testSetPathNoSlash(boolean baseUrlEndsInSlash) throws HttpException {
        URL url = new URL();
        url.setBaseUrl("http://1.com" + (baseUrlEndsInSlash ? "/" : ""));
        url.setPath("path/here");

        assertEquals(url.build(), "http://1.com/path/here");
    }
    
    @Test
    public void testSetNullPath() throws HttpException {
        URL url = new URL();
        url.setBaseUrl("http://1.com");
        url.setPath(null);

        assertEquals(url.build(), "http://1.com");
    }

    
    @Test(dataProvider="testSetPathNoSlashData")
    public void testAddPathNoSlash(boolean baseUrlEndsInSlash) throws HttpException {
        URL url = new URL();
        url.setBaseUrl("http://1.com" + (baseUrlEndsInSlash ? "/" : ""));
        url.addPath("path/here");

        assertEquals(url.build(), "http://1.com/path/here");
    }
}
