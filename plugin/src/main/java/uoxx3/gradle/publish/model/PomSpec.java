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
import uoxx3.gradle.publish.extra.Validator;
import uoxx3.gradle.publish.utilities.ValidatorUtils;

import java.net.URI;
import java.util.Collections;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

/**
 * Represents the specifications for a Project Object Model (POM),
 * including metadata such as ID, URL, description, licenses, developers,
 * and source control management (SCM) information. This class implements
 * the {@link Validator} interface, providing validation functionality
 * for POM specifications.
 */
public final class PomSpec implements Validator {

	/* -------------------------------------------------------------------
	 * Properties
	 * ------------------------------------------------------------------- */

	/**
	 * The unique identifier for the artifact, also known as the project ID.
	 */
	@Expose
	@RequireNotNull
	@SerializedName(value = "id", alternate = {"module", "module-name", "artifact-id", "project-id"})
	private String id;

	/**
	 * The URL of the project, typically used for publication.
	 */
	@Expose
	@RequireNotNull
	@SerializedName(value = "url", alternate = {"project-url", "publication-url"})
	private String url;

	/**
	 * A brief description of the project. This field is optional.
	 */
	@Expose
	@SerializedName(value = "description")
	private String description;

	/**
	 * A set of licenses associated with the project. This field is required.
	 */
	@Expose
	@RequireNotNull
	@SerializedName(value = "licenses")
	private Set<LicenseSpec> licenses = new HashSet<>();

	/**
	 * Developer specifications for the project. This field is optional.
	 */
	@Expose
	@SerializedName(value = "developers")
	private PomDeveloperSpec developers;

	/**
	 * Source Control Management (SCM) information related to the project.
	 * This field is optional.
	 */
	@Expose
	@SerializedName(value = "scm")
	private ScmSpec scm;

	/* -------------------------------------------------------------------
	 * Internal definitions
	 * ------------------------------------------------------------------- */

	/**
	 * Create new instance of {@link PomSpec}
	 */
	public PomSpec() {
	}

	/**
	 * Validates the POM specifications, checking for required fields
	 * and validating nested specifications for developers and SCM.
	 *
	 * @param ctx the context in which to validate the POM specifications
	 * @throws ValidationException if any validation errors occur
	 */
	@Override
	public void validate(@NotNull Context ctx) throws ValidationException {
		ValidatorUtils.validateNotNullProperties(this);

		// Validate developers if present
		if (developers().isPresent()) {
			developers().get().validate(ctx);
		}

		// Validate SCM if present
		if (scm().isPresent()) {
			scm().get().validate(ctx);
		}
	}

	/* -------------------------------------------------------------------
	 * Methods
	 * ------------------------------------------------------------------- */

	/**
	 * Returns the description of the project as an Optional.
	 *
	 * @return an Optional containing the project description, or empty if not set
	 */
	public @NotNull Optional<String> description() {
		return Optional.ofNullable(description);
	}

	/**
	 * Returns the developer specifications for the project as an Optional.
	 *
	 * @return an Optional containing the developer specifications, or empty if not set
	 */
	public @NotNull Optional<PomDeveloperSpec> developers() {
		return Optional.ofNullable(developers);
	}

	/**
	 * Returns the unique identifier for the artifact (project ID).
	 *
	 * @return the artifact ID
	 */
	public @NotNull String id() {
		return id;
	}

	/**
	 * Returns an unmodifiable view of the set of licenses associated with the project.
	 *
	 * @return an unmodifiable set of licenses
	 */
	@Contract(pure = true)
	public @NotNull @UnmodifiableView Set<LicenseSpec> licenses() {
		return Collections.unmodifiableSet(licenses);
	}

	/**
	 * Returns the SCM specifications for the project as an Optional.
	 *
	 * @return an Optional containing the SCM specifications, or empty if not set
	 */
	public @NotNull Optional<ScmSpec> scm() {
		return Optional.ofNullable(scm);
	}

	/**
	 * Returns the project URL as an Optional URI. If the URL is invalid, it will be set to null.
	 *
	 * @return an Optional containing the valid project URL as a URI, or empty if not set or invalid
	 */
	public @NotNull Optional<URI> url() {
		Optional<URI> result = ValidatorUtils.getValidUri(url);
		// Remove value if the result is empty
		if (result.isEmpty() && url != null) {
			url = null;
		}

		return result;
	}

	/**
	 * Returns a string representation of the PomSpec,
	 * including the description, ID, URL, licenses, developers, and SCM.
	 *
	 * @return a string representation of this object
	 */
	@Contract(pure = true)
	@Override
	public @NotNull String toString() {
		return "PomSpec{" +
			"description='" + description + '\'' +
			", id='" + id + '\'' +
			", url='" + url + '\'' +
			", licenses=" + licenses +
			", developers=" + developers +
			", scm=" + scm +
			'}';
	}
}