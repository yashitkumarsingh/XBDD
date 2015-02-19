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

import xbdd.model.cucumber.CucumberStep;
import xbdd.model.cucumber.CucumberTag;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Background extends BaseModel {

	private String description;
	private String type;
	private List<CucumberTag> tags = new ArrayList<>();
	private List<CucumberStep> steps = new ArrayList<>();

	private String result;

	public Background() {
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

	@Override
	public int hashCode() {
		return new HashCodeBuilder().appendSuper(super.hashCode())
				.append(this.description)
				.append(this.steps)
				.append(this.type)
				.append(this.tags).toHashCode();
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
		final Background other = (Background) obj;
		return new EqualsBuilder().appendSuper(super.equals(obj))
				.append(this.description, other.description)
				.append(this.steps, other.steps)
				.append(this.type, other.type)
				.append(this.tags, other.tags).isEquals();
	}

	@Override
	public String toString() {
		return "Background [getName()=" + getName() + ", getId()=" + getId() + ", getKeyword()=" + getKeyword() + ", getLine()="
				+ getLine() + ", description=" + this.description + ", type=" + this.type + ", tags=" + this.tags + ", steps=" + this.steps
				+ ", result=" + this.result + "]";
	}

}
