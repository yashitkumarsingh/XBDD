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

import xbdd.model.Coordinates;
import xbdd.model.databind.CoordinatesConverter.SimpleCoordinates;

import com.fasterxml.jackson.databind.util.StdConverter;

/**
 * Convert {@link Coordinates} to a simple object with no synthetic properties as Jackson cannot seem to handle serializing a property by
 * getter only.
 */
public class CoordinatesConverter extends StdConverter<Coordinates, SimpleCoordinates> {

	public static class SimpleCoordinates {
		public String product;
		public Integer major;
		public Integer minor;
		public Integer servicePack;
		public String build;
		public String version;
	}

	@Override
	public SimpleCoordinates convert(final Coordinates value) {
		final SimpleCoordinates co = new SimpleCoordinates();
		co.build = value.getBuild();
		co.major = value.getMajor();
		co.minor = value.getMinor();
		co.product = value.getProduct();
		co.servicePack = value.getServicePack();
		co.version = value.getVersion();
		return co;
	}

}
