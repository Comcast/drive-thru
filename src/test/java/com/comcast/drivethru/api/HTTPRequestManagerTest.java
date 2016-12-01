package com.comcast.drivethru.api;

import static org.testng.Assert.assertTrue;

import java.io.IOException;
import java.security.InvalidParameterException;

import org.apache.http.HttpEntity;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.impl.client.CloseableHttpClient;
import org.testng.annotations.Test;

import com.comcast.drivethru.model.ResponseContainer;

public class HTTPRequestManagerTest
{
    private static final String BASE_URL = "http://imateapot.com";
    
    private static final String K_MULTIPART_CALL = "multipart call";
    private static final String K_SIMPLE_CALL = "simple call";
    
    @Test
    public void testMultipartFormDataRequest() throws IOException
    {
        HttpEntity multipart = MultipartEntityBuilder.
                create().
                addTextBody("deviceId", "0123456789", ContentType.TEXT_PLAIN).
                addTextBody("metaData", "imjson", ContentType.TEXT_PLAIN).
                build();
        
        // Ensure that having multipart form data doesn't throw an exception
        HTTPRequestManagerMock manager = new HTTPRequestManagerMock.
                MockBuilder().
                url(BASE_URL).
                method("POST").
                contentType("multipart/form-data").
                multipart(multipart).
                build();
        
        ResponseContainer container =  manager.sendRequest();
        
        assertTrue(container.getResponseBody().equals(K_MULTIPART_CALL), "The wrong function was called for a multipart form data request");
    }
    
    @Test(expectedExceptions = InvalidParameterException.class)
    public void testMultipartFormDataRequestWithBadContentType() throws IOException
    {
        HttpEntity multipart = MultipartEntityBuilder.
                create().
                addTextBody("deviceId", "0123456789", ContentType.TEXT_PLAIN).
                addTextBody("metaData", "imjson", ContentType.TEXT_PLAIN).
                build();
        
        HTTPRequestManagerMock manager = new HTTPRequestManagerMock.
                MockBuilder().
                url(BASE_URL).
                method("POST").
                multipart(multipart).
                build();
        
        manager.sendRequest();
    }
    
    @Test
    public void testMultipartFormDataRequestWithNoData() throws IOException
    {
        HTTPRequestManagerMock manager = new HTTPRequestManagerMock.
                MockBuilder().
                url(BASE_URL).
                method("POST").
                contentType("multipart/form-data").
                build();
        
        ResponseContainer container = manager.sendRequest();
        
        assertTrue(!container.getResponseBody().equals(K_MULTIPART_CALL), "The multipart form data function was incorrectly called");
    }
    
    static class HTTPRequestManagerMock extends HTTPRequestManager
    {
        HTTPRequestManagerMock(Builder builder)
        {
            super(builder);
        }
        
        @Override
        ResponseContainer sendRequestWithMultipartData(CloseableHttpClient client, HttpUriRequest request) throws IOException
        {
            return new ResponseContainer(K_MULTIPART_CALL);
        }
        
        @Override
        ResponseContainer sendRequest(CloseableHttpClient client, Object request) throws IOException
        {
            return new ResponseContainer(K_SIMPLE_CALL);
        }
        
        static class MockBuilder extends Builder
        {
            public MockBuilder url(String url)
            {
                super.url(url);
                return this;
            }
            
            public MockBuilder method(String url)
            {
                super.url(url);
                return this;
            }
            
            public MockBuilder contentType(String contentType)
            {
                super.contentType(contentType);
                return this;
            }
            
            public MockBuilder multipart(HttpEntity multiPart)
            {
                super.multipart(multiPart);
                return this;
            }
            
            public HTTPRequestManagerMock build()
            {
                return new HTTPRequestManagerMock(this);
            }
        }
    }
}
