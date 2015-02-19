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

import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.when;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.glassfish.jersey.server.mvc.Viewable;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;

public class IndexViewTest {

	private final IndexView view = new IndexView();
	
	@Mock
	private HttpServletRequest client;


	@SuppressWarnings({"unchecked"})
	@Test
	public void testView() {
		client = Mockito.mock(HttpServletRequest.class);
		when(client.isUserInRole(anyString())).thenReturn(true);
		final Viewable viewable = this.view.getIt(this.client);
		Assert.assertThat(viewable.getTemplateName(), Matchers.is(IndexView.VIEW_ID));
		final Map<String, Boolean> expected = new HashMap<String, Boolean>();
		expected.put("isAdmin", true);
		Assert.assertThat((Map<String, Boolean>) viewable.getModel(), Matchers.is((expected)));
	}

}
