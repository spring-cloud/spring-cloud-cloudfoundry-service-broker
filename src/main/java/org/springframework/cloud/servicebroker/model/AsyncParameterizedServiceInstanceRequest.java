/*
 * Copyright 2002-2017 the original author or authors.
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

package org.springframework.cloud.servicebroker.model;

import java.util.Map;

import org.apache.commons.beanutils.BeanUtils;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

/**
 * Details of a request that supports arbitrary parameters and asynchronous behavior.
 *
 * @author Scott Frederick
 */
@Getter
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
public abstract class AsyncParameterizedServiceInstanceRequest
		extends AsyncServiceInstanceRequest {
	/**
	 * Parameters passed by the user in the form of a JSON structure. The service broker
	 * is responsible for validating the contents of the parameters for correctness or
	 * applicability.
	 */
	@JsonSerialize
	@JsonProperty("parameters")
	protected final Map<String, Object> parameters;

	public AsyncParameterizedServiceInstanceRequest(Map<String, Object> parameters) {
		this.parameters = parameters;
	}

	public <T> T getParameters(Class<T> cls) {
		try {
			T bean = cls.newInstance();
			BeanUtils.populate(bean, parameters);
			return bean;
		}
		catch (Exception e) {
			throw new IllegalArgumentException(
					"Error mapping parameters to class of type " + cls.getName(), e);
		}
	}
}
