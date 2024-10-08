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

import com.google.gson.JsonSyntaxException;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.google.gson.reflect.TypeToken;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.UnmodifiableView;
import uoxx3.gradle.publish.Context;
import uoxx3.gradle.publish.annotations.RequireNotNull;
import uoxx3.gradle.publish.error.ResolveException;
import uoxx3.gradle.publish.error.ValidationException;
import uoxx3.gradle.publish.extra.ResVal;
import uoxx3.gradle.publish.utilities.ProjectUtils;
import uoxx3.gradle.publish.utilities.ValidatorUtils;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Type;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collections;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

/**
 * Represents the specifications for developers defined in a POM file,
 * including their type, location, and additional details about each developer.
 * This class implements the {@link ResVal} interface, providing validation and resolution
 * functionality for developer specifications.
 */
public final class PomDeveloperSpec implements ResVal {

	/* -------------------------------------------------------------------
	 * Properties
	 * ------------------------------------------------------------------- */

	/**
	 * The type of developer specification, which can be either RAW or REFERENCE.
	 * Defaults to RAW.
	 */
	@Expose
	@RequireNotNull
	@SerializedName(value = "type", alternate = {"t"})
	private DeveloperType type = DeveloperType.Raw;

	/**
	 * The location of the developer data file, which is used when the developer type is REFERENCE.
	 * Defaults to "{PROJECT}/project.developers.json".
	 */
	@Expose
	@SerializedName(value = "location", alternate = {"file", "ref", "path"})
	private String location = "{PROJECT}/project.developers.json";

	/**
	 * A set of developer specifications. This will contain the details of each developer.
	 */
	@Expose
	@SerializedName(value = "developers", alternate = {"devs", "data", "values"})
	private Set<DeveloperSpec> developers = new HashSet<>();

	/* -------------------------------------------------------------------
	 * Internal definitions
	 * ------------------------------------------------------------------- */

	/**
	 * Create new instance of {@link PomDeveloperSpec}
	 */
	public PomDeveloperSpec() {
	}

	/**
	 * Resolves the developer data from the specified location if the developer type is REFERENCE.
	 *
	 * @param ctx the context in which to resolve the developer specifications
	 * @throws ResolveException if an error occurs while resolving the developer data
	 */
	@Override
	public void resolve(@NotNull Context ctx) throws ResolveException {
		// Open the developer file and read contents
		if (type() != DeveloperType.Reference || location().isEmpty()) return;
		Path resolved = Path.of(location);

		try (InputStream resourceStream = Files.newInputStream(resolved);
		     InputStreamReader reader = new InputStreamReader(resourceStream)) {
			// Generate content type
			Type listType = new TypeToken<Set<DeveloperSpec>>() {
			}.getType();
			Set<DeveloperSpec> content = ctx.gson().fromJson(reader, listType);

			// Attach content to existing object
			if (content == null) return;
			developers.addAll(content);
		} catch (JsonSyntaxException | IOException e) {
			throw new ResolveException("\"%s\" - %s".formatted(
				resolved, e.getMessage()));
		}
	}

	/**
	 * Validates the developer specifications, checking for required fields
	 * and resolving the developer data if necessary.
	 *
	 * @param ctx the context in which to validate the developer specifications
	 * @throws ValidationException if any validation errors occur
	 */
	@Override
	public void validate(@NotNull Context ctx) throws ValidationException {
		ValidatorUtils.validateNotNullProperties(this);
		// Type dependencies
		switch (type()) {
			case Raw -> ValidatorUtils.validateNotNullNamedProperties(this, "developers");
			case Reference -> {
				ValidatorUtils.validateNotNullNamedProperties(this, "location");
				location = ProjectUtils.resolvePathLocation(location, ctx).toString();
				// Resolve developer elements when the type is Reference
				try {
					resolve(ctx);
				} catch (ResolveException e) {
					throw new ValidationException(e);
				}
			}
		}

		// Validate all developers
		for (DeveloperSpec developer : developers()) {
			developer.validate(ctx);
		}
	}

	/* -------------------------------------------------------------------
	 * Methods
	 * ------------------------------------------------------------------- */

	/**
	 * Returns an unmodifiable view of the set of developer specifications.
	 *
	 * @return an unmodifiable set of developer specifications
	 */
	public @NotNull @UnmodifiableView Set<DeveloperSpec> developers() {
		return Collections.unmodifiableSet(developers);
	}

	/**
	 * Returns the location of the developer data file as an Optional.
	 *
	 * @return an Optional containing the location of the developer data file, or empty if not set
	 */
	public @NotNull Optional<String> location() {
		return Optional.ofNullable(location);
	}

	/**
	 * Returns the type of developer specification.
	 *
	 * @return the type of developer specification
	 */
	public @NotNull DeveloperType type() {
		return type;
	}

	/**
	 * Returns a string representation of the PomDeveloperSpec,
	 * including the developers, type, and location.
	 *
	 * @return a string representation of this object
	 */
	@Contract(pure = true)
	@Override
	public @NotNull String toString() {
		return "PomDeveloperSpec{" +
			"developers=" + developers +
			", type=" + type +
			", location='" + location + '\'' +
			'}';
	}
}