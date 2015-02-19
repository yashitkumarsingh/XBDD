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
package xbdd.webapp.view;

import java.util.Map;

import org.glassfish.jersey.server.mvc.Viewable;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import xbdd.model.Coordinates;

public class PrintableReportViewTest {

	private final PrintableReportView printableReportView = new PrintableReportView();
	private Coordinates coordinates;

	@Before
	public void setup() {
		this.coordinates = new Coordinates();
		this.coordinates.setBuild("build");
		this.coordinates.setMajor(1);
		this.coordinates.setMinor(2);
		this.coordinates.setServicePack(3);
		this.coordinates.setProduct("test-product");
	}

	@SuppressWarnings("unchecked")
	@Test
	public void test() {
		final Viewable viewable = this.printableReportView.getIt(this.coordinates);

		Assert.assertThat(viewable.getTemplateName(), Matchers.is(PrintableReportView.VIEW_ID));
		final Map<String, String> model = (Map<String, String>) viewable.getModel();

		Assert.assertThat(model.get("product"), Matchers.is("test-product"));
		Assert.assertThat(model.get("version"), Matchers.is("1.2.3"));
		Assert.assertThat(model.get("build"), Matchers.is("build"));
	}

}
