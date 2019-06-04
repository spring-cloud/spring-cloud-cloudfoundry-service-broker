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

package org.springframework.cloud.servicebroker.service.events;

import java.util.List;

import reactor.core.publisher.Flux;

import org.springframework.cloud.servicebroker.model.binding.GetLastServiceBindingOperationRequest;
import org.springframework.cloud.servicebroker.model.binding.GetLastServiceBindingOperationResponse;
import org.springframework.cloud.servicebroker.service.events.flows.AsyncOperationServiceInstanceBindingCompletionFlow;
import org.springframework.cloud.servicebroker.service.events.flows.AsyncOperationServiceInstanceBindingErrorFlow;
import org.springframework.cloud.servicebroker.service.events.flows.AsyncOperationServiceInstanceBindingInitializationFlow;

/**
 * Event flow registry for asynchronous get last binding operation requests.
 *
 * @author ilyavy
 */
public class AsyncOperationServiceInstanceBindingEventFlowRegistry
        extends EventFlowRegistry<AsyncOperationServiceInstanceBindingInitializationFlow,
        AsyncOperationServiceInstanceBindingCompletionFlow, AsyncOperationServiceInstanceBindingErrorFlow,
        GetLastServiceBindingOperationRequest, GetLastServiceBindingOperationResponse> {

    public AsyncOperationServiceInstanceBindingEventFlowRegistry(
            final List<AsyncOperationServiceInstanceBindingInitializationFlow> initializationFlows,
            final List<AsyncOperationServiceInstanceBindingCompletionFlow> completionFlows,
            final List<AsyncOperationServiceInstanceBindingErrorFlow> errorFlows) {
        super(initializationFlows, completionFlows, errorFlows);
    }

    @Override
    public Flux<Void> getInitializationFlows(GetLastServiceBindingOperationRequest request) {
        return getInitializationFlowsInternal()
                .flatMap(flow -> flow.initialize(request));
    }

    @Override
    public Flux<Void> getCompletionFlows(
            GetLastServiceBindingOperationRequest request, GetLastServiceBindingOperationResponse response) {

        return getCompletionFlowsInternal()
                .flatMap(flow -> flow.complete(request, response));
    }

    @Override
    public Flux<Void> getErrorFlows(GetLastServiceBindingOperationRequest request, Throwable t) {
        return getErrorFlowsInternal()
                .flatMap(flow -> flow.error(request, t));
    }
}
