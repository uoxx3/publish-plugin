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
import uoxx3.gradle.publish.error.ValidationException;

/**
 * Represents a contract for objects that can validate certain conditions or states
 * within a given context.
 */
public interface Validator {

	/**
	 * Validates conditions or states using the specified context.
	 *
	 * @param ctx the context containing information necessary for the validation, must not be {@code null}.
	 * @throws ValidationException if the validation process fails.
	 */
	void validate(@NotNull Context ctx) throws ValidationException;

}