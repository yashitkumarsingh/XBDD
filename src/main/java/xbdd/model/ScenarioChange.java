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

import java.util.List;

public class ScenarioChange {

	private String scenario;
	private List<StepChange> changes;

	public void setScenario(final String scenarioName) {
		this.scenario = scenarioName;
	}

	public String getScenario() {
		return this.scenario;
	}

	public List<StepChange> getChanges() {
		return this.changes;
	}

	public void setChanges(final List<StepChange> temp) {
		this.changes = temp;
	}

	@Override
	public String toString() {
		return "ScenarioChange [scenario=" + this.scenario + ", changes=" + this.changes + "]";
	}

}
