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

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;

import xbdd.model.databind.RecentFeatureConverter;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

/**
 * Represents a User.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonSerialize(converter = RecentFeatureConverter.class)
public class RecentFeature {
	private Coordinates coordinates;
	private String id;
	private String name;

	public RecentFeature() {
	}
	
	public RecentFeature(final Coordinates coordinates, final String id, final String name) {
		this.coordinates = coordinates;
		this.id = id;
		this.name = name;
	}
	
	public Coordinates getCoordinates() {
		return this.coordinates;
	}
	
	public String getID() {
		return this.id;
	}
	
	public String getName() {
		return this.name;
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
		final RecentFeature other = (RecentFeature) obj;
		return new EqualsBuilder().appendSuper(super.equals(obj))
				.append(this.coordinates, other.coordinates)
				.append(this.id, other.id)
				.append(this.name, other.name).isEquals();
	}

	@Override
	public int hashCode() {
		return new HashCodeBuilder().appendSuper(super.hashCode())
				.append(this.id)
				.append(this.name)
				.append(this.coordinates).toHashCode();
	}
}
