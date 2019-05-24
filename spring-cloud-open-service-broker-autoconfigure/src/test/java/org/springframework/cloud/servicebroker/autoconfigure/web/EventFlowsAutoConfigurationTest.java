package org.springframework.cloud.servicebroker.autoconfigure.web;

import java.util.List;

import org.junit.Test;
import reactor.core.publisher.Mono;

import org.springframework.boot.autoconfigure.AutoConfigurations;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.test.context.assertj.AssertableApplicationContext;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;
import org.springframework.cloud.servicebroker.model.binding.CreateServiceInstanceBindingRequest;
import org.springframework.cloud.servicebroker.model.binding.CreateServiceInstanceBindingResponse;
import org.springframework.cloud.servicebroker.model.binding.DeleteServiceInstanceBindingRequest;
import org.springframework.cloud.servicebroker.model.binding.DeleteServiceInstanceBindingResponse;
import org.springframework.cloud.servicebroker.model.instance.CreateServiceInstanceRequest;
import org.springframework.cloud.servicebroker.model.instance.CreateServiceInstanceResponse;
import org.springframework.cloud.servicebroker.model.instance.DeleteServiceInstanceRequest;
import org.springframework.cloud.servicebroker.model.instance.DeleteServiceInstanceResponse;
import org.springframework.cloud.servicebroker.model.instance.GetLastServiceOperationRequest;
import org.springframework.cloud.servicebroker.model.instance.GetLastServiceOperationResponse;
import org.springframework.cloud.servicebroker.model.instance.UpdateServiceInstanceRequest;
import org.springframework.cloud.servicebroker.model.instance.UpdateServiceInstanceResponse;
import org.springframework.cloud.servicebroker.service.events.AsyncOperationServiceInstanceEventFlowRegistry;
import org.springframework.cloud.servicebroker.service.events.CreateServiceInstanceBindingEventFlowRegistry;
import org.springframework.cloud.servicebroker.service.events.CreateServiceInstanceEventFlowRegistry;
import org.springframework.cloud.servicebroker.service.events.DeleteServiceInstanceBindingEventFlowRegistry;
import org.springframework.cloud.servicebroker.service.events.DeleteServiceInstanceEventFlowRegistry;
import org.springframework.cloud.servicebroker.service.events.EventFlowRegistries;
import org.springframework.cloud.servicebroker.service.events.EventFlowRegistry;
import org.springframework.cloud.servicebroker.service.events.UpdateServiceInstanceEventFlowRegistry;
import org.springframework.cloud.servicebroker.service.events.flows.AsyncOperationServiceInstanceCompletionFlow;
import org.springframework.cloud.servicebroker.service.events.flows.AsyncOperationServiceInstanceErrorFlow;
import org.springframework.cloud.servicebroker.service.events.flows.AsyncOperationServiceInstanceInitializationFlow;
import org.springframework.cloud.servicebroker.service.events.flows.CreateServiceInstanceBindingCompletionFlow;
import org.springframework.cloud.servicebroker.service.events.flows.CreateServiceInstanceBindingErrorFlow;
import org.springframework.cloud.servicebroker.service.events.flows.CreateServiceInstanceBindingInitializationFlow;
import org.springframework.cloud.servicebroker.service.events.flows.CreateServiceInstanceCompletionFlow;
import org.springframework.cloud.servicebroker.service.events.flows.CreateServiceInstanceErrorFlow;
import org.springframework.cloud.servicebroker.service.events.flows.CreateServiceInstanceInitializationFlow;
import org.springframework.cloud.servicebroker.service.events.flows.DeleteServiceInstanceBindingCompletionFlow;
import org.springframework.cloud.servicebroker.service.events.flows.DeleteServiceInstanceBindingErrorFlow;
import org.springframework.cloud.servicebroker.service.events.flows.DeleteServiceInstanceBindingInitializationFlow;
import org.springframework.cloud.servicebroker.service.events.flows.DeleteServiceInstanceCompletionFlow;
import org.springframework.cloud.servicebroker.service.events.flows.DeleteServiceInstanceErrorFlow;
import org.springframework.cloud.servicebroker.service.events.flows.DeleteServiceInstanceInitializationFlow;
import org.springframework.cloud.servicebroker.service.events.flows.UpdateServiceInstanceCompletionFlow;
import org.springframework.cloud.servicebroker.service.events.flows.UpdateServiceInstanceErrorFlow;
import org.springframework.cloud.servicebroker.service.events.flows.UpdateServiceInstanceInitializationFlow;
import org.springframework.context.annotation.Bean;
import org.springframework.test.util.ReflectionTestUtils;

import static org.assertj.core.api.Assertions.assertThat;

public class EventFlowsAutoConfigurationTest {

	private final ApplicationContextRunner contextRunner = new ApplicationContextRunner()
			.withConfiguration(AutoConfigurations.of(EventFlowsAutoConfiguration.class));

	@Test
	public void servicesAreCreatedWithMinimalConfiguration() {
		this.contextRunner.run(this::assertBeans);
	}

	@Test
	public void servicesAreCreatedWithAlternateFlowBeansConfiguration() {
		this.contextRunner
				.withUserConfiguration(AlternateEventFlowRegistryBeansConfiguration.class)
				.run(this::assertBeans);
	}

	@Test
	public void createInstanceEventFlowBeansAreConfigured() {
		this.contextRunner
				.withUserConfiguration(CreateInstanceEventFlowBeansConfiguration.class)
				.run(context -> {
					assertBeans(context);
					CreateServiceInstanceEventFlowRegistry registry =
							context.getBean(CreateServiceInstanceEventFlowRegistry.class);
					assertEventFlowBeans(registry, 2, 1, 1);
				});
	}

	@Test
	public void updateInstanceEventFlowBeansAreConfigured() {
		this.contextRunner
				.withUserConfiguration(UpdateInstanceEventFlowBeansConfiguration.class)
				.run(context -> {
					assertBeans(context);
					UpdateServiceInstanceEventFlowRegistry registry =
							context.getBean(UpdateServiceInstanceEventFlowRegistry.class);
					assertEventFlowBeans(registry, 1, 1, 1);
				});
	}

	@Test
	public void deleteInstanceEventFlowBeansAreConfigured() {
		this.contextRunner
				.withUserConfiguration(DeleteInstanceEventFlowBeansConfiguration.class)
				.run(context -> {
					assertBeans(context);
					DeleteServiceInstanceEventFlowRegistry registry =
							context.getBean(DeleteServiceInstanceEventFlowRegistry.class);
					assertEventFlowBeans(registry, 1, 1, 2);
				});
	}

	@Test
	public void asyncOperationEventFlowBeansAreConfigured() {
		this.contextRunner
				.withUserConfiguration(AsyncOperationServiceInstanceEventFlowBeansConfiguration.class)
				.run(context -> {
					assertBeans(context);
					AsyncOperationServiceInstanceEventFlowRegistry registry =
							context.getBean(AsyncOperationServiceInstanceEventFlowRegistry.class);
					assertEventFlowBeans(registry, 1, 2, 1);
				});
	}

	@Test
	public void createInstanceBindingEventFlowBeansAreConfigured() {
		this.contextRunner
				.withUserConfiguration(CreateInstanceBindingEventFlowBeansConfiguration.class)
				.run(context -> {
					assertBeans(context);
					CreateServiceInstanceBindingEventFlowRegistry registry =
							context.getBean(CreateServiceInstanceBindingEventFlowRegistry.class);
					assertEventFlowBeans(registry, 1, 1, 1);
				});
	}

	@Test
	public void deleteInstanceBindingEventFlowBeansAreConfigured() {
		this.contextRunner
				.withUserConfiguration(DeleteInstanceBindingEventFlowBeansConfiguration.class)
				.run(context -> {
					assertBeans(context);
					DeleteServiceInstanceBindingEventFlowRegistry registry =
							context.getBean(DeleteServiceInstanceBindingEventFlowRegistry.class);
					assertEventFlowBeans(registry, 1, 1, 1);
				});
	}

	private void assertBeans(AssertableApplicationContext context) {
		assertThat(context)
				.getBean(CreateServiceInstanceEventFlowRegistry.class)
				.isNotNull();

		assertThat(context)
				.getBean(UpdateServiceInstanceEventFlowRegistry.class)
				.isNotNull();

		assertThat(context)
				.getBean(DeleteServiceInstanceEventFlowRegistry.class)
				.isNotNull();

		assertThat(context)
				.getBean(AsyncOperationServiceInstanceEventFlowRegistry.class)
				.isNotNull();

		assertThat(context)
				.getBean(CreateServiceInstanceBindingEventFlowRegistry.class)
				.isNotNull();

		assertThat(context)
				.getBean(DeleteServiceInstanceBindingEventFlowRegistry.class)
				.isNotNull();

		assertThat(context)
				.getBean(EventFlowRegistries.class)
				.isNotNull();

		EventFlowRegistries eventFlowRegistries = context.getBean(EventFlowRegistries.class);
		assertThat(eventFlowRegistries.getCreateInstanceRegistry())
				.isEqualTo(context.getBean(CreateServiceInstanceEventFlowRegistry.class));

		assertThat(eventFlowRegistries.getUpdateInstanceRegistry())
				.isEqualTo(context.getBean(UpdateServiceInstanceEventFlowRegistry.class));

		assertThat(eventFlowRegistries.getDeleteInstanceRegistry())
				.isEqualTo(context.getBean(DeleteServiceInstanceEventFlowRegistry.class));

		assertThat(eventFlowRegistries.getAsyncOperationRegistry())
				.isEqualTo(context.getBean(AsyncOperationServiceInstanceEventFlowRegistry.class));

		assertThat(eventFlowRegistries.getCreateInstanceBindingRegistry())
				.isEqualTo(context.getBean(CreateServiceInstanceBindingEventFlowRegistry.class));

		assertThat(eventFlowRegistries.getDeleteInstanceBindingRegistry())
				.isEqualTo(context.getBean(DeleteServiceInstanceBindingEventFlowRegistry.class));
	}

	private void assertEventFlowBeans(EventFlowRegistry<?, ?, ?, ?, ?> registry, int initializationFlowCount,
									  int completionFlowCount, int errorFlowCount) {
		List<?> initializationFlows = (List<?>) ReflectionTestUtils
				.getField(registry, "initializationFlows");
		assertThat(initializationFlows.size()).isEqualTo(initializationFlowCount);

		List<?> completionFlows = (List<?>) ReflectionTestUtils.getField(registry, "completionFlows");
		assertThat(completionFlows.size()).isEqualTo(completionFlowCount);

		List<?> errorFlows = (List<?>) ReflectionTestUtils.getField(registry, "errorFlows");
		assertThat(errorFlows.size()).isEqualTo(errorFlowCount);
	}

	@TestConfiguration
	public static class AlternateEventFlowRegistryBeansConfiguration {

		@SuppressWarnings("deprecation")
		@Bean
		public CreateServiceInstanceEventFlowRegistry createInstanceRegistry() {
			return new CreateServiceInstanceEventFlowRegistry();
		}

		@SuppressWarnings("deprecation")
		@Bean
		public UpdateServiceInstanceEventFlowRegistry updateInstanceRegistry() {
			return new UpdateServiceInstanceEventFlowRegistry();
		}

		@SuppressWarnings("deprecation")
		@Bean
		public DeleteServiceInstanceEventFlowRegistry deleteInstanceRegistry() {
			return new DeleteServiceInstanceEventFlowRegistry();
		}

		@SuppressWarnings("deprecation")
		@Bean
		public CreateServiceInstanceBindingEventFlowRegistry createInstanceBindingRegistry() {
			return new CreateServiceInstanceBindingEventFlowRegistry();
		}

		@SuppressWarnings("deprecation")
		@Bean
		public DeleteServiceInstanceBindingEventFlowRegistry deleteInstanceBindingRegistry() {
			return new DeleteServiceInstanceBindingEventFlowRegistry();
		}

	}

	@TestConfiguration
	public static class CreateInstanceEventFlowBeansConfiguration {

		@Bean
		public CreateServiceInstanceInitializationFlow createInitFlow1() {
			return new CreateServiceInstanceInitializationFlow() {
				@Override
				public Mono<Void> initialize(CreateServiceInstanceRequest request) {
					return Mono.empty();
				}
			};
		}

		@Bean
		public CreateServiceInstanceInitializationFlow createInitFlow2() {
			return new CreateServiceInstanceInitializationFlow() {
				@Override
				public Mono<Void> initialize(CreateServiceInstanceRequest request) {
					return Mono.empty();
				}
			};
		}

		@Bean
		public CreateServiceInstanceCompletionFlow createCompleteFlow() {
			return new CreateServiceInstanceCompletionFlow() {
				@Override
				public Mono<Void> complete(CreateServiceInstanceRequest request,
										   CreateServiceInstanceResponse response) {
					return Mono.empty();
				}
			};
		}

		@Bean
		public CreateServiceInstanceErrorFlow createErrorFlow() {
			return new CreateServiceInstanceErrorFlow() {
				@Override
				public Mono<Void> error(CreateServiceInstanceRequest request, Throwable t) {
					return Mono.empty();
				}
			};
		}
	}

	@TestConfiguration
	public static class UpdateInstanceEventFlowBeansConfiguration {

		@Bean
		public UpdateServiceInstanceInitializationFlow updateInitFlow() {
			return new UpdateServiceInstanceInitializationFlow() {
				@Override
				public Mono<Void> initialize(UpdateServiceInstanceRequest request) {
					return Mono.empty();
				}
			};
		}

		@Bean
		public UpdateServiceInstanceCompletionFlow updateCompleteFlow() {
			return new UpdateServiceInstanceCompletionFlow() {
				@Override
				public Mono<Void> complete(UpdateServiceInstanceRequest request,
										   UpdateServiceInstanceResponse response) {
					return Mono.empty();
				}
			};
		}

		@Bean
		public UpdateServiceInstanceErrorFlow updateErrorFlow() {
			return new UpdateServiceInstanceErrorFlow() {
				@Override
				public Mono<Void> error(UpdateServiceInstanceRequest request, Throwable t) {
					return Mono.empty();
				}
			};
		}
	}

	@TestConfiguration
	public static class DeleteInstanceEventFlowBeansConfiguration {

		@Bean
		public DeleteServiceInstanceInitializationFlow deleteInitFlow() {
			return new DeleteServiceInstanceInitializationFlow() {
				@Override
				public Mono<Void> initialize(DeleteServiceInstanceRequest request) {
					return Mono.empty();
				}
			};
		}

		@Bean
		public DeleteServiceInstanceCompletionFlow deleteCompleteFlow() {
			return new DeleteServiceInstanceCompletionFlow() {
				@Override
				public Mono<Void> complete(DeleteServiceInstanceRequest request,
										   DeleteServiceInstanceResponse response) {
					return Mono.empty();
				}
			};
		}

		@Bean
		public DeleteServiceInstanceErrorFlow deleteErrorFlow1() {
			return new DeleteServiceInstanceErrorFlow() {
				@Override
				public Mono<Void> error(DeleteServiceInstanceRequest request, Throwable t) {
					return Mono.empty();
				}
			};
		}

		@Bean
		public DeleteServiceInstanceErrorFlow deleteErrorFlow2() {
			return new DeleteServiceInstanceErrorFlow() {
				@Override
				public Mono<Void> error(DeleteServiceInstanceRequest request, Throwable t) {
					return Mono.empty();
				}
			};
		}
	}

	@TestConfiguration
	public static class AsyncOperationServiceInstanceEventFlowBeansConfiguration {

		@Bean
		public AsyncOperationServiceInstanceInitializationFlow getLastOperationInitFlow() {
			return new AsyncOperationServiceInstanceInitializationFlow() {
				@Override
				public Mono<Void> initialize(GetLastServiceOperationRequest request) {
					return Mono.empty();
				}
			};
		}

		@Bean
		public AsyncOperationServiceInstanceCompletionFlow getLastOperationCompleteFlow1() {
			return new AsyncOperationServiceInstanceCompletionFlow() {
				@Override
				public Mono<Void> complete(GetLastServiceOperationRequest request,
										   GetLastServiceOperationResponse response) {
					return Mono.empty();
				}
			};
		}

		@Bean
		public AsyncOperationServiceInstanceCompletionFlow getLastOperationCompleteFlow2() {
			return new AsyncOperationServiceInstanceCompletionFlow() {
				@Override
				public Mono<Void> complete(GetLastServiceOperationRequest request,
										   GetLastServiceOperationResponse response) {
					return Mono.empty();
				}
			};
		}

		@Bean
		public AsyncOperationServiceInstanceErrorFlow getLastOperationErrorFlow() {
			return new AsyncOperationServiceInstanceErrorFlow() {
				@Override
				public Mono<Void> error(GetLastServiceOperationRequest request, Throwable t) {
					return Mono.empty();
				}
			};
		}
	}

	@TestConfiguration
	public static class CreateInstanceBindingEventFlowBeansConfiguration {

		@Bean
		public CreateServiceInstanceBindingInitializationFlow createInitFlow() {
			return new CreateServiceInstanceBindingInitializationFlow() {
				@Override
				public Mono<Void> initialize(CreateServiceInstanceBindingRequest request) {
					return Mono.empty();
				}
			};
		}

		@Bean
		public CreateServiceInstanceBindingCompletionFlow createCompleteFlow() {
			return new CreateServiceInstanceBindingCompletionFlow() {
				@Override
				public Mono<Void> complete(CreateServiceInstanceBindingRequest request,
										   CreateServiceInstanceBindingResponse response) {
					return Mono.empty();
				}
			};
		}

		@Bean
		public CreateServiceInstanceBindingErrorFlow createErrorFlow() {
			return new CreateServiceInstanceBindingErrorFlow() {
				@Override
				public Mono<Void> error(CreateServiceInstanceBindingRequest request, Throwable t) {
					return Mono.empty();
				}
			};
		}
	}

	@TestConfiguration
	public static class DeleteInstanceBindingEventFlowBeansConfiguration {

		@Bean
		public DeleteServiceInstanceBindingInitializationFlow createInitFlow() {
			return new DeleteServiceInstanceBindingInitializationFlow() {
				@Override
				public Mono<Void> initialize(DeleteServiceInstanceBindingRequest request) {
					return Mono.empty();
				}
			};
		}

		@Bean
		public DeleteServiceInstanceBindingCompletionFlow createCompleteFlow() {
			return new DeleteServiceInstanceBindingCompletionFlow() {
				@Override
				public Mono<Void> complete(DeleteServiceInstanceBindingRequest request,
										   DeleteServiceInstanceBindingResponse response) {
					return Mono.empty();
				}
			};
		}

		@Bean
		public DeleteServiceInstanceBindingErrorFlow createErrorFlow() {
			return new DeleteServiceInstanceBindingErrorFlow() {
				@Override
				public Mono<Void> error(DeleteServiceInstanceBindingRequest request, Throwable t) {
					return Mono.empty();
				}
			};
		}
	}

}