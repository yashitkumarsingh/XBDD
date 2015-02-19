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
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.jongo.MongoCollection;
import org.jongo.marshall.jackson.oid.ObjectId;

import java.util.ArrayList;
import java.util.List;

import static org.jongo.Oid.withOid;

/**
 * Represents a User.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class Environment {
	@ObjectId
	private ObjectId _id;
	private List<String> environments;
	private Coordinates coordinates;

	public Environment() {
	}

	/**
	 * Creates a new Environment from a Coordinates object and an initial environment
	 * Save to the collection with .insert()
	 *
	 * @param coordinates A complete coordinates object
	 * @param build The initial build
	 */
	public Environment(final Coordinates coordinates, final String environment) {
		this.coordinates = coordinates;
		this.environments = new ArrayList<>();
		this.environments.add(environment);
	}

	/**
	 * @return The Coordinates object for the Environment
	 */
	public Coordinates getCoordinates() {
		return this.coordinates;
	}

	/**
	 * @return The _id field in mongo
	 */
	public ObjectId getObjectID() {
		return this._id;
	}

	/**
	 * @param newID The new ID to assign this summary doc, used for renaming products
	 */
	public void setObjectID(final ObjectId newID) {
		this._id = newID;
	}

	/**
	 * @param newCoordinates Coordinates the new coordinates object, used for renaming products and/or versions
	 */
	public void setCoordinates(final Coordinates newCoordinates) {
		this.coordinates = newCoordinates;
	}

	public void save(MongoCollection saver) {
		saver.update(withOid(this._id.toString())).with(this);
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
		final Environment other = (Environment) obj;
		return new EqualsBuilder().appendSuper(super.equals(obj))
				.append(this.coordinates, other.coordinates)
				.append(this.environments, other.environments).isEquals();
	}

	@Override
	public int hashCode() {
		return new HashCodeBuilder().appendSuper(super.hashCode())
				.append(this.environments)
				.append(this.coordinates).toHashCode();
	}
}
