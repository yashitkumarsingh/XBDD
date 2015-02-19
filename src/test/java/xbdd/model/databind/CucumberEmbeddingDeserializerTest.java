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
import static org.hamcrest.Matchers.nullValue;

import static org.junit.Assert.assertThat;

import java.io.IOException;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;

import xbdd.model.cucumber.CucumberEmbedding;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.databind.DeserializationContext;

@RunWith(MockitoJUnitRunner.class)
public class CucumberEmbeddingDeserializerTest {

	@Mock
	private JsonParser jp;
	@Mock
	private DeserializationContext ctxt;

	private final CucumberEmbeddingDeserializer deserializer = new CucumberEmbeddingDeserializer();

	@Test
	public void testDeserializeEmbededReference() throws IOException {
		Mockito.when(this.jp.getCurrentToken()).thenReturn(JsonToken.START_OBJECT);
		final EmbededReference ref = new EmbededReference();
		ref.setFilename("test-filename");
		ref.setMimeType("test-mime");
		Mockito.when(this.jp.readValueAs(EmbededReference.class)).thenReturn(ref);

		final CucumberEmbedding actual = this.deserializer.deserialize(this.jp, this.ctxt);

		assertThat(actual.getFilename(), is("test-filename"));
		assertThat(actual.getMimeType(), is("test-mime"));
		assertThat(actual.getData(), nullValue());
	}

	@Test
	public void testDeserializeAsFilename() throws IOException {
		Mockito.when(this.jp.getCurrentToken()).thenReturn(JsonToken.VALUE_STRING);

		Mockito.when(this.jp.getValueAsString()).thenReturn("test-filename");

		final CucumberEmbedding actual = this.deserializer.deserialize(this.jp, this.ctxt);

		assertThat(actual.getFilename(), is("test-filename"));
		assertThat(actual.getMimeType(), nullValue());
		assertThat(actual.getData(), nullValue());
	}

}
