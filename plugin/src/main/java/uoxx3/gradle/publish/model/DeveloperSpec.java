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
 * Represents the specifications for a developer, including their identification,
 * contact information, organization affiliation, and roles within the project.
 * This class implements the {@link Validator} interface, providing a method for
 * validating the developer specifications.
 */
public final class DeveloperSpec implements Validator {

	/* -------------------------------------------------------------------
	 * Properties
	 * ------------------------------------------------------------------- */

	/**
	 * The unique identifier for the developer, which is required.
	 * This can also be referred to as the developer's nickname.
	 */
	@Expose
	@RequireNotNull
	@SerializedName(value = "id", alternate = {"nick"})
	private String id;

	/**
	 * The name of the developer. This field is optional.
	 */
	@Expose
	@SerializedName(value = "name")
	private String name;

	/**
	 * The email address of the developer, which is required for contact.
	 */
	@Expose
	@RequireNotNull
	@SerializedName(value = "email")
	private String email;

	/**
	 * The timezone of the developer. This field is optional.
	 */
	@Expose
	@SerializedName(value = "timezone")
	private String timezone;

	/**
	 * The URL associated with the developer, which could be a GitHub or personal website.
	 * This field is optional.
	 */
	@Expose
	@SerializedName(value = "url", alternate = {"github", "github-url", "website", "website-url"})
	private String url;

	/**
	 * The organization that the developer is associated with. This field is optional.
	 */
	@Expose
	@SerializedName(value = "organization", alternate = {"company"})
	private OrganizationSpec organization;

	/**
	 * The roles assigned to the developer in the project. This field is optional
	 * and defaults to an empty set.
	 */
	@Expose
	@SerializedName(value = "roles")
	private Set<String> roles = new HashSet<>();

	/* -------------------------------------------------------------------
	 * Internal definitions
	 * ------------------------------------------------------------------- */

	/**
	 * Create new instance of {@link DeveloperSpec}
	 */
	public DeveloperSpec() {
	}

	/**
	 * Validates the developer specifications within the provided context,
	 * ensuring that all required fields are populated and valid.
	 *
	 * @param ctx the context in which to validate developer specifications
	 * @throws ValidationException if any validation error occurs
	 */
	@Override
	public void validate(@NotNull Context ctx) throws ValidationException {
		ValidatorUtils.validateNotNullProperties(this);

		// Validate the organization if present
		if (organization().isEmpty()) return;
		organization().get().validate(ctx);
	}

	/* -------------------------------------------------------------------
	 * Methods
	 * ------------------------------------------------------------------- */

	/**
	 * Returns the email of the developer.
	 *
	 * @return the developer's email
	 */
	public @NotNull String email() {
		return email;
	}

	/**
	 * Returns the unique identifier of the developer.
	 *
	 * @return the developer's id
	 */
	public @NotNull String id() {
		return id;
	}

	/**
	 * Returns the name of the developer if present.
	 *
	 * @return an Optional containing the developer's name, or empty if not set
	 */
	public Optional<String> name() {
		return Optional.ofNullable(name);
	}

	/**
	 * Returns the organization of the developer if present.
	 *
	 * @return an Optional containing the organization specification, or empty if not set
	 */
	public Optional<OrganizationSpec> organization() {
		return Optional.ofNullable(organization);
	}

	/**
	 * Returns an unmodifiable view of the developer's roles.
	 *
	 * @return an unmodifiable set of roles
	 */
	public @NotNull @UnmodifiableView Set<String> roles() {
		return Collections.unmodifiableSet(roles);
	}

	/**
	 * Returns the timezone of the developer if present.
	 *
	 * @return an Optional containing the timezone, or empty if not set
	 */
	public @NotNull Optional<String> timezone() {
		return Optional.ofNullable(timezone);
	}

	/**
	 * Returns the URL associated with the developer if present.
	 * If the URL is invalid, it will be cleared.
	 *
	 * @return an Optional containing a valid URI of the developer's URL, or empty if not set or invalid
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
	 * Returns a string representation of the developer specifications.
	 *
	 * @return a string representation of the developer specifications
	 */
	@Contract(pure = true)
	@Override
	public @NotNull String toString() {
		return "DeveloperSpec{" +
			"email='" + email + '\'' +
			", id='" + id + '\'' +
			", name='" + name + '\'' +
			", timezone='" + timezone + '\'' +
			", url='" + url + '\'' +
			", organization=" + organization +
			", roles=" + roles +
			'}';
	}
}