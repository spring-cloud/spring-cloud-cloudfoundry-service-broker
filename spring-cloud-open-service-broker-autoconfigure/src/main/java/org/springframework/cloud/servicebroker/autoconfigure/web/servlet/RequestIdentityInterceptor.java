/*
 * Copyright 2002-2019 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.springframework.cloud.servicebroker.autoconfigure.web.servlet;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.cloud.servicebroker.model.ServiceBrokerRequest;
import org.springframework.util.StringUtils;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

/**
 * {@link HandlerInterceptor} that inspects the request for the presence of the {@literal X-Broker-API-Request
 * -Identity} header and sets the corresponding value in the same response header
 *
 * @author Roy Clarkson
 */
public class RequestIdentityInterceptor extends HandlerInterceptorAdapter {

	/**
	 * Sets the {@literal X-Broker-API-Request-Identity} header in the response if a value is received in the request
	 * from the platform
	 *
	 * @param request {@inheritDoc}
	 * @param response {@inheritDoc}
	 * @param handler {@inheritDoc}
	 */
	@Override
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
		String requestIdentity = request.getHeader(ServiceBrokerRequest.REQUEST_IDENTITY_HEADER);
		if (StringUtils.hasLength(requestIdentity)) {
			response.addHeader(ServiceBrokerRequest.REQUEST_IDENTITY_HEADER, requestIdentity);
		}
		return true;
	}

}
