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
package xbdd.webapp.resource;

import java.net.URI;
import java.net.URISyntaxException;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class LogoutResourceTest {

	@Mock
	private HttpServletRequest request;
	@Mock
	private ServletContext context;

	private final LogoutResource logoutResource = new LogoutResource();

	@Test
	public void testLogoutRedirectsToContextPath() throws URISyntaxException, ServletException {

		Mockito.when(this.context.getContextPath()).thenReturn("/test-context");

		final Response r = this.logoutResource.logout(this.request, this.context);

		Mockito.verify(this.request).logout();
		Assert.assertEquals(r.getStatus(), Status.TEMPORARY_REDIRECT.getStatusCode());
		Assert.assertThat(r.getLocation(), Matchers.is(new URI("/test-context")));
	}

}
