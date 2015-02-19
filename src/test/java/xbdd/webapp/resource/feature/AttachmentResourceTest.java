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
package xbdd.webapp.resource.feature;

import java.io.InputStream;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;

import org.apache.commons.io.IOUtils;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;

import com.mongodb.gridfs.GridFS;
import com.mongodb.gridfs.GridFSDBFile;

@RunWith(MockitoJUnitRunner.class)
public class AttachmentResourceTest {

	@Mock
	private GridFS gridFS;

	@Mock
	private GridFSDBFile file;

	@InjectMocks
	private AttachmentResource attachmentResource;

	@Rule
	public ExpectedException expectedException = ExpectedException.none();

	@Test
	public void testGetNullAttachmentReturns404() {
		Mockito.when(this.gridFS.findOne("test-id")).thenReturn(null);
		this.expectedException.expect(WebApplicationException.class);

		this.attachmentResource.getAttachment("test-id");
	}

	@Test
	public void testGetAttachment() {

		final InputStream is = IOUtils.toInputStream("{ test: \"value\"}");
		Mockito.when(this.file.getContentType()).thenReturn("application/json");
		Mockito.when(this.file.getInputStream()).thenReturn(is);

		Mockito.when(this.gridFS.findOne("test-id")).thenReturn(this.file);

		final Response r = this.attachmentResource.getAttachment("test-id");

		Assert.assertEquals(200, r.getStatus());
	}
}
