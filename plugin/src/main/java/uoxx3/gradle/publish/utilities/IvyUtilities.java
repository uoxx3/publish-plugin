package uoxx3.gradle.publish.utilities;

import org.gradle.api.Project;
import org.gradle.api.artifacts.repositories.IvyArtifactRepository;
import org.gradle.api.publish.ivy.IvyModuleDescriptorSpec;
import org.gradle.api.publish.ivy.IvyPublication;
import org.jetbrains.annotations.NotNull;
import uoxx3.gradle.publish.Context;
import uoxx3.gradle.publish.model.*;

import java.util.Optional;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Utility class for configuring Ivy repositories and publications.
 * <p>
 * This class cannot be instantiated as it serves only as a container for static utility methods.
 */
public final class IvyUtilities extends CommonRepositoryUtilities {

	/**
	 * Private constructor to prevent instantiation.
	 * <p>
	 * This class cannot be instantiated.
	 * </p>
	 *
	 * @throws IllegalStateException if an attempt is made to instantiate this class.
	 */
	private IvyUtilities() {
		throw new IllegalStateException("IvyUtilities cannot be instantiated");
	}

	/* -------------------------------------------------------------------
	 * Methods
	 * ------------------------------------------------------------------- */

	/**
	 * Configures the specified Ivy repository using the given specifications.
	 * <p>
	 * This method sets the repository name, URL, and credentials based on the provided parameters.
	 * It also checks the project version against an optional regular expression defined in the repository specifications.
	 * </p>
	 *
	 * @param ctx            the context in which the repository is being configured
	 * @param project        the project for which the repository is being configured
	 * @param repositorySpec the specifications for the repository
	 * @param projectSpec    the specifications for the project
	 * @param repository     the Ivy artifact repository to configure
	 */
	public static void configureRepository(
		@NotNull Context ctx,
		@NotNull Project project,
		@NotNull RepositorySpec repositorySpec,
		@NotNull ProjectSpec projectSpec,
		@NotNull IvyArtifactRepository repository
	) {
		// Configure repository base information
		repository.setName(repositorySpec.name());

		// Check regular expression
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
	 * Generates a publication based on the provided specifications.
	 * <p>
	 * This method sets the revision, module, component, and artifacts for the specified publication.
	 * It also configures the publication descriptor using the project specifications.
	 * </p>
	 *
	 * @param ctx             the context in which the publication is being generated
	 * @param publication     the Ivy publication to configure
	 * @param project         the project for which the publication is being generated
	 * @param projectSpec     the specifications for the project
	 * @param publicationSpec the specifications for the publication
	 */
	public static void generatePublication(
		@NotNull Context ctx,
		@NotNull IvyPublication publication,
		@NotNull Project project,
		@NotNull ProjectSpec projectSpec,
		@NotNull PublicationSpec publicationSpec
	) {
		// Base configuration
		publication.setRevision(projectSpec.version());
		publication.setModule(publicationSpec.pom().id());

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

		// Configure Descriptor object
		publication.descriptor(descriptor -> configureDescriptor(
			ctx, descriptor, project, projectSpec, publicationSpec));
	}

	/**
	 * Configures the descriptor for the given Ivy publication.
	 * <p>
	 * This method sets the description, homepage, SCM information, licenses, and authors for the publication descriptor
	 * based on the provided specifications.
	 * </p>
	 *
	 * @param ctx             the context in which the descriptor is being configured
	 * @param descriptor      the Ivy module descriptor to configure
	 * @param project         the project for which the descriptor is being configured
	 * @param projectSpec     the specifications for the project
	 * @param publicationSpec the specifications for the publication
	 */
	private static void configureDescriptor(
		@NotNull Context ctx,
		@NotNull IvyModuleDescriptorSpec descriptor,
		@NotNull Project project,
		@NotNull ProjectSpec projectSpec,
		@NotNull PublicationSpec publicationSpec
	) {
		PomSpec pom = publicationSpec.pom();
		// Base configuration
		descriptor.description(d -> {
			pom.description().ifPresent(d.getText()::set);
			pom.url().ifPresent(v -> d.getHomepage().set(v.toString()));
		});

		// Configure scm
		if (pom.scm().isPresent()) {
			descriptor.setBranch(pom.scm().get().branch());
		}

		// Configure license
		for (LicenseSpec licence : pom.licenses()) {
			descriptor.license(l -> {
				l.getName().set(licence.name());
				licence.url().ifPresent(v -> l.getUrl().set(v.toString()));
			});
		}

		// Configure authors
		if (pom.developers().isEmpty()) return;
		for (DeveloperSpec developer : pom.developers().get().developers()) {
			descriptor.author(autor -> {
				autor.getName().set(developer.name().orElse(developer.id()));
				developer.url().ifPresent(v -> autor.getUrl().set(v.toString()));
			});
		}
	}

}