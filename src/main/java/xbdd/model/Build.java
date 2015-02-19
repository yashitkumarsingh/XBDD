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
package xbdd.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.mongodb.DB;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.jongo.Jongo;
import org.jongo.MongoCollection;
import org.jongo.MongoCursor;
import xbdd.webapp.factory.MongoDBAccessor;

import java.security.InvalidParameterException;
import java.util.ArrayList;
import java.util.List;

/**
 * Represents a User.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class Build {
	private final MongoCollection saver;
	private Coordinates coordinates;
	private final MongoDBAccessor client;

	/**
	 * Creates a new Build object from a set of coordinates
	 *
	 * @param coordinates The coordinates with which to setup the Build object
	 * @param req The current HttpServletRequest
	 * @throws InvalidParameterException If the coordinates object is missing required fields
	 */
	public Build(final MongoDBAccessor client,
			final Coordinates coordinates) {
		if (coordinates.getBuild() != null && coordinates.getMajor() != null && coordinates.getMinor() != null && coordinates.getServicePack() != null && coordinates.getProduct() != null) {
			this.coordinates = coordinates;
		} else {
			throw new InvalidParameterException("The supplied Coordinates object is missing required fields");
		}
		this.client = client;
		final DB bdd = this.client.getDB("bdd");
		final Jongo jongo = new Jongo(bdd);
		this.saver = jongo.getCollection("features");
	}

	public List<Feature> getFeatures() {
		final String query = this.coordinates.getQueryObject().toString();
		MongoCursor<Feature> cursor = this.saver.find(query).as(Feature.class);
		List<Feature> returns = new ArrayList<Feature>();

		while (cursor.hasNext()) {
			returns.add(cursor.next());
		}
		return returns;
	}
	
	@Override
	public boolean equals(final Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}
		final Build other = (Build) obj;
		return new EqualsBuilder().appendSuper(super.equals(obj))
				.append(this.coordinates, other.coordinates).isEquals();
	}

	@Override
	public int hashCode() {
		return new HashCodeBuilder().appendSuper(super.hashCode())
				.append(this.coordinates).toHashCode();
	}
}
