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
 * Represents the specifications for a software license, including the license's name, URL,
 * distribution details, and any comments. This class implements the {@link Validator} interface
 * to provide validation functionality for the license specifications.
 */
public final class LicenseSpec implements Validator {

	/* -------------------------------------------------------------------
	 * Properties
	 * ------------------------------------------------------------------- */

	/**
	 * Any comments regarding the license. This field is optional.
	 */
	@Expose
	@SerializedName(value = "comments")
	private String comments;

	/**
	 * The distribution method of the license. This field is optional.
	 */
	@Expose
	@SerializedName(value = "distribution")
	private String distribution;

	/**
	 * The name of the license, which is required.
	 * This can also be referred to as the license name.
	 */
	@Expose
	@RequireNotNull
	@SerializedName(value = "name", alternate = {"license-name"})
	private String name;

	/**
	 * The URL associated with the license, which is required.
	 * This URL typically points to the license text or information.
	 */
	@Expose
	@RequireNotNull
	@SerializedName(value = "url", alternate = {"license-url"})
	private String url;

	/* -------------------------------------------------------------------
	 * Internal definitions
	 * ------------------------------------------------------------------- */

	/**
	 * Create new instance of {@link LicenseSpec}
	 */
	public LicenseSpec() {
	}

	/**
	 * Validates the license specifications within the provided context,
	 * ensuring that all required fields are populated and valid.
	 *
	 * @param ctx the context in which to validate license specifications
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
	 * Returns the comments regarding the license if present.
	 *
	 * @return an Optional containing the comments, or empty if not set
	 */
	public @NotNull Optional<String> comments() {
		return Optional.ofNullable(comments);
	}

	/**
	 * Returns the distribution method of the license if present.
	 *
	 * @return an Optional containing the distribution, or empty if not set
	 */
	public @NotNull Optional<String> distribution() {
		return Optional.ofNullable(distribution);
	}

	/**
	 * Returns the name of the license.
	 *
	 * @return the name of the license
	 */
	public @NotNull String name() {
		return name;
	}

	/**
	 * Returns the URL associated with the license if present.
	 * If the URL is invalid, it will be cleared.
	 *
	 * @return an Optional containing a valid URI of the license URL, or empty if not set or invalid
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
	 * Returns a string representation of the license specifications.
	 *
	 * @return a string representation of the license specifications
	 */
	@Contract(pure = true)
	@Override
	public @NotNull String toString() {
		return "LicenseSpec{" +
			"comments='" + comments + '\'' +
			", distribution='" + distribution + '\'' +
			", name='" + name + '\'' +
			", url='" + url + '\'' +
			'}';
	}
}