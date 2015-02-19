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
package xbdd.model.databind;

import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;

import java.io.IOException;

import org.apache.commons.codec.binary.Base64;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;

import xbdd.model.Coordinates;
import xbdd.model.cucumber.CucumberEmbedding;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.mongodb.gridfs.GridFS;
import com.mongodb.gridfs.GridFSInputFile;

@RunWith(MockitoJUnitRunner.class)
public class CucumberEmbeddingSerializerTest {

	@Mock
	private GridFS gridFs;

	@Mock
	private JsonGenerator jgen;

	@Mock
	private SerializerProvider provider;

	@InjectMocks
	private CucumberEmbeddingSerializer serializer;

	private Coordinates coordinates;

	@Before
	public void setup() {
		this.coordinates = new Coordinates();
		this.coordinates.setBuild("build");
		this.coordinates.setMajor(1);
		this.coordinates.setMinor(2);
		this.coordinates.setServicePack(3);
		this.coordinates.setProduct("test");

		Mockito.when(this.provider.getAttribute("coordinates")).thenReturn(this.coordinates);
	}

	@Test
	public void testSerializeCucumberEmbeddingWithData() throws JsonProcessingException, IOException {
		final CucumberEmbedding embedding = new CucumberEmbedding();

		final byte[] data = new byte[] { 0xB, 0xE, 0xE, 0xF };
		embedding.setData(Base64.encodeBase64String(data));

		final String mimeType = "image/png";
		embedding.setMimeType(mimeType);

		final GridFSInputFile file = Mockito.mock(GridFSInputFile.class);
		Mockito.when(this.gridFs.createFile(data)).thenReturn(file);

		this.serializer.serialize(embedding, this.jgen, this.provider);

		Mockito.verify(file).setContentType(mimeType);
		Mockito.verify(file).setMetaData(this.coordinates.getReportCoordinates());
		Mockito.verify(file).save();

		final ArgumentCaptor<EmbededReference> actualRefCap = ArgumentCaptor.forClass(EmbededReference.class);
		Mockito.verify(this.jgen).writeObject(actualRefCap.capture());

		final EmbededReference actual = actualRefCap.getValue();

		Assert.assertThat(actual.getFilename(), notNullValue());
		Assert.assertThat(actual.getMimeType(), is(mimeType));
	}

	@Test
	public void testSerializeCucumberEmbeddingWithFilename() throws JsonProcessingException, IOException {
		final CucumberEmbedding embedding = new CucumberEmbedding();
		final String mimeType = "image/png";
		embedding.setMimeType(mimeType);
		final String filename = "test-file";
		embedding.setFilename(filename);

		this.serializer.serialize(embedding, this.jgen, this.provider);

		final ArgumentCaptor<EmbededReference> actualRefCap = ArgumentCaptor.forClass(EmbededReference.class);
		Mockito.verify(this.jgen).writeObject(actualRefCap.capture());

		final EmbededReference actual = actualRefCap.getValue();

		Assert.assertThat(actual.getFilename(), is(filename));
		Assert.assertThat(actual.getMimeType(), is(mimeType));
	}
}
