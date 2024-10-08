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
import uoxx3.gradle.publish.utilities.ProjectUtils;
import uoxx3.gradle.publish.utilities.ValidatorUtils;

/**
 * Represents the credentials required for accessing a repository, including
 * a username and password. This class implements the {@link ResVal} interface,
 * providing functionality for resolving and validating repository credentials.
 */
public final class RepositoryCredentialsSpec implements ResVal {

	/* -------------------------------------------------------------------
	 * Properties
	 * ------------------------------------------------------------------- */

	/**
	 * The username for accessing the repository.
	 * This field is required and can also be referred to as "user".
	 */
	@Expose
	@RequireNotNull
	@SerializedName(value = "username", alternate = {"user"})
	private String username;

	/**
	 * The password for accessing the repository.
	 * This field is required and can also be referred to as "pass".
	 */
	@Expose
	@RequireNotNull
	@SerializedName(value = "password", alternate = {"pass"})
	private String password;

	/* -------------------------------------------------------------------
	 * Internal definitions
	 * ------------------------------------------------------------------- */

	/**
	 * Create new instance of {@link RepositoryCredentialsSpec}
	 */
	public RepositoryCredentialsSpec() {
	}

	/**
	 * Resolves any environment variables present in the username and password fields.
	 * This method replaces the username and password with their resolved values
	 * from the environment context.
	 *
	 * @param ctx the context in which to resolve the credentials
	 * @throws ResolveException if any errors occur during resolution
	 */
	@Override
	public void resolve(@NotNull Context ctx) throws ResolveException {
		// Resolve username environment variable
		username = ProjectUtils.resolveEnvironmentExp(username, ctx);
		// Resolve password environment variable
		password = ProjectUtils.resolveEnvironmentExp(password, ctx);
	}

	/**
	 * Validates the repository credentials to ensure that all required properties
	 * are present and valid.
	 *
	 * @param ctx the context in which to validate the credentials
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
	 * Returns the password associated with the repository credentials.
	 *
	 * @return the repository password
	 */
	public @NotNull String password() {
		return password;
	}

	/**
	 * Returns the username associated with the repository credentials.
	 *
	 * @return the repository username
	 */
	public @NotNull String username() {
		return username;
	}

	/**
	 * Returns a string representation of the RepositoryCredentialsSpec,
	 * including the username and password (with the password obfuscated).
	 *
	 * @return a string representation of this object
	 */
	@Contract(pure = true)
	@Override
	public @NotNull String toString() {
		String pws = "*".repeat(password.length());
		return "RepositoryCredentialsSpec{" +
			"password='" + pws + "'" +
			", username='" + username + '\'' +
			'}';
	}
}
