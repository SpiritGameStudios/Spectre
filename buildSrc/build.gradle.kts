plugins {
	`kotlin-dsl`
}

repositories {
	gradlePluginPortal()
	maven("https://maven.fabricmc.net/")
}

dependencies {
	// Allows for libs.versions.toml usage in convention plugins
	implementation(files(libs.javaClass.superclass.protectionDomain.codeSource.location))
	implementation(libs.loom)
}
