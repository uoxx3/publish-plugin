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
import uoxx3.gradle.publish.error.ValidationException;
import uoxx3.gradle.publish.extra.Validator;
import uoxx3.gradle.publish.utilities.ValidatorUtils;

import java.net.URI;
import java.util.Optional;

/**
 * Represents the specification for a Source Control Management (SCM) configuration.
 * This class implements the {@link Validator} interface, providing functionality for
 * validating SCM properties such as connections and branches.
 */
public final class ScmSpec implements Validator {

	/* -------------------------------------------------------------------
	 * Properties
	 * ------------------------------------------------------------------- */

	/**
	 * The branch of the source control repository.
	 * Defaults to "main" if not specified.
	 */
	@Expose
	@RequireNotNull
	@SerializedName(value = "branch")
	private String branch = "main";

	/**
	 * The connection URL for accessing the repository.
	 * This field is required and can also be referred to as "url-connection".
	 */
	@Expose
	@RequireNotNull
	@SerializedName(value = "connection", alternate = {"url-connection"})
	private String connection;

	/**
	 * The developer connection URL for accessing the repository,
	 * typically used for pushing changes. This field is required
	 * and can also be referred to as "url-dev-connection".
	 */
	@Expose
	@RequireNotNull
	@SerializedName(value = "developer-connection", alternate = {"url-dev-connection"})
	private String developerConnection;

	/**
	 * The URL of the SCM repository. This field is required and can
	 * also be referred to as "url-repository".
	 */
	@Expose
	@RequireNotNull
	@SerializedName(value = "url", alternate = {"url-repository"})
	private String url;

	/* -------------------------------------------------------------------
	 * Internal definitions
	 * ------------------------------------------------------------------- */

	/**
	 * Create new instance of {@link ScmSpec}
	 */
	public ScmSpec() {
	}

	/**
	 * Validates the SCM specification, ensuring that all required properties
	 * are present and valid.
	 *
	 * @param ctx the context in which to validate the specification
	 * @throws ValidationException if any validation errors occur
	 */
	@Override
	public void validate(@NotNull Context ctx) throws ValidationException {
		ValidatorUtils.validateNotNullProperties(this);
	}

	/* -------------------------------------------------------------------
	 * Methods
	 * ------------------------------------------------------------------- */

	/**
	 * Returns the branch of the source control repository.
	 *
	 * @return the SCM branch name
	 */
	public @NotNull String branch() {
		return branch;
	}

	/**
	 * Returns the connection URL for the SCM repository.
	 *
	 * @return the connection URL
	 */
	public @NotNull String connection() {
		return connection;
	}

	/**
	 * Returns the developer connection URL for the SCM repository.
	 *
	 * @return the developer connection URL
	 */
	public @NotNull String developerConnection() {
		return developerConnection;
	}

	/**
	 * Returns the URL of the SCM repository as an {@link Optional}.
	 * If the URL is not valid, it sets the URL property to null.
	 *
	 * @return an {@link Optional} containing the valid repository URL, or empty if not valid
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
	 * Returns a string representation of the ScmSpec, including the
	 * branch, connection URLs, and repository URL.
	 *
	 * @return a string representation of this object
	 */
	@Contract(pure = true)
	@Override
	public @NotNull String toString() {
		return "ScmSpec{" +
			"branch='" + branch + '\'' +
			", connection='" + connection + '\'' +
			", developerConnection='" + developerConnection + '\'' +
			", url='" + url + '\'' +
			'}';
	}
}