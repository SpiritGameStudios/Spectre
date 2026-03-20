plugins {
	id("spectre.common-conventions")
}

version = "0.1.0"

dependencies {
	for (subproject in subprojects) {
		api(subproject)

		subproject.afterEvaluate {
			val subproj = project.project("${subproject.path}:")

			clientImplementation(subproj.sourceSets.client.get().output)

			testmodImplementation(subproj.sourceSets.testmod.get().output)
			testmodClientImplementation(subproj.sourceSets.testmodClient.get().output)
		}
	}
}

sourceSets {
	get("testmod").apply {
		compileClasspath += sourceSets["main"].compileClasspath
		runtimeClasspath += sourceSets["main"].runtimeClasspath
	}

	get("testmodClient").apply {
		compileClasspath += sourceSets["main"].compileClasspath
		runtimeClasspath += sourceSets["main"].runtimeClasspath

		compileClasspath += sourceSets["client"].compileClasspath
		runtimeClasspath += sourceSets["client"].runtimeClasspath

		compileClasspath += sourceSets["testmod"].compileClasspath
		runtimeClasspath += sourceSets["testmod"].runtimeClasspath
	}
}
