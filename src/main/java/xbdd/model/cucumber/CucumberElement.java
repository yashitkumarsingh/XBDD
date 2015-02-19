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

@JsonIgnoreProperties(ignoreUnknown = true)
public class CucumberElement extends CucumberBaseModel {
	private String description;
	private String type;
	private List<CucumberTag> tags = new ArrayList<>();
	private List<CucumberStep> steps = new ArrayList<>();

	public CucumberElement() {
	}

	public String getDescription() {
		return this.description;
	}

	public void setDescription(final String description) {
		this.description = description;
	}

	public List<CucumberTag> getTags() {
		return this.tags;
	}

	public void setTags(final List<CucumberTag> tags) {
		this.tags = tags;
	}

	public List<CucumberStep> getSteps() {
		return this.steps;
	}

	public void setSteps(final List<CucumberStep> steps) {
		this.steps = steps;
	}

	public String getType() {
		return this.type;
	}

	public void setType(final String type) {
		this.type = type;
	}

	@Override
	public String toString() {
		return "CucumberElement [description=" + this.description + ", type=" + this.type + ", tags=" + this.tags + ", steps=" + this.steps
				+ "]";
	}

}
