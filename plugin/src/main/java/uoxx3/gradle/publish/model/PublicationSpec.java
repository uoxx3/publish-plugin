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

package uoxx3.gradle.publish.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import uoxx3.gradle.publish.Context;
import uoxx3.gradle.publish.annotations.RequireNotNull;
import uoxx3.gradle.publish.error.ResolveException;
import uoxx3.gradle.publish.error.ValidationException;
import uoxx3.gradle.publish.extra.ResVal;
import uoxx3.gradle.publish.utilities.ValidatorUtils;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

/**
 * Represents the specifications for a publication, including metadata such as
 * type, name, component, associated artifacts, and POM specifications.
 * This class implements the {@link ResVal} interface, providing functionality
 * for resolving and validating publication specifications.
 */
public final class PublicationSpec implements ResVal {

	/* -------------------------------------------------------------------
	 * Properties
	 * ------------------------------------------------------------------- */

	/**
	 * The type of the repository for the publication, e.g., Maven.
	 * This field is required.
	 */
	@Expose
	@RequireNotNull
	@SerializedName(value = "type", alternate = {"publication-type", "pt", "t"})
	private RepositoryType type = RepositoryType.Maven;

	/**
	 * The name of the publication, which serves as an identifier.
	 * This field is required.
	 */
	@Expose
	@RequireNotNull
	@SerializedName(value = "name", alternate = {"id", "key", "n", "pn", "publication-name"})
	private String name;

	/**
	 * The component associated with the publication, defaulting to "java".
	 * This field is required.
	 */
	@Expose
	@RequireNotNull
	@SerializedName(value = "component", alternate = {"c", "cmp", "publication-component"})
	private String component = "java";

	/**
	 * A set of artifacts related to the publication. This field is optional.
	 */
	@Expose
	@SerializedName(value = "artifacts", alternate = {"a", "arts", "publication-artifacts"})
	private Set<ArtifactSpec> artifacts = new HashSet<>();

	/**
	 * The POM specifications associated with the publication.
	 * This field is required.
	 */
	@Expose
	@RequireNotNull
	@SerializedName(value = "pom", alternate = {"descriptor", "publication-descriptor", "p", "publication-pom"})
	private PomSpec pom;

	/* -------------------------------------------------------------------
	 * Internal implementations
	 * ------------------------------------------------------------------- */

	/**
	 * Create new instance of {@link PublicationSpec}
	 */
	public PublicationSpec() {
	}

	/**
	 * Resolves the artifacts specified in the publication. If no artifacts
	 * are present, this method does nothing. This method uses the provided
	 * context for resolution.
	 *
	 * @param ctx the context in which to resolve the publication artifacts
	 * @throws ResolveException if any errors occur during resolution
	 */
	@Override
	public void resolve(@NotNull Context ctx) throws ResolveException {
		// Artifacts resolution
		if (artifacts().isEmpty()) return;
		for (ArtifactSpec artifact : artifacts().get()) {
			artifact.resolve(ctx);
		}
	}

	/**
	 * Validates the publication specifications, ensuring that all required fields
	 * are present and valid. This includes validating the POM and any associated
	 * artifacts.
	 *
	 * @param ctx the context in which to validate the publication specifications
	 * @throws ValidationException if any validation errors occur
	 */
	@Override
	public void validate(@NotNull Context ctx) throws ValidationException {
		ValidatorUtils.validateNotNullProperties(this);

		// POM validation
		pom.validate(ctx);

		// Artifacts validation
		if (artifacts().isEmpty()) return;
		for (ArtifactSpec artifact : artifacts().get()) {
			artifact.validate(ctx);
		}
	}

	/* -------------------------------------------------------------------
	 * Methods
	 * ------------------------------------------------------------------- */

	/**
	 * Returns the set of artifacts associated with the publication as an Optional.
	 *
	 * @return an Optional containing the set of artifacts, or empty if none are present
	 */
	public @NotNull Optional<Set<ArtifactSpec>> artifacts() {
		return Optional.ofNullable(artifacts);
	}

	/**
	 * Returns the component associated with the publication.
	 *
	 * @return the publication component
	 */
	public @NotNull String component() {
		return component;
	}

	/**
	 * Returns the name of the publication.
	 *
	 * @return the publication name
	 */
	public @NotNull String name() {
		return name;
	}

	/**
	 * Returns the POM specifications associated with the publication.
	 *
	 * @return the POM specifications
	 */
	public @NotNull PomSpec pom() {
		return pom;
	}

	/**
	 * Returns the type of the repository for the publication.
	 *
	 * @return the repository type
	 */
	public @NotNull RepositoryType type() {
		return type;
	}

	/**
	 * Returns a string representation of the PublicationSpec,
	 * including artifacts, type, name, component, and POM.
	 *
	 * @return a string representation of this object
	 */
	@Contract(pure = true)
	@Override
	public @NotNull String toString() {
		return "PublicationSpec{" +
			"artifacts=" + artifacts +
			", type=" + type +
			", name='" + name + '\'' +
			", component='" + component + '\'' +
			", pom=" + pom +
			'}';
	}
}