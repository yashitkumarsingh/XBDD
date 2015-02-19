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
package xbdd.model.cucumber;

public class CucumberTag {

	private String name;
	private Integer line;

	public CucumberTag() {

	}

	public String getName() {
		return this.name;
	}

	public void setName(final String name) {
		this.name = name;
	}

	public Integer getLine() {
		return this.line;
	}

	public void setLine(final Integer line) {
		this.line = line;
	}

	@Override
	public String toString() {
		return "CucumberTag [name=" + this.name + ", line=" + this.line + "]";
	}

}
