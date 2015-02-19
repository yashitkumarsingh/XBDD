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
import xbdd.model.RecentFeature;
import xbdd.model.databind.RecentFeatureConverter.SimpleRecentFeature;

import com.fasterxml.jackson.databind.util.StdConverter;

/**
 * Convert {@link RecentFeature} to a simple object with no synthetic properties as Jackson cannot seem to handle serializing a property by
 * getter only.
 */
public class RecentFeatureConverter extends StdConverter<RecentFeature, SimpleRecentFeature> {

	public static class SimpleRecentFeature {
		public String id;
		public String name;
		public Coordinates coordinates;
		public String version;
		public String product;
	}

	@Override
	public SimpleRecentFeature convert(final RecentFeature value) {
		final SimpleRecentFeature srf = new SimpleRecentFeature();
		srf.id = value.getID();
		srf.name = value.getName();
		srf.coordinates = value.getCoordinates();
		return srf;
	}

}
