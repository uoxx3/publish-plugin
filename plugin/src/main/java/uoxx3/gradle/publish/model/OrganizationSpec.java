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
 * Represents the specifications for an organization, including its name and URL.
 * This class implements the {@link Validator} interface to provide validation functionality
 * for the organization specifications.
 */
public final class OrganizationSpec implements Validator {

	/* -------------------------------------------------------------------
	 * Properties
	 * ------------------------------------------------------------------- */

	/**
	 * The name of the organization, which is required.
	 */
	@Expose
	@RequireNotNull
	@SerializedName(value = "name")
	private String name;

	/**
	 * The URL of the organization's website, which is required.
	 */
	@Expose
	@RequireNotNull
	@SerializedName(value = "url")
	private String organizationUrl;

	/* -------------------------------------------------------------------
	 * Internal definitions
	 * ------------------------------------------------------------------- */

	/**
	 * Create new instance of {@link OrganizationSpec}
	 */
	public OrganizationSpec() {
	}

	/**
	 * Validates the organization specifications within the provided context,
	 * ensuring that all required fields are populated and valid.
	 *
	 * @param ctx the context in which to validate organization specifications
	 * @throws ValidationException if any validation error occurs
	 */
	@Override
	public void validate(@NotNull Context ctx) throws ValidationException {
		ValidatorUtils.validateNotNullProperties(this);
	}

	/* -------------------------------------------------------------------
	 * Methods
	 * ------------------------------------------------------------------- */

	/**
	 * Returns the name of the organization.
	 *
	 * @return the name of the organization
	 */
	public String name() {
		return name;
	}

	/**
	 * Returns the URL associated with the organization if present.
	 * If the URL is invalid, it will be cleared.
	 *
	 * @return an Optional containing a valid URI of the organization URL, or empty if not set or invalid
	 */
	public @NotNull Optional<URI> organizationUrl() {
		Optional<URI> result = ValidatorUtils.getValidUri(organizationUrl);
		// Remove value if the result is empty
		if (result.isEmpty() && organizationUrl != null) {
			organizationUrl = null;
		}

		return result;
	}

	/**
	 * Returns a string representation of the organization specifications.
	 *
	 * @return a string representation of the organization specifications
	 */
	@Contract(pure = true)
	@Override
	public @NotNull String toString() {
		return "OrganizationSpec{" +
			"name='" + name + '\'' +
			", organizationUrl='" + organizationUrl + '\'' +
			'}';
	}
}