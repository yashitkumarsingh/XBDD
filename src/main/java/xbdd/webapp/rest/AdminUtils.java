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

import java.util.regex.Pattern;

import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import com.mongodb.*;
import de.undercouch.bson4jackson.types.ObjectId;
import org.jongo.Jongo;
import org.jongo.MongoCollection;
import org.jongo.MongoCursor;
import xbdd.model.Coordinates;
import xbdd.model.Environment;
import xbdd.model.SummaryItem;
import xbdd.model.User;
import xbdd.webapp.factory.MongoDBAccessor;

@Path("/rest/admin")
public class AdminUtils {

	private final MongoDBAccessor client;

	@Inject
	public AdminUtils(final MongoDBAccessor client,
			@Context final HttpServletRequest req) {
		this.client = client;
		if (!req.isUserInRole("admin")) {
			throw new WebApplicationException();
		}
	}
	
	@DELETE
	@Path("/delete/{product}")
	@Produces("application/json")
	public Response softDeleteEntireProduct(@PathParam("product") final String product) {

		final DB bdd = this.client.getDB("bdd");
		final Jongo jongo = new Jongo(bdd);
		final MongoCollection summaryCollection = jongo.getCollection("summary");
		final MongoCollection delCollection = jongo.getCollection("deletedSummary");

		//final BasicDBObject query = new BasicDBObject("coordinates.product",product);

		MongoCursor<SummaryItem> cursor = summaryCollection.find("{coordinates.product: #}", product).as(SummaryItem.class);
		SummaryItem doc;
		
		while(cursor.hasNext()) {
			doc = cursor.next();
			try {
				delCollection.insert(doc);
			} catch (Throwable e) {
				return Response.status(500).build();
			}
		}
		
		summaryCollection.remove("{coordinates.product: #}", product);
		return Response.ok().build();
	}
	
	@DELETE
	@Path("/delete/{product}/{version}")
	@Produces("application/json")
	public Response softDeleteSingleVersion(@PathParam("product") final String product,
			@PathParam("version") final String version) {
		
		final DB db = this.client.getDB("bdd");
		final DBCollection collection = db.getCollection("summary");
		final DBCollection targetCollection = db.getCollection("deletedSummary");
		
		final Pattern productReg = java.util.regex.Pattern.compile("^"+product+"/"+version+"$");
		final BasicDBObject query = new BasicDBObject("_id", productReg);
		
		final DBCursor cursor = collection.find(query);
		DBObject doc;
		
		while(cursor.hasNext()) {
			doc = cursor.next();
			//kill the old id
			doc.removeField("_id");
			try {
				targetCollection.insert(doc);
			} catch (Throwable e) {
				return Response.status(500).build();
			}
		}
		
		collection.remove(query);
		
		return Response.ok().build();
	}
	
	@DELETE
	@Path("/delete/{product}/{version}/{build}")
	@Produces("application/json")
	public Response softDeleteSingleBuild(@PathParam("product") final String product,
			@PathParam("build") final String build,
			@PathParam("version") final String version) {
		
		final DB db = this.client.getDB("bdd");
		final DBCollection collection = db.getCollection("summary");
		final DBCollection targetCollection = db.getCollection("deletedSummary");
		
		final Pattern productReg = java.util.regex.Pattern.compile("^"+product+"/"+version+"$");
		final BasicDBObject query = new BasicDBObject("_id", productReg);
		
		final DBCursor cursor = collection.find(query);
		
		while (cursor.hasNext()) {
			DBObject doc = cursor.next();
			DBObject backupDoc = doc;
			//Make sure the backup document only has the deleted build number
			try {
				final String[] singleBuild = {build};
				backupDoc.put("builds", singleBuild);
				targetCollection.insert(backupDoc);
			} catch (DuplicateKeyException e) {
				//The backup document already exists, possibly already deleted a build
				//Lets add the deleted build to the existing document
				targetCollection.update(new BasicDBObject("_id",backupDoc.get("_id")), new BasicDBObject("$push", new BasicDBObject("builds",build)));
			} catch(Exception e) {
				return Response.status(500).build();
			}
			//Remove the build number from the current document and push it back into the collection
			try {
				collection.update(new BasicDBObject("_id",doc.get("_id")), new BasicDBObject("$pull",new BasicDBObject("builds", build)));
			} catch(Exception e) {
				System.out.println(e);
				return Response.status(500).build();
			}
		}
		return Response.ok().build();
	}

	
	//Lets register the helper class that replaces the instances of the old product name with the new one
	private DBObject renameDoc(String product, String newname, DBObject doc) {
		doc.put("_id", ((String) doc.get("_id")).replaceAll(product+"/", newname+"/"));
		if (doc.containsField("coordinates")) {
			DBObject coordinates = (DBObject) doc.get("coordinates");
			coordinates.put("product",newname);
			doc.put("coordinates", coordinates);
		}
		return doc;
	}
	
	public static class Product {
	    public String name;
	}

	/**
	 * @param product The old product name
	 * @param renameObject A {@link Product} object with the new product name
	 * @return Response
	 */
	@PUT @Consumes(MediaType.APPLICATION_JSON)
	@Path("/{product}")
	@Produces("application/json")
	public Response renameProduct(@PathParam("product") final String product,
			final Product renameObject) {

		final DB bdd = this.client.getDB("bdd");
		final Jongo jongo = new Jongo(bdd);
//, jongo.getCollection("features"), jongo.getCollection("testingTips"))
		final MongoCollection summaryCollection = jongo.getCollection("summary");

		final String newName = renameObject.name;
		final String oldNameQuery = "{_id: {$regex: \"^" + product + "/\"}}";
		final String newNameQuery = "{_id: {$regex: \"^" + newName + "/\"}}";

		// Before anything lets check the new name isn't already in use

		final boolean summaryExists = (summaryCollection.findOne(newNameQuery).as(SummaryItem.class) != null);
		final boolean deleteExists = (jongo.getCollection("deletedSummary").findOne(newNameQuery).as(SummaryItem.class) != null);

		if (summaryExists || deleteExists) {
			throw new WebApplicationException();
		}

		//We need to rename the product everywhere
		//First up are all the SummaryItems
		MongoCursor<SummaryItem> cursor = summaryCollection.find(oldNameQuery).as(SummaryItem.class);
		while(cursor.hasNext()) {
			SummaryItem doc = cursor.next();
			Coordinates coordinates = doc.getCoordinates();
			coordinates.setProduct(newName);
			doc.setCoordinates(coordinates);
			doc.save(summaryCollection);
		}

		//Then we deal with the environments collection where only the coordinates.product is set
		final MongoCollection environmentCollection = jongo.getCollection("environments");
		final String enviroQuery = "{coordinates.product: \"" +  product +"\"}";
		final Coordinates newCoordinates = new Coordinates();
		newCoordinates.setProduct(newName);

		MongoCursor<Environment> enviroCursor = environmentCollection.find(enviroQuery).as(Environment.class);
		while (enviroCursor.hasNext()) {
			Environment doc = enviroCursor.next();
			doc.setCoordinates(newCoordinates);
			doc.save(environmentCollection);
		}

		//Then we correct the name in any users favourites object
		final MongoCollection userCollection = jongo.getCollection("users");
		MongoCursor<User> users = userCollection.find().as(User.class);

		while(users.hasNext()) {
			User doc = users.next();
			if (doc.isFavourite(product)) {
				doc.removeFavourite(product);
				doc.addFavourite(newName);
				doc.save(userCollection);
			}
		}

		return Response.ok().build();
	}
}
