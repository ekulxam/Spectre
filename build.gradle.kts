import kotlin.io.path.createDirectories
import kotlin.io.path.notExists
import kotlin.io.path.writer

plugins {
	`java-library`
	`maven-publish`
	alias(libs.plugins.fabric.loom)
}

val modId: String by project
val modVersion: String by project

version = "$modVersion+${libs.versions.minecraft.get()}"
base.archivesName = modId

loom {
	splitEnvironmentSourceSets()
	accessWidenerPath = file("src/main/resources/spectre.accesswidener")

	runs.forEach {
		it.isIdeConfigGenerated = true
	}
}

sourceSets {
	val main by getting
	val client by getting

	val testmod by creating {
		compileClasspath += main.compileClasspath
		runtimeClasspath += main.runtimeClasspath
	}

	create("testmodClient") {
		compileClasspath += main.compileClasspath
		runtimeClasspath += main.runtimeClasspath

		compileClasspath += testmod.compileClasspath
		runtimeClasspath += testmod.runtimeClasspath

		compileClasspath += client.compileClasspath
		runtimeClasspath += client.runtimeClasspath
	}
}

loom {
	mods {
		register("spectre") {
			sourceSet("main")
			sourceSet("client")
		}

		register("testmod") {
			sourceSet("testmod")
			sourceSet("testmodClient")
		}
	}

	runs {
		create("gametest") {
			server()
			name = "Game Test"

			vmArg("-Dfabric-api.gametest")
			vmArg("-Dfabric-api.gametest.report-file=${project.layout.buildDirectory.get()}/junit.xml")
			runDir = "build/gametest"

			source(sourceSets["testmod"])
		}

		create("testmodClient") {
			client()
			configName = "Testmod Client"
			vmArgs("-DMC_DEBUG_DEBUG_ENABLED=true")

			source(sourceSets["testmodClient"])
		}

		create("testmodServer") {
			server()
			name = "Testmod Server"
			vmArgs("-DMC_DEBUG_DEBUG_ENABLED=true")
			source(sourceSets["testmod"])
		}

		configureEach {
			vmArgs("-Dmixin.debug.verify=true")
		}
	}
}

repositories {
	mavenCentral()

	maven {
		name = "Spirit Studios Releases"
		url = uri("https://maven.spiritstudios.dev/releases/")

		content {
			@Suppress("UnstableApiUsage")
			includeGroupAndSubgroups("dev.spiritstudios")
		}
	}

	maven {
		name = "ParchmentMC"
		url = uri("https://maven.parchmentmc.org")

		content {
			@Suppress("UnstableApiUsage")
			includeGroupAndSubgroups("org.parchmentmc")
		}
	}
}

dependencies {
	minecraft(libs.minecraft)

	@Suppress("UnstableApiUsage")
	mappings(
		loom.layered {
			officialMojangMappings()
			parchment(libs.parchment)
		}
	)

	modImplementation(libs.fabric.loader)

	modImplementation(libs.fabric.api)

	"testmodImplementation"(sourceSets["main"].output)
	"testmodClientImplementation"(sourceSets["testmod"].output)
	"testmodClientImplementation"(sourceSets["client"].output)
}

abstract class GeneratePackageInfosTask : DefaultTask() {
	@SkipWhenEmpty
	@InputDirectory
	val root = project.objects.directoryProperty()

	@OutputDirectory
	val output = project.objects.directoryProperty()

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

tasks.processResources {
	val map = mapOf(
		"version" to modVersion
	)

	inputs.properties(map)

	filesMatching("fabric.mod.json") { expand(map) }
}

java {
	withSourcesJar()

	sourceCompatibility = JavaVersion.VERSION_21
	targetCompatibility = JavaVersion.VERSION_21
}

tasks.withType<JavaCompile> {
	options.encoding = "UTF-8"
	options.release = 21
}

tasks.jar {
	from("LICENSE") { rename { "${it}_spectre" } }
}

publishing {
	publications {
		create<MavenPublication>("mavenJava") {
			artifactId = modId
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
