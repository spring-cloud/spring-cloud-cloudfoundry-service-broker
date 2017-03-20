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

import org.hibernate.validator.constraints.NotEmpty;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

/**
 * A service plan available for a ServiceDefinition
 *
 * @author sgreenberg@pivotal.io
 * @author Scott Frederick
 */
@Getter
@ToString
@EqualsAndHashCode
@JsonAutoDetect(getterVisibility = JsonAutoDetect.Visibility.NONE)
@JsonIgnoreProperties(ignoreUnknown = true)
public class Plan {

	/**
	 * An identifier used to correlate this plan in future requests to the catalog. This
	 * must be unique within a Cloud Foundry deployment. Using a GUID is recommended.
	 */
	@NotEmpty
	@JsonSerialize
	@JsonProperty("id")
	private String id;

	/**
	 * A CLI-friendly name of the plan that will appear in the catalog. The value should
	 * be all lowercase, with no spaces.
	 */
	@NotEmpty
	@JsonSerialize
	@JsonProperty("name")
	private String name;

	/**
	 * A user-friendly short description of the plan that will appear in the catalog.
	 */
	@NotEmpty
	@JsonSerialize
	@JsonProperty("description")
	private String description;

	/**
	 * A map of metadata to further describe a service plan.
	 */
	@JsonSerialize(nullsUsing = EmptyMapSerializer.class)
	@JsonProperty("metadata")
	private Map<String, Object> metadata;

	/**
	 * Indicates whether the service with this plan can be bound to applications. This is
	 * an optional field. If the value is <code>null</code>, the field will be omitted
	 * from the serialized JSON.
	 */
	@Getter(AccessLevel.NONE)
	@JsonSerialize
	@JsonProperty("bindable")
	@JsonInclude(Include.NON_NULL)
	private Boolean bindable;

	/**
	 * Indicates whether the plan can be limited by the non_basic_services_allowed field
	 * in a Cloud Foundry Quota.
	 */
	@JsonSerialize
	@JsonProperty("free")
	private boolean free = true;

	public Plan() {
	}

	public Plan(String id, String name, String description) {
		this.id = id;
		this.name = name;
		this.description = description;
	}

	public Plan(String id, String name, String description,
			Map<String, Object> metadata) {
		this(id, name, description);
		this.metadata = metadata;
	}

	public Plan(String id, String name, String description, Map<String, Object> metadata,
			boolean free) {
		this(id, name, description, metadata);
		this.free = free;
	}

	public Plan(String id, String name, String description, Map<String, Object> metadata,
			boolean free, boolean bindable) {
		this(id, name, description, metadata, free);
		this.bindable = bindable;
	}

	public Boolean isBindable() {
		return bindable;
	}
}
