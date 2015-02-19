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
import java.util.List;

import javax.inject.Inject;

import org.apache.commons.lang3.Validate;

import xbdd.model.Feature;
import xbdd.model.Scenario;
import xbdd.model.cucumber.CucumberFeature;

import com.google.common.base.Function;

public class FeatureTransformer implements Function<CucumberFeature, Feature> {

	private final ScenarioListTransformer scenarioListTransformer;

	@Inject
	public FeatureTransformer(final ScenarioListTransformer scenarioListTransformer) {
		this.scenarioListTransformer = scenarioListTransformer;
	}

	@Override
	public Feature apply(final CucumberFeature cucumberFeature) {
		Validate.notNull(cucumberFeature, "Argument 'cucumberFeature' cannot be null.");

		final Feature feature = new Feature();
		feature.setId(cucumberFeature.getId());
		feature.setDescription(cucumberFeature.getDescription());
		feature.setKeyword(cucumberFeature.getKeyword());
		feature.setName(cucumberFeature.getName());
		feature.setLine(cucumberFeature.getLine());
		feature.setUri(cucumberFeature.getUri());

		if (cucumberFeature.getTags() != null) {
			feature.setTags(new ArrayList<>(cucumberFeature.getTags()));
		}

		if (cucumberFeature.getScenarios() != null) {
			final List<Scenario> scenarios = this.scenarioListTransformer.apply(cucumberFeature.getScenarios());
			feature.setScenarios(scenarios);
		}

		return feature;
	}

}
