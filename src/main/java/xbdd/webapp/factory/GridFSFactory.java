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
package xbdd.webapp.factory;

import javax.inject.Inject;
import javax.inject.Singleton;

import org.glassfish.hk2.api.Factory;

import com.mongodb.DB;
import com.mongodb.MongoClient;
import com.mongodb.ServerAddress;
import com.mongodb.gridfs.GridFS;

/**
 * Factory for {@link MongoClient} that retrieves hostname and port parameters from the ServletContext. The {@link MongoClient} is designed
 * to be used a {@link Singleton}. <br>
 * To specify a hostname, port number, username and password for the mongo db connection you can add parameters
 * <code>xbdd.mongo.hostname</code>, <code>xbdd.monogo.port</code>, <code>xbdd.monogo.username</code> and <code>xbdd.monogo.password</code>.
 * The parameters are optional and will default to the mongo defaults {@link ServerAddress#defaultHost()} and
 * {@link ServerAddress#defaultPort()} respectively.
 * 
 */
public class GridFSFactory implements Factory<GridFS> {

	private final MongoDBAccessor accessor;

	@Inject
	public GridFSFactory(final MongoDBAccessor accessor) {
		this.accessor = accessor;
	}

	@Override
	public void dispose(final GridFS gridFS) {

	}

	@Override
	public GridFS provide() {
		final DB grid = this.accessor.getDB("grid");
		return new GridFS(grid);
	}

}
