## Publish Plugin

Gradle plugin used to automatically configure Maven or Ivy releases, without dying in the attempt.

All configuration is defined in a "project.json" file and the plugin will make the changes automatically.

### How to use

This plugin only works on gradle projects and should be used as follows.

`build.gradle`
```groovy
plugins {
    id 'io.github.uoxx3.publishing' version '1.0'
}
```

`build.gradle.kts`
```kotlin
plugins {
    id("io.github.uoxx3.publishing") version "1.0"
}
```

and that's it, the plugin will take care of the tedious configurations for you.

If you want to access any configuration, you can access it through the extension defined within the project.

`build.gradle`
```groovy
// Access to configuration
println projectSpec.version()
```

`build.gradle.kts`
```kotlin
// Access to configuration
println(projectSpec.version())
```

## Types

### Path

Data type used to determine the path of a project resource.

Unlike a text string, this type accepts expressions enclosed in braces that will be
replaced by the path of the given resource. The resources that can be obtained are the following:

- `PROJECT`
    - Replaces the text with the path of the current project.
- `PROJECT_ROOT`
    - Replaces the text with the path of the main project. This project is usually the root where the "settings.gradle" file is
      located.

#### Example

```shell
"{PROJECT}/build.gradle.kts"      # ->  "<project-directory>/build.gradle.kts"
"{PROJECT_ROOT}/build.gradle.kts" # ->  "<project-directory-root>/build.gradle.kts"
```

### EnvironmentVariable

Like path, this data type replaces expressions in braces with a defined environment variable.

Environment variables can be those already defined in the system or any that are in a `*.env` or `*.properties` file.

#### Example

```shell
"{JAVA_HOME}/bin/java.exe"  # ->  "<java-directory>/bin/java.exe"
"{USER}"                    # ->  "<unix-user-name>"
```

## Enums

### ArtifactType

- `File`
    - __alternative values__:
        - `FILE`
        - `f`
        - `file`
- `Task`
    - __alternative values__:
        - `TASK`
        - `t`
        - `task`

### DeveloperType

- `Raw`
    - __alternative values__:
        - `RAW`
        - `raw`
- `Reference`
    - __alternative values__:
        - `REFERENCE`
        - `ref`

### RepositoryType

- `Maven`
    - __alternative values__:
        - `maven`
        - `MAVEN`
        - `mvn`
- `Ivy`
    - __alternative values__:
        - `ivy`
        - `IVY`

## Structures

### ProjectSpec

- `group`:
    - __alternative names__:
        - `group-name`
        - `g`
    - __optional__: `false`
    - __type__: `String`
- `name`:
    - __alternative names__:
        - `project-name`
        - `n`
    - __optional__: `false`
    - __type__: `String`
- `version`:
    - __alternative names__:
        - `version-name`
        - `v`
    - __optional__: `false`
    - __type__: `String`
- `publications`:
    - __alternative names__:
        - `p`
    - __default__: `[]`
    - __optional__: `true`
    - __type__: [`Array<PublicationSpec>`](#publicationspec)
- `repositories`:
    - __alternative names__:
        - `repos`
        - `r`
    - __default__: `[]`
    - __optional__: `true`
    - __type__: [`Array<RepositorySpec>`](#repositoryspec)

### PublicationSpec

- `type`:
    - __alternative names__:
        - `publication-type`
        - `pt`
        - `t`
    - __default__: `Maven`
    - __optional__: `true`
    - __type__: [`Enum<RepositoryType>`](#repositorytype)
- `name`:
    - __alternative names__:
        - `id`
        - `key`
        - `n`
        - `pn`
        - `publication-name`
    - __optional__: `false`
    - __type__: `String`
- `component`:
    - __alternative names__:
        - `c`
        - `cmp`
        - `publication-component`
    - __default__: `java`
    - __optional__: `true`
    - __type__: `String`
- `artifacts`:
    - __alternative names__:
        - `a`
        - `arts`
        - `publication-artifacts`
    - __default__: `[]`
    - __optional__: `true`
    - __type__: [`Array<ArtifactSpec>`](#artifactspec)
- `pom`:
    - __alternative names__:
        - `descriptor`
        - `p`
        - `publication-descriptor`
        - `publication-pom`
    - __optional__: `false`
    - __type__: [`PomSpec`](#pomspec)

### ArtifactSpec

- `type`:
    - __alternative names__:
        - `artifact-type`
        - `at`
        - `t`
    - __default__: `Task`
    - __optional__: `true`
    - __type__: [`Enum<ArtifactType>`](#artifacttype)
- `name`:
    - __alternative names__:
        - `file-name`
        - `file`
        - `n`
        - `task-name`
        - `task`
    - __optional__: `false`
    - __type__: `String`
- `required`:
    - __alternative names__:
        - `r`
    - __default__: `false`
    - __optional__: `true`
    - __type__: `Boolean`

### PomSpec

- `id`:
    - __alternative names__:
        - `artifact-id`
        - `module-name`
        - `module`
        - `project-id`
    - __optional__: `false`
    - __type__: `String`
- `description`:
    - __optional__: `true`
    - __type__: `String`
- `developers`:
    - __default__: `[]`
    - __optional__: `true`
    - __type__: [`PomDeveloperSpec`](#pomdeveloperspec)
- `licenses`:
    - __default__: `[]`
    - __optional__: `true`
    - __type__: [`Array<LicenseSpec>`](#licensespec)

### PomDeveloperSpec

- `type`:
    - __alternative names__:
        - `dt`
        - `t`
    - __default__: `Raw`
    - __optional__: `true`
    - __type__: [`Enum<DeveloperType>`](#developertype)
- `location`:
    - __alternative names__:
        - `file`
        - `path`
        - `ref`
    - __default__: `{PROJECT}/project.developers.json`
    - __optional__: `true`
    - __type__: `Path`
- `developers`:
    - __alternative names__:
        - `data`
        - `devs`
        - `values`
    - __default__: `[]`
    - __optional__: `true`
    - __type__: [`Array<DeveloperSpec>`](#developerspec)

### DeveloperSpec

- `name`:
    - __default__: `Maven`
    - __optional__: `true`
    - __type__: `String`
- `email`:
    - __optional__: `false`
    - __type__: `String`
- `timezone`:
    - __optional__: `true`
    - __type__: `String`
- `url`:
    - __alternative names__:
        - `github-url`
        - `github`
        - `website-url`
        - `website`
    - __optional__: `true`
    - __type__: `String`
- `organization`:
    - __alternative names__:
        - `company`
    - __optional__: `true`
    - __type__: [`OrganizationSpec`](#organizationspec)
- `roles`:
    - __default__: `[]`
    - __optional__: `true`
    - __type__: `Array<String>`

### LicenseSpec

- `comments`:
    - __optional__: `true`
    - __type__: `String`
- `dsitribution`:
    - __optional__: `true`
    - __type__: `String`
- `name`:
    - __alternative names__:
        - `license-name`
    - __optional__: `false`
    - __type__: `String`
- `url`:
    - __alternative names__:
        - `license-url`
    - __optional__: `false`
    - __type__: `String`

### RepositorySpec

- `type`:
    - __alternative names__:
        - `repository-type`
        - `rt`
        - `t`
    - __default__: `Maven`
    - __optional__: `true`
    - __type__: [`Enum<RepositoryType>`](#repositorytype)
- `name`:
    - __alternative names__:
        - `repository-name`
        - `rn`
        - `n`
    - __optional__: `false`
    - __type__: `String`
- `credentials`:
    - __optional__: `true`
    - __type__: [`RepositoryCredentialsSpec`](#repositorycredentialsspec)
- `regex`:
    - __optional__: `true`
    - __type__: `String`
- `url`:
    - __default__: `Maven`
    - __optional__: `false`
    - __type__: `String`

### RepositoryCredentialsSpec

- `username`:
    - __alternative names__:
        - `user`
    - __optional__: `false`
    - __type__:
        - `String`
        - [`EnvironmentVariable`](#environmentvariable)
- `password`:
    - __alternative names__:
        - `pass`
    - __optional__: `false`
    - __type__:
        - `String`
      - [`EnvironmentVariable`](#environmentvariable)

### OrganizationSpec

- `name`:
    - __optional__: `false`
    - __type__: `String`
- `url`:
    - __optional__: `false`
    - __type__: `String`

## Json Example

All fields will be resolved when loading the project and no modifications are needed in the build.gradle file.

If the `developers` field is set to `Reference` then it will look for the file specified in the `location` field and 
load all the information from the file. It is also possible to perform a mixed load where you can fill in the information 
from the `developers` field and also load the file from the `location` field.

`project.json`
```json5
{
    "group": "<group-id>",
    "name": "<project-name>",
    "version": "<project-version>",
    "repositories": [
        {
            "name": "snapshot",
            "type": "maven",
            "url": "<maven-repository-url>",
            "regex": "<regular-expression-version-match>",
            "credentials": {
                "user": "{REPO_USERNAME}",    // From environment variable
                "password": "{REPO_PASSWORD}" // From environment variable
            }
        },
        {
            "name": "release",
            "type": "ivy",
            "url": "<ivy-repository-url>",
            "regex": "<regular-expression-version-match>"
        }
    ],
    "publications": [
        {
            "type": "mvn",
            "name": "<publication-name>",
            "component": "<publication-component-name>",
            "artifacts": [
                {
                    "type": "task",
                    "name": "javadoc-jar"
                }
            ],
            "pom": {
                "id": "<project-artifact-publication-id>",
                "description": "<publication-project-description>",
                "url": "<project-url>",
                "developers": {
                    "type": "ref", // Reference | Raw
                    "location": "{PROJECT_ROOT}/developers.json", // Only if type Reference
                    "developers": [
                        {
                            "id": "<username-or-id>",
                            "name": "<full-name>",
                            "email": "<developer-email>",
                            "roles": [
                                "<rol-1>",
                                "<rol-2>",
                                "<rol-n>"
                            ],
                            "organization": {
                                "name": "<organization-name>",
                                "url": "<organization-url>"
                            }
                        }
                    ]
                },
                "scm": {
                    "url": "https://<server>.com/<user-name>/<repository-name>.git",
                    "connection": "scm:git:git@<server>.com:<user>/<repository-name>.git",
                    "developer-connection": "scm:git:git@<server>.com:<user>/<repository-name>.git"
                }
            }
        }
    ]
}
```