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
package xbdd.webapp.view.feature;

import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.when;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.glassfish.jersey.server.mvc.Viewable;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;

import xbdd.model.Coordinates;

public class HTMLReportViewTest {

	private final HTMLReportView view = new HTMLReportView();
	private Coordinates coordinates;
	
	@Mock
	private HttpServletRequest client;

	@Before
	public void setup() {
		this.coordinates = new Coordinates();
		this.coordinates.setBuild("build");
		this.coordinates.setMajor(1);
		this.coordinates.setMinor(2);
		this.coordinates.setServicePack(3);
		this.coordinates.setProduct("test-product");
		this.client = Mockito.mock(HttpServletRequest.class);
		when(this.client.isUserInRole(anyString())).thenReturn(true);
	}

	@SuppressWarnings("unchecked")
	@Test
	public void testView() {
		final Viewable viewable = this.view.getIt(this.coordinates, this.client);

		Assert.assertThat(viewable.getTemplateName(), Matchers.is(HTMLReportView.VIEW_ID));
		final Map<String, String> model = (Map<String, String>) viewable.getModel();

		Assert.assertThat(model.get("product"), Matchers.is("test-product"));
		Assert.assertThat(model.get("version"), Matchers.is("1.2.3"));
		Assert.assertThat(model.get("build"), Matchers.is("build"));
		Assert.assertThat(model.get("featureid"), Matchers.nullValue());
	}

	@SuppressWarnings("unchecked")
	@Test
	public void testViewWithFeatureId() {
		final Viewable viewable = this.view.getIt(this.coordinates, "feature-id", this.client);

		Assert.assertThat(viewable.getTemplateName(), Matchers.is(HTMLReportView.VIEW_ID));
		final Map<String, String> model = (Map<String, String>) viewable.getModel();

		Assert.assertThat(model.get("product"), Matchers.is("test-product"));
		Assert.assertThat(model.get("version"), Matchers.is("1.2.3"));
		Assert.assertThat(model.get("build"), Matchers.is("build"));
		Assert.assertThat(model.get("featureid"), Matchers.is("feature-id"));
	}
}
