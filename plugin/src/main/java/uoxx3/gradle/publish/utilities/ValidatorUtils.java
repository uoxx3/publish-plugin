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

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import uoxx3.gradle.publish.annotations.RequireNotNull;
import uoxx3.gradle.publish.error.ValidationException;

import java.lang.reflect.Field;
import java.lang.reflect.InaccessibleObjectException;
import java.lang.reflect.Modifier;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;

/**
 * Utility class providing various validation methods.
 *
 * <p>The {@code ValidatorUtils} class contains static methods for validating different types of input,
 * including URIs, property values, and custom validation annotations. This class is not intended
 * to be instantiated, and all methods should be accessed statically.</p>
 *
 * <p>All methods in this class are designed to facilitate input validation in applications,
 * ensuring that data meets certain criteria before further processing.</p>
 */
public final class ValidatorUtils {

	/**
	 * This class cannot be instantiated
	 */
	private ValidatorUtils() {
		throw new IllegalStateException("ValidatorUtils cannot be instantiated");
	}

	/* -------------------------------------------------------------------
	 * Methods
	 * ------------------------------------------------------------------- */

	/**
	 * Validates that specific fields in the provided object, identified by their names, are not {@code null}.
	 *
	 * <p>This method allows for selective validation of fields by their names. It iterates through the provided
	 * field names and checks whether each corresponding field in the object is {@code null}. If any of the named fields
	 * is found to be {@code null}, a {@link ValidationException} is thrown, indicating which field is null.</p>
	 *
	 * <p>Specifically, it performs the following actions:</p>
	 * <ul>
	 *     <li>Retrieves all fields (both public and private) from the object's class.</li>
	 *     <li>For each field name provided, attempts to find the matching field in the class.</li>
	 *     <li>If the field exists, checks if its value is {@code null}. If it is, throws a {@link ValidationException}.</li>
	 * </ul>
	 *
	 * <p>Note that this method will ignore fields that are static, inaccessible, or do not exist in the object class.
	 * No exception is thrown if a field name provided does not match any field in the class.</p>
	 *
	 * @param obj   the object to validate, must not be {@code null}.
	 * @param names the names of the fields to validate, must not be {@code null}.
	 * @throws ValidationException if any of the specified fields is {@code null}.
	 */
	public static void validateNotNullNamedProperties(
		@NotNull Object obj,
		String @NotNull ... names
	) throws ValidationException {
		// Extract object information
		Class<?> cls = obj.getClass();
		String classname = cls.getCanonicalName();
		HashSet<Field> clsFields = new HashSet<>();

		// Fill property set
		clsFields.addAll(List.of(cls.getFields()));
		clsFields.addAll(List.of(cls.getDeclaredFields()));

		// Iterate all property names
		for (String name : names) {
			// Find the property in the collection
			Optional<Field> fieldOpt = clsFields.stream()
				.filter(f -> f.getName().equals(name))
				.findFirst();
			if (fieldOpt.isEmpty()) continue;
			// Get field instance
			Field field = fieldOpt.get();

			// Try to get field information
			try {
				// Change field access
				field.setAccessible(true);

				// Extract field information
				String fieldName = field.getName();
				int modifiers = field.getModifiers();
				Object value = Modifier.isStatic(modifiers) ?
					field.get(null) : field.get(obj);

				// Check field value
				if (value != null) return;
				throw new ValidationException("[%s] Property \"%s\" cannot be <null>".formatted(
					classname, fieldName));
			} catch (IllegalAccessException | InaccessibleObjectException |
			         SecurityException e) {/* Cannot access to field value */}
		}
	}

	/**
	 * Validates that all fields in the provided object marked with {@link RequireNotNull} are not {@code null}.
	 *
	 * <p>This method iterates over all the fields of the given object, including both public and private fields.
	 * If a field is annotated with {@link RequireNotNull}, it checks whether the field value is {@code null}.
	 * If a null value is found, a {@link ValidationException} is thrown with either a custom error message
	 * from the annotation or a default message indicating which field is null.</p>
	 *
	 * <p>Specifically, it performs the following actions:</p>
	 * <ul>
	 *     <li>Retrieves all fields (both public and private) from the object's class.</li>
	 *     <li>Checks if each field is annotated with {@link RequireNotNull}.</li>
	 *     <li>If the field's value is {@code null}, throws a {@link ValidationException} with the specified or default message.</li>
	 * </ul>
	 *
	 * <p>This method skips fields that are static or inaccessible and suppresses exceptions related to field access.</p>
	 *
	 * @param obj the object to validate, must not be {@code null}.
	 * @throws ValidationException if any field marked with {@link RequireNotNull} is {@code null}.
	 */
	public static void validateNotNullProperties(@NotNull Object obj) throws ValidationException {
		// Extract object information
		Class<?> cls = obj.getClass();
		String classname = cls.getCanonicalName();
		HashSet<Field> clsFields = new HashSet<>();

		// Fill property set
		clsFields.addAll(List.of(cls.getFields()));
		clsFields.addAll(List.of(cls.getDeclaredFields()));

		// Iterate all fields
		for (Field field : clsFields) {
			// Check if the field contains the annotation
			if (!field.isAnnotationPresent(RequireNotNull.class)) continue;

			// Store the annotation instance
			RequireNotNull annotation = field.getAnnotation(RequireNotNull.class);

			// Try to get field information
			try {
				// Change field access
				field.setAccessible(true);

				// Extract field information
				String fieldName = field.getName();
				int modifiers = field.getModifiers();
				Object value = Modifier.isStatic(modifiers) ?
					field.get(null) : field.get(obj);

				// Check field value
				if (value != null) return;

				// Launch validation error
				if (!annotation.msg().isBlank()) {
					throw new ValidationException(annotation.msg());
				}
				throw new ValidationException("[%s] Property \"%s\" cannot be <null>".formatted(
					classname, fieldName));
			} catch (IllegalAccessException | InaccessibleObjectException |
			         SecurityException e) {
				// Cannot access to field value, skip field
			}
		}
	}

	/**
	 * Validates the provided URI string and returns an {@link Optional} containing the valid URI if successful.
	 *
	 * <p>This method checks if the input string is {@code null}. If it is not null, it attempts to create a
	 * {@link URI} object from the string. If the URI is valid, it returns an {@link Optional} containing the URI.
	 * If the URI is invalid or the input string is {@code null}, it returns an empty {@link Optional}.</p>
	 *
	 * <p>Any exceptions thrown during URI creation due to invalid syntax are caught, and an error message is logged
	 * to the standard error output.</p>
	 *
	 * @param uri the string representation of the URI to validate, which can be {@code null}.
	 * @return an {@link Optional} containing the valid URI if the input is valid, or an empty {@link Optional}
	 * if the input is {@code null} or invalid.
	 */
	public static @NotNull Optional<URI> getValidUri(@Nullable String uri) {
		validateUri:
		{
			if (uri == null) break validateUri;
			try {
				return Optional.of(new URI(uri));
			} catch (URISyntaxException e) {
				System.err.printf("> [%s] Invalid url: %s%n",
					ValidatorUtils.class.getCanonicalName(), uri);
			}
		}

		return Optional.empty();
	}

}
