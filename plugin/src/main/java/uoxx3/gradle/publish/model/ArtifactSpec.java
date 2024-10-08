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
 * Represents the specifications for an artifact, including its type, name, and whether it is required.
 * This class implements the {@link ResVal} interface, providing methods for validation and resolution of its properties.
 */
public final class ArtifactSpec implements ResVal {

	/* -------------------------------------------------------------------
	 * Properties
	 * ------------------------------------------------------------------- */

	/**
	 * The type of the artifact. This field is required and defaults to {@link ArtifactType#Task}.
	 */
	@Expose
	@RequireNotNull
	@SerializedName(value = "type", alternate = {"artifact-type", "at", "t"})
	private ArtifactType type = ArtifactType.Task;

	/**
	 * The name of the artifact, which can be a file path or a task name.
	 * This field is required.
	 */
	@Expose
	@RequireNotNull
	@SerializedName(value = "name", alternate = {"file", "file-name", "n", "task", "task-name"})
	private String name;

	/**
	 * Indicates whether the artifact is required. This field is optional and defaults to false.
	 */
	@Expose
	@RequireNotNull
	@SerializedName(value = "required", alternate = {"r"})
	private boolean required = false;

	/* -------------------------------------------------------------------
	 * Internal definitions
	 * ------------------------------------------------------------------- */

	/**
	 * Create new instance of {@link ArtifactSpec}
	 */
	public ArtifactSpec() {
	}

	/**
	 * Resolves the artifact specifications within the provided context,
	 * converting the name of the artifact based on its type.
	 *
	 * @param ctx the context in which to resolve artifact specifications
	 * @throws ResolveException if any resolution error occurs
	 */
	@Override
	public void resolve(@NotNull Context ctx) throws ResolveException {
		// Resolve some project instance dependencies
		name = switch (type) {
			case File -> ProjectUtils.resolvePathLocation(name, ctx).toString();
			case Task -> name;
		};
	}

	/**
	 * Validates the artifact specifications within the provided context,
	 * ensuring that all required fields are populated and valid.
	 *
	 * @param ctx the context in which to validate artifact specifications
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
	 * Returns the name of the artifact.
	 *
	 * @return the artifact name
	 */
	public String name() {
		return name;
	}

	/**
	 * Returns whether the artifact is required.
	 *
	 * @return true if the artifact is required, false otherwise
	 */
	public boolean required() {
		return required;
	}

	/**
	 * Returns the type of the artifact.
	 *
	 * @return the artifact type
	 */
	public ArtifactType type() {
		return type;
	}

	/**
	 * Returns a string representation of the artifact specifications.
	 *
	 * @return a string representation of the artifact specifications
	 */
	@Contract(pure = true)
	@Override
	public @NotNull String toString() {
		return "ArtifactSpec{" +
			"name='" + name + '\'' +
			", type=" + type +
			", required=" + required +
			'}';
	}
}