package com.intellij.webcore.resourceRoots

/**
 * The public 2026.2 SDK predates the FrameworkExclusionProvider rename in the IntelliJ monorepo.
 * Keep the standalone build source-compatible until a 2026.3 PhpStorm SDK is available.
 */
typealias FrameworkExclusionProvider = PhpFrameworkExclusionProvider