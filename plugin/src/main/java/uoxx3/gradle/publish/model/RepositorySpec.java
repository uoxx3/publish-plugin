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

import java.net.URI;
import java.util.Optional;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

/**
 * Represents the specification for a repository, including its type, name,
 * credentials, URL, and optional regex pattern for matching. This class implements
 * the {@link ResVal} interface, providing functionality for resolving and validating
 * repository specifications.
 */
public final class RepositorySpec implements ResVal {

	/* -------------------------------------------------------------------
	 * Properties
	 * ------------------------------------------------------------------- */

	/**
	 * The type of the repository (e.g., Maven, NPM, etc.).
	 * This field is required and can also be referred to as "repository-type".
	 */
	@Expose
	@RequireNotNull
	@SerializedName(value = "type", alternate = {"repository-type", "rt", "t"})
	private RepositoryType type = RepositoryType.Maven;

	/**
	 * The name of the repository.
	 * This field is required and can also be referred to as "repository-name".
	 */
	@Expose
	@RequireNotNull
	@SerializedName(value = "name", alternate = {"repository-name", "rn", "n"})
	private String name;

	/**
	 * The credentials required to access the repository, if applicable.
	 */
	@Expose
	@SerializedName(value = "credentials")
	private RepositoryCredentialsSpec credentials;

	/**
	 * An optional regex pattern for matching repository elements.
	 */
	@Expose
	@SerializedName(value = "regex")
	private String regex;

	/**
	 * The URL of the repository.
	 * This field is required.
	 */
	@Expose
	@RequireNotNull
	@SerializedName(value = "url")
	private String url;

	/* -------------------------------------------------------------------
	 * Internal definitions
	 * ------------------------------------------------------------------- */

	/**
	 * Create new instance of {@link RepositorySpec}
	 */
	public RepositorySpec() {
	}

	/**
	 * Resolves the repository credentials, if they are present.
	 * This method replaces any environment variables in the credentials with
	 * their resolved values from the context.
	 *
	 * @param ctx the context in which to resolve the credentials
	 * @throws ResolveException if any errors occur during resolution
	 */
	@Override
	public void resolve(@NotNull Context ctx) throws ResolveException {
		// Resolve the credentials
		if (credentials().isPresent()) credentials().get().resolve(ctx);
	}

	/**
	 * Validates the repository specification, ensuring that all required properties
	 * are present and valid. Also validates the credentials, if they are provided.
	 *
	 * @param ctx the context in which to validate the specification
	 * @throws ValidationException if any validation errors occur
	 */
	@Override
	public void validate(@NotNull Context ctx) throws ValidationException {
		ValidatorUtils.validateNotNullProperties(this);

		// Validate the credentials
		if (credentials().isPresent()) credentials().get().validate(ctx);
	}

	/* -------------------------------------------------------------------
	 * Methods
	 * ------------------------------------------------------------------- */

	/**
	 * Returns the credentials associated with the repository, if present.
	 *
	 * @return an {@link Optional} containing the repository credentials, or empty if not present
	 */
	public @NotNull Optional<RepositoryCredentialsSpec> credentials() {
		return Optional.ofNullable(credentials);
	}

	/**
	 * Returns the name of the repository.
	 *
	 * @return the repository name
	 */
	public @NotNull String name() {
		return name;
	}

	/**
	 * Returns the regex pattern for matching repository elements as an
	 * {@link Optional}. If the regex is not valid, it sets the regex property to null.
	 *
	 * @return an {@link Optional} containing the compiled regex pattern, or empty if not present or invalid
	 */
	public @NotNull Optional<Pattern> regex() {
		validatePattern:
		{
			if (regex == null) break validatePattern;
			try {
				return Optional.of(Pattern.compile(regex, Pattern.CASE_INSENSITIVE));
			} catch (PatternSyntaxException e) {
				System.err.printf("> [%s] Error compiling pattern \"%s\": %s%n",
					getClass().getCanonicalName(), regex, e.getMessage());
				// Set the property to <null>
				regex = null;
			}
		}
		return Optional.empty();
	}

	/**
	 * Returns the type of the repository.
	 *
	 * @return the repository type
	 */
	public @NotNull RepositoryType type() {
		return type;
	}

	/**
	 * Returns the URL of the repository as an {@link Optional}. If the URL is not valid,
	 * it sets the URL property to null.
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
	 * Returns a string representation of the RepositorySpec, including the
	 * repository's credentials, name, type, regex, and URL.
	 *
	 * @return a string representation of this object
	 */
	@Contract(pure = true)
	@Override
	public @NotNull String toString() {
		return "RepositorySpec{" +
			"credentials=" + credentials +
			", name='" + name + '\'' +
			", type=" + type +
			", regex='" + regex + '\'' +
			", url='" + url + '\'' +
			'}';
	}
}