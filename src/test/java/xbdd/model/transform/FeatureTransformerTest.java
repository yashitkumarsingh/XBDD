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

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;

import static org.junit.Assert.assertThat;

import java.util.Arrays;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import xbdd.model.Feature;
import xbdd.model.Scenario;
import xbdd.model.cucumber.CucumberElement;
import xbdd.model.cucumber.CucumberFeature;
import xbdd.model.cucumber.CucumberTag;

public class FeatureTransformerTest {

	private final CucumberFeature cuke = new CucumberFeature();

	private final FeatureTransformer transformer = new FeatureTransformer(new ScenarioListTransformer());

	@Rule
	public ExpectedException expectedException = ExpectedException.none();

	@Test
	public void testTransformNullFeature() {
		this.expectedException.expect(NullPointerException.class);
		this.transformer.apply(null);
	}

	@Test
	public void testTransformBasicProperties() {
		this.cuke.setId("test-id");
		this.cuke.setDescription("test-description");
		this.cuke.setKeyword("Feature");
		this.cuke.setLine(2);
		this.cuke.setUri("test-uri");

		final Feature actual = this.transformer.apply(this.cuke);

		assertThat(actual.getId(), is("test-id"));
		assertThat(actual.getDescription(), is("test-description"));
		assertThat(actual.getKeyword(), is("Feature"));
		assertThat(actual.getLine(), is(Integer.valueOf(2)));
		assertThat(actual.getUri(), is("test-uri"));
	}

	@Test
	public void testTransformTags() {
		final CucumberTag tag1 = new CucumberTag();
		tag1.setLine(1);
		tag1.setName("tag1");

		final CucumberTag tag2 = new CucumberTag();
		tag2.setLine(2);
		tag2.setName("tag2");
		this.cuke.setTags(Arrays.asList(tag1, tag2));

		final Feature actual = this.transformer.apply(this.cuke);

		assertThat(actual.getTags(), is(Arrays.asList(tag1, tag2)));
	}

	@Test
	public void testTransformScenario() {
		final CucumberElement cucumberBackground = new CucumberElement();
		cucumberBackground.setName("test-background");
		cucumberBackground.setType("background");

		final CucumberElement cucumberScenario = new CucumberElement();
		cucumberScenario.setName("test-scenario");
		cucumberScenario.setType("scenario");

		this.cuke.setScenarios(Arrays.asList(cucumberBackground, cucumberScenario));

		final Feature actual = this.transformer.apply(this.cuke);

		assertThat(actual.getScenarios(), hasSize(1));
		final Scenario actualScenario = actual.getScenarios().get(0);
		assertThat(actualScenario.getName(), is("test-scenario"));
		assertThat(actualScenario.getBackground().getName(), is("test-background"));
	}
}
