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

import com.comcast.cereal.CerealException;
import com.comcast.cereal.engines.CerealEngine;
import com.comcast.drivethru.exception.HttpException;


/**
 * An abstract {@link Transformer} that delegates to a {@link CerealEngine} for conversion.
 *
 * @author <a href="mailto:cmalmgren@gmail.com">Clark Malmgren</a>
 */
public abstract class CerealTransformer extends StringTransformer {

    private CerealEngine engine;

    /**
     * Construct a new {@link CerealTransformer} that will delegate to the given
     * {@link CerealEngine}.
     *
     * @param engine
     *            the engine to use to do the conversion
     */
    public CerealTransformer(CerealEngine engine) {
        this.engine = engine;
    }

    /*
     * (non-Javadoc)
     * @see com.comcast.drivethru.transform.StringTransformer#readString(java.lang.String,
     * java.lang.Class)
     */
    @Override
    public <T> T readString(String s, Class<T> type) throws HttpException {
        try {
            return engine.readFromString(s, type);
        } catch (CerealException cex) {
            throw new HttpException("Failed to deserialize from string", cex);
        }
    }

    /*
     * (non-Javadoc)
     * @see com.comcast.drivethru.transform.StringTransformer#writeString(java.lang.Object)
     */
    @Override
    public <T> String writeString(T t) throws HttpException {
        try {
            return engine.writeToString(t);
        } catch (CerealException cex) {
            throw new HttpException("Failed to serialize object to string", cex);
        }
    }
}
