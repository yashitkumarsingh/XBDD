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

import java.net.UnknownHostException;
import java.security.InvalidParameterException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import com.mongodb.MongoClient;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;

import org.codehaus.jackson.annotate.JsonIgnore;
import org.jongo.MongoCollection;
import xbdd.model.databind.SummaryItemConverter;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import xbdd.webapp.factory.MongoDBAccessor;

import static org.jongo.Oid.withOid;

/**
 * Represents a User.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonSerialize(converter = SummaryItemConverter.class)
public class SummaryItem {
	@JsonIgnore
	private final MongoDBAccessor client;
	@JsonProperty("_id")
	private String _id;
	private String id;
	private List<String> builds;
	private Coordinates coordinates;
	private List<String> pinned;
	private Boolean favourite;

	public SummaryItem() throws UnknownHostException {
		this.client = new MongoDBAccessor(new MongoClient());

	}

	/**
	 * Creates a new SummaryItem from a Coordinates object and an initial build
	 * Save to the collection with .insert()
	 *
	 * @param coordinates A complete coordinates object
	 * @param build The initial build
	 */
	public SummaryItem(final Coordinates coordinates, final String build) throws UnknownHostException {
		this.coordinates = coordinates;
		this.builds = Arrays.asList(build);
		this.pinned = Collections.emptyList();
		this.id = coordinates.getProduct() + "/" + coordinates.getVersion();
		this.client = new MongoDBAccessor(new MongoClient());
	}

	/**
	 * @return The Coordinates object for the SummaryItem
	 */
	public Coordinates getCoordinates() {
		return this.coordinates;
	}

	/**
	 * @return The _id field in mongo
	 */
	public String getObjectID() {
		return this._id;
	}

	public void setID() {
		if (this.id == null) {
			this.id = this._id;
		}
	}

	/**
	 * @param newID The new ID to assign this summary doc, used for renaming products
	 */
	public void setObjectID(final String newID) {
		this.setID();
		this._id = newID;
	}

	/**
	 * @param newCoordinates Coordinates the new coordinates object, used for renaming products and/or versions
	 */
	public void setCoordinates(final Coordinates newCoordinates) {
		this.setID();
		this.coordinates = newCoordinates;
		final String newID = this.getCoordinates().getProduct() + "/" + this.getCoordinates().getVersion();
		this.setObjectID(newID);
	}

	/**
	 * @return A string in the format {product}/{version} that uniquely identifies this SummaryItem
	 */
	public String getID() {
		return this.id;
	}

	/**
	 * @return A list of all builds associated with this product and version
	 */
	public List<String> getBuilds() {
		return this.builds;
	}

	/**
	 * @return A list of all builds associated with this product and version that are pinned globally
	 */
	public List<String> getPinnedBuilds() {
		return this.pinned;
	}

	/**
	 * @param build Adds a build to this SummaryItem
	 */
	public void addBuild(final String build) {
		this.setID();
		this.builds.add(build);
	}

	/**
	 * @param build Pins an already existing build in this SummaryItem
	 * @throws InvalidParameterException When the build does not exist
	 */
	public void pinBuild(final String build) throws InvalidParameterException {
		this.setID();
		if (this.hasBuild(build)) {
			this.pinned.add(build);
		} else {
			throw new InvalidParameterException("That build does not exists for this Summary Item");
		}
	}

	/**
	 * @param build The build to check for
	 * @return True if the build exists in the summary item, False if it does not
	 */
	public boolean hasBuild(final String build) {
		return this.builds.contains(build);
	}

	/**
	 * @param build The build name / number you wish to fetch
	 * @return A Build object
	 */
	public Build getBuild(final String build) {
		final Coordinates buildCoordinates = this.coordinates;
		buildCoordinates.setBuild(build);
		return new Build(this.client, buildCoordinates);
	}

	/**
	 * @return A list of Build objects for all the builds in this SummaryItem
	 */
	public List<Build> getAllBuilds() {
		final List<Build> builds = new ArrayList<Build>();
		for (String build : this.builds) {
			builds.add(this.getBuild(build));
		}
		return builds;
	}

	/**
	 * @param build The build to check for
	 * @return True if the build is pinned, False if it is not or if the build does not exist
	 */
	public boolean isPinned(final String build) {
		return this.pinned.contains(build);
	}

	/**
	 * @param value Marks this SummaryItem as a favourite for this REST request
	 */
	public void markFavourite(boolean value) {
		this.setID();
		this.favourite = new Boolean(value);
	}

	/**
	 * @return True / False if the SummaryItem is a favourite of the user or not
	 */
	public boolean isFavourite() {
		return (this.favourite != null && this.favourite);
	}

	public void save(MongoCollection saver) {
		this.setID();
		saver.remove(withOid(this.id));
		saver.insert(this);
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
		final SummaryItem other = (SummaryItem) obj;
		return new EqualsBuilder().appendSuper(super.equals(obj))
				.append(this.coordinates, other.coordinates)
				.append(this.id, other.id).isEquals();
	}

	@Override
	public int hashCode() {
		return new HashCodeBuilder().appendSuper(super.hashCode())
				.append(this.id)
				.append(this.coordinates).toHashCode();
	}
}
