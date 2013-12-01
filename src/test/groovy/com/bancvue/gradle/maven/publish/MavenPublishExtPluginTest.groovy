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
package com.bancvue.gradle.maven.publish

import com.bancvue.gradle.test.AbstractPluginTest
import org.gradle.api.Task
import org.gradle.api.artifacts.ArtifactRepositoryContainer
import org.gradle.api.artifacts.repositories.MavenArtifactRepository
import org.gradle.api.publish.maven.tasks.PublishToMavenLocal
import org.gradle.api.publish.maven.tasks.PublishToMavenRepository
import org.junit.Before
import org.junit.Test

class MavenPublishExtPluginTest extends AbstractPluginTest {

	MavenPublishExtPluginTest() {
		super(MavenPublishExtPlugin.PLUGIN_NAME)
	}

	@Before
	void setUp() {
		setArtifactId('1.0')
	}

	@Test
	void apply_ShouldApplyMavenPlugins() {
		applyPlugin()

		assertNamedPluginApplied('maven-publish')
	}

	@Test
	void apply_ShouldAddNexusDependencyRepository() {
		project.ext.repositoryName = 'repo'
		project.ext.repositoryPublicUrl = 'http://public-url'

		applyPlugin()

		MavenArtifactRepository nexusRepo = getMavenRepo('repo')
		assert nexusRepo != null
		assert nexusRepo.url.toString() == 'http://public-url'
	}

	private MavenArtifactRepository getMavenRepo(String name) {
		project.repositories.getByName(name) as MavenArtifactRepository
	}

	@Test
	void apply_ShouldAddMavenLocalDependencyRepository() {
		applyPlugin()

		MavenArtifactRepository mavenLocal = getMavenRepo(ArtifactRepositoryContainer.DEFAULT_MAVEN_LOCAL_REPO_NAME)
		assert mavenLocal != null
	}

	private MavenArtifactRepository getMavenPublishingRepo(String name) {
		project.publishing.repositories.getByName(name)
	}

	@Test
	void apply_ShouldAddNexusSnapshotPublishingRepository_IfVersionSnapshot() {
		project.version = '1.0-SNAPSHOT'
		project.ext.repositoryName = 'repo'
		project.ext.repositorySnapshotUrl = 'http://snapshot-url'

		applyPlugin()

		MavenArtifactRepository nexusRepo = getMavenPublishingRepo('repo')
		assert nexusRepo.url.toString() == 'http://snapshot-url'
	}

	@Test
	void apply_ShouldAddNexusReleasePublishingRepository_IfVersionNotSnapshot() {
		project.version = '1.0'
		project.ext.repositoryName = 'repo'
		project.ext.repositoryReleaseUrl = 'http://release-url'

		applyPlugin()

		MavenArtifactRepository nexusRepo = getMavenPublishingRepo('repo')
		assert nexusRepo.url.toString() == 'http://release-url'
	}

	private Task acquireSingleDependencyForTask(String taskName) {
		List dependencies = getDependenciesForTask(taskName)
		assert dependencies.size() == 1
		dependencies[0]
	}

	private List getDependenciesForTask(String taskName) {
		Task task = project.tasks.findByName(taskName)
		task.taskDependencies.getDependencies(task).toList()
	}

	@Test
	void apply_ShouldRemapPublishTaskToPublishRemote() {
		applyPlugin()
		project.evaluate()

		Task publishRemoteDependency = acquireSingleDependencyForTask('publishRemote')
		assert publishRemoteDependency instanceof PublishToMavenRepository
		assert !(publishRemoteDependency instanceof PublishToMavenLocal)
	}

	@Test
	void apply_ShouldRemapPublishToMavenLocalTaskToPublish() {
		applyPlugin()
		project.evaluate()

		Task publishRemoteDependency = acquireSingleDependencyForTask('publish')
		assert publishRemoteDependency instanceof PublishToMavenLocal
	}

}
