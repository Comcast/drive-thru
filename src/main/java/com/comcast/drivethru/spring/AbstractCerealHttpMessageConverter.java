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

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.io.Writer;

import org.apache.commons.io.IOUtils;
import org.springframework.http.HttpInputMessage;
import org.springframework.http.HttpOutputMessage;
import org.springframework.http.MediaType;
import org.springframework.http.converter.AbstractHttpMessageConverter;
import org.springframework.http.converter.HttpMessageConverter;

import com.comcast.cvs.cereal.CerealException;
import com.comcast.cvs.cereal.engines.CerealEngine;

/**
 * Abstract implementation of a {@link HttpMessageConverter} that delegates to a
 * {@link CerealEngine} to do the internal converting.
 *
 * @author <a href="mailto:clark_malmgren@cable.comcast.com">Clark Malmgren</a>
 */
public class AbstractCerealHttpMessageConverter extends AbstractHttpMessageConverter<Object> {

    private CerealEngine engine;

    /**
     * Construct a new {@link AbstractCerealHttpMessageConverter} that delegates to the given
     * <code>engine</code> and supports the given <code>mediaType</code>.
     *
     * @param engine
     *            the {@link CerealEngine} to delegate to
     * @param mediaType
     *            the supported media (mime) type
     */
    public AbstractCerealHttpMessageConverter(CerealEngine engine, MediaType mediaType) {
        super(mediaType);
        this.engine = engine;
    }

    /*
     * (non-Javadoc)
     * @see
     * org.springframework.http.converter.AbstractHttpMessageConverter#supports(java.lang.Class)
     */
    @Override
    public boolean supports(Class<?> clazz) {
        return true;
    }

    /*
     * (non-Javadoc)
     * @see
     * org.springframework.http.converter.AbstractHttpMessageConverter#readInternal(java.lang.Class
     * , org.springframework.http.HttpInputMessage)
     */
    @Override
    public Object readInternal(Class<?> clazz, HttpInputMessage inputMessage) throws IOException {
        try {
            Reader reader = new InputStreamReader(inputMessage.getBody());
            return engine.read(reader, clazz);
        } catch (CerealException cex) {
            throw new IOException("Failed to decerealize the content", cex);
        } finally {
            IOUtils.closeQuietly(inputMessage.getBody());
        }
    }

    /*
     * (non-Javadoc)
     * @see
     * org.springframework.http.converter.AbstractHttpMessageConverter#writeInternal(java.lang.Object
     * , org.springframework.http.HttpOutputMessage)
     */
    @Override
    public void writeInternal(Object t, HttpOutputMessage outputMessage) throws IOException {
        try {
            Writer writer = new OutputStreamWriter(outputMessage.getBody());
            engine.write(t, writer);
            writer.close();
        } catch (CerealException cex) {
            throw new IOException("Failed to cerealize the content", cex);
        } finally {
            IOUtils.closeQuietly(outputMessage.getBody());
        }
    }
}
