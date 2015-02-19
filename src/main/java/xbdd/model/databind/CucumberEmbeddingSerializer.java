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

import java.io.IOException;
import java.util.UUID;

import javax.inject.Inject;

import org.apache.commons.codec.binary.Base64;

import xbdd.model.Coordinates;
import xbdd.model.cucumber.CucumberEmbedding;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.mongodb.BasicDBObject;
import com.mongodb.gridfs.GridFS;
import com.mongodb.gridfs.GridFSInputFile;

public class CucumberEmbeddingSerializer extends JsonSerializer<CucumberEmbedding> {
	private final GridFS gridFS;

	@Inject
	public CucumberEmbeddingSerializer(final GridFS gridFS) {
		this.gridFS = gridFS;
	}

	@Override
	public void serialize(final CucumberEmbedding value, final JsonGenerator jgen, final SerializerProvider provider)
			throws IOException,
			JsonProcessingException {

		final EmbededReference ref = new EmbededReference();
		if (value.getData() != null) {
			final GridFSInputFile inputFile = this.gridFS.createFile(Base64.decodeBase64(value.getData()));
			final String filename = UUID.randomUUID().toString();
			inputFile.setFilename(filename);

			final Coordinates coordinates = (Coordinates) provider.getAttribute("coordinates");

			// meta data to help identify which report a file belongs to
			final BasicDBObject metadata = coordinates.getReportCoordinates();

			inputFile.setMetaData(metadata);
			inputFile.setContentType(value.getMimeType());
			inputFile.save();

			ref.setFilename(filename);
		} else {
			ref.setFilename(value.getFilename());
		}

		ref.setMimeType(value.getMimeType());
		jgen.writeObject(ref);
	}
}
