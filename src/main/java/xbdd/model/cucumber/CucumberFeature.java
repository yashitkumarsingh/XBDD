/**
 * Copyright (C) 2015 Orion Health (Orchestral Development Ltd)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package xbdd.model.cucumber;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Represents a Feature.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class CucumberFeature extends CucumberBaseModel {

	private List<CucumberTag> tags;
	private String uri;
	private String description;
	@JsonProperty("elements")
	private List<CucumberElement> scenarios = new ArrayList<>();

	public CucumberFeature() {
	}

	public List<CucumberTag> getTags() {
		return this.tags;
	}

	public void setTags(final List<CucumberTag> tags) {
		this.tags = tags;
	}

	public List<CucumberElement> getScenarios() {
		return this.scenarios;
	}

	public void setScenarios(final List<CucumberElement> scenarios) {
		this.scenarios = scenarios;
	}

	public String getUri() {
		return this.uri;
	}

	public void setUri(final String uri) {
		this.uri = uri;
	}

	public String getDescription() {
		return this.description;
	}

	public void setDescription(final String description) {
		this.description = description;
	}

	@Override
	public String toString() {
		return "CucumberFeature [getName()=" + getName() + ", getId()=" + getId() + ", getKeyword()=" + getKeyword() + ", getLine()="
				+ getLine() + ", tags=" + this.tags + ", uri=" + this.uri + ", description=" + this.description + ", scenarios="
				+ this.scenarios + "]";
	}

}
