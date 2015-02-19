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

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Represents an embedded file. Data is base64 encoded string, filename is the id of the file saved in GridFS.
 */
public class EmbededReference {

	@JsonProperty("mime_type")
	private String mimeType;

	private String filename;

	public String getMimeType() {
		return this.mimeType;
	}

	public void setMimeType(final String mimeType) {
		this.mimeType = mimeType;
	}

	public String getFilename() {
		return this.filename;
	}

	public void setFilename(final String filename) {
		this.filename = filename;
	}

	@Override
	public String toString() {
		return "EmbededReference [mimeType=" + this.mimeType + ", filename=" + this.filename + "]";
	}

}
