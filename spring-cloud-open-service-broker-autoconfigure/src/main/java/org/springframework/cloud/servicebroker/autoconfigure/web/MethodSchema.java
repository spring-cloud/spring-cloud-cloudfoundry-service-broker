/*
 * Copyright 2002-2018 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.springframework.cloud.servicebroker.autoconfigure.web;

import java.util.HashMap;
import java.util.Map;

/**
 * JSON Schema for a service broker object method.
 *
 * @author Sam Gunaratne
 * @author Roy Clarkson
 */
class MethodSchema {

	/**
	 * A map of JSON schema for configuration parameters.
	 */
	private final Map<String, Object> parameters = new HashMap<>();

	public Map<String, Object> getParameters() {
		return this.parameters;
	}

	public void setParameters(Map<String, Object> parameters) {
		this.parameters.putAll(parameters);
	}

	public org.springframework.cloud.servicebroker.model.catalog.MethodSchema toModel() {
		return org.springframework.cloud.servicebroker.model.catalog.MethodSchema.builder()
				.parameters(this.parameters)
				.build();
	}

}
