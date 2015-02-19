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
package xbdd.webapp.rest;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.jongo.Jongo;
import org.jongo.MongoCollection;
import org.jongo.MongoCursor;

import xbdd.model.Feature;
import xbdd.webapp.factory.MongoDBAccessor;
import xbdd.util.MultipleBuildsFeatureMergeHelper;

import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBObject;

@Path("rest/reports/multiple")
public class MergeBuilds {

	public static class Merge {
		public List<String> builds;
		public String version;
		public String product;
	}

	private final MongoDBAccessor client;

	@Inject
	public MergeBuilds(final MongoDBAccessor client) {
		this.client = client;
	}

	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces("application/json")
	public DBObject getMergedBuilds(final Merge merge) {
		// Get features collection
		final DB bdd = this.client.getDB("bdd");
		final Jongo jongo = new Jongo(bdd);
		final MongoCollection features = jongo.getCollection("features");

		final List<String> or = new ArrayList<String>();
		
		for (int i = 0; i < merge.builds.size(); i++) {
			final Pattern regex = Pattern.compile("^" + merge.product + "/" + merge.version + "/" + (merge.builds.get(i))
					+ "/");
			or.add("{\"_id\": {\"$regex\": \"" + regex.toString() + "\"}}");
		}
		
		final MongoCursor<Feature> all = features.find("{'$or': "+ or.toString() +"}").as(Feature.class);

		// A list of all the features in every build, where features have only the attributes the client requires
		final List<Feature> featureList = new ArrayList<Feature>();

		// query wrapper object
		final MultipleBuildsFeatureMergeHelper buildMerge = new MultipleBuildsFeatureMergeHelper(merge.builds);
		
		while (all.hasNext()) {
			final Feature feature = all.next();
			if (feature.getCoordinates().getBuild().equals(merge.builds.get(0))) {
				featureList.add(feature);
			}
			buildMerge.addFeature(feature);
		}

		// returns object containing all features and scenarios from all builds and the merged build
		final DBObject buildObject = new BasicDBObject();
		final List<DBObject> resultList = new ArrayList<DBObject>();

		for (final Feature feature : featureList) {
			resultList.add(buildMerge.getMergeFeature(feature.getId()).toDBObject());
		}
		merge.builds.add(0, "Merged");
		buildObject.put("features", resultList);
		buildObject.put("builds", merge.builds);

		return buildObject;
	}
}