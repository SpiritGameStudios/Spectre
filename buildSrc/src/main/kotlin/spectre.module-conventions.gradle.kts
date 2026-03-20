import org.gradle.accessors.dm.LibrariesForLibs

plugins {
	`maven-publish`
	id("spectre.common-conventions")
}

val libs = the<LibrariesForLibs>()

group = "dev.spiritstudios.spectre"
base.archivesName = name

version = "${rootProject.version}+${libs.versions.minecraft.get()}"

for (sourceSet in arrayOf(sourceSets["main"], sourceSets["client"])) {
	val task = tasks.register<GeneratePackageInfosTask>(sourceSet.getTaskName("generate", "PackageInfos")) {
		group = "codegen"

		root = file("src/${sourceSet.name}/java")
		output = file("src/generated/${sourceSet.name}")
	}

	sourceSet.java.srcDir(task)

	val cleanTask = tasks.register<Delete>(sourceSet.getTaskName("clean", "PackageInfos")) {
		group = "codegen"
		delete(file("src/generated/${sourceSet.name}"))
	}

	tasks.clean.configure { dependsOn(cleanTask) }
}

tasks.javadoc {
	dependsOn(tasks.withType<GeneratePackageInfosTask>())
}

publishing {
	publications {
		create<MavenPublication>("mavenJava") {
			artifactId = artifactId
			from(components["java"])
		}
	}

	repositories {
		maven {
			name = "SpiritStudiosReleases"
			url = uri("https://maven.spiritstudios.dev/releases")
			credentials(PasswordCredentials::class)
		}

		maven {
			name = "SpiritStudiosSnapshots"
			url = uri("https://maven.spiritstudios.dev/snapshots")
			credentials(PasswordCredentials::class)
		}

		mavenLocal()
	}
}
