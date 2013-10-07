/**
 * Copyright 2013 BancVue, LTD
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.bancvue.gradle.test

import org.gradle.api.tasks.testing.logging.TestExceptionFormat
import org.gradle.api.tasks.testing.logging.TestLogEvent
import org.gradle.api.tasks.testing.logging.TestStackTraceFilter
import org.junit.Test

class TestExtPluginTest extends AbstractPluginTest {

	TestExtPluginTest() {
		super(TestExtPlugin.PLUGIN_NAME)
	}

	@Test
	void apply_ShouldWriteStackTraceOnTestFailure() {
		applyPlugin()

		org.gradle.api.tasks.testing.Test test = project.tasks.getByName('test')

		assert test.testLogging.exceptionFormat == TestExceptionFormat.FULL
		assert test.testLogging.stackTraceFilters.contains(TestStackTraceFilter.GROOVY)
	}

	@Test
	void apply_ShouldWriteSkippedEvents() {
		applyPlugin()

		org.gradle.api.tasks.testing.Test test = project.tasks.getByName('test')

		assert test.testLogging.events.contains(TestLogEvent.SKIPPED)
	}

	@Test
	void apply_ShouldAddStyledTestOutputTaskAndConfigureToExecuteBeforeTestTasks() {
		applyPlugin()

		StyledTestOutput styledOutputTask = project.tasks.getByName('styledTestOutput')
		use(TaskCategory) {
			styledOutputTask.assertMustRunBefore('test')
		}
	}

}
