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

import java.util.Date;
import java.util.List;

public class Edit {

	private String statusLastEditedBy;
	private Date lastEditOn;
	private String previousStatus;
	private String currentStatus;
	private List<ScenarioChange> stepChanges;

	public String getName() {
		return this.statusLastEditedBy;
	}

	public void setName(final String statusLastEditedBy) {
		this.statusLastEditedBy = statusLastEditedBy;
	}

	public Date getDate() {
		return this.lastEditOn;
	}

	public void setDate(final Date lastEditOn) {
		this.lastEditOn = lastEditOn;

	}

	public String getPreviousStatus() {
		return this.previousStatus;
	}

	public void setPreviousStatus(final String calculatedStatus) {
		this.previousStatus = calculatedStatus;

	}

	public String getCurrentStatus() {
		return this.currentStatus;
	}

	public void setCurrentStatus(final String calculatedStatus) {
		this.currentStatus = calculatedStatus;

	}

	public void setStepChanges(final List<ScenarioChange> constructEditStepChanges) {
		this.stepChanges = constructEditStepChanges;

	}

	public List<ScenarioChange> getStepChanges() {
		return this.stepChanges;
	}

	@Override
	public String toString() {
		return "Edit [statusLastEditedBy=" + this.statusLastEditedBy + ", lastEditOn=" + this.lastEditOn + ", previousStatus="
				+ this.previousStatus + ", currentStatus=" + this.currentStatus + ", stepChanges=" + this.stepChanges + "]";
	}

}
