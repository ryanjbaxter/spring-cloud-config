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

import java.io.File;
import java.io.IOException;
import java.util.Map;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLMapper;

/**
 * @author Ryan Baxter
 */
public class NativeEnvironmentRepositoryUpdater {

	public NativeEnvironmentRepositoryUpdater() {
	}

	public void update(String path, String property, String value) throws IOException {
		ObjectMapper mapper = new YAMLMapper();
		Map<String, Object> data = mapper.readValue(new File(path.strip()), new TypeReference<Map<String, Object>>() {
		});

		data.put(property, value);
		mapper.writeValue(new File(path), data);
	}

}
