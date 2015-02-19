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

import java.io.IOException;
import java.util.List;

import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.BeanParam;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;

import org.jongo.Jongo;
import org.jongo.MongoCollection;

import xbdd.webapp.factory.MongoDBAccessor;
import xbdd.model.Coordinates;
import xbdd.model.SummaryItem;
import xbdd.model.User;
import xbdd.webapp.rest.ReportResource;

import com.mongodb.DB;

@Path("/rest/favourites")
public class Favourites {

	private final MongoDBAccessor client;
	private User user;
	private final MongoCollection saver;

	@Inject
	public Favourites(final MongoDBAccessor client, 
			@Context final HttpServletRequest req) {
		this.client = client;
		final DB bdd = this.client.getDB("bdd");
		final Jongo jongo = new Jongo(bdd);
		this.saver = jongo.getCollection("users");

		this.user = this.saver.findAndModify("{user_id: #}", req.getRemoteUser()).upsert().returnNew().with("{$setOnInsert: {\"favourites\": [], \"recentBuilds\": [], \"recentFeatures\": []}}").as(
				User.class);
	}

	@PUT
	@Path("/{product}")
	@Produces("application/json")
	public Response productFavouriteStateOn(@PathParam("product") final String product) {
		this.user.addFavourite(product);
		this.user.save(this.saver);
		return Response.ok().build();
	}
	
	@DELETE
	@Path("/{product}")
	@Produces("application/json")
	public Response productFavouriteStateOff(@PathParam("product") final String product) {
		this.user.removeFavourite(product);
		this.user.save(this.saver);
		return Response.ok().build();
	}
	
	@GET
	@Path("/{product}")
	@Produces("application/json")
	public boolean getFavouriteStateOfProduct(@PathParam("product") final String product) {
		return this.user.isFavourite(product);
	}
	
	@GET
	@Path("/")
	@Produces("application/json")
	public List<SummaryItem> getSummaryOfAllReports(@Context HttpServletRequest req) throws IOException {
		return new ReportResource(client, null, null, null, req).getSummaryOfAllFavourites(true);
	}
	
	private void setPinStateOfBuild(final Coordinates coordinates,
			final boolean state) {
		
		final DB bdd = this.client.getDB("bdd");
		final Jongo jongo = new Jongo(bdd);
		MongoCollection collection = jongo.getCollection("summary");
		
		collection.update("{_id: '#/#'}", coordinates.getProduct(), coordinates.getVersion()).with("{#: {pinned: #}}", (state ? "$addToSet" : "$pull"), coordinates.getBuild());
	}
	
	@PUT
	@Path("/pin/{product}/{major}.{minor}.{servicePack}/{build}/")
	@Produces("application/json")
	public Response pinABuild(@BeanParam Coordinates coordinates) {
		
		setPinStateOfBuild(coordinates, true);
		return Response.ok().build();
	}
	
	@DELETE
	@Path("/pin/{product}/{major}.{minor}.{servicePack}/{build}/")
	@Produces("application/json")
	public Response unPinABuild(@BeanParam Coordinates coordinates) {
		
		setPinStateOfBuild(coordinates, false);
		return Response.ok().build();
	}
}
