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

package uoxx3.gradle.publish.utilities;

import org.gradle.api.Project;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.UnmodifiableView;
import uoxx3.gradle.publish.Context;
import uoxx3.gradle.publish.error.ResolveException;
import uoxx3.gradle.publish.error.ValidationException;
import uoxx3.gradle.publish.extra.Resolver;
import uoxx3.gradle.publish.extra.Validator;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.function.Function;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Utility class for project-related operations.
 *
 * <p>The {@code ProjectUtils} class provides static methods for validating and resolving items
 * in collections of {@link Validator} and {@link Resolver} instances. This class is not intended
 * to be instantiated, and its constructor throws an exception if an attempt is made to create
 * an instance.</p>
 *
 * <p>This class includes constants and methods for handling project expressions and performing
 * operations relevant to project management in the context of validation and resolution.</p>
 */
public final class ProjectUtils {

	/**
	 * This class cannot be instantiated
	 */
	private ProjectUtils() {
		throw new IllegalStateException("ProjectUtils cannot be instantiated");
	}

	/* -------------------------------------------------------------------
	 * Constants
	 * ------------------------------------------------------------------- */

	/**
	 * A pattern used for matching project expressions.
	 *
	 * <p>This regular expression pattern is designed to identify project expressions that
	 * follow the format: "{{@literal <identifier>}}". The identifier can consist of letters,
	 * underscores, and hyphens. The pattern is case-insensitive.</p>
	 */
	private static final Pattern PROJECT_EXPRESSION_PATTERN =
		Pattern.compile("(\\{)([A-Za-z_-]+)(})", Pattern.CASE_INSENSITIVE);

	/**
	 * The index of the match group used for extracting the project identifier
	 * from a project expression.
	 *
	 * <p>This constant specifies the match group index (2) that corresponds to
	 * the identifier captured by the {@link #PROJECT_EXPRESSION_PATTERN} regex.
	 * This is used when extracting the actual identifier from a matched project
	 * expression.</p>
	 *
	 * @see #PROJECT_EXPRESSION_PATTERN
	 */
	private static final int PROJECT_EXPRESSION_MATCH_GROUP = 2;

	/* -------------------------------------------------------------------
	 * General methods
	 * ------------------------------------------------------------------- */

	/**
	 * Validates and removes invalid items from the provided collection of {@link Validator} instances.
	 *
	 * <p>This method iterates over the collection, calling the {@code validate} method on each
	 * {@link Validator}. If a validation error occurs, the invalid item is removed from the collection
	 * and an error message is printed to the standard error stream.</p>
	 *
	 * @param collection the collection of validators to validate and filter, must not be {@code null}.
	 * @param ctx        the context used for validation, must not be {@code null}.
	 */
	public static void validateAndRemoveItems(
		@NotNull Collection<? extends Validator> collection,
		@NotNull Context ctx
	) {
		Iterator<? extends Validator> iterator = collection.iterator();

		while (iterator.hasNext()) {
			// Store current item
			Validator current = iterator.next();

			// Verify if the current instance is valid
			try {
				current.validate(ctx);
			} catch (ValidationException e) {
				System.err.printf("> [%s] Invalid object: %s%n",
					current.getClass().getCanonicalName(), e);
				// Remove the instance from the collection
				iterator.remove();
			}
		}
	}

	/**
	 * Resolves and removes invalid items from the provided collection of {@link Resolver} instances.
	 *
	 * <p>This method iterates over the collection, calling the {@code resolve} method on each
	 * {@link Resolver}. If a resolution error occurs, the invalid item is removed from the collection
	 * and an error message is printed to the standard error stream.</p>
	 *
	 * @param collection the collection of resolvers to resolve and filter, must not be {@code null}.
	 * @param ctx        the context used for resolution, must not be {@code null}.
	 */
	public static void resolveAndRemoveItems(
		@NotNull Collection<? extends Resolver> collection,
		@NotNull Context ctx
	) {
		Iterator<? extends Resolver> iterator = collection.iterator();

		while (iterator.hasNext()) {
			// Store current item
			Resolver current = iterator.next();

			// Verify if the current instance is valid
			try {
				current.resolve(ctx);
			} catch (ResolveException e) {
				System.err.printf("> [%s] Invalid object resolution: %s%n",
					current.getClass().getCanonicalName(), e);
				// Remove the instance from the collection
				iterator.remove();
			}
		}
	}

	/* -------------------------------------------------------------------
	 * Project definitions
	 * ------------------------------------------------------------------- */

	/**
	 * A pattern used for matching project definition files.
	 *
	 * <p>This regular expression pattern identifies files that conform to the naming
	 * convention for project definitions, specifically those that start with "project"
	 * and may optionally have a suffix followed by ".json". The pattern is case-insensitive.</p>
	 *
	 * @see #getProjectDefinitions(Project)
	 */
	private static final Pattern PROJECT_DEFINITION_PATTERN =
		Pattern.compile("(project)(\\.[A-Za-z_-]+)?\\.json", Pattern.CASE_INSENSITIVE);

	/**
	 * Retrieves a map of project definition file paths for a given project.
	 *
	 * <p>This method scans the project's root directory for files that match the naming
	 * convention specified by the {@link #PROJECT_DEFINITION_PATTERN}. It returns a
	 * read-only {@link Map} where the keys are the valid project definition names
	 * (extracted from the filenames), and the values are the corresponding {@link Path}
	 * objects representing the locations of these files.</p>
	 *
	 * <p>Only files directly within the project's root directory are considered.
	 * Files in subdirectories are ignored. If no valid project definition files are found,
	 * an empty map is returned.</p>
	 *
	 * @param project the project for which to retrieve the definition files, must not be {@code null}.
	 * @return an unmodifiable view of a map containing project definition names as keys
	 * and their corresponding file paths as values.
	 * @throws NullPointerException if the provided project is {@code null}.
	 */
	public static @NotNull @UnmodifiableView Map<String, Path> getProjectDefinitions(@NotNull Project project) {
		Path projectRoot = project.getProjectDir().toPath();
		Map<String, Path> result = new HashMap<>();

		// Iterate all first level files
		try (var fsstream = Files.walk(projectRoot, 1)) {
			// Filter all elements
			Path[] foundPaths = fsstream.filter(Files::isRegularFile)
				.filter(p -> {
					String filename = p.getFileName().toString();
					Matcher matcher = PROJECT_DEFINITION_PATTERN.matcher(filename);

					return matcher.find();
				}).toArray(Path[]::new);

			// Generate map entries
			for (Path item : foundPaths) {
				Matcher matcher = PROJECT_DEFINITION_PATTERN.matcher(item.getFileName().toString());
				// Only to prevent bad files
				if (!matcher.find()) continue;

				// Check match groups
				String groupName = matcher.group(2);
				if (groupName == null) {
					result.put(matcher.group(1), item);
				} else {
					// Clean group name
					String validGroup = groupName.substring(1);
					result.put(validGroup, item);
				}
			}
		} catch (IOException e) {/* Do nothing */}

		return Collections.unmodifiableMap(result);
	}

	/* -------------------------------------------------------------------
	 * Project path resolver methods
	 * ------------------------------------------------------------------- */

	/**
	 * A map that associates project path keys with their respective resolver functions.
	 *
	 * <p>This map contains entries for resolving project directory paths, specifically:
	 * <ul>
	 *     <li>{@code "PROJECT"}: Resolves to the directory of the current project.</li>
	 *     <li>{@code "PROJECT_ROOT"}: Resolves to the root directory of the parent project.</li>
	 * </ul>
	 * The keys are used to retrieve the appropriate resolver function that returns a
	 * {@link Path} for the given {@link Project}.</p>
	 */
	private static final Map<String, Function<Project, Path>> PROJECT_PATH_RESOLVER = Map.ofEntries(
		Map.entry("PROJECT", ProjectUtils::projectDirResolver),
		Map.entry("PROJECT_ROOT", ProjectUtils::projectRootDirResolver)
	);

	/**
	 * Resolves and returns the absolute path of the current project's directory.
	 *
	 * @param project the project for which to resolve the directory path, must not be {@code null}.
	 * @return the absolute path of the project's directory.
	 * @throws NullPointerException if the provided project is {@code null}.
	 */
	private static @NotNull Path projectDirResolver(@NotNull Project project) {
		// Only extract the project directory path
		return project.getProjectDir().toPath().toAbsolutePath();
	}

	/**
	 * Resolves and returns the absolute path of the root project's directory.
	 *
	 * <p>This method traverses the project hierarchy to find the main project (the root)
	 * and returns the absolute path of its directory. This is useful for scenarios
	 * where the current project is a subproject and its root directory is needed.</p>
	 *
	 * @param project the project for which to resolve the root directory path, must not be {@code null}.
	 * @return the absolute path of the root project's directory.
	 * @throws NullPointerException if the provided project is {@code null}.
	 */
	private static @NotNull Path projectRootDirResolver(@NotNull Project project) {
		// Store the current project
		Project current = project;

		// Iterate projects until reaching the main project
		while (current.getParent() != null) {
			current = current.getParent();
		}

		// Solve the path of the main project
		return projectDirResolver(current);
	}

	/**
	 * Resolves a given location string to an absolute path, taking into account
	 * special project expressions.
	 *
	 * <p>This method checks if the provided location matches the pattern defined
	 * by {@link #PROJECT_EXPRESSION_PATTERN}. If a match is found, it uses
	 * registered path resolvers to replace the matched expression with the
	 * corresponding resolved path. If no match is found, the method returns
	 * the absolute path of the location as is.</p>
	 *
	 * <p>Special expressions recognized by this method include identifiers defined
	 * in the {@link #PROJECT_PATH_RESOLVER} map.</p>
	 *
	 * @param location the location string to resolve, must not be {@code null}.
	 * @param ctx      the context containing the project information needed for resolution,
	 *                 must not be {@code null}.
	 * @return the resolved absolute path based on the given location string.
	 * @throws NullPointerException if either the location or context is {@code null}.
	 */
	public static @NotNull Path resolvePathLocation(@NotNull String location, @NotNull Context ctx) {
		// Check if the location match
		Matcher matcher = PROJECT_EXPRESSION_PATTERN.matcher(location);
		if (!matcher.find()) return Path.of(location).toAbsolutePath();

		// Resolve the special cases locations
		String result = matcher.replaceAll(mr -> {
			String expression = mr.group(PROJECT_EXPRESSION_MATCH_GROUP).trim();

			// Iterate all path resolvers
			for (var resolver : PROJECT_PATH_RESOLVER.entrySet()) {
				// Check if the resolver exists
				if (!resolver.getKey().equalsIgnoreCase(expression)) continue;

				// Resolve expression path
				Path path = resolver.getValue().apply(ctx.project());
				return path.toString().replaceAll("\\\\", "/");
			}

			return mr.group(0);
		});

		return Path.of(result);
	}

	/* -------------------------------------------------------------------
	 * Environment variables resolver methods
	 * ------------------------------------------------------------------- */

	/**
	 * Resolves environment expressions within a given content string by replacing
	 * them with their corresponding values from the project environment.
	 *
	 * <p>This method checks if the content contains any expressions that match the
	 * pattern defined by {@link #PROJECT_EXPRESSION_PATTERN}. If matches are found,
	 * it replaces each expression with its value retrieved from the provided context's
	 * environment. If no matches are found, the original content is returned unchanged.</p>
	 *
	 * @param content the content string containing potential environment expressions,
	 *                must not be {@code null}.
	 * @param ctx     the context containing the environment variables used for resolution,
	 *                must not be {@code null}.
	 * @return the content string with environment expressions resolved to their values.
	 * @throws NullPointerException if either the content or context is {@code null}.
	 */
	public static @NotNull String resolveEnvironmentExp(@NotNull String content, @NotNull Context ctx) {
		// Check if the environment match
		Matcher matcher = PROJECT_EXPRESSION_PATTERN.matcher(content);
		if (!matcher.find()) return content;

		// Replace expression content
		return matcher.replaceAll(mr -> {
			String expression = mr.group(PROJECT_EXPRESSION_MATCH_GROUP).trim();

			// Check if the expression exists in the environment
			// variables
			if (ctx.environment().containsKey(expression)) {
				return ctx.environment().get(expression, "");
			}

			// Get the original expression
			return matcher.group(0);
		});
	}

}