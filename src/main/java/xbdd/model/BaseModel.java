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

public abstract class BaseModel {
	private String name;
	private String id;
	private String keyword;
	private Integer line;

	public BaseModel() {
	}

	public String getName() {
		return this.name;
	}

	public void setName(final String name) {
		this.name = name;
	}

	public String getId() {
		return this.id;
	}

	public void setId(final String id) {
		this.id = id;
	}

	public String getKeyword() {
		return this.keyword;
	}

	public void setKeyword(final String keyword) {
		this.keyword = keyword;
	}

	public Integer getLine() {
		return this.line;
	}

	public void setLine(final Integer line) {
		this.line = line;
	}

	@Override
	public int hashCode() {
		return new HashCodeBuilder()
				.append(this.id)
				.append(this.keyword)
				.append(this.line)
				.append(this.name).toHashCode();
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
		final BaseModel other = (BaseModel) obj;
		return new EqualsBuilder()
				.append(this.id, other.id)
				.append(this.keyword, other.keyword)
				.append(this.line, other.line)
				.append(this.name, other.name).isEquals();
	}

}
