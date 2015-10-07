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


import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;

import com.comcast.cvs.cereal.engines.YamlCerealEngine;

/**
 * A {@link HttpMessageConverter} that can be used with spring that will do the internal conversion
 * using a {@link YamlCerealEngine}. When configured, this will convert all {@link ResponseBody} and
 * {@link RequestBody} objects that use the content type of "application/yaml".
 *
 * <p>
 * To use this in a spring web application, simply add this code to your
 * <code>app-context.xml</code>:
 * </p>
 *
 * <pre>
 *   &lt;mvc:annotation-driven&gt;
 *     &lt;mvc:message-converters register-defaults="false"&gt;
 *       &lt;bean class="com.comcast.tvx.megahttp.spring.YamlCerealHttpMessageConverter" /&gt;
 *     &lt;/mvc:message-converters&gt;
 *   &lt;/mvc:annotation-driven&gt;
 * </pre>
 *
 * <p>
 * To configure to use an existing (and shared) {@link YamlCerealEngine} named "yamlEngine", use the
 * following configuration pattern:
 * </p>
 *
 * <pre>
 *   &lt;mvc:annotation-driven&gt;
 *     &lt;mvc:message-converters register-defaults="false"&gt;
 *       &lt;bean class="com.comcast.tvx.megahttp.spring.YamlCerealHttpMessageConverter"&gt;
 *         &lt;constructor-arg index="0" ref="yamlEngine" /&gt;
 *       &lt;/bean&gt;
 *     &lt;/mvc:message-converters&gt;
 *   &lt;/mvc:annotation-driven&gt;
 * </pre>
 *
 * @see MediaType#APPLICATION_JSON
 *
 * @author <a href="mailto:clark_malmgren@cable.comcast.com">Clark Malmgren</a>
 */
public class YamlCerealHttpMessageConverter extends AbstractCerealHttpMessageConverter {

    /**
     * Default constructor that will use a new {@link YamlCerealEngine} without block flow to
     * conserve size.
     */
    public YamlCerealHttpMessageConverter() {
        this(new YamlCerealEngine(false));
    }

    /**
     * Construct a new {@link YamlCerealHttpMessageConverter} that will delegate to the given
     * <code>engine</code>.
     *
     * @param engine
     *            the engine to delegate to
     */
    public YamlCerealHttpMessageConverter(YamlCerealEngine engine) {
        super(engine, MediaType.parseMediaType("application/yaml"));
    }
}
