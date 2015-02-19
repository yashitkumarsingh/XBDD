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
package xbdd.webapp;

import javax.inject.Singleton;

import org.glassfish.hk2.utilities.binding.AbstractBinder;
import org.glassfish.jersey.media.multipart.MultiPartFeature;
import org.glassfish.jersey.server.ResourceConfig;
import org.glassfish.jersey.server.ServerProperties;
import org.glassfish.jersey.server.TracingConfig;
import org.glassfish.jersey.server.mvc.jsp.JspMvcFeature;
import org.glassfish.jersey.server.mvc.jsp.JspProperties;

import xbdd.model.databind.CucumberEmbeddingDeserializer;
import xbdd.model.databind.CucumberEmbeddingSerializer;
import xbdd.model.transform.FeatureTransformer;
import xbdd.model.transform.ScenarioListTransformer;
import xbdd.webapp.factory.GridFSFactory;
import xbdd.webapp.factory.MongoDBAccessor;
import xbdd.webapp.factory.ServletContextMongoClientFactory;

import com.mongodb.gridfs.GridFS;

public class XbddApplication extends ResourceConfig {

	public XbddApplication() {
		packages(getClass().getPackage().getName());

		// MVC feature
		register(JspMvcFeature.class);
		register(MultiPartFeature.class);

		// Logging.
		// register(LoggingFilter.class);

		property(ServerProperties.TRACING, TracingConfig.ON_DEMAND.name());
		property(JspProperties.TEMPLATES_BASE_PATH, "WEB-INF/jsp");

		register(new AbstractBinder() {
			@Override
			protected void configure() {
				/* Bindings */
				bind(CucumberEmbeddingDeserializer.class).to(CucumberEmbeddingDeserializer.class);
				bind(CucumberEmbeddingSerializer.class).to(CucumberEmbeddingSerializer.class);
				bind(ScenarioListTransformer.class).to(ScenarioListTransformer.class);
				bind(FeatureTransformer.class).to(FeatureTransformer.class);

				/* Factories */
				bindFactory(ServletContextMongoClientFactory.class).to(MongoDBAccessor.class).in(Singleton.class);
				bindFactory(GridFSFactory.class).to(GridFS.class);
			}
		});
	}
}
