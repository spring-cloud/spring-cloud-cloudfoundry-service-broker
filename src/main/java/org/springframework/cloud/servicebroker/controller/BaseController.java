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

package org.springframework.cloud.servicebroker.controller;

import org.springframework.cloud.servicebroker.exception.ServiceBrokerApiVersionException;
import org.springframework.cloud.servicebroker.exception.ServiceBrokerAsyncRequiredException;
import org.springframework.cloud.servicebroker.exception.ServiceBrokerInvalidParametersException;
import org.springframework.cloud.servicebroker.exception.ServiceDefinitionDoesNotExistException;
import org.springframework.cloud.servicebroker.exception.ServiceInstanceDoesNotExistException;
import org.springframework.cloud.servicebroker.model.AsyncRequiredErrorMessage;
import org.springframework.cloud.servicebroker.model.ErrorMessage;
import org.springframework.cloud.servicebroker.model.ServiceDefinition;
import org.springframework.cloud.servicebroker.service.CatalogService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;

import lombok.extern.slf4j.Slf4j;

/**
 * Base controller.
 *
 * @author sgreenberg@pivotal.io
 * @author Scott Frederick
 */
@Slf4j
public class BaseController {

	private final CatalogService catalogService;

	public BaseController(CatalogService catalogService) {
		this.catalogService = catalogService;
	}

	protected CatalogService getCatalogService() {
		return catalogService;
	}

	protected ServiceDefinition getRequiredServiceDefinition(String serviceDefinitionId) {
		ServiceDefinition serviceDefinition = getServiceDefinition(serviceDefinitionId);
		if (serviceDefinition == null) {
			throw new ServiceDefinitionDoesNotExistException(serviceDefinitionId);
		}
		return serviceDefinition;
	}

	protected ServiceDefinition getServiceDefinition(String serviceDefinitionId) {
		return catalogService.getServiceDefinition(serviceDefinitionId);
	}

	@ExceptionHandler(ServiceBrokerApiVersionException.class)
	public ResponseEntity<ErrorMessage> handleException(
			ServiceBrokerApiVersionException ex) {
		log.debug("Unsupported service broker API version: ", ex);
		return getErrorResponse(ex.getMessage(), HttpStatus.PRECONDITION_FAILED);
	}

	@ExceptionHandler(ServiceInstanceDoesNotExistException.class)
	public ResponseEntity<ErrorMessage> handleException(
			ServiceInstanceDoesNotExistException ex) {
		log.debug("Service instance does not exist: ", ex);
		return getErrorResponse(ex.getMessage(), HttpStatus.UNPROCESSABLE_ENTITY);
	}

	@ExceptionHandler(ServiceDefinitionDoesNotExistException.class)
	public ResponseEntity<ErrorMessage> handleException(
			ServiceDefinitionDoesNotExistException ex) {
		log.debug("Service definition does not exist: ", ex);
		return getErrorResponse(ex.getMessage(), HttpStatus.UNPROCESSABLE_ENTITY);
	}

	@ExceptionHandler(HttpMessageNotReadableException.class)
	public ResponseEntity<ErrorMessage> handleException(
			HttpMessageNotReadableException ex) {
		log.debug("Unprocessable request received: ", ex);
		return getErrorResponse(ex.getMessage(), HttpStatus.UNPROCESSABLE_ENTITY);
	}

	@ExceptionHandler(MethodArgumentNotValidException.class)
	public ResponseEntity<ErrorMessage> handleException(
			MethodArgumentNotValidException ex) {
		log.debug("Unprocessable request received: ", ex);
		BindingResult result = ex.getBindingResult();
		String message = "Missing required fields:";
		for (FieldError error : result.getFieldErrors()) {
			message += " " + error.getField();
		}
		return getErrorResponse(message, HttpStatus.UNPROCESSABLE_ENTITY);
	}

	@ExceptionHandler(ServiceBrokerAsyncRequiredException.class)
	public ResponseEntity<AsyncRequiredErrorMessage> handleException(
			ServiceBrokerAsyncRequiredException ex) {
		log.debug("Broker requires async support: ", ex);
		return new ResponseEntity<>(new AsyncRequiredErrorMessage(ex.getMessage()),
				HttpStatus.UNPROCESSABLE_ENTITY);
	}

	@ExceptionHandler(ServiceBrokerInvalidParametersException.class)
	public ResponseEntity<ErrorMessage> handleException(
			ServiceBrokerInvalidParametersException ex) {
		log.debug("Invalid parameters received: ", ex);
		return getErrorResponse(ex.getMessage(), HttpStatus.UNPROCESSABLE_ENTITY);
	}

	@ExceptionHandler(Exception.class)
	public ResponseEntity<ErrorMessage> handleException(Exception ex) {
		log.debug("Unknown exception handled: ", ex);
		return getErrorResponse(ex.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
	}

	public ResponseEntity<ErrorMessage> getErrorResponse(String message,
			HttpStatus status) {
		return new ResponseEntity<>(new ErrorMessage(message), status);
	}
}
