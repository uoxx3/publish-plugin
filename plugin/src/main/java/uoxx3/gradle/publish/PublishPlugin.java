/*
 * Copyright (c) 2024 Brian Alvarez
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated
 * documentation files (the "Software"), to deal in the Software without restriction, including without limitation
 * the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and
 * to permit persons to whom the Software is furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all copies or substantial
 * portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT
 * LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN
 * NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER
 * IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE
 * USE OR OTHER DEALINGS IN THE SOFTWARE.
 */
package uoxx3.gradle.publish;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.gradle.api.Plugin;
import org.gradle.api.Project;
import org.gradle.api.publish.PublishingExtension;
import org.gradle.api.publish.ivy.IvyPublication;
import org.gradle.api.publish.maven.MavenPublication;
import org.jetbrains.annotations.NotNull;
import uoxx3.gradle.publish.error.ResolveException;
import uoxx3.gradle.publish.error.ValidationException;
import uoxx3.gradle.publish.model.ProjectSpec;
import uoxx3.gradle.publish.model.PublicationSpec;
import uoxx3.gradle.publish.model.RepositorySpec;
import uoxx3.gradle.publish.utilities.IvyUtilities;
import uoxx3.gradle.publish.utilities.MavenUtilities;
import uoxx3.gradle.publish.utilities.ProjectUtils;
import uoxx3.project.environment.extension.ProjectEnvironmentExtension;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * The {@code PublishPlugin} class is a custom Gradle plugin used to configure publishing-related tasks for a project.
 *
 * <p>This plugin is responsible for applying necessary plugins, setting up the project context, validating
 * project definitions, and configuring repositories and publications based on the project specifications.</p>
 *
 * <p>Upon applying this plugin to a Gradle {@link Project}, it performs the following actions:</p>
 * <ul>
 *     <li>Applies required plugins, such as Maven and Ivy publishing.</li>
 *     <li>Initializes the {@link Context} object with project-specific information.</li>
 *     <li>Validates the presence of required project definitions.</li>
 *     <li>Configures repositories and publications after the project evaluation.</li>
 * </ul>
 *
 * <p>This class implements the {@link Plugin} interface and overrides the {@link #apply(Project)} method to provide
 * custom behavior when the plugin is applied to a project.</p>
 *
 * @see Plugin
 * @see Project
 */
public class PublishPlugin implements Plugin<Project> {

	/**
	 * Constant that holds the name of the project definition file.
	 */
	private static final String PROJECT_DEFINITION_NAME = "project";

	/**
	 * Array of strings representing the required plugins for the project.
	 *
	 * <p>These plugins are essential for the project setup and include Gradle's Java,
	 * Maven publishing, Ivy publishing, and a custom project environment plugin.</p>
	 */
	private static final String[] REQUIRED_PLUGINS = {
		"org.gradle.java",
		"org.gradle.maven-publish",
		"org.gradle.ivy-publish",
		"io.github.uoxx3.project-environment"
	};

	/**
	 * Constant that defines the name of the plugin extension used in the project.
	 *
	 * <p>This name is used to access the project-specific configuration in Gradle scripts.</p>
	 */
	public static final String PLUGIN_EXTENSION_NAME = "projectSpec";

	/* -------------------------------------------------------------------
	 * Properties
	 * ------------------------------------------------------------------- */

	private Context context;

	/* -------------------------------------------------------------------
	 * Plugin methods
	 * ------------------------------------------------------------------- */

	/**
	 * Create new instance of {@link PublishPlugin}
	 */
	public PublishPlugin() {
	}

	/**
	 * Applies the necessary plugins and initializes the project context.
	 *
	 * <p>This method is invoked when the plugin is applied to a Gradle project.
	 * It performs the following actions:</p>
	 * <ul>
	 *     <li>Attaches required plugins to the project if they are not already applied.</li>
	 *     <li>Initializes the {@link Context} with the project's environment extension, the project itself,
	 *         a configured {@link Gson} instance for JSON handling, and the project definitions.</li>
	 *     <li>Validates the project definitions using {@code validateDefinitions()}. If validation fails,
	 *         it stops further execution.</li>
	 *     <li>Initializes the project definition using {@code initializeDefinition()}.</li>
	 *     <li>Schedules the publication initialization to run after the project evaluation is complete using
	 *         {@code afterEvaluateInitialization()}.</li>
	 * </ul>
	 *
	 * @param project the Gradle {@link Project} to which the plugin is applied, must not be {@code null}.
	 */
	@Override
	public void apply(@NotNull Project project) {
		// Attach plugins
		for (String plugin : REQUIRED_PLUGINS) {
			if (project.getPlugins().hasPlugin(plugin)) continue;
			project.getPlugins().apply(plugin);
		}

		// Initialize context information
		context = new Context(
			project.getExtensions().getByType(ProjectEnvironmentExtension.class),
			project,
			(new GsonBuilder())
				.excludeFieldsWithoutExposeAnnotation()
				.serializeNulls()
				.setPrettyPrinting()
				.create(),
			ProjectUtils.getProjectDefinitions(project)
		);

		// Validate definition
		if (!validateDefinitions()) return;

		// Initialize definitions
		initializeDefinition();

		// Initialize publications when the configuration finished
		project.afterEvaluate(this::afterEvaluateInitialization);
	}

	/* -------------------------------------------------------------------
	 * Internal methods
	 * ------------------------------------------------------------------- */

	/**
	 * Validates that the project definitions contain the required project definition file.
	 *
	 * <p>This method checks whether the project definitions map includes the required key
	 * for the "project.json" file. If not found, it logs an error message and returns {@code false}.</p>
	 *
	 * @return {@code true} if the project definition is found, {@code false} otherwise.
	 */
	private boolean validateDefinitions() {
		if (!context.definitions().containsKey(PROJECT_DEFINITION_NAME)) {
			System.err.printf("> [%s] %s - Project \"project.json\" definition not found",
				getClass().getCanonicalName(), context.project());
			return false;
		}
		return true;
	}

	/**
	 * Initializes the project definition by reading and parsing the project definition file.
	 *
	 * <p>This method retrieves the project definition file, parses it into a {@link ProjectSpec} object,
	 * validates and resolves the spec, and registers it in the project's extension. If any error occurs
	 * during this process, appropriate error messages are logged.</p>
	 *
	 * <p>Specifically, it performs the following actions:</p>
	 * <ul>
	 *     <li>Reads the project definition file as an input stream.</li>
	 *     <li>Parses the file into a {@link ProjectSpec} object using the {@link Gson} instance.</li>
	 *     <li>Validates and resolves the {@link ProjectSpec} by calling {@code validateAndResolve()}.</li>
	 *     <li>Registers the {@link ProjectSpec} in the project's extension under the name defined by
	 *         {@code PLUGIN_EXTENSION_NAME}.</li>
	 *     <li>Logs success or error messages, depending on the outcome of parsing, validation, or resolution.</li>
	 * </ul>
	 */
	private void initializeDefinition() {
		Path definition = context.definitions().get(PROJECT_DEFINITION_NAME);
		try (InputStream stream = Files.newInputStream(definition);
		     InputStreamReader reader = new InputStreamReader(stream)) {
			// Generate spec instance
			ProjectSpec projectSpec = context.gson().fromJson(reader, ProjectSpec.class);

			// validate and resolve instance
			projectSpec.validateAndResolve(context);

			// Register spec in the project
			context.project()
				.getExtensions()
				.add(ProjectSpec.class, PLUGIN_EXTENSION_NAME, projectSpec);
			System.out.printf("> [%s]: Project definition result - Ok%n",
				projectSpec.getClass().getCanonicalName());

			// Configure base properties
			initializeBaseConfiguration(projectSpec);
		} catch (IOException e) {
			System.err.printf("> [%s] Error to parse project definition: %s%n",
				getClass().getCanonicalName(), e.getMessage());
		} catch (ValidationException e) {
			System.err.printf("> [%s] Error to validate project definition: %s%n",
				getClass().getCanonicalName(), e.getMessage());
		} catch (ResolveException e) {
			System.err.printf("> [%s] Error to resolve project definition: %s%n",
				getClass().getCanonicalName(), e.getMessage());
		} catch (Exception e) {
			System.err.printf("> [%s]: %s%n",
				getClass().getCanonicalName(), e.getMessage());
		}
	}

	/**
	 * Sets the base configuration of the project using the provided {@link ProjectSpec}.
	 *
	 * <p>This method configures the project by setting its group and version
	 * based on the values defined in the {@link ProjectSpec}.</p>
	 *
	 * @param spec the {@link ProjectSpec} that contains the base project configuration, must not be {@code null}.
	 */
	private void initializeBaseConfiguration(@NotNull ProjectSpec spec) {
		// Set base project configurations
		context.project().setGroup(spec.group());
		context.project().setVersion(spec.version());
	}

	/* -------------------------------------------------------------------
	 * Internal after evaluation options
	 * ------------------------------------------------------------------- */

	/**
	 * Performs post-evaluation initialization tasks for the project.
	 *
	 * <p>This method is invoked after the project has been fully evaluated by Gradle. It retrieves the
	 * necessary extensions, including the {@link ProjectSpec} and {@link PublishingExtension}, and
	 * configures the repositories and publications for the project based on the project specifications.</p>
	 *
	 * <p>Specifically, it performs the following actions:</p>
	 * <ul>
	 *     <li>Retrieves the {@link ProjectSpec} from the project's extensions using {@code PLUGIN_EXTENSION_NAME}.</li>
	 *     <li>Retrieves the {@link PublishingExtension} to manage the publication configuration.</li>
	 *     <li>Calls {@code configureRepositories()} to set up repositories for the project.</li>
	 *     <li>Calls {@code configurePublications()} to define the project's publications.</li>
	 * </ul>
	 *
	 * @param project the Gradle {@link Project} to which the post-evaluation configuration is applied, must not be {@code null}.
	 */
	private void afterEvaluateInitialization(@NotNull Project project) {
		// Get required extensions
		ProjectSpec spec = (ProjectSpec) project.getExtensions().findByName(PLUGIN_EXTENSION_NAME);
		PublishingExtension publishing = project.getExtensions().findByType(PublishingExtension.class);

		// Generate repository and publications
		if (spec != null && publishing != null) {
			configureRepositories(spec, publishing, project);
			configurePublications(spec, publishing, project);
		}
	}

	/**
	 * Configures the repositories for the project based on the provided {@link ProjectSpec}.
	 *
	 * <p>This method accesses the repository container within the {@link PublishingExtension} and iterates over
	 * all the repositories defined in the {@link ProjectSpec}. Depending on the repository type
	 * (Maven or Ivy), it delegates the configuration to the appropriate utility class
	 * (e.g., {@link MavenUtilities} or {@link IvyUtilities}).</p>
	 *
	 * <p>For each repository, this method:</p>
	 * <ul>
	 *     <li>For Maven repositories, it calls {@link MavenUtilities#configureRepository} to configure the repository.</li>
	 *     <li>For Ivy repositories, it calls {@link IvyUtilities#configureRepository} to configure the repository.</li>
	 * </ul>
	 *
	 * @param projectSpec the {@link ProjectSpec} containing the repository definitions, must not be {@code null}.
	 * @param extension   the {@link PublishingExtension} where the repositories are configured, must not be {@code null}.
	 * @param project     the Gradle {@link Project} to which the repositories are applied, must not be {@code null}.
	 */
	private void configureRepositories(
		@NotNull ProjectSpec projectSpec,
		@NotNull PublishingExtension extension,
		@NotNull Project project
	) {
		// Access to repository container
		extension.repositories(container -> {
			// Iterate all repositories
			for (RepositorySpec repository : projectSpec.repositories()) {
				switch (repository.type()) {
					case Maven -> container.maven(repo -> MavenUtilities.configureRepository(
						context,
						project,
						repository,
						projectSpec,
						repo));
					case Ivy -> container.ivy(repo -> IvyUtilities.configureRepository(
						context,
						project,
						repository,
						projectSpec,
						repo));
				}
			}
		});
	}

	/**
	 * Configures the publications for the project based on the provided {@link ProjectSpec}.
	 *
	 * <p>This method accesses the publication container within the {@link PublishingExtension} and iterates over
	 * all the publications defined in the {@link ProjectSpec}. Depending on the publication type
	 * (Maven or Ivy), it creates the appropriate publication and delegates the actual configuration
	 * to the relevant utility class (e.g., {@link MavenUtilities} or {@link IvyUtilities}).</p>
	 *
	 * <p>For each publication, this method:</p>
	 * <ul>
	 *     <li>For Maven publications, it calls {@link MavenUtilities#generatePublication} to configure the publication.</li>
	 *     <li>For Ivy publications, it calls {@link IvyUtilities#generatePublication} to configure the publication.</li>
	 * </ul>
	 *
	 * @param projectSpec the {@link ProjectSpec} containing the publication definitions, must not be {@code null}.
	 * @param extension   the {@link PublishingExtension} where the publications are configured, must not be {@code null}.
	 * @param project     the Gradle {@link Project} to which the publications are applied, must not be {@code null}.
	 */
	private void configurePublications(
		@NotNull ProjectSpec projectSpec,
		@NotNull PublishingExtension extension,
		@NotNull Project project
	) {
		// Access to publication container
		extension.publications(container -> {
			// Iterate all publications
			for (PublicationSpec publication : projectSpec.publications()) {
				switch (publication.type()) {
					case Maven -> container.create(
						publication.name(),
						MavenPublication.class,
						mvnP -> MavenUtilities.generatePublication(context, mvnP, project, projectSpec, publication));
					case Ivy -> container.create(
						publication.name(),
						IvyPublication.class,
						ivyP -> IvyUtilities.generatePublication(context, ivyP, project, projectSpec, publication));
				}
			}
		});
	}

}
