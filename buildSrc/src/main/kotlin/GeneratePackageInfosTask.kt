import org.gradle.api.DefaultTask
import org.gradle.api.file.DirectoryProperty
import org.gradle.api.tasks.InputDirectory
import org.gradle.api.tasks.OutputDirectory
import org.gradle.api.tasks.SkipWhenEmpty
import org.gradle.api.tasks.TaskAction
import java.io.File
import kotlin.io.path.createDirectories
import kotlin.io.path.notExists
import kotlin.io.path.writer

abstract class GeneratePackageInfosTask : DefaultTask() {
	@SkipWhenEmpty
	@InputDirectory
	val root: DirectoryProperty = project.objects.directoryProperty()

	@OutputDirectory
	val output: DirectoryProperty = project.objects.directoryProperty()

	@TaskAction
	fun action() {
		val outputPath = output.get().asFile.toPath()
		val rootPath = root.get().asFile.toPath()

		for (dir in arrayOf("impl", "mixin")) {
			val sourceDir = rootPath.resolve("dev/spiritstudios/spectre/$dir")
			if (sourceDir.notExists()) continue

			sourceDir.toFile().walk()
				.filter { it.isDirectory }
				.forEach {
					val hasFiles = it.listFiles()
						?.filter { file -> !file.isDirectory }
						?.any { file -> file.isFile && file.name.endsWith(".java") } ?: false;

					if (!hasFiles || it.resolve("package-info.java").exists())
						return@forEach

					val relative = rootPath.relativize(it.toPath())
					val target = outputPath.resolve(relative)
					target.createDirectories()

					val packageName = relative.toString().replace(File.separator, ".")
					target.resolve("package-info.java").writer().use { writer ->
						writer.write(
							"""
							|/**
							| * Internal implementation classes for Spectre.
							| * Do not use these classes directly.
							| */
							|
							|@ApiStatus.Internal
							|package $packageName;
							|
							|import org.jetbrains.annotations.ApiStatus;
							 """.trimMargin()
						)
					}
				}
		}
	}
}
