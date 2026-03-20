import org.gradle.accessors.dm.LibrariesForLibs

plugins {
	`java-library`
	`maven-publish`
	id("net.fabricmc.fabric-loom")
}

val libs = the<LibrariesForLibs>()

@Suppress("UnstableApiUsage")
repositories {
	maven("https://maven.spiritstudios.dev/releases/") {
		name = "Spirit Studios Releases"
		content { includeGroupAndSubgroups("dev.spiritstudios") }
	}

	maven("https://maven.spiritstudios.dev/snapshots/") {
		name = "Spirit Studios Snapshots"
		content { includeGroupAndSubgroups("dev.spiritstudios") }
	}

	mavenLocal()
	mavenCentral()
}

val debugArgs = arrayOf(
	"-enableassertions",

	// Mixin debugging, should make failures happen quicker
	"-Dmixin.debug.verify=true",
	"-Dmixin.debug.strict=true",
	"-Dmixin.debug.countInjections=true",

	// Memory Usage Optimization
	"-XX:+UseZGC",
	"-XX:+UseCompactObjectHeaders",
	"-XX:+UseStringDeduplication",

	"-XX:+AlwaysPreTouch" // Apparently makes startup faster
)


loom {
	runtimeOnlyLog4j = true

	splitEnvironmentSourceSets()

	val classTweakerFile = file("src/main/resources/${name}.classtweaker")

	if (classTweakerFile.exists()) {
		accessWidenerPath = classTweakerFile
	}

	sourceSets {
		register("testmod") {
			compileClasspath += sourceSets["main"].compileClasspath
			runtimeClasspath += sourceSets["main"].runtimeClasspath
		}

		register("testmodClient") {
			compileClasspath += sourceSets["main"].compileClasspath
			runtimeClasspath += sourceSets["main"].runtimeClasspath

			compileClasspath += sourceSets["client"].compileClasspath
			runtimeClasspath += sourceSets["client"].runtimeClasspath

			compileClasspath += sourceSets["testmod"].compileClasspath
			runtimeClasspath += sourceSets["testmod"].runtimeClasspath
		}
	}

	mods {
		create(name) {
			sourceSet("main")
			sourceSet("client")
		}

		create("$name-testmod") {
			sourceSet("testmod")
			sourceSet("testmodClient")
		}
	}


	runs {
		create("testmodClient") {
			client()
			name = "Testmod Client"
			source("testmodClient")
			ideConfigGenerated(project == rootProject)
		}

		create("testmodServer") {
			server()
			name = "Testmod Server"
			source("testmod")
			ideConfigGenerated(project == rootProject)
		}
	}
}

dependencies {
	minecraft(libs.minecraft)

	implementation(libs.fabric.loader)
	implementation(libs.fabric.api)

	"testmodImplementation"(sourceSets["main"].output)
	"testmodClientImplementation"(sourceSets["client"].output)
	"testmodClientImplementation"(sourceSets["testmod"].output)
}

java {
	withSourcesJar()
	withJavadocJar()

	sourceCompatibility = JavaVersion.VERSION_25
	targetCompatibility = JavaVersion.VERSION_25
}

tasks {
	javadoc {
		with(options as StandardJavadocDocletOptions) {
			source = "25"
			encoding = "UTF-8"
			charset("UTF-8")
			memberLevel = JavadocMemberLevel.PACKAGE
			addStringOption("Xdoclint:none", "-quiet")
			addBooleanOption("-syntax-highlight", true)

			links(
				"https://guava.dev/releases/33.5.0-jre/api/docs/",
				"https://asm.ow2.io/javadoc/",
				"https://docs.oracle.com/en/java/javase/25/docs/api/",
			)

			include("**/api/**")

			tags(
				"apiNote:a:API Note:",
				"implSpec:a:Implementation Requirements:",
				"implNote:a:Implementation Note:"
			)

			isFailOnError = false
		}
	}

	withType<JavaCompile> {
		options.release.set(25)
	}
}
