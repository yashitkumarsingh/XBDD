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
package xbdd.webapp.resource.feature;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.BeanParam;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response.Status;

import org.jongo.Jongo;
import org.jongo.MongoCollection;

import xbdd.model.Coordinates;
import xbdd.model.Coordinates.Field;
import xbdd.model.Edit;
import xbdd.model.Feature;
import xbdd.model.Scenario;
import xbdd.model.ScenarioChange;
import xbdd.model.StepChange;
import xbdd.model.cucumber.CucumberStep;
import xbdd.util.StatusHelper;
import xbdd.webapp.factory.MongoDBAccessor;

import com.mongodb.BasicDBList;
import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;
import com.mongodb.util.JSON;

@Path("/rest/feature")
public class FeatureResource {

	private final MongoDBAccessor client;
	private static int MAX_ENVIRONMENTS_FOR_A_PRODUCT = 10;

	@Inject
	public FeatureResource(final MongoDBAccessor client) {
		this.client = client;
	}

	@SuppressWarnings("unchecked")
	/**
	 * Uses the '.+' regexp on featureId to allow for symbols such as slashes in the id
	 *
	 * @param String featureId The featureId to get the history for
	 * @return DBObjet Returns the past feature status for the given featureId
	 */
	@GET
	@Path("/rollup/{product}/{major}.{minor}.{servicePack}/{featureId:.+}")
	@Produces(MediaType.APPLICATION_JSON)
	public DBObject getFeatureRollup(@BeanParam final Coordinates coordinates, @PathParam("featureId") final String featureId) {
		final List<BasicDBObject> features = new ArrayList<BasicDBObject>();
		final DB db = this.client.getDB("bdd");
		final DBCollection collection = db.getCollection("features");

		final BasicDBObject example = coordinates.getRollupQueryObject(featureId);

		final DBCursor cursor = collection.find(example,
				new BasicDBObject("id", 1).append("coordinates.build", 1).append("calculatedStatus", 1)
						.append("originalAutomatedStatus", 1).append("statusLastEditedBy", 1));
		try {
			while (cursor.hasNext()) {
				final DBObject doc = cursor.next();
				final BasicDBObject rollup = new BasicDBObject()
						.append("build", ((DBObject) doc.get("coordinates")).get("build"))
						.append("calculatedStatus", doc.get("calculatedStatus"))
						.append("originalAutomatedStatus", doc.get("originalAutomatedStatus"))
						.append("statusLastEditedBy", doc.get("statusLastEditedBy"));
				features.add(rollup);
			}
		} finally {
			cursor.close();
		}

		final BasicDBObject returns = new BasicDBObject()
				.append("coordinates", coordinates.getRollupCoordinates().append("featureId", featureId).append("version", coordinates.getVersion()))
				.append("rollup", features);
		
		final DBObject buildOrder = collection.findOne(coordinates.getQueryObject());
		final List<String> buildArray = (List<String>) buildOrder.get("builds");
		final List<BasicDBObject> orderedFeatures = new ArrayList<BasicDBObject>();
		
		for (String build : buildArray) {
			for (BasicDBObject feature : features) {
				if (feature.get("build").equals(build)) {
					orderedFeatures.add(feature);
					break;
				}
			}
		}
		
		returns.append("rollup", orderedFeatures);

		return returns;
	}

	/**
	 * Uses the '.+' regexp on featureId to allow for symbols such as slashes in the id
	 *
	 * @param featureId The featureId to get the history for
	 * @return DBObject Returns the the current features state and details (environments, tips, steps and scenarios)
	 */
	@GET
	@Path("/{product}/{major}.{minor}.{servicePack}/{build}/{featureId:.+}")
	@Produces(MediaType.APPLICATION_JSON)
	public Feature getFeature(@BeanParam final Coordinates co, @PathParam("featureId") final String featureId) {
		final DB bdd = this.client.getDB("bdd");
		final Jongo jongo = new Jongo(bdd);
		final MongoCollection features = jongo.getCollection("features");

		final Feature feature = features.findOne("{id: #, coordinates: #}", featureId, co).as(
				Feature.class);

		if (feature == null) {
			throw new WebApplicationException(Status.NOT_FOUND);
		}
		FeatureResource.embedTestingTips(feature, co, bdd);
		return feature;
	}

	protected void updateTestingTips(final DB db, final Coordinates coordinates, final String featureId, final Feature feature) {
		final DBCollection tips = db.getCollection("testingTips");
		final List<Scenario> elements = feature.getScenarios();
		for (final Scenario scenario : elements) {
			if (scenario.getTestingTips() != null) {
				final String tipText = scenario.getTestingTips();
				final String scenarioId = scenario.getId();
				final BasicDBObject tipQuery = coordinates.getTestingTipsCoordinatesQueryObject(featureId, scenarioId);
				DBObject oldTip = null;
				// get the most recent tip that is LTE to the current coordinates. i.e. sort in reverse chronological order and take the
				// first item (if one exists).
				final DBCursor oldTipCursor = tips.find(tipQuery)
						.sort(new BasicDBObject("coordinates.major", -1).append("coordinates.minor", -1)
								.append("coordinates.servicePack", -1).append("coordinates.build", -1)).limit(1);
				try {
					if (oldTipCursor.hasNext()) {
						oldTip = oldTipCursor.next();
					}
				} finally {
					oldTipCursor.close();
				}
				if (oldTip != null) { // if there is an old tip...
					final String oldTipText = (String) oldTip.get("testing-tips"); // get it and...
					if (!tipText.equals(oldTipText)) {// compare it to the current tip to it, if they're not the same...
						final DBObject newTip = new BasicDBObject("testing-tips", tipText).append("coordinates",
								coordinates.getTestingTipsCoordinates(featureId, scenarioId))
								.append("_id", coordinates.getTestingTipsId(featureId, scenarioId));
						tips.save(newTip);// then save this as a new tip.
					}
				} else { // no prior tip exists, add this one.
					final DBObject newTip = new BasicDBObject("testing-tips", tipText).append("coordinates",
							coordinates.getTestingTipsCoordinates(featureId, scenarioId))
							.append("_id", coordinates.getTestingTipsId(featureId, scenarioId));
					tips.save(newTip);// then save this as a new tip.
				}
			}
			scenario.setTestingTips(null);
		}
	}

	/**
	 * Uses the '.+' regexp on featureId to allow for symbols such as slashes in the id
	 *
	 * @param featureId The featureId to make changes to
	 * @return DBObjet Returns the the features new state if changes were made and returns null if bad JSON was sent
	 */
	@PUT
	@Path("/{product}/{major}.{minor}.{servicePack}/{build}/{featureId:.+}")
	@Consumes("application/json")
	@Produces(MediaType.APPLICATION_JSON)
	public Feature putFeature(@BeanParam final Coordinates co, @PathParam("featureId") final String featureId,
			@Context final HttpServletRequest req, final Feature feature) {
		feature.setCalculatedStatus(StatusHelper.getFeatureStatus(feature));
		try {
			final DB bdd = this.client.getDB("bdd");
			final Jongo jongo = new Jongo(bdd);
			final MongoCollection collection = jongo.getCollection("features");
			final BasicDBObject example = co.getReportCoordinatesQueryObject().append("id", featureId);

			final Feature report = collection.findOne(JSON.serialize(example)).as(Feature.class);

			// get the differences/new edits
			feature.setStatusLastEditedBy(req.getRemoteUser());
			feature.setLastEditOn(new Date());
			feature.setEdits(updateEdits(feature, report));

			updateTestingTips(bdd, co, featureId, feature); // save testing tips / strip them out of the document.
			updateEnvironmentDetails(bdd, co, feature);

			collection.save(feature);
			FeatureResource.embedTestingTips(feature, co, bdd); // rembed testing tips.
			return feature;// pull back feature - will re-include tips that were extracted prior to saving
		} catch (final Throwable th) {
			th.printStackTrace();
			return null;
		}
	}

	/**
	 * Goes through each environment detail on this feature and pushes each unique one to a per-product document in the 'environments'
	 * collection.
	 *
	 * @param db
	 * @param coordinates
	 * @param feature
	 */
	public void updateEnvironmentDetails(final DB db, final Coordinates coordinates, final Feature feature) {
		final DBCollection env = db.getCollection("environments");
		final List<Scenario> elements = feature.getScenarios();
		final BasicDBObject envQuery = coordinates.getQueryObject(Field.PRODUCT);
		// pull back the "product" document containing all the environments.
		DBObject productEnvironments = env.findOne(envQuery);
		// if one doesn't exist then create it.
		if (productEnvironments == null) {
			productEnvironments = new BasicDBObject();
			productEnvironments.put("coordinates", coordinates.getObject(Field.PRODUCT));
		}
		// pull back the list of environments
		List<Object> envs = (List<Object>) productEnvironments.get("environments");
		// if the list doesn't exist then create it.
		if (envs == null) {
			envs = new BasicDBList();
			productEnvironments.put("environments", envs);
		}
		final List<String> titleCache = new ArrayList<String>();
		// go through each scenario, pull out the environment details and add them to the back of the list.
		for (final Scenario scenario : elements) {
			String notes = scenario.getEnvironment();
			if (notes != null) {
				notes = notes.trim();
				if (notes.length() > 0) {
					if (!titleCache.contains(notes)) {
						titleCache.add(notes);
					}
				}
			}
		}
		// go through each unique environment detail, remove it if it is already in the list and append to the end.
		for (final String environmentDetail : titleCache) {
			envs.remove(environmentDetail);
			envs.add(environmentDetail);
		}
		// if the list gets too long, truncate it on a LRU basis.
		if (envs.size() > MAX_ENVIRONMENTS_FOR_A_PRODUCT) {
			envs = envs.subList(envs.size() - MAX_ENVIRONMENTS_FOR_A_PRODUCT, envs.size());
			productEnvironments.put("environments", envs);
		}
		// save the list back.
		env.save(productEnvironments);
	}

	public static void embedTestingTips(final Feature feature, final Coordinates co, final DB db) {
		final DBCollection tips = db.getCollection("testingTips");
		final List<Scenario> elements = feature.getScenarios();
		for (final Scenario scenario : elements) {
			DBObject oldTip = null;
			final BasicDBObject tipQuery = co.getTestingTipsCoordinatesQueryObject(feature.getId(), scenario.getId());
			// get the most recent tip that is LTE to the current coordinates. i.e. sort in reverse chronological order and take the first
			// item (if one exists).
			final DBCursor oldTipCursor = tips.find(tipQuery)
					.sort(new BasicDBObject("coordinates.major", -1).append("coordinates.minor", -1)
							.append("coordinates.servicePack", -1).append("coordinates.build", -1)).limit(1);
			try {
				if (oldTipCursor.hasNext()) {
					oldTip = oldTipCursor.next();
					scenario.setTestingTips(oldTip.get("testing-tips").toString());
				}
			} finally {
				oldTipCursor.close();
			}
		}
	}

	private List<Edit> updateEdits(final Feature feature, final Feature previousVersion) {
		List<Edit> edits = feature.getEdits();
		if (edits == null) {
			edits = new ArrayList<>();
		}
		final List<Edit> newEdits = new ArrayList<>();
		final Edit edit = new Edit();
		edit.setName(feature.getStatusLastEditedBy());
		edit.setDate(feature.getLastEditOn());
		edit.setPreviousStatus(previousVersion.getCalculatedStatus());
		edit.setCurrentStatus(feature.getCalculatedStatus());
		edit.setStepChanges(
				constructEditStepChanges(feature, previousVersion));
		newEdits.add(edit);
		newEdits.addAll(edits);
		return newEdits;
	}

	private List<ScenarioChange> constructEditStepChanges(final Feature currentVersion, final Feature previousVersion) {
		final List<ScenarioChange> scenarioChanges = new ArrayList<>();
		final List<Scenario> elements = currentVersion.getScenarios();
		final List<Scenario> prevElements = previousVersion.getScenarios();
		if (elements != null) {
			for (int i = 0; i < elements.size(); i++) {
				final List<StepChange> stepChanges = new ArrayList<>();
				final Scenario element = elements.get(i);
				final Scenario prevElement = prevElements.get(i);
				final String scenarioName = element.getName();

				boolean currManual = false;
				boolean prevManual = false;

				final Map<String, CucumberStep> currentStepsByName = new HashMap<>();
				final Map<String, CucumberStep> previousStepsByName = new HashMap<>();

				if (element.getBackground() != null) {
					final List<CucumberStep> backgroundSteps = element.getBackground().getSteps();
					if (backgroundSteps != null) {
						for (int j = 0; j < backgroundSteps.size(); j++) {
							final CucumberStep step = backgroundSteps.get(j);
							final CucumberStep prevStep = prevElement.getBackground().getSteps().get(j);

							final String stepName = step.getKeyword() + step.getName();
							currentStepsByName.put(stepName, step);
							previousStepsByName.put(stepName, prevStep);

							if (step.getResult().getManualStatus() != null) {
								currManual = true;
							}
							if (prevStep.getResult().getManualStatus() != null) {
								prevManual = true;
							}
						}
					}
				}
				if (element.getSteps() != null) {
					final List<CucumberStep> steps = element.getSteps();
					for (int j = 0; j < steps.size(); j++) {
						final CucumberStep step = steps.get(j);
						final CucumberStep prevStep = prevElement.getSteps().get(j);
						final String stepName = step.getKeyword() + step.getName();

						currentStepsByName.put(stepName, step);
						previousStepsByName.put(stepName, prevStep);

						if (step.getResult().getManualStatus() != null) {
							currManual = true;
						}
						if (prevStep.getResult().getManualStatus() != null) {
							prevManual = true;
						}
					}
				}

				for (final String stepName : currentStepsByName.keySet()) {
					addStep(previousStepsByName.get(stepName), currentStepsByName.get(stepName), stepChanges, currManual, prevManual);
				}

				// only add if changes have been made
				if (stepChanges.size() > 0) {
					final ScenarioChange singleScenario = new ScenarioChange();
					singleScenario.setScenario(scenarioName);
					singleScenario.setChanges(stepChanges);
					scenarioChanges.add(singleScenario);
				}
			}
		}
		return scenarioChanges;
	}

	private void addStep(final CucumberStep previousStep, final CucumberStep step,
			final List<StepChange> changes, final boolean currManual, final boolean prevManual) {
		String currState, currCause, prevState, prevCause;
		if (step.getResult().getManualStatus() != null) {
			currState = step.getResult().getManualStatus();
			currCause = "manual";
		} else {
			currCause = "auto";
			if (currManual) {
				currState = "undefined";
			} else {
				currState = step.getResult().getStatus();
			}

		}
		if (previousStep.getResult().getManualStatus() != null) {
			prevState = previousStep.getResult().getManualStatus();
			prevCause = "manual";
		} else {
			prevCause = "auto";
			if (prevManual) {
				prevState = "undefined";
			} else {
				prevState = previousStep.getResult().getStatus();
			}
		}

		// only add if different
		if (!currState.equals(prevState) || !currCause.equals(prevCause)) {
			final StepChange stepChange = new StepChange();
			final String id = step.getKeyword() + step.getName();
			stepChange.setId(id);
			stepChange.setCurr(currState);
			stepChange.setPrev(prevState);
			changes.add(stepChange);
		}
	}
}