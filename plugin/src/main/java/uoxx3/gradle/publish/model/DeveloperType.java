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

/**
 * Enum representing the types of developers associated with a project.
 * This enum provides two possible developer types: {@code Raw} and {@code Reference}.
 */
public enum DeveloperType {
	/**
	 * Represents a raw developer type.
	 * This type signifies that the developer information is defined directly within the configuration.
	 */
	@Expose
	@SerializedName(value = "raw", alternate = {"Raw", "RAW"})
	Raw,

	/**
	 * Represents a reference developer type.
	 * This type signifies that the developer information is linked to an external file.
	 * The developer details will be obtained from the specified file instead of being defined directly.
	 */
	@Expose
	@SerializedName(value = "reference", alternate = {"Reference", "REFERENCE", "ref"})
	Reference
}