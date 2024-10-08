package uoxx3.gradle.publish.utilities;

import org.gradle.api.Project;
import org.gradle.api.Task;
import org.gradle.api.artifacts.PublishArtifact;
import org.gradle.api.publish.Publication;
import org.gradle.api.publish.ivy.IvyPublication;
import org.gradle.api.publish.maven.MavenArtifact;
import org.gradle.api.publish.maven.MavenPublication;
import org.gradle.api.tasks.bundling.AbstractArchiveTask;
import org.jetbrains.annotations.NotNull;
import uoxx3.gradle.publish.model.ArtifactSpec;

import java.io.File;

/**
 * Abstract utility class that provides static methods to assist with
 * configuring and managing Maven and Ivy repositories.
 * <p>
 * This class is intended to be extended by other classes that need
 * repository-related functionalities, but it cannot be instantiated.
 * </p>
 *
 * <h2>Methods</h2>
 * <ul>
 *     <li>Static methods for repository configuration.</li>
 *     <li>Utility methods for managing repository artifacts.</li>
 *     <li>Common functionalities for both Maven and Ivy repositories.</li>
 * </ul>
 */
abstract class CommonRepositoryUtilities {

	/**
	 * Configures an artifact within a Maven publication based on the specified artifact
	 * specification.
	 *
	 * @param publication  the IvyPublication to which the artifact will be attached,
	 *                     must not be {@code null}.
	 * @param project      the current Gradle project, must not be {@code null}.
	 * @param artifactSpec the specification detailing the artifact settings, must not be
	 *                     {@code null}.
	 * @throws IllegalStateException if the required artifact cannot be found or is invalid.
	 */
	protected static void configureArtifact(
		@NotNull Publication publication,
		@NotNull Project project,
		@NotNull ArtifactSpec artifactSpec
	) {
		switch (artifactSpec.type()) {
			case Task -> {
				// Extract artifact task
				Task taskInstance = project.getTasks().findByName(artifactSpec.name());
				// Check task value and requirements
				if (taskInstance == null && artifactSpec.required()) {
					throw new IllegalStateException("> [%s] Artifact task not found: %s".formatted(
						publication.getClass().getCanonicalName(),
						artifactSpec.name()));
				}

				// Attach artifact
				if (taskInstance instanceof MavenArtifact ||
					taskInstance instanceof AbstractArchiveTask ||
					taskInstance instanceof PublishArtifact
				) {
					if (publication instanceof IvyPublication) {
						((IvyPublication) publication).artifact(taskInstance);
					} else if (publication instanceof MavenPublication) {
						((MavenPublication) publication).artifact(taskInstance);
					}
				}
			}
			case File -> {
				// Extract file location
				File location = new File(artifactSpec.name());
				// Check if the file exists
				if (!location.exists()) {
					throw new IllegalStateException("> [%s] Artifact file not found: %s".formatted(
						publication.getClass().getCanonicalName(),
						location));
				}

				// Attach artifact file
				if (publication instanceof IvyPublication) {
					((IvyPublication) publication).artifact(location);
				} else if (publication instanceof MavenPublication) {
					((MavenPublication) publication).artifact(location);
				}
			}
		}
	}

}
