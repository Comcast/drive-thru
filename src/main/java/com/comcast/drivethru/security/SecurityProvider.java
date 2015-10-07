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
package com.comcast.drivethru.security;

import org.apache.http.client.methods.HttpUriRequest;

import com.comcast.drivethru.exception.HttpException;

/**
 * A SecurityProvider provides signing (or encryption) services for a {@link HttpUriRequest} just
 * prior to internal execution.
 *
 * @author <a href="mailto:clark_malmgren@cable.comcast.com">Clark Malmgren</a>
 */
public interface SecurityProvider {

    /**
     * Signs the given request.
     *
     * @param request
     *            the request to sign
     *
     * @throws HttpException
     *             if signing the request failed
     */
    void sign(HttpUriRequest request) throws HttpException;
}
