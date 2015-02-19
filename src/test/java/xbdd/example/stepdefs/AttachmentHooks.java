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
package xbdd.example.stepdefs;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

import org.apache.commons.io.IOUtils;

import cucumber.api.Scenario;
import cucumber.api.java.After;

public class AttachmentHooks {

	@After("@attachment")
	public void embedAttachment(final Scenario scenario) throws FileNotFoundException, IOException {
		scenario.embed(IOUtils.toByteArray(new FileInputStream("src/test/resources/example/catbus-500x320.jpg")), "image/jpg");
	}

}
