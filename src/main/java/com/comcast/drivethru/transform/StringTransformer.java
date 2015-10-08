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
package com.comcast.drivethru.transform;

import com.comcast.drivethru.exception.HttpException;


/**
 * An abstract class for String-based {@link Transformer}s.
 *
 * @author <a href="mailto:cmalmgren@gmail.com">Clark Malmgren</a>
 */
public abstract class StringTransformer implements Transformer {

    /**
     * Transform the given string body into the given java type.
     *
     * @param body
     *            the string body
     * @param type
     *            the java type to transform to
     *
     * @return the created java object
     *
     * @throws HttpException
     *             if a problem occurs while transforming
     */
    public abstract <T> T readString(String body, Class<T> type) throws HttpException;

    /**
     * Write the given java object to a string to be included in a HTTP body.
     *
     * @param t
     *            the java object to transform
     *
     * @return the transformed string
     *
     * @throws HttpException
     *             if a problem occurs while transforming
     */
    public abstract <T> String writeString(T t) throws HttpException;

    /*
     * (non-Javadoc)
     * @see com.comcast.tvx.megahttp.transform.Transformer#read(byte[], java.lang.Class)
     */
    @Override
    public <T> T read(byte[] body, Class<T> type) throws HttpException {
        return readString(new String(body), type);
    }

    /*
     * (non-Javadoc)
     * @see com.comcast.tvx.megahttp.transform.Transformer#write(java.lang.Object)
     */
    @Override
    public <T> byte[] write(T t) throws HttpException {
        return writeString(t).getBytes();
    }
}
