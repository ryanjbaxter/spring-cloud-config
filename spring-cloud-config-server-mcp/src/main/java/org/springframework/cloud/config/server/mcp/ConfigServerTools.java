/*
 * Copyright 2018-2025 the original author or authors.
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

package org.springframework.cloud.config.server.mcp;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;
import org.springframework.cloud.config.environment.Environment;
import org.springframework.cloud.config.server.encryption.EncryptionController;
import org.springframework.cloud.config.server.environment.EnvironmentController;
import org.springframework.cloud.config.server.environment.EnvironmentRepository;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.ApplicationEventPublisherAware;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;

/**
 * @author Ryan Baxter
 */
@Service
public class ConfigServerTools implements ApplicationEventPublisherAware {

	private final EnvironmentController environmentController;

	private ApplicationEventPublisher applicationEventPublisher;

	private EnvironmentRepository environmentRepository;

	private List<EnvironmentRepository> allEnvironmentRepositories;

	private NativeEnvironmentRepositoryUpdater updater;

	private ApplicationEventPublisher publisher;

	private EncryptionController encryptionController;

	public ConfigServerTools(EnvironmentRepository environmentRepository,
			List<EnvironmentRepository> allEnvironmentRepositories, EnvironmentController environmentController,
			EncryptionController encryptionController, NativeEnvironmentRepositoryUpdater updater) {
		this.environmentRepository = environmentRepository;
		this.allEnvironmentRepositories = allEnvironmentRepositories;
		this.updater = updater;
		this.environmentController = environmentController;
		this.encryptionController = encryptionController;
	}

	@Tool(description = "Gets environment for an application using a given profile and label.")
	public Environment getEnvironment(String applicationName, String profile, String label) {
		return environmentController.getEnvironment(applicationName, profile, label, false);
		// return environmentRepository.findOne(applicationName, profile, label);
	}

	@Tool(description = "Updates a property of a native environment property source with a new value.")
	public void updateEnvironment(String path, String property, String value) throws IOException {
		updater.update(path, property, value);
	}

	@Tool(description = "Send a refresh notification to an application to inform it to refresh its configuration.")
	public void refreshConfiguration(String applicationName) {
		// TODO implement with Spring Cloud Bus
		// applicationEventPublisher.publishEvent(new RefreshRemoteApplicationEvent());

	}

	@Tool(description = "Gets all configured environment repositories for this config server.")
	public List<String> configuredEnvironmentRepositories() {
		return allEnvironmentRepositories.stream()
			.map(environmentRepository1 -> environmentRepository1.getClass().getName())
			.collect(Collectors.toList());
	}

	@Tool(description = "Gets details of environment repositories given a class type.")
	public String environmentRepositoryDetails(@ToolParam(
			description = "A Java class type for the environment repositories to provide details about.") String classType) {
		StringBuilder details = new StringBuilder();
		for (EnvironmentRepository environmentRepository : allEnvironmentRepositories) {
			if (classType.contains(environmentRepository.getClass().getSimpleName())) {
				details.append(environmentRepository).append("/n");
			}
		}
		return details.toString();
	}

	@Tool(description = "Gets properties representation for an application's configuration given its profile and label.")
	public String getsProperties(String applicationName, String profile) throws IOException {
		return environmentController.properties(applicationName, profile, false).getBody();
	}

	@Tool(description = "Gets a yaml representation for an application's  configuration given its profile and label.")
	public String getsYaml(String applicationName, String profile, String label) throws Exception {
		return environmentController.labelledYaml(applicationName, profile, label, false).getBody();
	}

	@Tool(description = "Gets a JSON representation for an application's configuration given its profile and label.")
	public String getsJson(String applicationName, String profile, String label) throws Exception {
		return environmentController.labelledJsonProperties(applicationName, profile, label, false).getBody();
	}

	@Tool(description = "Encrypts a value.")
	public String encrypt(String value) {
		return this.encryptionController.encrypt(value, MediaType.TEXT_PLAIN);
	}

	@Tool(description = "Decrypts a value.")
	public String decrypt(String value) {
		return this.encryptionController.decrypt(value, MediaType.TEXT_PLAIN);
	}

	@Override
	public void setApplicationEventPublisher(ApplicationEventPublisher applicationEventPublisher) {
		this.publisher = applicationEventPublisher;
	}

}
