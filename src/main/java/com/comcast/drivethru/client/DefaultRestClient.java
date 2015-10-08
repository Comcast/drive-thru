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

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.io.IOUtils;
import org.apache.http.HttpEntity;
import org.apache.http.HttpEntityEnclosingRequest;
import org.apache.http.HttpStatus;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.HttpResponseException;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.client.utils.HttpClientUtils;
import org.apache.http.config.SocketConfig;
import org.apache.http.entity.ByteArrayEntity;
import org.apache.http.impl.client.HttpClientBuilder;

import com.comcast.drivethru.RestClient;
import com.comcast.drivethru.exception.HttpException;
import com.comcast.drivethru.exception.HttpStatusException;
import com.comcast.drivethru.security.SecurityProvider;
import com.comcast.drivethru.transform.JsonTransformer;
import com.comcast.drivethru.transform.Transformer;
import com.comcast.drivethru.utils.Method;
import com.comcast.drivethru.utils.RestRequest;
import com.comcast.drivethru.utils.RestResponse;
import com.comcast.drivethru.utils.URL;

/**
 * The default implementation of a {@link RestClient}.
 *
 * @author <a href="mailto:cmalmgren@gmail.com">Clark Malmgren</a>
 * @author <a href="mailto:kevin_pearson@cable.comcast.com">Kevin Pearson</a>
 */
public class DefaultRestClient implements RestClient {

    private String defaultBaseUrl;
    private Transformer transformer;
    private HttpClient delegate;
    private SecurityProvider securityProvider;
    private Map<String, String> defaultHeaders;

    /**
     * Constructs a new {@link DefaultRestClient} with no <code>defaultBaseUrl</code> that will use
     * the the standard {@link JsonTransformer} and a default {@link HttpClient} with a default
     * timeout of 10 seconds ({@link RestClient#DEFAULT_TIMEOUT}).
     * <p>
     * Because no <code>defaultBaseUrl</code> has been defined, all executions must provide fully
     * defined {@link URL}s or they will fail. Use of this constructor is discouraged.
     * </p>
     */
    public DefaultRestClient() {
        this(null);
    }

    private static HttpClient defaultClient() {
        return HttpClientBuilder.create().setDefaultSocketConfig(SocketConfig.custom().setSoTimeout(DEFAULT_TIMEOUT).build()).build();
    }

    /**
     * Constructs a new {@link DefaultRestClient} with the given <code>defaultBaseUrl</code> that
     * will use the the standard {@link JsonTransformer} and a default {@link HttpClient} with a
     * default timeout of 10 seconds ({@link RestClient#DEFAULT_TIMEOUT}).
     *
     * @param defaultBaseUrl
     *            the default base URL used for this connection
     */
    public DefaultRestClient(String defaultBaseUrl) {
        this(defaultBaseUrl, new JsonTransformer());
    }

    /**
     * Constructs a new {@link DefaultRestClient} with the given <code>defaultBaseUrl</code> that
     * will use the given {@link Transformer} and a default {@link HttpClient} with a default
     * timeout of 10 seconds ({@link RestClient#DEFAULT_TIMEOUT}).
     *
     * @param defaultBaseUrl
     *            the default base URL used for this connection
     * @param transformer
     *            the transformer for handling serialization of HTTP body contents
     */
    public DefaultRestClient(String defaultBaseUrl, Transformer transformer) {
        this(defaultBaseUrl, transformer, defaultClient());
    }

    /**
     * Constructs a new {@link DefaultRestClient} with the given <code>defaultBaseUrl</code> that
     * will use the given {@link HttpClient} and the standard {@link JsonTransformer}.
     *
     * @param defaultBaseUrl
     *            the default base URL used for this connection
     * @param delegate
     *            the inner HTTP connection component
     */
    public DefaultRestClient(String defaultBaseUrl, HttpClient delegate) {
        this(defaultBaseUrl, new JsonTransformer(), delegate);
    }

    /**
     * Constructs a new {@link DefaultRestClient} with the given <code>defaultBaseUrl</code> that
     * will use the given {@link Transformer} and {@link HttpClient}.
     *
     * @param defaultBaseUrl
     *            the default base URL used for this connection
     * @param transformer
     *            the transformer for handling serialization of HTTP body contents
     * @param delegate
     *            the inner HTTP connection component
     */
    public DefaultRestClient(String defaultBaseUrl, Transformer transformer,
            HttpClient delegate) {
        this.defaultBaseUrl = defaultBaseUrl;
        this.transformer = transformer;
        this.delegate = delegate;
        this.securityProvider = null;
        this.defaultHeaders = new HashMap<>();
    }

    /*
     * (non-Javadoc)
     * @see com.comcast.tvx.megahttp.RestClient#getDefaultBaseUrl()
     */
    @Override
    public String getDefaultBaseUrl() {
        return defaultBaseUrl;
    }

    /*
     * (non-Javadoc)
     * @see com.comcast.tvx.megahttp.clients.BasicMegaHttpClient#addDefaultHeader(java.lang.String,
     * java.lang.String)
     */
    @Override
    public void addDefaultHeader(String name, String value) {
        this.defaultHeaders.put(name, value);
    }

    /*
     * (non-Javadoc)
     * @see
     * com.comcast.tvx.megahttp.clients.BasicMegaHttpClient#setSecurityProvider(com.comcast.tvx.
     * megahttp.security.SecurityProvider)
     */
    @Override
    public void setSecurityProvider(SecurityProvider securityProvider) {
        this.securityProvider = securityProvider;
    }

    /*
     * (non-Javadoc)
     * @see
     * com.comcast.tvx.megahttp.RestClient#setTransformer(com.comcast.tvx.megahttp.rest.Transformer
     * )
     */
    @Override
    public void setTransformer(Transformer transformer) {
        this.transformer = transformer;
    }

    /*
     * (non-Javadoc)
     * @see
     * com.comcast.tvx.megahttp.clients.BasicMegaHttpClient#execute(com.comcast.tvx.megahttp.HttpRequest
     * )
     */
    @Override
    public RestResponse execute(RestRequest request) throws HttpException {
        /* Build the URL String */
        String url = request.getUrl().setDefaultBaseUrl(defaultBaseUrl).build();

        /* Get our Apache RestRequest object */
        Method method = request.getMethod();
        HttpRequestBase req = method.getRequest(url);
        req.setConfig(request.getConfig());

        /* Add the Body */
        byte[] payload = request.getBody();
        if (null != payload) {
            if (req instanceof HttpEntityEnclosingRequest) {
                HttpEntity entity = new ByteArrayEntity(payload);
                ((HttpEntityEnclosingRequest) req).setEntity(entity);
            } else {
                throw new HttpException("Cannot attach a body to a " + method.name() + " request");
            }
        }

        /* Add all Headers */
        for (Entry<String, String> pair : defaultHeaders.entrySet()) {
            req.addHeader(pair.getKey(), pair.getValue());
        }
        for (Entry<String, String> pair : request.getHeaders().entrySet()) {
            req.addHeader(pair.getKey(), pair.getValue());
        }

        /* If there is a security provider, sign */
        if (null != securityProvider) {
            securityProvider.sign(req);
        }

        /* Closed in the finally block */
        InputStream in = null;
        ByteArrayOutputStream baos = null;

        try {
            /* Finally, execute the thing */
            org.apache.http.HttpResponse resp = delegate.execute(req);

            /* Create our response */
            RestResponse response = new RestResponse(resp.getStatusLine());

            /* Add all Headers */
            response.addAll(resp.getAllHeaders());

            /* Add the content */
            HttpEntity body = resp.getEntity();
            if (null != body) {
                in = body.getContent();
                baos = new ByteArrayOutputStream();
                IOUtils.copy(in, baos);
                response.setBody(baos.toByteArray());
            }

            return response;
        } catch (RuntimeException ex) {
            // release resources immediately
            req.abort();
            throw ex;
        } catch (HttpResponseException hrex) {
            throw new HttpStatusException(hrex.getStatusCode());
        } catch (ClientProtocolException cpex) {
            throw new HttpException("HTTP Protocol error occurred.", cpex);
        } catch (IOException ioex) {
            throw new HttpException("Error establishing connection.", ioex);
        } finally {
            req.abort();
            IOUtils.closeQuietly(in);
            IOUtils.closeQuietly(baos);
        }
    }

    /*
     * (non-Javadoc)
     * @see com.comcast.tvx.megahttp.clients.EasyMegaHttpClient#get(java.lang.String,
     * java.lang.Class)
     */
    @Override
    public <T> T get(String path, Class<T> type) throws HttpException {
        return this.get(new URL().setPath(path), type);
    }

    /*
     * (non-Javadoc)
     * @see
     * com.comcast.tvx.megahttp.clients.EasyMegaHttpClient#get(com.comcast.tvx.megahttp.utils.URL,
     * java.lang.Class)
     */
    @Override
    public <T> T get(URL url, Class<T> type) throws HttpException {
        RestRequest request = new RestRequest(url, Method.GET);
        RestResponse response = execute(request);

        if (response.getStatusCode() != HttpStatus.SC_OK) {
            throw new HttpStatusException(response.getStatusCode(), response.getStatusMessage());
        } else {
            String contentType = response.getContentType();
            if ((contentType != null) && contentType.equals(transformer.getMime())) {
                byte[] body = response.getBody();
                return transformer.read(body, type);
            } else {
                throw new HttpException("Invalid Content Type: " + contentType);
            }
        }
    }

    /*
     * (non-Javadoc)
     * @see com.comcast.tvx.megahttp.clients.EasyMegaHttpClient#put(java.lang.String, T)
     */
    @Override
    public <T> boolean put(String path, T t) throws HttpException {
        return this.put(new URL().setPath(path), t);
    }

    /*
     * (non-Javadoc)
     * @see
     * com.comcast.tvx.megahttp.clients.EasyMegaHttpClient#put(com.comcast.tvx.megahttp.utils.URL,
     * T)
     */
    @Override
    public <T> boolean put(URL url, T t) throws HttpException {
        RestRequest request = new RestRequest(url, Method.PUT);
        request.setContentType(transformer.getMime());
        request.setBody(transformer.write(t));

        RestResponse response = execute(request);
        switch (response.getStatusCode()) {
            case HttpStatus.SC_CREATED:
                return true;
            case HttpStatus.SC_OK:
            case HttpStatus.SC_NO_CONTENT:
                return false;
            default:
                throw new HttpStatusException(response.getStatusCode(), response.getStatusMessage());
        }
    }

    /*
     * (non-Javadoc)
     * @see com.comcast.tvx.megahttp.clients.EasyMegaHttpClient#delete(java.lang.String)
     */
    @Override
    public boolean delete(String path) throws HttpException {
        return this.delete(new URL().setPath(path));
    }

    /*
     * (non-Javadoc)
     * @see
     * com.comcast.tvx.megahttp.clients.EasyHttpClient#delete(com.comcast.tvx.megahttp.utils.URL)
     */
    @Override
    public boolean delete(URL url) throws HttpException {
        RestRequest request = new RestRequest(url, Method.DELETE);

        RestResponse response = execute(request);
        switch (response.getStatusCode()) {
            case HttpStatus.SC_OK:
                return true;
            case HttpStatus.SC_ACCEPTED:
            case HttpStatus.SC_NO_CONTENT:
                return false;
            default:
                throw new HttpStatusException(response.getStatusCode(), response.getStatusMessage());
        }
    }

    /*
     * (non-Javadoc)
     * @see com.comcast.tvx.megahttp.RestClient#post(java.lang.String, java.lang.Class)
     */
    @Override
    public <T> T post(String path, Class<T> responseType) throws HttpException {
        return post(path, null, responseType);
    }

    /*
     * (non-Javadoc)
     * @see com.comcast.tvx.megahttp.RestClient#post(java.lang.String, java.lang.Class)
     */
    @Override
    public <T> T post(URL url, Class<T> responseType) throws HttpException {
        return post(url, null, responseType);
    }

    /*
     * (non-Javadoc)
     * @see com.comcast.tvx.megahttp.RestClient#post(java.lang.String, java.lang.Object,
     * java.lang.Class)
     */
    @Override
    public <P, T> T post(String path, P payload, Class<T> responseType) throws HttpException {
        return this.post(new URL().setPath(path), payload, responseType);
    }

    /*
     * (non-Javadoc)
     * @see com.comcast.tvx.megahttp.RestClient#post(com.comcast.tvx.megahttp.utils.URL,
     * java.lang.Object, java.lang.Class)
     */
    @Override
    public <P, T> T post(URL url, P payload, Class<T> responseType) throws HttpException {
        RestRequest request = new RestRequest(url, Method.POST);
        if (null != payload) {
            request.setContentType(transformer.getMime());
            request.setBody(transformer.write(payload));
        }

        RestResponse response = execute(request);
        /* Handle required status codes as per http://www.w3.org/Protocols/rfc2616/rfc2616-sec9.html */
        switch (response.getStatusCode()) {
            case HttpStatus.SC_OK:
            case HttpStatus.SC_CREATED:
            case HttpStatus.SC_NO_CONTENT:
                break;
            default:
                throw new HttpStatusException(response.getStatusCode(), response.getStatusMessage());
        }

        /* Read data if the Content-Type was correct */
        if (responseType.equals(void.class)) {
            return null;
        } else {
            String contentType = response.getContentType();
            if ((contentType != null) && contentType.equals(transformer.getMime())) {

                byte[] body = response.getBody();
                return transformer.read(body, responseType);
            } else {
                throw new HttpException("Invalid Content Type: " + contentType);
            }
        }
    }

    /*
     * (non-Javadoc)
     * @see java.io.Closeable#close()
     */
    @Override
    public void close() throws IOException {
        HttpClientUtils.closeQuietly(delegate);
    }
}
