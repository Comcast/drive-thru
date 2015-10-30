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
 *  {@link ByteTransformer} for writing and reading bytes.
 *
 * @author <a href="mailto:Prasad_Menon2@cable.comcast.com">Prasad Menon</a>
 */
public class ByteTransformer implements Transformer {
	
	@Override
	public String getMime() {

		return "application/octet-stream";
	}

	@Override
	public <T> byte[] write(T t) throws HttpException {
		return (byte[]) t;
	}

	@SuppressWarnings("unchecked")
	@Override
	public <T> T read(byte[] body, Class<T> type) throws HttpException {
		return (T) body;
	}


}
