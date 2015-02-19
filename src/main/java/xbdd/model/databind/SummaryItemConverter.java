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

import java.util.List;

import xbdd.model.Coordinates;
import xbdd.model.SummaryItem;
import xbdd.model.databind.SummaryItemConverter.SimpleSummaryItem;

import com.fasterxml.jackson.databind.util.StdConverter;

/**
 * Convert {@link SummaryItem} to a simple object with no synthetic properties as Jackson cannot seem to handle serializing a property by
 * getter only.
 */
public class SummaryItemConverter extends StdConverter<SummaryItem, SimpleSummaryItem> {

	public static class SimpleSummaryItem {
		public String _id;
		public String id;
		public List<String> builds;
		public Coordinates coordinates;
		public List<String> pinned;
		public Boolean favourites;
	}

	@Override
	public SimpleSummaryItem convert(final SummaryItem value) {
		final SimpleSummaryItem srf = new SimpleSummaryItem();
		srf._id = value.getObjectID();
		srf.id = value.getID();
		srf.builds = value.getBuilds();
		srf.pinned = value.getPinnedBuilds();
		srf.coordinates = value.getCoordinates();
		srf.favourites = value.isFavourite();
		return srf;
	}

}
