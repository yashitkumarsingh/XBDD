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
import static org.hamcrest.Matchers.notNullValue;
import static org.hamcrest.Matchers.nullValue;

import static org.junit.Assert.assertThat;

import java.util.Arrays;
import java.util.List;

import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import xbdd.model.Scenario;
import xbdd.model.cucumber.CucumberElement;
import xbdd.model.cucumber.CucumberStep;
import xbdd.model.cucumber.CucumberTag;

public class ScenarioListTransformerTest {

	private final ScenarioListTransformer transformer = new ScenarioListTransformer();

	@Rule
	public ExpectedException expectedException = ExpectedException.none();

	@Test
	public void testTransformNull() {
		this.expectedException.expect(NullPointerException.class);
		this.transformer.apply(null);
	}

	@Test
	public void testTransformBasicScenario() {
		final CucumberElement scenario = new CucumberElement();
		scenario.setType("scenario");
		scenario.setId("test-id");
		scenario.setKeyword("Scenario");
		scenario.setName("test-name");
		scenario.setDescription("test-description");
		scenario.setLine(4);

		final List<Scenario> actual = this.transformer.apply(Arrays.asList(scenario));

		Assert.assertThat(actual, hasSize(1));

		final Scenario actualScenario = actual.get(0);
		assertThat(actualScenario.getType(), is("scenario"));
		assertThat(actualScenario.getId(), is("test-id"));
		assertThat(actualScenario.getKeyword(), is("Scenario"));
		assertThat(actualScenario.getName(), is("test-name"));
		assertThat(actualScenario.getDescription(), is("test-description"));
		assertThat(actualScenario.getLine(), is(Integer.valueOf(4)));

	}

	@Test
	public void testTransformTags() {
		final CucumberElement scenario = new CucumberElement();
		scenario.setType("scenario");

		final CucumberTag tag1 = new CucumberTag();
		tag1.setLine(1);
		tag1.setName("tag1");

		final CucumberTag tag2 = new CucumberTag();
		tag2.setLine(2);
		tag2.setName("tag2");
		scenario.setTags(Arrays.asList(tag1, tag2));

		final List<Scenario> actual = this.transformer.apply(Arrays.asList(scenario));

		Assert.assertThat(actual, hasSize(1));

		final Scenario actualScenario = actual.get(0);
		Assert.assertThat(actualScenario.getTags(), is(Arrays.asList(tag1, tag2)));
	}

	@Test
	public void testTransformSteps() {
		final CucumberElement scenario = new CucumberElement();
		scenario.setType("scenario");

		final CucumberStep step1 = new CucumberStep();
		step1.setId("step1");

		final CucumberStep step2 = new CucumberStep();
		step2.setId("step2");

		scenario.setSteps(Arrays.asList(step1, step2));

		final List<Scenario> actual = this.transformer.apply(Arrays.asList(scenario));

		Assert.assertThat(actual, hasSize(1));

		final Scenario actualScenario = actual.get(0);
		Assert.assertThat(actualScenario.getSteps(), is(Arrays.asList(step1, step2)));
	}

	@Test
	public void testTransformScenarioWithBackgroundBasic() {
		final CucumberElement background = new CucumberElement();
		background.setType("background");
		background.setId("background-id");
		background.setKeyword("Background");
		background.setName("test-name");
		background.setDescription("test-description");

		final CucumberElement scenario = new CucumberElement();
		scenario.setType("scenario");
		scenario.setName("scenario-name");

		final List<Scenario> actual = this.transformer.apply(Arrays.asList(background, scenario));

		Assert.assertThat(actual, hasSize(1));

		final Scenario actualScenario = actual.get(0);
		assertThat(actualScenario.getName(), is("scenario-name"));
		assertThat(actualScenario.getBackground(), notNullValue());
		assertThat(actualScenario.getBackground().getId(), is("background-id"));
		assertThat(actualScenario.getBackground().getKeyword(), is("Background"));
		assertThat(actualScenario.getBackground().getName(), is("test-name"));
		assertThat(actualScenario.getBackground().getDescription(), is("test-description"));
	}

	@Test
	public void testTransformBackgroundOnly() {
		final CucumberElement background = new CucumberElement();
		background.setType("background");
		background.setId("background-id");

		final List<Scenario> actual = this.transformer.apply(Arrays.asList(background));

		Assert.assertThat(actual, hasSize(1));

		final Scenario actualScenario = actual.get(0);
		Assert.assertThat(actualScenario.getBackground(), nullValue());

		// TODO is this valid?
		assertThat(actualScenario.getId(), is("background-id"));
		assertThat(actualScenario.getType(), is("background"));
	}

	@Test
	public void testTransformScenarioWithBackgroundTags() {
		final CucumberElement background = new CucumberElement();
		background.setType("background");

		final CucumberTag tag1 = new CucumberTag();
		tag1.setLine(1);
		tag1.setName("tag1");

		final CucumberTag tag2 = new CucumberTag();
		tag2.setLine(2);
		tag2.setName("tag2");

		background.setTags(Arrays.asList(tag1, tag2));

		final CucumberElement scenario = new CucumberElement();
		scenario.setType("scenario");

		final List<Scenario> actual = this.transformer.apply(Arrays.asList(background, scenario));

		Assert.assertThat(actual, hasSize(1));

		final Scenario actualScenario = actual.get(0);
		assertThat(actualScenario.getBackground(), notNullValue());
		assertThat(actualScenario.getBackground().getTags(), is(Arrays.asList(tag1, tag2)));
	}

	@Test
	public void testTransformScenarioWithBackgroundSteps() {
		final CucumberElement background = new CucumberElement();
		background.setType("background");

		final CucumberStep step1 = new CucumberStep();
		step1.setId("step1");

		final CucumberStep step2 = new CucumberStep();
		step2.setId("step2");

		background.setSteps(Arrays.asList(step1, step2));

		final CucumberElement scenario = new CucumberElement();
		scenario.setType("scenario");

		final List<Scenario> actual = this.transformer.apply(Arrays.asList(background, scenario));

		assertThat(actual, hasSize(1));

		final Scenario actualScenario = actual.get(0);
		assertThat(actualScenario.getBackground(), notNullValue());
		assertThat(actualScenario.getBackground().getSteps(), is(Arrays.asList(step1, step2)));
	}

}
