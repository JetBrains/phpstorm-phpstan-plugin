# Contributing

Thank you for contributing to the PHPStan plugin.

## Before starting

For a user-visible change:

- Create an issue in [our tracker](https://youtrack.jetbrains.com/newIssue?project=WI).
- Comment on the issue that you intend to work on it.
- Cover the change with a test.
- Include the issue ID in the pull request title or description.

## Build and run

The Gradle wrapper downloads the required Gradle and JDK versions. Build the installable plugin archive with:

```shell
./gradlew buildPlugin
```

The archive is written to `build/distributions`. To launch a development instance of PhpStorm with the plugin installed, run:

```shell
./gradlew runIde
```

## Tests

Run the tests supported by the standalone PhpStorm SDK with:

```shell
./gradlew test
```

Some parser, completion, type-inference, and quality-tool tests use internal PhpStorm test fixtures that are not published with the SDK. Those tests remain part of the IntelliJ monorepo's `intellij.phpstan.tests` suite. The network-dependent `PhpStanNewTagsTest` is also excluded from the default Gradle test task.

Before opening a pull request, run:

```shell
./gradlew test buildPlugin verifyPlugin
```
