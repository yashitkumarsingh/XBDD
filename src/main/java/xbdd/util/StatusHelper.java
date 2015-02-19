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
package xbdd.util;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import xbdd.model.Feature;
import xbdd.model.Scenario;
import xbdd.model.cucumber.CucumberResult;
import xbdd.model.cucumber.CucumberStep;

public class StatusHelper {


	/**
	 * For a given collection statuses return the "reduced" state.
	 * 
	 * @param allStatuses
	 * @return the reduced state
	 */
	public static Statuses reduceStatuses(final Collection<String> allStatuses) {
		if (allStatuses.contains("failed")) {
			return Statuses.FAILED;
		}
		if (allStatuses.contains("undefined")) {
			return Statuses.UNDEFINED;
		}
		if (allStatuses.contains("skipped")) {
			return Statuses.SKIPPED;
		}
		if (allStatuses.contains("passed")) {
			return Statuses.PASSED;
		} else {
			return Statuses.UNKNOWN;
		}
	}

	// go through all the steps in a scenario and reduce to a status for the scenario.
	/**
	 * Reduce the status of all steps in the {@link Scenario}
	 * 
	 * @param scenario the {@link Scenario}
	 * @param includeManualResults whether to include the results of manual tests
	 * @return the reduced status
	 */
	public static Statuses getFinalScenarioStatus(final Scenario scenario, final boolean includeManualResults) {
		final ArrayList<String> allStatuses = new ArrayList<String>();
		final List<CucumberStep> steps = scenario.getSteps();
		if (includeManualResults) { // if we have got a bunch of manual step executions
			boolean hasManuallyExecutedSteps = false;
			final List<String> manualSteps = new ArrayList<String>();
			// go through each step creating an array as though they were manual
			if (steps != null) {
				for (final CucumberStep step : steps) {
					final CucumberResult result = step.getResult();
					if (result != null) {
						if (result.getManualStatus() != null) {
							manualSteps.add(result.getManualStatus()); // if there is manual status include it
							hasManuallyExecutedSteps = true; // mark that there is a manual step executed
						} else {
							manualSteps.add("undefined"); // otherwise it is effectively unexecuted/undefined
						}
					}
				}
			}
			// do the same for the background steps
			if (scenario.getBackground() != null) {// only if there are background steps.
				final List<CucumberStep> backgroundSteps = scenario.getBackground().getSteps();
				if (backgroundSteps != null) {
					for (final CucumberStep backGroundStep : backgroundSteps) {
						final CucumberResult result = backGroundStep.getResult();
						if (result != null) {
							final String manualStatus = result.getManualStatus();
							if (manualStatus != null) {
								manualSteps.add(manualStatus); // if there is manual status include it
								hasManuallyExecutedSteps = true; // mark that there is a manual step executed
							} else {
								manualSteps.add("undefined"); // otherwise it is effectively unexecuted/undefined
							}
						}
					}
				}
			}
			if (hasManuallyExecutedSteps) { // if any steps have been executed
				allStatuses.addAll(manualSteps);// then treat this scenario as though it has been manually executed.
			} else {
				if (steps != null) {
					for (final CucumberStep step : steps) {
						final CucumberResult result = step.getResult();
						if (result == null) {
							throw new RuntimeException(
									"You are missing a 'result' element in your steps, perhaps you need to use a later version of cucumber to generate your report (>1.1.3)?'");
						}
						allStatuses.add(result.getStatus());// otherwise just include whatever automated step statuses exist.
					}
				}
				if (scenario.getBackground() != null) {
					final List<CucumberStep> backgroundSteps = scenario.getBackground().getSteps();
					if (backgroundSteps != null) {
						for (final CucumberStep step : backgroundSteps) {
							final CucumberResult result = step.getResult();
							allStatuses.add(result.getStatus());// make sure to include the background steps too.
						}
					}
				}
			}
		} else { // if we are not including manual steps then just include the automated statuses.
			if (steps != null) {
				for (final CucumberStep step : steps) {
					final CucumberResult result = step.getResult();
					allStatuses.add(result.getStatus());
				}
			}
			if (scenario.getBackground() != null) {
				final List<CucumberStep> backgroundSteps = scenario.getBackground().getSteps();
				if (backgroundSteps != null) {
					for (final CucumberStep step : backgroundSteps) {
						allStatuses.add(step.getResult().getStatus());// make sure to include the background steps too.
					}
				}
			}
		}

		return reduceStatuses(allStatuses);
	}

	private static String getScenarioStatus(final Scenario scenario) {
		return getFinalScenarioStatus(scenario, true).getTextName();
	}

	private static boolean isScenarioKeyword(final String keyword) {
		if (keyword.equals("Scenario") || keyword.equals("Scenario Outline")) {
			return true;
		} else {
			return false;
		}
	}

	public static String getFeatureStatus(final Feature feature) {
		final List<String> allStatuses = new ArrayList<>();
		final List<Scenario> featureElements = feature.getScenarios();
		if (featureElements != null) {
			for (final Scenario scenario : featureElements) {
				if (isScenarioKeyword(scenario.getKeyword())) {
					allStatuses.add(getScenarioStatus(scenario));
				}
			}
		}

		final String result = reduceStatuses(allStatuses).getTextName();
		return result;
	}
}
