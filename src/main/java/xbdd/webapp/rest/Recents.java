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

import java.util.List;

import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.BeanParam;
import javax.ws.rs.GET;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;

import org.jongo.Jongo;
import org.jongo.MongoCollection;

import xbdd.webapp.factory.MongoDBAccessor;
import xbdd.model.Coordinates;
import xbdd.model.RecentFeature;
import xbdd.model.User;

import com.mongodb.DB;

@Path("/rest/recents")
public class Recents {

	private final MongoDBAccessor client;
	private User user;
	private MongoCollection saver;

	@Inject
	public Recents(final MongoDBAccessor client,
			@Context HttpServletRequest req) {
		this.client = client;
		final DB bdd = this.client.getDB("bdd");
		final Jongo jongo = new Jongo(bdd);
		this.saver = jongo.getCollection("users");

		this.user = this.saver.findAndModify("{user_id: #}", req.getRemoteUser()).upsert().returnNew().with("{$setOnInsert: {\"favourites\": [], \"recentBuilds\": [], \"recentFeatures\": []}}").as(
				User.class);
	}

	/**
	 * Uses the '.+' regexp on featureId to allow for symbols such as slashes in the id
	 * 
	 * @param String id The featureId to get the history for
	 * @return Response Either a 200 response or a 500
	 */
	@PUT
	@Path("/feature/{product}/{major}.{minor}.{servicePack}/{build}/{id:.+}")
	@Produces("application/json")
	public Response addFeatureToRecents(@QueryParam("name") final String featureName,
			@BeanParam Coordinates coordinates,
			@PathParam("id") final String featureID) {
		
		this.user.addRecentFeature(coordinates, featureID, featureName);
		this.user.save(this.saver);
		return Response.ok().build();
	}
	
	
	@PUT
	@Path("/build/{product}/{major}.{minor}.{servicePack}/{build}")
	@Produces("application/json")
	public Response addBuildToRecents(@BeanParam Coordinates coordinates) {
		
		this.user.addRecentBuild(coordinates);
		this.user.save(this.saver);
		return Response.ok().build();
	}
	
	@GET
	@Path("/builds")
	@Produces("application/json")
	public List<Coordinates> getRecentBuilds() {
		return this.user.getRecentBuilds();
	}


	@GET
	@Path("/features")
	@Produces("application/json")
	public List<RecentFeature> getRecentFeatures() {
		return this.user.getRecentFeatures();
	}
}
