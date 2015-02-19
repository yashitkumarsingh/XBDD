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

import java.util.Arrays;
import java.util.List;

import xbdd.model.BaseModel;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class CucumberStep extends BaseModel {

	private CucumberResult result;
	private CucumberMatch match;
	private Integer[] matchedColumns;
	private List<CucumberEmbedding> embedding;

	public CucumberStep() {
	}

	public CucumberResult getResult() {
		return this.result;
	}

	public void setResult(final CucumberResult result) {
		this.result = result;
	}

	public CucumberMatch getMatch() {
		return this.match;
	}

	public void setMatch(final CucumberMatch match) {
		this.match = match;
	}

	public Integer[] getMatchedColumns() {
		return this.matchedColumns;
	}

	public void setMatchedColumns(final Integer[] matchedColumns) {
		this.matchedColumns = matchedColumns;
	}

	public List<CucumberEmbedding> getEmbeddings() {
		return this.embedding;
	}

	public void setEmbeddings(final List<CucumberEmbedding> embedding) {
		this.embedding = embedding;
	}

	@Override
	public String toString() {
		return "CucumbersStep [getId()=" + getId() + ", getName()=" + getName() + ", getKeyword()=" + getKeyword() + ", getLine()="
				+ getLine()
				+ ", result=" + this.result + ", match=" + this.match + ", matchedColumns=" + Arrays.toString(this.matchedColumns) + "]";
	}

}
