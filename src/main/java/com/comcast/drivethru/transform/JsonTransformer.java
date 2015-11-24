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

import com.comcast.cereal.engines.JsonCerealEngine;


/**
 * A {@link Transformer} for writing and reading JSON contents using a {@link JsonCerealEngine}.
 *
 * @author <a href="mailto:cmalmgren@gmail.com">Clark Malmgren</a>
 */
public class JsonTransformer extends CerealTransformer {

    /**
     * Create a new {@link JsonTransformer}
     */
    public JsonTransformer() {
        super(new JsonCerealEngine());
    }

    /*
     * (non-Javadoc)
     * @see com.comcast.drivethru.rest.RestTransformer#getMime()
     */
    @Override
    public String getMime() {
        return "application/json";
    }
}
