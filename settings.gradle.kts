pluginManagement {
	repositories {
		maven("https://maven.fabricmc.net/") { name = "Fabric" }
		gradlePluginPortal()
	}
}

rootProject.name = "spectre"

fun module(path: String) {
	include(path)
	val project = project(":$path")
	project.projectDir = file("modules/$path")
}

module("spectre-effects")
module("spectre-models")
module("spectre-registration")
