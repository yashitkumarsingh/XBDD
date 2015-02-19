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
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.BeanParam;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.apache.log4j.Logger;
import org.glassfish.jersey.media.multipart.FormDataContentDisposition;
import org.glassfish.jersey.media.multipart.FormDataParam;
import org.jongo.Jongo;
import org.jongo.MongoCollection;
import org.jongo.MongoCursor;
import org.jongo.marshall.jackson.JacksonMapper;
import org.jongo.marshall.jackson.configuration.WriterCallback;

import xbdd.model.Coordinates;
import xbdd.model.Coordinates.Field;
import xbdd.model.Feature;
import xbdd.model.Scenario;
import xbdd.model.SummaryItem;
import xbdd.model.User;
import xbdd.model.cucumber.CucumberEmbedding;
import xbdd.model.cucumber.CucumberFeature;
import xbdd.model.databind.CucumberEmbeddingDeserializer;
import xbdd.model.databind.CucumberEmbeddingSerializer;
import xbdd.model.transform.FeatureTransformer;
import xbdd.util.StatusHelper;
import xbdd.webapp.factory.MongoDBAccessor;
import xbdd.webapp.resource.feature.FeatureResource;
import xbdd.webapp.resource.feature.QueryBuilder;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.google.common.base.Predicate;
import com.google.common.collect.Collections2;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.mongodb.AggregationOutput;
import com.mongodb.BasicDBList;
import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBObject;
import com.mongodb.util.JSON;

@Path("/rest/reports")
public class ReportResource {

	private static final Logger LOGGER = Logger.getLogger(ReportResource.class);

	private final MongoDBAccessor client;

	private final FeatureTransformer featureTransformer;

	private final CucumberEmbeddingDeserializer cucumberEmbeddingDeserializer;

	private final CucumberEmbeddingSerializer cucumberEmbeddingSerializer;

	private User user;

	@Inject
	public ReportResource(final MongoDBAccessor client, final FeatureTransformer featureTransformer,
			final CucumberEmbeddingDeserializer cucumberEmbeddingDeserializer,
			final CucumberEmbeddingSerializer cucumberEmbeddingSerializer,
			@Context final HttpServletRequest req) {
		this.client = client;
		this.featureTransformer = featureTransformer;
		this.cucumberEmbeddingDeserializer = cucumberEmbeddingDeserializer;
		this.cucumberEmbeddingSerializer = cucumberEmbeddingSerializer;
		
		final DB bdd = this.client.getDB("bdd");
		final Jongo jongo = new Jongo(bdd);
		final MongoCollection users = jongo.getCollection("users");
		this.user = users.findAndModify("{user_id: #}", req.getRemoteUser()).upsert().returnNew().with("{$setOnInsert: {\"favourites\": []}}").as(
				User.class);
	}

	@GET
	@Path("/{product}/{major}.{minor}.{servicePack}/{build}")
	@Produces(MediaType.APPLICATION_JSON)
	public List<Feature> getReportByProductVersionId(@BeanParam final Coordinates coordinates,
			@QueryParam("searchText") final String searchText, @QueryParam("viewPassed") final Integer viewPassed,
			@QueryParam("viewFailed") final Integer viewFailed,
			@QueryParam("viewUndefined") final Integer viewUndefined, @QueryParam("viewSkipped") final Integer viewSkipped,
			@QueryParam("start") final String start, @QueryParam("limit") final Integer limit) throws IOException {

		final BasicDBObject example = QueryBuilder.getInstance().buildFilterQuery(coordinates, searchText, viewPassed,
				viewFailed, viewUndefined, viewSkipped, start);

		final DB bdd = this.client.getDB("bdd");
		final Jongo jongo = new Jongo(bdd);
		final MongoCollection collection = jongo.getCollection("features");

		try (MongoCursor<Feature> cursor = collection.find(JSON.serialize(example))
				.sort(JSON.serialize(Coordinates.getFeatureSortingObject())).as(Feature.class)) {

			if (limit != null) {
				// cursor.limit(limit);
			}
			final List<Feature> featuresToReturn = new ArrayList<>();
			while (cursor.hasNext()) {
				featuresToReturn.add(cursor.next());
			}
			embedTestingTips(featuresToReturn, coordinates, bdd);
			return featuresToReturn;
		}
	}

	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public List<SummaryItem> getSummaryOfAllReports() throws IOException {
		return this.getAllProducts(false);
	}
	
	public List<SummaryItem> getSummaryOfAllFavourites(final boolean favourites) throws IOException {
		return this.getAllProducts(favourites);
	}
	
	private List<SummaryItem> getAllProducts(final boolean favourites) throws IOException {
		final DB bdd = this.client.getDB("bdd");
		final Jongo jongo = new Jongo(bdd);
		MongoCollection collection = jongo.getCollection("summary");

		final MongoCursor<SummaryItem> cursor = collection.find().as(SummaryItem.class);

		try {
			final List<SummaryItem> returns = new ArrayList<SummaryItem>();
			SummaryItem doc;

			while (cursor.hasNext()) {
				doc = cursor.next();
				final String product = doc.getCoordinates().getProduct();
				doc.markFavourite(this.user.isFavourite(product));
				if ((favourites && this.user.isFavourite(product)) || !favourites) {
					returns.add(doc);
				}
			}

			return returns;
		} finally {
			cursor.close();
		}
	}

	@GET
	@Produces(MediaType.APPLICATION_JSON)
	@Path("/featureIndex/{product}/{major}.{minor}.{servicePack}/{build}")
	public List<Feature> getFeatureIndexForReport(@BeanParam final Coordinates co,
			@QueryParam("searchText") final String searchText, @QueryParam("viewPassed") final Integer viewPassed,
			@QueryParam("viewFailed") final Integer viewFailed,
			@QueryParam("viewUndefined") final Integer viewUndefined, @QueryParam("viewSkipped") final Integer viewSkipped,
			@QueryParam("start") final String start) throws IOException {

		final BasicDBObject example = QueryBuilder.getInstance().buildFilterQuery(co, searchText, viewPassed,
				viewFailed, viewUndefined, viewSkipped, start);

		final DB bdd = this.client.getDB("bdd");
		final Jongo jongo = new Jongo(bdd);
		final MongoCollection featuresCollection = jongo.getCollection("features");

		final MongoCursor<Feature> features = featuresCollection.find(example.toString())
				.projection("{id: 1, name: 1, calculatedStatus: 1, originalAutomatedStatus: 1, tags: 1, uri: 1}")
				.sort(Coordinates.getFeatureSortingObject().toString())
				.as(Feature.class);

		final List<Feature> featureIndex = new ArrayList<Feature>();
		try {
			for (final Feature f : features) {
				featureIndex.add(f);
			}
		} finally {
			features.close();
		}
		return featureIndex;
	}

	protected void embedTestingTips(final List<Feature> featureList, final Coordinates co, final DB db) {
		for (final Feature feature : featureList) {
			FeatureResource.embedTestingTips(feature, co, db);
		}
	}

	protected String s4() {
		return Double.toHexString(Math.floor((1 + Math.random()) * 0x10000)).substring(1);
	}

	/**
	 * Generates a GUID
	 */
	protected String guid() {
		return s4() + s4() + '-' + s4() + '-' + s4() + '-' +
				s4() + '-' + s4() + s4() + s4();
	}

	protected void updateSummaryDocument(final DB bdd, final Coordinates co) {
		final Jongo jongo = new Jongo(bdd);
		final MongoCollection collection = jongo.getCollection("summary");
		final String id = co.getProduct() + "/" + co.getVersion();

		try {
			final SummaryItem summary = collection.findOne("{'_id': '" + id + "'}").as(SummaryItem.class);

			if (!summary.hasBuild(co.getBuild())) {
				// only update it if this build hasn't been added to it before.
				summary.addBuild(co.getBuild());
				collection.update("{_id: '" + id + "'}").with(summary);
			}
		} catch (Exception e) {
			final List<String> buildList = new ArrayList<String>();
			buildList.add(co.getBuild());
			collection.insert("{_id: '" + id + "', coordinates: #, builds: #}", co.getObject(Field.PRODUCT, Field.VERSION), buildList);
		}
	}

	@GET
	@Produces(MediaType.APPLICATION_JSON)
	@Path("/tags/{product}/{major}.{minor}.{servicePack}/{build}")
	public DBObject getTagList(@BeanParam final Coordinates co) {
		final DB bdd = this.client.getDB("bdd");
		final DBCollection features = bdd.getCollection("features");
		// Build objects for aggregation pipeline
		// id option: returns each tag with a list of associated feature ids
		final DBObject match = new BasicDBObject("$match", co.getReportCoordinatesQueryObject());
		final DBObject fields = new BasicDBObject("tags.name", 1);
		fields.put("_id", 0); // comment out for id option
		final DBObject project = new BasicDBObject("$project", fields);
		final DBObject unwind = new BasicDBObject("$unwind", "$tags");
		final DBObject groupFields = new BasicDBObject("_id", "$tags.name");
		// groupFields.put("features", new BasicDBObject("$addToSet", "$_id")); //comment in for id option
		groupFields.put("amount", new BasicDBObject("$sum", 1));
		final DBObject group = new BasicDBObject("$group", groupFields);
		final DBObject sort = new BasicDBObject("$sort", new BasicDBObject("amount", -1));

		final AggregationOutput output = features.aggregate(Arrays.asList(match, project, unwind, group, sort));

		// get _ids from each entry of output.result
		final BasicDBList returns = new BasicDBList();
		for (final DBObject obj : output.results()) {
			final String id = obj.get("_id").toString();
			returns.add(id);
		}
		return returns;
	}

	protected void updateStatsDocument(final DB bdd, final Coordinates co, final List<Feature> features) {
		// product and version are redundant for search, but ensure they're populated if the upsert results in an insert.
		final DBCollection statsCollection = bdd.getCollection("reportStats");
		final String id = co.getProduct() + "/" + co.getVersion() + "/" + co.getBuild();
		statsCollection.remove(new BasicDBObject("_id", id));
		final BasicDBObject stats = new BasicDBObject("coordinates", co.getReportCoordinates());
		stats.put("_id", id);
		final BasicDBObject summary = new BasicDBObject();
		stats.put("summary", summary);
		final BasicDBObject feature = new BasicDBObject();
		stats.put("feature", feature);
		for (final Feature ob : features) {
			final List<Scenario> scenarios = ob.getScenarios();
			if (scenarios != null) {
				for (final Scenario o : scenarios) {
					final String status = StatusHelper.getFinalScenarioStatus(o, false).getTextName();
					final Integer statusCounter = (Integer) summary.get(status);
					if (statusCounter == null) {
						summary.put(status, 1);
					} else {
						summary.put(status, statusCounter + 1);
					}
				}
			}
		}
		statsCollection.save(stats);
	}

	@PUT
	@Path("/{product}/{major}.{minor}.{servicePack}/{build}")
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	public List<Feature> putReport(@BeanParam final Coordinates co, final List<CucumberFeature> uploadFeatures) throws IOException {

		//
		// transformation

		final List<Feature> features = ImmutableList.copyOf(Lists.transform(uploadFeatures, this.featureTransformer));

		// save

		final DB bdd = this.client.getDB("bdd");
		final Jongo jongo = new Jongo(bdd, new JacksonMapper.Builder()
				.addDeserializer(CucumberEmbedding.class, this.cucumberEmbeddingDeserializer)
				.addSerializer(CucumberEmbedding.class, this.cucumberEmbeddingSerializer)
				.setWriterCallback(new WriterCallback() {
					@Override
					public ObjectWriter getWriter(final ObjectMapper mapper, final Object pojo) {
						return mapper.writer().withAttribute("coordinates", co);
					}
				})
				.build());

		final MongoCollection featuresCollection = jongo.getCollection("features");

		updateSummaryDocument(bdd, co);

		for (final Feature feature : features) {
			// take each feature and give it a unique id.
			final String _id = co.getFeature_Id(feature.getId());
			feature.setObjectId(_id);
			feature.setCoordinates(co);

			feature.setScenarios(mergeExistingScenarios(featuresCollection, feature));

			final String originalStatus = StatusHelper.getFeatureStatus(feature);
			feature.setCalculatedStatus(originalStatus);
			feature.setOriginalAutomatedStatus(originalStatus);
			LOGGER.info("Saving: " + feature.getName() + " - " + feature.getCalculatedStatus());
			if (LOGGER.isTraceEnabled()) {
				LOGGER.trace("Adding feature:" + JSON.serialize(feature));
			}
			featuresCollection.save(feature);
		}

		final MongoCursor<Feature> cursor = featuresCollection.find(
				"{coordinates.product: #, coordiantes.major: #, coordinates.minor: #, coordinates.servicePack: #, coordinates.build: #}",
				co.getProduct(), co.getMajor(), co.getMinor(), co.getServicePack(), co.getBuild()).as(Feature.class);
		final ArrayList<Feature> returns = new ArrayList<>();
		try {
			while (cursor.hasNext()) {
				returns.add(cursor.next());
			}
		} finally {
			cursor.close();
		}

		updateStatsDocument(bdd, co, returns);
		return returns;
	}

	private List<Scenario> mergeExistingScenarios(final MongoCollection features, final Feature feature) {
		final List<Scenario> newElements = feature.getScenarios() != null ? feature.getScenarios() : new ArrayList<Scenario>();

		final Feature existingFeature = features.findOne("{_id: #}", feature.getObjectId()).as(Feature.class);
		if (existingFeature != null) {
			final List<Scenario> existingElements = existingFeature.getScenarios() != null ? existingFeature.getScenarios()
					: new ArrayList<Scenario>();

			final Set<String> newElementIds = new HashSet<String>();
			for (final Scenario scenario : newElements) {
				newElementIds.add(scenario.getId());
			}

			newElements.addAll(Collections2.filter(existingElements, new Predicate<Scenario>() {
				@Override
				public boolean apply(final Scenario input) {
					return !newElementIds.contains(input.getId());
				}
			}));

		}
		return newElements;
	}

	@SuppressWarnings("unchecked")
	@POST
	@Path("/{product}/{major}.{minor}.{servicePack}/{build}")
	@Consumes(MediaType.MULTIPART_FORM_DATA)
	public Response uploadFile(
			@BeanParam final Coordinates coord,
			@FormDataParam("file") final InputStream uploadFeatures,
			@FormDataParam("file") final FormDataContentDisposition fileDetail) throws IOException {
		final ObjectMapper mapper = new ObjectMapper();
		putReport(coord, (List<CucumberFeature>) mapper.readValue(uploadFeatures, new TypeReference<List<CucumberFeature>>() {
		}));
		return Response.status(200).entity("success").build();
	}
}
