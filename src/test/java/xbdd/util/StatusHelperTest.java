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

import static org.hamcrest.Matchers.is;

import static org.junit.Assert.assertThat;

import java.util.Arrays;

import org.junit.Before;
import org.junit.Test;

import xbdd.model.Feature;
import xbdd.model.Scenario;
import xbdd.model.cucumber.CucumberResult;
import xbdd.model.cucumber.CucumberStep;

public class StatusHelperTest {

	private Feature feature;

	private final CucumberResult undefined = new CucumberResult("undefined");
	private final CucumberResult passed = new CucumberResult("passed");

	@Before
	public void setup() {

		this.feature = new Feature();

		final Scenario scenario1 = new Scenario();
		final CucumberStep step1 = new CucumberStep();
		step1.setResult(this.undefined);
		scenario1.setSteps(Arrays.asList(step1));

		final Scenario scenario2 = new Scenario();
		final CucumberStep step2 = new CucumberStep();
		step2.setResult(this.undefined);
		final CucumberStep step3 = new CucumberStep();
		step3.setResult(this.passed);
		scenario2.setSteps(Arrays.asList(step2, step3));

		this.feature.setScenarios(Arrays.asList(scenario1, scenario2));
	}

	@Test
	public void testReduceStatusFailed() {
		assertThat(StatusHelper.reduceStatuses(Arrays.asList("failed")), is(Statuses.FAILED));
		assertThat(StatusHelper.reduceStatuses(Arrays.asList("failed", "undefined", "skipped", "passed")), is(Statuses.FAILED));
	}

	@Test
	public void testReduceStatusUndefined() {
		assertThat(StatusHelper.reduceStatuses(Arrays.asList("undefined")), is(Statuses.UNDEFINED));
		assertThat(StatusHelper.reduceStatuses(Arrays.asList("undefined", "skipped", "passed")), is(Statuses.UNDEFINED));
	}

	@Test
	public void testReduceStatusSkipped() {
		assertThat(StatusHelper.reduceStatuses(Arrays.asList("skipped")), is(Statuses.SKIPPED));
		assertThat(StatusHelper.reduceStatuses(Arrays.asList("skipped", "passed")), is(Statuses.SKIPPED));
	}

	@Test
	public void testReduceStatusPassed() {
		assertThat(StatusHelper.reduceStatuses(Arrays.asList("passed")), is(Statuses.PASSED));
		assertThat(StatusHelper.reduceStatuses(Arrays.asList("passed", "passed")), is(Statuses.PASSED));
	}

	@Test
	public void testReduceStatusUnknown() {
		assertThat(StatusHelper.reduceStatuses(Arrays.asList("notAStatus")),
				is(Statuses.UNKNOWN));
		assertThat(StatusHelper.reduceStatuses(Arrays.asList("notAStatus", "notAStatusEither")),
				is(Statuses.UNKNOWN));
	}
}
