package dev.spiritstudios.spectre

import org.gradle.api.artifacts.Dependency
import org.gradle.api.artifacts.ProjectDependency
import org.gradle.kotlin.dsl.DependencyHandlerScope
import org.gradle.kotlin.dsl.project

fun DependencyHandlerScope.spectre(module: String): ProjectDependency {
	return dependencies.project(":spectre-$module", configuration = "default")
}

fun DependencyHandlerScope.module(module: String, include: Boolean = false, api: Boolean = true): Dependency? {
	if (include) {
		add("include", spectre(module))
	}

	return if (!api) {
		add("implementation", spectre(module))
	} else {
		add("api", spectre(module))
	}
}
