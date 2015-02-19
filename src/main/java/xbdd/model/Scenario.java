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
package xbdd.model;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.jongo.marshall.jackson.oid.Id;

import xbdd.model.cucumber.CucumberStep;
import xbdd.model.cucumber.CucumberTag;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Scenario extends BaseModel {
	@Id
	private String objectId;
	private String description;
	private String type;
	private List<CucumberTag> tags = new ArrayList<>();
	private List<CucumberStep> steps = new ArrayList<>();

	private String result;
	@JsonProperty("execution-notes")
	private String executionNotes = "";
	@JsonProperty("environment-notes")
	private String environment = "";
	@JsonProperty("testing-tips")
	private String testingTips = "";
	private Background background;

	public Scenario() {
	}

	public String getObjectId() {
		return this.objectId;
	}

	public void setObjectId(final String objectId) {
		this.objectId = objectId;
	}

	public String getDescription() {
		return this.description;
	}

	public void setDescription(final String description) {
		this.description = description;
	}

	public String getResult() {
		return this.result;
	}

	public void setResult(final String result) {
		this.result = result;
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

	public String getExecutionNotes() {
		return this.executionNotes;
	}

	public void setExecutionNotes(final String executionNotes) {
		this.executionNotes = executionNotes;
	}

	public String getEnvironment() {
		return this.environment;
	}

	public void setEnvironment(final String environment) {
		this.environment = environment;
	}

	public String getTestingTips() {
		return this.testingTips;
	}

	public void setTestingTips(final String testingTips) {
		this.testingTips = testingTips;
	}

	public void setBackground(final Background background) {
		this.background = background;
	}

	public Background getBackground() {
		return this.background;
	}

	@Override
	public int hashCode() {
		return new HashCodeBuilder().appendSuper(super.hashCode())
				.append(this.description)
				.append(this.steps)
				.append(this.tags)
				.append(this.testingTips).toHashCode();
	}

	@Override
	public boolean equals(final Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}
		final Scenario other = (Scenario) obj;
		return new EqualsBuilder().appendSuper(super.equals(obj))
				.append(this.description, other.description)
				.append(this.steps, other.steps)
				.append(this.tags, other.tags)
				.append(this.testingTips, other.testingTips).isEquals();
	}

	@Override
	public String toString() {
		return "Scenario [getName()=" + getName() + ", getId()=" + getId() + ", getKeyword()=" + getKeyword() + ", getLine()=" + getLine()
				+ ", objectId=" + this.objectId + ", description=" + this.description + ", type=" + this.type + ", tags=" + this.tags
				+ ", steps=" + this.steps + ", result=" + this.result + ", executionNotes=" + this.executionNotes + ", environment="
				+ this.environment + ", testingTips=" + this.testingTips + ", background=" + this.background + "]";
	}

}
