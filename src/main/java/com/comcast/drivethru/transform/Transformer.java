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
 * A {@link Transformer} provides the underlying capability to transform between Java objects and
 * the byte array payload of an HTTP body.
 *
 * @author <a href="mailto:cmalmgren@gmail.com">Clark Malmgren</a>
 */
public interface Transformer {

    /**
     * The <a href="http://www.iana.org/assignments/media-types">MIME type</a> associated with the
     * HTTP body format that this {@link Transformer} supports
     *
     * @return the MIME type
     */
    String getMime();

    /**
     * Write the given java object to an array of bytes to be included in a HTTP body.
     *
     * @param t
     *            the java object to transform
     *
     * @return the transformed byte array
     *
     * @throws HttpException
     *             if a problem occurs while transforming
     */
    <T> byte[] write(T t) throws HttpException;

    /**
     * Read the given body into a java object of the given type.
     *
     * @param body
     *            the body
     * @param type
     *            the java type of the object to create
     *
     * @return the transformed java object
     *
     * @throws HttpException
     *             if a problem occurs while transforming
     */
    <T> T read(byte[] body, Class<T> type) throws HttpException;
}
