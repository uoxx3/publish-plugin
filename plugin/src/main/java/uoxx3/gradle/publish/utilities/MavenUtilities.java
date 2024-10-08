package uoxx3.gradle.publish.utilities;

import org.gradle.api.Project;
import org.gradle.api.artifacts.repositories.MavenArtifactRepository;
import org.gradle.api.publish.maven.MavenPom;
import org.gradle.api.publish.maven.MavenPublication;
import org.jetbrains.annotations.NotNull;
import uoxx3.gradle.publish.Context;
import uoxx3.gradle.publish.model.*;

import java.util.Optional;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Utility class providing methods for configuring Maven repositories and publications
 * in a Gradle project. This class cannot be instantiated.
 *
 * <p>It contains methods to configure repository settings based on specifications,
 * as well as to generate Maven publications and their associated POM configurations.</p>
 */
public final class MavenUtilities extends CommonRepositoryUtilities {

	/**
	 * This class cannot be instantiated
	 */
	private MavenUtilities() {
		throw new IllegalStateException("MavenUtilities cannot be instantiated");
	}

	/* -------------------------------------------------------------------
	 * Methods
	 * ------------------------------------------------------------------- */

	/**
	 * Configures the specified Maven repository with the provided settings from the
	 * repository specification and project specification.
	 *
	 * @param ctx            the context containing project information, must not be {@code null}.
	 * @param project        the current Gradle project, must not be {@code null}.
	 * @param repositorySpec the specification detailing the repository settings,
	 *                       must not be {@code null}.
	 * @param projectSpec    the project specification, must not be {@code null}.
	 * @param repository     the MavenArtifactRepository to configure, must not be {@code null}.
	 */
	@SuppressWarnings("unused")
	public static void configureRepository(
		@NotNull Context ctx,
		@NotNull Project project,
		@NotNull RepositorySpec repositorySpec,
		@NotNull ProjectSpec projectSpec,
		@NotNull MavenArtifactRepository repository
	) {
		// Configure repository base information
		repository.setName(repositorySpec.name());

		// Check if the regular expression exists
		Optional<Pattern> regexOpt = repositorySpec.regex();
		if (regexOpt.isPresent()) {
			Matcher matcher = regexOpt.get().matcher(projectSpec.version());
			if (!matcher.find()) return;
		}

		repositorySpec.url().ifPresent(repository::setUrl);

		// Configure repository credentials
		repositorySpec.credentials().ifPresent(credentials -> repository.credentials(form -> {
			form.setUsername(credentials.username());
			form.setPassword(credentials.password());
		}));
	}

	/**
	 * Generates a Maven publication based on the provided specifications and
	 * configures it accordingly.
	 *
	 * @param ctx             the context containing project information, must not be {@code null}.
	 * @param publication     the MavenPublication to configure, must not be {@code null}.
	 * @param project         the current Gradle project, must not be {@code null}.
	 * @param projectSpec     the project specification, must not be {@code null}.
	 * @param publicationSpec the publication specification detailing the publication settings,
	 *                        must not be {@code null}.
	 */
	public static void generatePublication(
		@NotNull Context ctx,
		@NotNull MavenPublication publication,
		@NotNull Project project,
		@NotNull ProjectSpec projectSpec,
		@NotNull PublicationSpec publicationSpec
	) {
		// Base configuration
		publication.setGroupId(projectSpec.group());
		publication.setVersion(projectSpec.version());

		// Configure artifact id
		publication.setArtifactId(publicationSpec.pom().id());

		// Configure component if exists
		Optional.ofNullable(project.getComponents().findByName(publicationSpec.component()))
			.ifPresent(publication::from);

		// Configure artifacts
		if (publicationSpec.artifacts().isPresent()) {
			Set<ArtifactSpec> artifacts = publicationSpec.artifacts().get();
			for (ArtifactSpec artifact : artifacts) {
				configureArtifact(publication, project, artifact);
			}
		}

		// Configure POM object
		publication.pom(mavenPom -> configurePom(
			ctx, mavenPom, project, projectSpec, publicationSpec));
	}

	/**
	 * Configures the POM (Project Object Model) for a Maven publication based on the
	 * given specifications.
	 *
	 * @param ctx             the context containing project information, must not be {@code null}.
	 * @param pom             the MavenPom object to configure, must not be {@code null}.
	 * @param project         the current Gradle project, must not be {@code null}.
	 * @param projectSpec     the project specification, must not be {@code null}.
	 * @param publicationSpec the publication specification detailing the POM settings,
	 *                        must not be {@code null}.
	 */
	@SuppressWarnings("unused")
	private static void configurePom(
		@NotNull Context ctx,
		@NotNull MavenPom pom,
		@NotNull Project project,
		@NotNull ProjectSpec projectSpec,
		@NotNull PublicationSpec publicationSpec
	) {
		// Base pom configuration
		pom.getName().set(publicationSpec.pom().id());
		publicationSpec.pom().url().ifPresent(v -> pom.getUrl().set(v.toString()));
		publicationSpec.pom().description().ifPresent(pom.getDescription()::set);

		// Licenses configuration
		pom.licenses(container -> {
			// Iterate all licenses
			for (LicenseSpec license : publicationSpec.pom().licenses()) {
				container.license(mvnLicense -> {
					mvnLicense.getName().set(license.name());
					license.comments().ifPresent(mvnLicense.getComments()::set);
					license.distribution().ifPresent(mvnLicense.getDistribution()::set);
					license.url().ifPresent(v -> mvnLicense.getUrl().set(v.toString()));
				});
			}
		});

		// Developers configuration
		pom.developers(container -> {
			if (publicationSpec.pom().developers().isEmpty()) return;

			// Iterate all developers
			PomDeveloperSpec developers = publicationSpec.pom().developers().get();
			for (DeveloperSpec developer : developers.developers()) {
				container.developer(mvnDeveloper -> {
					// Required information
					mvnDeveloper.getId().set(developer.id());
					mvnDeveloper.getEmail().set(developer.email());
					mvnDeveloper.getRoles().set(developer.roles());
					// Optional information
					developer.name().ifPresent(mvnDeveloper.getName()::set);
					developer.url().ifPresent(v -> mvnDeveloper.getUrl().set(v.toString()));
					developer.timezone().ifPresent(mvnDeveloper.getTimezone()::set);

					developer.organization().ifPresent(org -> {
						mvnDeveloper.getOrganization().set(org.name());
						org.organizationUrl().ifPresent(v -> mvnDeveloper.getOrganizationUrl().set(v.toString()));
					});
				});
			}
		});

		// SCM configuration
		if (publicationSpec.pom().scm().isEmpty()) return;
		ScmSpec scm = publicationSpec.pom().scm().get();
		pom.scm(mvnScm -> {
			scm.url().ifPresent(v -> mvnScm.getUrl().set(v.toString()));
			mvnScm.getConnection().set(scm.connection());
			mvnScm.getDeveloperConnection().set(scm.developerConnection());
		});
	}

}
