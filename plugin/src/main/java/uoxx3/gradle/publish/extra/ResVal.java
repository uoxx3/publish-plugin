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

package uoxx3.gradle.publish.extra;

import org.jetbrains.annotations.NotNull;
import uoxx3.gradle.publish.Context;

/**
 * An interface that combines the functionalities of both {@link Resolver} and {@link Validator}.
 * It defines a method to validate and resolve conditions or states within a given context.
 */
public interface ResVal extends Resolver, Validator {

	/**
	 * Validates and resolves conditions or states using the specified context.
	 * This method first calls the {@link Validator#validate(Context)} method,
	 * and then the {@link Resolver#resolve(Context)} method.
	 *
	 * @param ctx the context containing information necessary for validation and resolution, must not be {@code null}.
	 * @throws Exception if the validation or resolution process fails.
	 */
	default void validateAndResolve(@NotNull Context ctx) throws Exception {
		validate(ctx);
		resolve(ctx);
	}

}