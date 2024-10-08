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

package uoxx3.gradle.publish;

import com.google.gson.Gson;
import org.gradle.api.Project;
import org.jetbrains.annotations.NotNull;
import uoxx3.project.environment.extension.ProjectEnvironmentExtension;

import java.nio.file.Path;
import java.util.Map;

/**
 * The {@code Context} class is a record that holds the necessary configuration and resources
 * for project-related operations. It encapsulates several components needed to manage the
 * environment, the project itself, and other related resources such as JSON parsing
 * and project definitions.
 *
 * <p>This class is immutable and provides convenient access to its fields.</p>
 *
 * @param environment the {@link ProjectEnvironmentExtension} that provides the environment settings
 *                    related to the project, must not be {@code null}.
 * @param project     the {@link Project} object representing the current project instance,
 *                    must not be {@code null}.
 * @param gson        the {@link Gson} instance used for JSON serialization and deserialization
 *                    within the project, must not be {@code null}.
 * @param definitions a map of {@code String} keys to {@link Path} values representing the project
 *                    definitions, must not be {@code null}.
 */
public record Context(
	@NotNull ProjectEnvironmentExtension environment,
	@NotNull Project project,
	@NotNull Gson gson,
	@NotNull Map<String, Path> definitions
) {
}
