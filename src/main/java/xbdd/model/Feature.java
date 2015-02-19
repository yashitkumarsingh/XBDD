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
import java.util.Date;
import java.util.List;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.jongo.marshall.jackson.oid.Id;

import xbdd.model.cucumber.CucumberTag;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Represents a Feature.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class Feature extends BaseModel {

	private List<CucumberTag> tags;
	private String uri;
	private String description;
	@JsonProperty("elements")
	private List<Scenario> scenarios = new ArrayList<>();

	@Id
	private String objectId;
	private String result;
	private Coordinates coordinates;
	private String calculatedStatus;
	private String originalAutomatedStatus;
	private String statusLastEditedBy;
	private Date lastEditOn;
	private List<Edit> edits;

	public Feature() {
	}

	public String getObjectId() {
		return this.objectId;
	}

	public void setObjectId(final String objectId) {
		this.objectId = objectId;
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

	public List<Scenario> getScenarios() {
		return this.scenarios;
	}

	public void setScenarios(final List<Scenario> scenarios) {
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

	public Coordinates getCoordinates() {
		return this.coordinates;
	}

	public void setCoordinates(final Coordinates co) {
		this.coordinates = co;
	}

	public void setCalculatedStatus(final String status) {
		this.calculatedStatus = status;
	}

	public String getCalculatedStatus() {
		return this.calculatedStatus;
	}

	public void setOriginalAutomatedStatus(final String originalStatus) {
		this.originalAutomatedStatus = originalStatus;

	}

	public String getOriginalAutomatedStatus() {
		return this.originalAutomatedStatus;
	}

	public String getStatusLastEditedBy() {
		return this.statusLastEditedBy;
	}

	public void setStatusLastEditedBy(final String remoteUser) {
		this.statusLastEditedBy = remoteUser;
	}

	public Date getLastEditOn() {
		return this.lastEditOn;
	}

	public void setLastEditOn(final Date date) {
		this.lastEditOn = date;

	}

	public void setEdits(final List<Edit> edits) {
		this.edits = edits;
	}

	public List<Edit> getEdits() {
		return this.edits;
	}

	@Override
	public int hashCode() {
		return new HashCodeBuilder().appendSuper(super.hashCode())
				.append(this.result)
				.append(this.scenarios)
				.append(this.coordinates)
				.append(this.description)
				.append(this.uri)
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
		final Feature other = (Feature) obj;
		return new EqualsBuilder().appendSuper(super.equals(obj))
				.append(this.result, other.result)
				.append(this.scenarios, other.scenarios)
				.append(this.coordinates, other.coordinates)
				.append(this.description, other.description)
				.append(this.uri, other.uri)
				.append(this.tags, other.tags).isEquals();
	}

	@Override
	public String toString() {
		return "Feature [getName()=" + getName() + ", getId()=" + getId() + ", getKeyword()=" + getKeyword() + ", getLine()=" + getLine()
				+ ", tags=" + this.tags + ", uri=" + this.uri + ", description=" + this.description + ", scenarios=" + this.scenarios
				+ ", objectId=" + this.objectId + ", result=" + this.result + ", coordinates=" + this.coordinates + ", calculatedStatus="
				+ this.calculatedStatus + ", originalAutomatedStatus=" + this.originalAutomatedStatus + ", statusLastEditedBy="
				+ this.statusLastEditedBy + ", lastEditOn=" + this.lastEditOn + ", edits=" + this.edits + "]";
	}

}
