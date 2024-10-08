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
 * Enum representing the types of repositories that can be configured for a project.
 * This enum provides two possible repository types: {@code Maven} and {@code Ivy}.
 */
public enum RepositoryType {
	/**
	 * Represents a Maven repository type.
	 * Maven repositories are used for managing and distributing project dependencies in a Maven-compatible manner.
	 */
	@Expose
	@SerializedName(value = "maven", alternate = {"Maven", "MAVEN", "mvn"})
	Maven,

	/**
	 * Represents an Ivy repository type.
	 * Ivy repositories are used for dependency management in an Ivy-compatible manner.
	 */
	@Expose
	@SerializedName(value = "ivy", alternate = {"Ivy", "IVY"})
	Ivy
}