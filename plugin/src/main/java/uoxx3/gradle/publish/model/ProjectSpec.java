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
import org.jetbrains.annotations.UnmodifiableView;
import uoxx3.gradle.publish.Context;
import uoxx3.gradle.publish.annotations.RequireNotNull;
import uoxx3.gradle.publish.error.ValidationException;
import uoxx3.gradle.publish.extra.ResVal;
import uoxx3.gradle.publish.utilities.ProjectUtils;
import uoxx3.gradle.publish.utilities.ValidatorUtils;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

/**
 * Represents the specifications for a project, including its name, version, group, publications, and repositories.
 * This class implements the {@link ResVal} interface, providing methods for validation and resolution of its properties.
 */
public final class ProjectSpec implements ResVal {

	/* -------------------------------------------------------------------
	 * Properties
	 * ------------------------------------------------------------------- */

	/**
	 * The name of the project. This field is required.
	 */
	@Expose
	@RequireNotNull
	@SerializedName(value = "name", alternate = {"project-name", "n"})
	private String name;

	/**
	 * The version of the project. This field is required.
	 */
	@Expose
	@RequireNotNull
	@SerializedName(value = "version", alternate = {"version-name", "v"})
	private String version;

	/**
	 * The group of the project. This field is required.
	 */
	@Expose
	@RequireNotNull
	@SerializedName(value = "group", alternate = {"group-name", "g"})
	private String group;

	/**
	 * A set of publication specifications associated with the project.
	 * This field is optional.
	 */
	@Expose
	@SerializedName(value = "publications", alternate = {"p"})
	private Set<PublicationSpec> publications = new HashSet<>();

	/**
	 * A set of repository specifications associated with the project.
	 * This field is optional.
	 */
	@Expose
	@SerializedName(value = "repositories", alternate = {"repos", "r"})
	private Set<RepositorySpec> repositories = new HashSet<>();

	/* -------------------------------------------------------------------
	 * Internal definitions
	 * ------------------------------------------------------------------- */

	/**
	 * Create new instance of {@link ProjectSpec}
	 */
	public ProjectSpec() {
	}

	/**
	 * Resolves the project specifications within the provided context,
	 * validating and removing any invalid publications and repositories.
	 *
	 * @param ctx the context in which to resolve project specifications
	 */
	@Override
	public void resolve(@NotNull Context ctx) {
		// Validate all publications
		if (!publications().isEmpty()) {
			ProjectUtils.resolveAndRemoveItems(publications, ctx);
		}

		// Validate all repositories
		if (!repositories().isEmpty()) {
			ProjectUtils.resolveAndRemoveItems(repositories, ctx);
		}
	}

	/**
	 * Validates the project specifications within the provided context,
	 * ensuring that all required fields are populated and valid.
	 *
	 * @param ctx the context in which to validate project specifications
	 * @throws ValidationException if any validation error occurs
	 */
	@Override
	public void validate(@NotNull Context ctx) throws ValidationException {
		// Check required fields
		ValidatorUtils.validateNotNullProperties(this);

		// Validate all publications
		if (!publications().isEmpty()) {
			ProjectUtils.validateAndRemoveItems(publications, ctx);
		}

		// Validate all repositories
		if (!repositories().isEmpty()) {
			ProjectUtils.validateAndRemoveItems(repositories, ctx);
		}
	}

	/* -------------------------------------------------------------------
	 * Methods
	 * ------------------------------------------------------------------- */

	/**
	 * Returns the name of the project.
	 *
	 * @return the project name
	 */
	public @NotNull String name() {
		return name;
	}

	/**
	 * Returns the version of the project.
	 *
	 * @return the project version
	 */
	public @NotNull String version() {
		return version;
	}

	/**
	 * Returns the group of the project.
	 *
	 * @return the project group
	 */
	public @NotNull String group() {
		return group;
	}

	/**
	 * Returns an unmodifiable view of the publications associated with the project.
	 *
	 * @return an unmodifiable set of publication specifications
	 */
	public @NotNull @UnmodifiableView Set<PublicationSpec> publications() {
		return Collections.unmodifiableSet(publications);
	}

	/**
	 * Returns an unmodifiable view of the repositories associated with the project.
	 *
	 * @return an unmodifiable set of repository specifications
	 */
	public @NotNull @UnmodifiableView Set<RepositorySpec> repositories() {
		return Collections.unmodifiableSet(repositories);
	}

	/**
	 * Returns a string representation of the project specifications.
	 *
	 * @return a string representation of the project specifications
	 */
	@Contract(pure = true)
	@Override
	public @NotNull String toString() {
		return "ProjectSpec{" +
			"group='" + group + '\'' +
			", name='" + name + '\'' +
			", version='" + version + '\'' +
			", publications=" + publications +
			", repositories=" + repositories +
			'}';
	}
}