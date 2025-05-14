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

import java.util.List;

import org.springframework.ai.tool.ToolCallbackProvider;
import org.springframework.ai.tool.method.MethodToolCallbackProvider;
import org.springframework.cloud.config.server.encryption.EncryptionController;
import org.springframework.cloud.config.server.environment.EnvironmentController;
import org.springframework.cloud.config.server.environment.EnvironmentRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author Ryan Baxter
 */
@Configuration
public class ConfigServerMcpConfiguration {

	@Bean
	public ToolCallbackProvider routeTools(ConfigServerTools configServerTools) {
		return MethodToolCallbackProvider.builder().toolObjects(configServerTools).build();
	}

	@Bean
	public ConfigServerTools configServerTools(EnvironmentRepository environmentRepository,
			EnvironmentController environmentController, NativeEnvironmentRepositoryUpdater updater,
			List<EnvironmentRepository> environmentRepositories, EncryptionController encryptionController) {
		return new ConfigServerTools(environmentRepository, environmentRepositories, environmentController,
				encryptionController, updater);
	}

	@Bean
	public NativeEnvironmentRepositoryUpdater nativeEnvironmentRepositoryUpdater() {
		return new NativeEnvironmentRepositoryUpdater();
	}

}
