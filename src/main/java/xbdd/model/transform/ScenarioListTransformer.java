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
package xbdd.model.transform;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import xbdd.model.Background;
import xbdd.model.Scenario;
import xbdd.model.cucumber.CucumberElement;

import com.google.common.base.Function;

/**
 * Transform a list of {@link CucumberElement} into a list of {@link Scenario}s with {@link Background} elements.
 */
public class ScenarioListTransformer implements Function<List<CucumberElement>, List<Scenario>> {

	@Override
	public List<Scenario> apply(final List<CucumberElement> input) {
		final List<Scenario> scenarios = new ArrayList<>();
		final Iterator<CucumberElement> scenarioIT = input.iterator();
		while (scenarioIT.hasNext()) {
			final CucumberElement element = scenarioIT.next();
			final Scenario scenario;
			if ("background".equals(element.getType()) && scenarioIT.hasNext()) {
				final Background background = new Background();
				background.setId(element.getId());
				background.setKeyword(element.getKeyword());
				background.setName(element.getName());
				background.setDescription(element.getDescription());
				if (element.getTags() != null) {
					background.setTags(new ArrayList<>(element.getTags()));
				}
				if (element.getSteps() != null) {
					background.setSteps(new ArrayList<>(element.getSteps()));
				}

				// Pack background into the corresponding scenario
				scenario = createScenario(scenarioIT.next());
				scenario.setBackground(background);
			} else {
				scenario = createScenario(element);
			}
			scenarios.add(scenario);
		}

		return scenarios;
	}

	private Scenario createScenario(final CucumberElement element) {
		final Scenario scenario = new Scenario();
		scenario.setType(element.getType());
		scenario.setId(element.getId());
		scenario.setKeyword(element.getKeyword());
		scenario.setName(element.getName());
		scenario.setLine(element.getLine());
		scenario.setDescription(element.getDescription());

		if (element.getTags() != null) {
			scenario.setTags(new ArrayList<>(element.getTags()));
		}

		if (element.getSteps() != null) {
			scenario.setSteps(new ArrayList<>(element.getSteps()));
		}
		return scenario;
	}

}
