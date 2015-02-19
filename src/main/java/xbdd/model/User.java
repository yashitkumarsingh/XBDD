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

import java.util.Arrays;
import java.util.List;

import org.jongo.MongoCollection;
import org.jongo.marshall.jackson.oid.Id;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import static org.jongo.Oid.withOid;

/**
 * Represents a User.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class User {

	@JsonProperty("user_id")
	private String userID;
	private List<String> favourites;
	private List<Coordinates> recentBuilds;
	private List<RecentFeature> recentFeatures;

	@Id
	private String objectId;

	public User() {
	}

	public String getObjectId() {
		return this.objectId;
	}

	public void setObjectId(final String objectId) {
		this.objectId = objectId;
	}

	public List<String> getFavourites() {
		return this.favourites;
	}
	
	public void addFavourite(final String product) {
		if (!this.isFavourite(product)) {
			this.favourites.add(product);
		}
	}
	
	public void removeFavourite(final String product) {
		if (this.isFavourite(product)) {
			this.favourites.remove(product);
		}
	}
	
	public void toggleFavourite(final String product) {
		if (this.isFavourite(product)) {
			this.removeFavourite(product);
		} else {
			this.addFavourite(product);
		}
	}
	
	public boolean isFavourite(final String product) {
		return (this.favourites.indexOf(product) != -1);
	}
	
	public void addRecentBuild(Coordinates build) {
		if (this.recentBuilds == null) {
			this.recentBuilds = Arrays.asList(build);
		} else {
			if (this.recentBuilds.indexOf(build) != -1) {
				// If the build already is in recents remove it so we dont get duplicates
				this.recentBuilds.remove(build);
			} else if (this.recentBuilds.size() == 5) {
				// If the recents list is already 5 long get rid of one so we never have more than 5
				this.recentBuilds.remove(0);
			}
			this.recentBuilds.add(build);
		}
	}
	
	public List<Coordinates> getRecentBuilds() {
		return this.recentBuilds;
	}
	
	public void addRecentFeature(Coordinates build, String featureID, String featureName) {
		RecentFeature recentFeature = new RecentFeature(build, featureID, featureName);
		if (this.recentFeatures == null) {
			this.recentFeatures = Arrays.asList(recentFeature);
		} else {
			if (this.recentFeatures.contains(recentFeature)) {
				this.recentFeatures.remove(recentFeature);
			} else if (this.recentFeatures.size() == 5) {
				this.recentFeatures.remove(0);
			}
			this.recentFeatures.add(recentFeature);
		}
	}
	
	public List<RecentFeature> getRecentFeatures() {
		return this.recentFeatures;
	}
	
	public void save(MongoCollection saver) {
		saver.update(withOid(this.objectId)).with(this);
	}
}
